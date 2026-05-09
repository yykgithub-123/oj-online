package com.yyk.oj.judge.codesandbox;

import com.yyk.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yyk.oj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;


    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("[代码沙箱] 发送请求 >> 语言={}, 测试用例数={}", 
                executeCodeRequest.getLanguage(), 
                executeCodeRequest.getInputList() != null ? executeCodeRequest.getInputList().size() : 0);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("[代码沙箱] 收到响应 << 状态={}, 输出数={}, 信息={}", 
                executeCodeResponse.getStatus(),
                executeCodeResponse.getOutputList() != null ? executeCodeResponse.getOutputList().size() : 0,
                executeCodeResponse.getMessage());
        return executeCodeResponse;
    }
}
