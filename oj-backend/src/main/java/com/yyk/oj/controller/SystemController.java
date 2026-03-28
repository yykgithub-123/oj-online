package com.yyk.oj.controller;

import com.yyk.oj.common.BaseResponse;
import com.yyk.oj.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统监控接口
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    /**
     * 获取系统状态
     */
    @GetMapping("/status")
    public BaseResponse<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("serverStatus", "running");
        status.put("databaseStatus", "connected");
        status.put("judgeServiceStatus", "available");
        status.put("activeConnections", 25);
        status.put("memoryUsage", 65.2);
        status.put("cpuUsage", 23.1);
        return ResultUtils.success(status);
    }
} 