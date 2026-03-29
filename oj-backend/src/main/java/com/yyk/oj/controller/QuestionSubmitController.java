package com.yyk.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyk.oj.common.BaseResponse;
import com.yyk.oj.common.ErrorCode;
import com.yyk.oj.common.ResultUtils;
import com.yyk.oj.exception.BusinessException;
import com.yyk.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yyk.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yyk.oj.model.entity.QuestionSubmit;
import com.yyk.oj.model.entity.User;
import com.yyk.oj.model.vo.QuestionSubmitVO;
import com.yyk.oj.service.QuestionSubmitService;
import com.yyk.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目提交接口
 *  
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录的id
     */
    @PostMapping("/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交记录
        final User loginUser = userService.getLoginUser(request);
        long resultId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(resultId);
    }

    /**
     * 分页获取题目提交列表（除管理员外，普通用户只能看到公开信息，比如语言、题目标签等）
     *
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        Page<QuestionSubmit> questionPage = questionSubmitService.page(new Page<>(current, size),
                // 获取的是所有的列表
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        // 获取用户信息
        final User loginUser = userService.getLoginUser(request);
        // 进行过滤，只获取公开信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionPage, loginUser));
    }

    /**
     * 获取今日提交统计
     */
    @GetMapping("/today/stats")
    public BaseResponse<Map<String, Object>> getTodaySubmissionStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取今日提交统计
        Integer todaySubmissions = questionSubmitService.countTodaySubmissions();
        Integer todayAccepted = questionSubmitService.countTodayAccepted();
        Integer todayUsers = questionSubmitService.countTodayUsers();
        
        stats.put("todaySubmissions", todaySubmissions);
        stats.put("todayAccepted", todayAccepted);
        stats.put("todayUsers", todayUsers);
        
        return ResultUtils.success(stats);
    }

    /**
     * 获取用户每日提交活跃情况
     */
    @GetMapping("/daily/activity")
    public BaseResponse<List<String>> getUserDailyActivity(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<String> dailyActivity = questionSubmitService.getUserDailyActivity(loginUser.getId());
        return ResultUtils.success(dailyActivity);
    }


    /**
     * 删除提交记录（仅管理员可用）
     *
     * @param id 提交记录ID
     * @param request
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionSubmit(@RequestBody Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交记录ID无效");
        }
        // 仅管理员可删除
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除提交记录");
        }
        boolean result = questionSubmitService.deleteQuestionSubmit(id);
        return ResultUtils.success(result);
    }
}
