package com.yyk.oj.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyk.oj.common.ErrorCode;
import com.yyk.oj.constant.CommonConstant;
import com.yyk.oj.exception.BusinessException;
import com.yyk.oj.mapper.QuestionSubmitMapper;
import com.yyk.oj.mapper.UserMapper;
import com.yyk.oj.model.dto.user.UserQueryRequest;
import com.yyk.oj.model.entity.QuestionSubmit;
import com.yyk.oj.model.entity.User;
import com.yyk.oj.model.enums.JudgeInfoMessageEnum;
import com.yyk.oj.model.enums.QuestionSubmitStatusEnum;
import com.yyk.oj.model.enums.UserRoleEnum;
import com.yyk.oj.model.vo.LoginUserVO;
import com.yyk.oj.model.vo.UserStatsVO;
import com.yyk.oj.model.vo.UserVO;
import com.yyk.oj.service.UserService;
import com.yyk.oj.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yyk.oj.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /** 排行榜ZSet的key */
    private static final String RANKING_TOTAL_KEY = "ranking:total";
    private static final String RANKING_WEEKLY_KEY = "ranking:weekly";

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "yyk";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserAvatar("/yake.webp");  // 设置默认头像
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.warn("[用户登录] 登录失败, 账号={}", userAccount);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 账号被封禁
        if (UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "账号已被封禁，无法登录");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 被封禁的用户，拒绝一切操作
        if (UserRoleEnum.BAN.getValue().equals(currentUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "账号已被封禁");
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public List<UserStatsVO> getUserRankingList(int limit) {
        // 1. 尝试从Redis ZSet获取排行
        List<UserStatsVO> result = getRankingFromZSet(RANKING_TOTAL_KEY, limit);
        if (result != null) {
            return result;
        }
        // 2. ZSet为空，从数据库加载并写入ZSet
        result = loadAndCacheRanking(RANKING_TOTAL_KEY, limit, false);
        return result;
    }

    @Override
    public List<UserStatsVO> getWeeklyUserRankingList(int limit) {
        // 1. 尝试从Redis ZSet获取排行
        List<UserStatsVO> result = getRankingFromZSet(RANKING_WEEKLY_KEY, limit);
        if (result != null) {
            return result;
        }
        // 2. ZSet为空，从数据库加载并写入ZSet
        result = loadAndCacheRanking(RANKING_WEEKLY_KEY, limit, true);
        return result;
    }

    /**
     * 从Redis ZSet获取排行榜
     */
    private List<UserStatsVO> getRankingFromZSet(String key, int limit) {
        Long size = stringRedisTemplate.opsForZSet().size(key);
        if (size == null || size == 0) {
            return null;
        }
        // 按分数降序获取Top N
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, limit - 1);
        if (tuples == null || tuples.isEmpty()) {
            return null;
        }
        List<UserStatsVO> result = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String userIdStr = tuple.getValue();
            if (userIdStr == null) {
                continue;
            }
            Long userId = Long.valueOf(userIdStr);
            UserStatsVO stats = getUserStats(userId);
            if (stats == null) {
                continue;
            }
            stats.setRank(rank++);
            result.add(stats);
        }
        return result;
    }

    /**
     * 从数据库加载排行榜并写入Redis ZSet
     */
    private List<UserStatsVO> loadAndCacheRanking(String key, int limit, boolean weekly) {
        List<User> userList = this.list();
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        // 并行获取每个用户的统计信息
        List<UserStatsVO> userStatsList = userList.parallelStream()
                .map(user -> {
                    try {
                        UserStatsVO stats = getUserStats(user.getId());
                        if (stats == null) {
                            stats = new UserStatsVO();
                            stats.setUserId(user.getId());
                            stats.setUserName(user.getUserName());
                            stats.setUserAvatar(user.getUserAvatar());
                            stats.setTotalSolved(0);
                            stats.setAcceptedSubmissions(0);
                            stats.setTotalSubmissions(0);
                            stats.setAcceptanceRate(0.0);
                        }
                        return stats;
                    } catch (Exception e) {
                        log.error("[用户统计] 获取失败, 用户编号={}", user.getId(), e);
                        return null;
                    }
                })
                .filter(stats -> stats != null)
                .collect(Collectors.toList());

        // 写入Redis ZSet
        for (UserStatsVO stats : userStatsList) {
            int solved = weekly ? (stats.getWeeklySolved() == null ? 0 : stats.getWeeklySolved()) : (stats.getTotalSolved() == null ? 0 : stats.getTotalSolved());
            if (solved > 0) {
                stringRedisTemplate.opsForZSet().add(key, String.valueOf(stats.getUserId()), solved);
            }
        }
        // 设置过期时间：总榜1小时，周榜10分钟
        stringRedisTemplate.expire(key, weekly ? 10 : 60, java.util.concurrent.TimeUnit.MINUTES);

        // 排序
        userStatsList.sort((a, b) -> {
            int solvedA = weekly ? (a.getWeeklySolved() == null ? 0 : a.getWeeklySolved()) : (a.getTotalSolved() == null ? 0 : a.getTotalSolved());
            int solvedB = weekly ? (b.getWeeklySolved() == null ? 0 : b.getWeeklySolved()) : (b.getTotalSolved() == null ? 0 : b.getTotalSolved());
            int solvedCompare = Integer.compare(solvedB, solvedA);
            if (solvedCompare != 0) {
                return solvedCompare;
            }
            return b.getAcceptanceRate().compareTo(a.getAcceptanceRate());
        });

        // 截取前N名并设置排名
        List<UserStatsVO> result = userStatsList.stream()
                .limit(limit)
                .collect(Collectors.toList());
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }
        return result;
    }

    @Override
    @Cacheable(value = "userStats", key = "#userId")
    public UserStatsVO getUserStats(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            return null;
        }

        UserStatsVO userStats = new UserStatsVO();
        userStats.setUserId(userId);
        userStats.setUserName(user.getUserName());
        userStats.setUserAvatar(user.getUserAvatar());

        // 查询用户的所有提交记录
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        // 仅查询必要的字段，减少数据传输量
        queryWrapper.select("id", "questionId", "status", "createTime", "judgeInfo");
        List<QuestionSubmit> submissions = questionSubmitMapper.selectList(queryWrapper);

        // 统计总提交次数
        userStats.setTotalSubmissions(submissions.size());

        // 筛选成功的提交
        // 注意：这里需要同时兼容 SUCCEED (2) 和 ACCEPTED (虽然通常只有 SUCCEED 存库，但 JudgeInfo 里的 message 才是 ACCEPTED)
        // 根据 QuestionSubmitStatusEnum: 0-等待, 1-判题中, 2-成功, 3-失败
        // 这里的"成功"是指判题过程成功，不代表通过题目。
        // 但是在 JudgeServiceImpl 中，只有当判题结果为 Accepted 时，才会更新 question 表的 acceptedNum。
        // 而对于 QuestionSubmit 表，status=2 仅仅表示沙箱跑完了。
        // 我们需要检查 judgeInfo 中的 message 字段来判断是否真的通过，但是我们为了性能只查了 status 字段。
        // 回看 JudgeServiceImpl，如果沙箱执行失败，status也会设为 2 (SUCCEED) 但 judgeInfo 会记录错误。
        // 所以仅仅通过 status=2 来统计通过数是不准确的！
        // 必须查询 judgeInfo 字段来判断，或者在 question_submit 表中增加一个 isAccepted 字段。
        // 鉴于不修改表结构，我们只能牺牲一点性能查询 judgeInfo，或者假设 acceptedNum 的更新逻辑是正确的。
        
        // 修正逻辑：重新查询带 judgeInfo 的记录，或者只查询 question 表的 acceptedNum？
        // 不行，UserStats 需要统计用户维度的。
        
        List<QuestionSubmit> acceptedSubmissions = submissions.stream()
                .filter(submit -> {
                    if (submit.getStatus() == null || !submit.getStatus().equals(QuestionSubmitStatusEnum.SUCCEED.getValue())) {
                        return false;
                    }
                    // 解析 judgeInfo
                    String judgeInfoStr = submit.getJudgeInfo();
                    if (StringUtils.isBlank(judgeInfoStr)) {
                        return false;
                    }
                    try {
                        // 使用 JSONUtil 解析，更稳健
                        JSONObject judgeInfo = JSONUtil.parseObj(judgeInfoStr);
                        String message = judgeInfo.getStr("message");

                        // 兼容多种 message 值：Accepted、成功、通过所有测试用例
                        boolean isAccepted = JudgeInfoMessageEnum.ACCEPTED.getValue().equals(message) ||
                                           JudgeInfoMessageEnum.ACCEPTED.getText().equals(message) ||
                                           "通过所有测试用例".equals(message);

                        if (!isAccepted) {
                             // log.info("提交ID: {}, 状态: {}, Message: {}", submit.getId(), submit.getStatus(), message);
                        }
                        return isAccepted;
                    } catch (Exception e) {
                        log.error("[用户统计] 解析判题信息失败, 提交编号={}, 原因={}", submit.getId(), e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());
        
        userStats.setAcceptedSubmissions(acceptedSubmissions.size());

        // 统计总完成题目数量（去重）
        long totalSolved = acceptedSubmissions.stream()
                .map(QuestionSubmit::getQuestionId)
                .distinct()
                .count();
        userStats.setTotalSolved((int) totalSolved);

        // 统计本周完成题目数量
        // 使用 Hutool 的 DateUtil 获取本周一（中国习惯）
        Date weekStart = DateUtil.beginOfWeek(new Date());

        long weeklySolved = acceptedSubmissions.stream()
                .filter(submit -> submit.getCreateTime() != null && submit.getCreateTime().after(weekStart))
                .map(QuestionSubmit::getQuestionId)
                .distinct()
                .count();
        userStats.setWeeklySolved((int) weeklySolved);

        // 计算成功率
        if (userStats.getTotalSubmissions() > 0) {
            double rate = (double) userStats.getAcceptedSubmissions() / userStats.getTotalSubmissions() * 100;
            // 保留一位小数
            userStats.setAcceptanceRate(Math.round(rate * 10.0) / 10.0);
        } else {
            userStats.setAcceptanceRate(0.0);
        }

        return userStats;
    }

    @Override
    @CacheEvict(value = "userStats", key = "#userId")
    public void clearUserStatsCache(Long userId) {
        // 仅用于清除缓存，无需额外操作
    }
}
