package com.yyk.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.yyk.oj.model.dto.question.JudgeCase;
import com.yyk.oj.model.dto.question.JudgeConfig;
import com.yyk.oj.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类
 * @TableName question
 */
@Data
public class QuestionVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 原始代码
     */
    private String sourceCode;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 判题用例（json 数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 题目难度
     */
    private String difficulty;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建用户信息
     */
    private UserVO userVO;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);

        // 安全解析 tags 字段
        try {
            String tagsStr = question.getTags();
            if (tagsStr != null && !tagsStr.isEmpty()) {
                List<String> tagList = JSONUtil.toList(tagsStr, String.class);
                questionVO.setTags(tagList);
            }
        } catch (Exception e) {
            // JSON 解析失败时，设置为空列表
            questionVO.setTags(new java.util.ArrayList<>());
        }

        // 安全解析 judgeConfig 字段
        try {
            String judgeConfigStr = question.getJudgeConfig();
            if (judgeConfigStr != null && !judgeConfigStr.isEmpty()) {
                questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
            }
        } catch (Exception e) {
            // JSON 解析失败时，设置为 null
            questionVO.setJudgeConfig(null);
        }

        // 安全解析 judgeCase 字段
        try {
            String judgeCaseStr = question.getJudgeCase();
            if (judgeCaseStr != null && !judgeCaseStr.isEmpty()) {
                List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
                if (judgeCaseList != null && !judgeCaseList.isEmpty()) {
                    // 仅保留第一个元素，防止泄露全部题目答案
                    JudgeCase firstCase = judgeCaseList.get(0);
                    questionVO.setJudgeCase(java.util.Collections.singletonList(firstCase));
                }
            }
        } catch (Exception e) {
            // JSON 解析失败时，设置为 null
            questionVO.setJudgeCase(null);
        }

        return questionVO;
    }

    private static final long serialVersionUID = 1L;
}