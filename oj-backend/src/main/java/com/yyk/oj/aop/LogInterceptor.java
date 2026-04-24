package com.yyk.oj.aop;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * 请求响应日志 AOP
 *  
 **/
@Aspect
@Component
@Slf4j
public class LogInterceptor {

    private static final long SLOW_REQUEST_THRESHOLD = 500L;

    /**
     * 执行拦截
     */
    @Around("execution(* com.yyk.oj.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取请求信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String method = httpServletRequest.getMethod();
        String url = httpServletRequest.getRequestURI();
        String ip = httpServletRequest.getRemoteHost();
        // 过滤参数：排除 HttpServletRequest/Response/MultipartFile，截断过长参数
        String params = formatParams(point.getArgs());
        // 执行原方法
        Object result;
        try {
            result = point.proceed();
        } catch (Throwable e) {
            stopWatch.stop();
            long cost = stopWatch.getTotalTimeMillis();
            log.warn("◆ {} {} | {} | {}ms | ip={} | params={} | error={}",
                    method, url, requestId, cost, ip, params, e.getMessage());
            throw e;
        }
        // 输出合并日志
        stopWatch.stop();
        long cost = stopWatch.getTotalTimeMillis();
        if (cost >= SLOW_REQUEST_THRESHOLD) {
            log.warn("◆ {} {} | {} | {}ms [慢] | ip={} | params={}",
                    method, url, requestId, cost, ip, params);
        } else {
            log.info("◆ {} {} | {} | {}ms | ip={}",
                    method, url, requestId, cost, ip);
        }
        return result;
    }

    /**
     * 格式化请求参数，过滤掉 Servlet 对象，截断过长内容
     */
    private String formatParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "-";
        }
        String result = Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .filter(arg -> !(arg instanceof HttpServletResponse))
                .filter(arg -> !(arg instanceof MultipartFile))
                .map(arg -> {
                    if (arg == null) return "null";
                    String str = arg.toString();
                    // 截断超长参数
                    return str.length() > 200 ? str.substring(0, 200) + "..." : str;
                })
                .collect(Collectors.joining(", "));
        return result.isEmpty() ? "-" : result;
    }
}

