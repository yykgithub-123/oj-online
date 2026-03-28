package com.yyk.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyk.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yyk.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yyk.oj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yyk.oj.model.entity.User;
import com.yyk.oj.model.vo.QuestionSubmitVO;

import java.util.List;

/**
*
* @description 针对表【question_submit(题目提交)】的数据库操作Service
*
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

    /**
     * 统计已接受的提交数量
     *
     * @return 已接受提交数量
     */
    Integer countAccepted();

    /**
     * 统计今日提交数量
     *
     * @return 今日提交数量
     */
    Integer countTodaySubmissions();

    /**
     * 统计用户已接受的提交数量
     *
     * @param userId 用户ID
     * @return 已接受提交数量
     */
    Integer countUserAccepted(Long userId);

    /**
     * 统计今日已接受的提交数量
     *
     * @return 今日已接受提交数量
     */
    Integer countTodayAccepted();

    /**
     * 统计今日活跃用户数量
     *
     * @return 今日活跃用户数量
     */
    Integer countTodayUsers();

    /**
     * 获取用户每日活跃情况（返回有提交记录的日期列表）
     *
     * @param userId 用户ID
     * @return 日期列表 (yyyy-MM-dd)
     */
    List<String> getUserDailyActivity(Long userId);
}
