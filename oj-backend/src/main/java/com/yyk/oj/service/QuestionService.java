package com.yyk.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yyk.oj.model.dto.question.QuestionQueryRequest;
import com.yyk.oj.model.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yyk.oj.model.vo.QuestionAdminVo;
import com.yyk.oj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
*
* @description 针对表【question(题目)】的数据库操作Service
*
*/
public interface QuestionService extends IService<Question> {


    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);
    
    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    Page<QuestionAdminVo> getQuestionAdminVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 批量删除题目
     *
     * @param ids 题目ID列表
     * @return 删除数量
     */
    Integer batchDeleteQuestions(List<Long> ids);

    /**
     * 更新题目难度
     *
     * @param questionId 题目ID
     * @param difficulty 难度
     * @return 是否成功
     */
    Boolean updateQuestionDifficulty(Long questionId, String difficulty);

    /**
     * 清除题目缓存
     *
     * @param questionId 题目ID
     */
    void clearQuestionCache(Long questionId);

}
