package com.yyk.oj.judge;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yyk.oj.common.ErrorCode;
import com.yyk.oj.exception.BusinessException;
import com.yyk.oj.judge.codesandbox.CodeSandbox;
import com.yyk.oj.judge.codesandbox.CodeSandboxFactory;
import com.yyk.oj.judge.codesandbox.CodeSandboxProxy;
import com.yyk.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yyk.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yyk.oj.judge.strategy.JudgeContext;
import com.yyk.oj.model.dto.question.JudgeCase;
import com.yyk.oj.judge.codesandbox.model.JudgeInfo;
import com.yyk.oj.model.entity.Question;
import com.yyk.oj.model.entity.QuestionSubmit;
import com.yyk.oj.model.enums.JudgeInfoMessageEnum;
import com.yyk.oj.model.enums.QuestionSubmitStatusEnum;
import com.yyk.oj.service.QuestionService;
import com.yyk.oj.service.QuestionSubmitService;
import com.yyk.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        if (StringUtils.isBlank(judgeCaseStr)) {
             judgeCaseStr = "[]";
        }
        // 防止出现 json 字符串中包含 json 字符串导致的解析错误
        judgeCaseStr = judgeCaseStr.replace("\\\"", "\"");
        judgeCaseStr = judgeCaseStr.replace("\"{", "{");
        judgeCaseStr = judgeCaseStr.replace("}\"", "}");

        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 将字面字符串 \n 转换为真正的换行符
        inputList = inputList.stream().map(input -> input.replace("\\n", "\n")).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("[判题服务] 提交编号={}, 题目编号={}, 沙箱执行状态={}", 
                questionSubmitId, questionId, executeCodeResponse.getStatus());
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        JudgeInfo executeJudgeInfo = executeCodeResponse.getJudgeInfo();
        Integer status = executeCodeResponse.getStatus();

        // 判题逻辑...
        // ... (此处省略部分异常处理逻辑，保持原样)
        
        // 正常判题逻辑
        if (executeJudgeInfo != null && outputList != null && (status == null || status == 1)) {
             judgeContext.setJudgeInfo(executeJudgeInfo);
             judgeContext.setInputList(inputList);
             judgeContext.setOutputList(outputList);
             judgeContext.setJudgeCaseList(judgeCaseList);
             judgeContext.setQuestion(question);
             judgeContext.setQuestionSubmit(questionSubmit);
        } else {
             // 异常处理逻辑，构建默认的 JudgeInfo
             JudgeInfo errorJudgeInfo = new JudgeInfo();
             String message = executeCodeResponse.getMessage();
             if (StringUtils.isBlank(message)) {
                 message = "执行失败";
             }
             errorJudgeInfo.setMessage(message);
             errorJudgeInfo.setMemory(executeJudgeInfo != null ? executeJudgeInfo.getMemory() : 0L);
             errorJudgeInfo.setTime(executeJudgeInfo != null ? executeJudgeInfo.getTime() : 0L);
             
             // 直接保存错误结果并返回
             questionSubmitUpdate = new QuestionSubmit();
             questionSubmitUpdate.setId(questionSubmitId);
             questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
             questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(errorJudgeInfo));
             // 设置执行时间、内存使用量、代码长度
             questionSubmitUpdate.setExecutionTime(errorJudgeInfo.getTime() != null ? errorJudgeInfo.getTime().intValue() : 0);
             questionSubmitUpdate.setMemoryUsage(errorJudgeInfo.getMemory() != null ? errorJudgeInfo.getMemory().intValue() : 0);
             questionSubmitUpdate.setCodeLength(code.length());
             update = questionSubmitService.updateById(questionSubmitUpdate);
             if (!update) {
                 throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
             }
             return questionSubmitService.getById(questionSubmitId);
        }

        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 6）修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        // 根据判题结果设置状态：Accepted 为成功，否则为失败
        if (JudgeInfoMessageEnum.ACCEPTED.getValue().equals(judgeInfo.getMessage())) {
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        } else {
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        }
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        // 设置执行时间、内存使用量、代码长度
        if (judgeInfo.getTime() != null) {
            questionSubmitUpdate.setExecutionTime(judgeInfo.getTime().intValue());
        }
        if (judgeInfo.getMemory() != null) {
            questionSubmitUpdate.setMemoryUsage(judgeInfo.getMemory().intValue());
        }
        questionSubmitUpdate.setCodeLength(code.length());
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        
        // 更新题目的通过数
        if (JudgeInfoMessageEnum.ACCEPTED.getValue().equals(judgeInfo.getMessage())) {
            UpdateWrapper<Question> questionUpdateWrapper = new UpdateWrapper<>();
            questionUpdateWrapper.eq("id", questionId);
            questionUpdateWrapper.setSql("acceptedNum = acceptedNum + 1");
            boolean updateQuestion = questionService.update(questionUpdateWrapper);
            if (!updateQuestion) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目通过数更新失败");
            }
            // 更新Redis排行榜ZSet分数
            Long userId = questionSubmit.getUserId();
            try {
                stringRedisTemplate.opsForZSet().incrementScore("ranking:total", String.valueOf(userId), 1);
                stringRedisTemplate.opsForZSet().incrementScore("ranking:weekly", String.valueOf(userId), 1);
            } catch (Exception e) {
                log.warn("[判题服务] 更新排行榜缓存失败", e);
            }
            // 清除用户统计缓存
            try {
                userService.clearUserStatsCache(userId);
            } catch (Exception e) {
                log.warn("[判题服务] 清除用户统计缓存失败", e);
            }
        }
        
        return questionSubmitService.getById(questionSubmitId);
    }
}
