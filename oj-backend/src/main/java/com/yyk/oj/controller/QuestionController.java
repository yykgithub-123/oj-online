package com.yyk.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yyk.oj.annotation.AuthCheck;
import com.yyk.oj.common.BaseResponse;
import com.yyk.oj.common.DeleteRequest;
import com.yyk.oj.common.ErrorCode;
import com.yyk.oj.common.ResultUtils;
import com.yyk.oj.constant.UserConstant;
import com.yyk.oj.exception.BusinessException;
import com.yyk.oj.exception.ThrowUtils;
import com.yyk.oj.mapper.UserMapper;
import com.yyk.oj.model.dto.question.*;
import com.yyk.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yyk.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yyk.oj.model.entity.Question;
import com.yyk.oj.model.entity.QuestionSubmit;
import com.yyk.oj.model.entity.User;
import com.yyk.oj.model.vo.QuestionAdminVo;
import com.yyk.oj.model.vo.QuestionSubmitVO;
import com.yyk.oj.model.vo.QuestionVO;
import com.yyk.oj.service.QuestionService;
import com.yyk.oj.service.QuestionSubmitService;
import com.yyk.oj.service.UserService;
import com.yyk.oj.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;

/**
 * 题目接口
 *  
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private TagService tagService;

    @Resource
    private UserMapper userMapper;
    private final static Gson GSON = new Gson();

    /**
     * 默认 Java 源代码模板
     */
    private static final String DEFAULT_SOURCE_CODE =
        "import java.util.Scanner;\n" +
        "// 1:无需package\n" +
        "// 2: 类名必须Main, 不可修改\n" +
        "\n" +
        "public class Main {\n" +
        "    public static void main(String[] args) {\n" +
        "        Scanner scan = new Scanner(System.in);\n" +
        "        //在此输入您的代码...\n" +
        "        scan.close();\n" +
        "    }\n" +
        "}";

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        // 如果 sourceCode 为空，设置默认模板
        if (StringUtils.isBlank(question.getSourceCode())) {
            question.setSourceCode(DEFAULT_SOURCE_CODE);
        }
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());

        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 题目预览
     */
    @GetMapping("/preview/{id}")
    public BaseResponse<QuestionVO> previewQuestion(@PathVariable Long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QuestionVO questionVO = questionService.getQuestionVO(question, request);
        return ResultUtils.success(questionVO);
    }

    /**
     * 获取所有标签
     */
    @GetMapping("/tags")
    public BaseResponse<List<String>> getAllTags() {
        List<String> tags = tagService.getAllTags();
        return ResultUtils.success(tags);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/tags/popular")
    public BaseResponse<List<String>> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        List<String> tags = tagService.getPopularTags(limit);
        return ResultUtils.success(tags);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 不是本人或管理员，不能直接获取所有信息
        if (!question.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 根据 id 获取（脱敏）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionAdminVo>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                  HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionAdminVOPage(questionPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录的 id
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

    /**
     * 获取题目统计信息
     */
    @GetMapping("/stats")
    public BaseResponse<Map<String, Object>> getQuestionStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取基础统计信息
        stats.put("totalQuestions", questionService.count());
        stats.put("totalSubmissions", questionSubmitService.count());
        stats.put("totalAccepted", questionSubmitService.countAccepted());
        stats.put("todaySubmissions", questionSubmitService.countTodaySubmissions());
        
        // 计算成功率
        Long totalSubmissions = questionSubmitService.count();
        Integer totalAccepted = questionSubmitService.countAccepted();
        double successRate = 0.0;
        if (totalSubmissions != null && totalSubmissions > 0) {
            successRate = Math.round((double) totalAccepted / totalSubmissions * 100.0 * 10.0) / 10.0;
        }
        stats.put("successRate", successRate);
        
        return ResultUtils.success(stats);
    }

    /**
     * 批量删除题目
     */
    @PostMapping("/batch/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Map<String, Object>> batchDeleteQuestions(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择要删除的题目");
        }
        
        int deletedCount = questionService.batchDeleteQuestions(ids);
        
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", deletedCount);
        
        return ResultUtils.success(result);
    }

    /**
     * 高级搜索题目
     */
    @PostMapping("/search/advanced")
    public BaseResponse<Page<QuestionVO>> advancedSearch(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        
        // 构建查询条件
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        
        String title = (String) request.get("title");
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        
        String difficulty = (String) request.get("difficulty");
        if (StringUtils.isNotBlank(difficulty)) {
            queryWrapper.eq("difficulty", difficulty);
        }
        
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) request.get("tags");
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                queryWrapper.like("tags", tag);
            }
        }
        
        @SuppressWarnings("unchecked")
        Map<String, String> dateRange = (Map<String, String>) request.get("dateRange");
        if (dateRange != null) {
            String start = dateRange.get("start");
            String end = dateRange.get("end");
            if (StringUtils.isNotBlank(start)) {
                queryWrapper.ge("createTime", start);
            }
            if (StringUtils.isNotBlank(end)) {
                queryWrapper.le("createTime", end);
            }
        }
        
        queryWrapper.eq("isDelete", false);
        
        // 排序
        String sortBy = (String) request.get("sortBy");
        String sortOrder = (String) request.get("sortOrder");
        if (StringUtils.isNotBlank(sortBy)) {
            queryWrapper.orderBy(true, "desc".equals(sortOrder), sortBy);
        } else {
            queryWrapper.orderByDesc("createTime");
        }
        
        // 分页
        Integer current = (Integer) request.get("current");
        Integer pageSize = (Integer) request.get("pageSize");
        current = current != null ? current : 1;
        pageSize = pageSize != null ? pageSize : 10;
        
        Page<Question> questionPage = questionService.page(new Page<>(current, pageSize), queryWrapper);
        
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, httpRequest));
    }

    /**
     * 获取题目预览信息
     */
    @GetMapping("/{id}/preview")
    public BaseResponse<QuestionVO> getQuestionPreview(@PathVariable Long id, HttpServletRequest request) {
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        
        QuestionVO questionVO = questionService.getQuestionVO(question, request);
        
        return ResultUtils.success(questionVO);
    }

    /**
     * 更新题目难度
     */
    @PostMapping("/{id}/difficulty")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionDifficulty(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String difficulty = request.get("difficulty");
        if (StringUtils.isBlank(difficulty)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "难度不能为空");
        }
        
        boolean result = questionService.updateQuestionDifficulty(id, difficulty);
        return ResultUtils.success(result);
    }


}
