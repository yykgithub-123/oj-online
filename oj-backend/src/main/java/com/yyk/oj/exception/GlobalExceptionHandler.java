package com.yyk.oj.exception;

import com.yyk.oj.common.BaseResponse;
import com.yyk.oj.common.ErrorCode;
import com.yyk.oj.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *  
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        // "未登录"已在 LogInterceptor 中以 DEBUG 处理，此处不再重复打印
        if (e.getCode() != 40100) {
            log.warn("[业务异常] 错误码={}, 原因={}", e.getCode(), e.getMessage());
        }
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("[系统异常] 未预期错误, 原因={}", e.getMessage(), e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
