package com.yyk.oj.aop;

import com.yyk.oj.exception.BusinessException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
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

    /** 接口中文名称映射（URL 关键词 → 中文说明） */
    private static final Map<String, String> URL_NAME_MAP = new LinkedHashMap<>();

    /** 高频轮询接口，只在 DEBUG 级别输出，避免刷屏 */
    private static final Set<String> QUIET_URLS = new HashSet<>(Arrays.asList(
            "/question_submit/list/page",
            "/question/question_submit/list/page"
    ));

    static {
        // 用户相关
        URL_NAME_MAP.put("/user/login", "用户登录");
        URL_NAME_MAP.put("/user/register", "用户注册");
        URL_NAME_MAP.put("/user/logout", "用户登出");
        URL_NAME_MAP.put("/user/get/login", "获取登录状态");
        URL_NAME_MAP.put("/user/list/page", "用户列表");
        URL_NAME_MAP.put("/user/ranking", "用户排行榜");
        URL_NAME_MAP.put("/user/stats", "用户统计");
        URL_NAME_MAP.put("/user/update", "更新用户");
        URL_NAME_MAP.put("/user/delete", "删除用户");
        // 题目相关
        URL_NAME_MAP.put("/question/add", "创建题目");
        URL_NAME_MAP.put("/question/delete", "删除题目");
        URL_NAME_MAP.put("/question/update", "更新题目");
        URL_NAME_MAP.put("/question/get/vo", "查看题目详情");
        URL_NAME_MAP.put("/question/list/page/vo", "题目列表(前台)");
        URL_NAME_MAP.put("/question/list/page", "题目列表(管理)");
        URL_NAME_MAP.put("/question/tags/popular", "热门标签");
        // 提交相关
        URL_NAME_MAP.put("/question_submit/do", "提交代码");
        URL_NAME_MAP.put("/question_submit/list/page", "提交记录(轮询)");
        URL_NAME_MAP.put("/question/question_submit/list/page", "提交记录(轮询)");
        URL_NAME_MAP.put("/question_submit/daily/activity", "每日活跃度");
        URL_NAME_MAP.put("/question_submit/stats", "提交统计");
        URL_NAME_MAP.put("/question_submit/delete", "删除提交");
        // 文件
        URL_NAME_MAP.put("/file/upload", "文件上传");
        // 系统
        URL_NAME_MAP.put("/system/status", "系统状态");
    }

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
        String url = httpServletRequest.getRequestURI();
        String ip = normalizeIp(httpServletRequest.getRemoteHost());
        String label = resolveLabel(url);
        boolean isQuiet = isQuietUrl(url);
        // 过滤参数：排除 HttpServletRequest/Response/MultipartFile，截断过长参数，脱敏密码
        String params = formatParams(point.getArgs());
        // 执行原方法
        Object result;
        try {
            result = point.proceed();
        } catch (Throwable e) {
            stopWatch.stop();
            long cost = stopWatch.getTotalTimeMillis();
            // "未登录"是正常流程（前端定期检查登录态），不刷屏
            if (e instanceof BusinessException && ((BusinessException) e).getCode() == 40100) {
                log.debug("◇ 未登录检查 | {} | 来源={}", label, ip);
            } else {
                log.warn("✘ 请求异常 | {} | {}ms | 来源={} | 参数={} | 异常={}",
                        label, cost, ip, params, e.getMessage());
            }
            throw e;
        }
        // 输出合并日志
        stopWatch.stop();
        long cost = stopWatch.getTotalTimeMillis();
        if (cost >= SLOW_REQUEST_THRESHOLD) {
            log.warn("▲ 慢请求 | {} | {}ms | 来源={} | 参数={}",
                    label, cost, ip, params);
        } else if (isQuiet) {
            // 高频轮询接口降为 DEBUG，不刷屏
            log.debug("  {} | {}ms", label, cost);
        } else {
            log.info("◆ {} | {}ms | 来源={}", label, cost, ip);
        }
        return result;
    }

    /**
     * 根据 URL 匹配中文说明
     */
    private String resolveLabel(String url) {
        for (Map.Entry<String, String> entry : URL_NAME_MAP.entrySet()) {
            if (url.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        // 没有匹配到的接口，直接显示路径
        return url;
    }

    /**
     * 判断是否为高频轮询接口
     */
    private boolean isQuietUrl(String url) {
        for (String quiet : QUIET_URLS) {
            if (url.contains(quiet)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 归一化 IP 地址：IPv6 本地回环 → localhost
     */
    private String normalizeIp(String ip) {
        if (ip == null) return "unknown";
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip) || "127.0.0.1".equals(ip)) {
            return "localhost";
        }
        return ip;
    }

    /**
     * 格式化请求参数，过滤掉 Servlet 对象，截断过长内容，脱敏密码
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
                    // 脱敏密码字段
                    str = str.replaceAll("((?i)password)=[^,)]*", "$1=***");
                    // 截断超长参数
                    return str.length() > 200 ? str.substring(0, 200) + "..." : str;
                })
                .collect(Collectors.joining(", "));
        return result.isEmpty() ? "-" : result;
    }
}

