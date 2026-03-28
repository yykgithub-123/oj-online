package com.yyk.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyk.oj.common.ErrorCode;
import com.yyk.oj.constant.CommonConstant;
import com.yyk.oj.exception.BusinessException;
import com.yyk.oj.judge.JudgeService;
import com.yyk.oj.mapper.QuestionMapper;
import com.yyk.oj.mapper.QuestionSubmitMapper;
import com.yyk.oj.mapper.UserMapper;
import com.yyk.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yyk.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yyk.oj.model.entity.Question;
import com.yyk.oj.model.entity.QuestionSubmit;
import com.yyk.oj.model.entity.User;
import com.yyk.oj.model.enums.QuestionSubmitLanguageEnum;
import com.yyk.oj.model.enums.QuestionSubmitStatusEnum;
import com.yyk.oj.model.vo.QuestionSubmitVO;
import com.yyk.oj.model.vo.QuestionVO;
import com.yyk.oj.model.vo.UserVO;
import com.yyk.oj.service.QuestionService;
import com.yyk.oj.service.QuestionSubmitService;
import com.yyk.oj.service.UserService;
import com.yyk.oj.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 *
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 *
 */
@Service
@Slf4j
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest（题目提交信息）
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        // 更新题目的提交数
        UpdateWrapper<Question> questionUpdateWrapper = new UpdateWrapper<>();
        questionUpdateWrapper.eq("id", questionId);
        questionUpdateWrapper.setSql("submitNum = submitNum + 1");
        boolean update = questionService.update(questionUpdateWrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交数更新失败");
        }
        // 执行判题服务
        Long questionSubmitId = questionSubmit.getId();
        CompletableFuture.runAsync(() -> {
            try {
                log.info("开始判题，提交ID: {}", questionSubmitId);
                judgeService.doJudge(questionSubmitId);
                log.info("判题完成，提交ID: {}", questionSubmitId);
            } catch (Exception e) {
                log.error("判题失败，提交ID: {}", questionSubmitId, e);
                // 更新状态为失败
                QuestionSubmit failedSubmit = new QuestionSubmit();
                failedSubmit.setId(questionSubmitId);
                failedSubmit.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
                failedSubmit.setJudgeInfo("{\"message\":\"判题失败: " + e.getMessage() + "\"}");
                this.updateById(failedSubmit);
            }
        });
        return questionSubmitId;
    }


    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        String status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 添加调试信息
        System.out.println("查询参数 - language: " + language + ", status: " + status + ", questionId: " + questionId + ", userId: " + userId);

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(StringUtils.isNotBlank(status), "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        
        // 打印生成的SQL
        System.out.println("生成的SQL: " + queryWrapper.getSqlSegment());
        
        return queryWrapper;
    }

    /**
     * 获取题目封装(连表查询)
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 1. 关联查询用户信息
        long userId = loginUser.getId();
        // 处理脱敏
        // 不是当前用户或者不是管理员，不能获取提交的代码
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        // 获取当前分页中的 QuestionSubmit 记录
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());

        // 如果记录为空，直接返回空的 VO 页
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }

        // 收集所有 questionId 和 userId
        Set<Long> questionIds = questionSubmitList.stream()
                .map(QuestionSubmit::getQuestionId)
                .collect(Collectors.toSet());
        Set<Long> userIds = questionSubmitList.stream()
                .map(QuestionSubmit::getUserId)
                .collect(Collectors.toSet());

        // 使用 MyBatis Plus 批量查询 question 信息
        Map<Long, QuestionVO> questionMap = questionMapper.selectBatchIds(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, this::convertToQuestionVO));

        // 使用 MyBatis Plus 批量查询 user 信息
        Map<Long, UserVO> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, this::convertToUserVO));

        // 构建 QuestionSubmitVO 列表
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> {
                    QuestionSubmitVO vo = QuestionSubmitVO.objToVo(questionSubmit);
                    vo.setQuestionVO(questionMap.get(questionSubmit.getQuestionId()));
                    vo.setUserVO(userMap.get(questionSubmit.getUserId()));
                    return vo;
                })
                .collect(Collectors.toList());

        // 设置结果到分页对象
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public Integer countAccepted() {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", QuestionSubmitStatusEnum.SUCCEED.getValue());
        queryWrapper.eq("isDelete", false);
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public Integer countTodaySubmissions() {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("createTime", LocalDate.now().atStartOfDay());
        queryWrapper.eq("isDelete", false);
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public Integer countUserAccepted(Long userId) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("status", QuestionSubmitStatusEnum.SUCCEED.getValue());
        queryWrapper.eq("isDelete", false);
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public Integer countTodayAccepted() {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("createTime", LocalDate.now().atStartOfDay());
        queryWrapper.eq("status", QuestionSubmitStatusEnum.SUCCEED.getValue());
        queryWrapper.eq("isDelete", false);
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public Integer countTodayUsers() {
        // 统计今日有提交记录的不同用户数量
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT userId");
        queryWrapper.ge("createTime", LocalDate.now().atStartOfDay());
        queryWrapper.eq("isDelete", false);
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public List<String> getUserDailyActivity(Long userId) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        // 仅查询日期，去重
        queryWrapper.select("DISTINCT DATE_FORMAT(createTime, '%Y-%m-%d') as activityDate");
        queryWrapper.eq("userId", userId);
        // 这里假设只要有通过记录就算活跃，或者只要有提交就算？
        // 通常日历打卡是指"成功解决题目"或者"有提交行为"。
        // 考虑到用户体验，只要有通过记录显示绿色比较合理。
        // 如果要显示"提交过"，可以去掉 status 条件。
        // 按照之前的 random 逻辑，这里先只统计 Accepted 的。
        queryWrapper.eq("status", QuestionSubmitStatusEnum.SUCCEED.getValue());
        queryWrapper.eq("isDelete", false);
        
        List<Map<String, Object>> maps = this.listMaps(queryWrapper);
        return maps.stream()
                .map(map -> (String) map.get("activityDate"))
                .collect(Collectors.toList());
    }

    private QuestionVO convertToQuestionVO(Question question) {
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(question, vo);
        return vo;
    }

    private UserVO convertToUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
