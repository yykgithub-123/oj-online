package com.yyk.oj.judge.codesandbox.impl;

import com.yyk.oj.judge.codesandbox.CodeSandbox;
import com.yyk.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yyk.oj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
@Slf4j
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("[代码沙箱] 调用第三方沙箱");
        return null;
    }
}
