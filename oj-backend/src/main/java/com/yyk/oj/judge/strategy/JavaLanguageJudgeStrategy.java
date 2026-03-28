package com.yyk.oj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.yyk.oj.model.dto.question.JudgeCase;
import com.yyk.oj.model.dto.question.JudgeConfig;
import com.yyk.oj.judge.codesandbox.model.JudgeInfo;
import com.yyk.oj.model.entity.Question;
import com.yyk.oj.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

/**
 * Java 程序的判题策略
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);
        // 先判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            String expected = judgeCase.getOutput();
            String actual = outputList.get(i);
            
            // 简单的处理：去除首尾空白字符后比较
            if (expected != null) {
                expected = expected.trim();
            } else {
                expected = "";
            }
            if (actual != null) {
                actual = actual.trim();
            } else {
                actual = "";
            }

            // 如果直接相等，则通过
            if (expected.equals(actual)) {
                continue;
            }

            // 增强版清洗逻辑：解决多行输出、JSON 格式、标点符号不一致等问题
            // 1. 去除方括号、逗号、引号等标点
            // 2. 将换行符、制表符替换为空格
            // 3. 将连续空白字符合并为单个空格
            // 4. 忽略大小写差异（可选，视题目要求而定，一般 OJ 不区分大小写）
            
            String simplifiedExpected = cleanOutput(expected);
            String simplifiedActual = cleanOutput(actual);
            
            if (!simplifiedExpected.equals(simplifiedActual)) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }
        
        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        // memory 和 needMemoryLimit 单位都是 MB，直接比较
        if (memory > needMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (time > needTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }

    private String cleanOutput(String content) {
        if (content == null) {
            return "";
        }
        // 去除方括号、逗号、双引号、单引号
        content = content.replaceAll("[\\[\\]\",']", " ");
        // 去除非数字、非字母、非负号的字符（更彻底的清洗）
        // 注意：保留负号 - 用于负数
        // content = content.replaceAll("[^a-zA-Z0-9-]", " ");
        
        // 将所有空白字符（包括换行、制表符）替换为单个空格
        content = content.replaceAll("\\s+", " ");
        // 去除首尾空格并转为小写
        return content.trim().toLowerCase();
    }
}
