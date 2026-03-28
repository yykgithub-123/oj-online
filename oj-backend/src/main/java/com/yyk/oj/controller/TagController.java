package com.yyk.oj.controller;

import com.yyk.oj.common.BaseResponse;
import com.yyk.oj.common.ResultUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签管理接口
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    /**
     * 获取所有标签
     */
    @GetMapping
    public BaseResponse<List<String>> getAllTags() {
        List<String> tags = Arrays.asList(
            "数组", "字符串", "链表", "树", "图", 
            "动态规划", "贪心", "回溯", "二分查找", "哈希表"
        );
        return ResultUtils.success(tags);
    }

    /**
     * 添加新标签
     */
    @PostMapping
    public BaseResponse<Map<String, Object>> addTag(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        
        // TODO: 实现标签添加逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("id", 1);
        result.put("name", name);
        result.put("description", description);
        
        return ResultUtils.success(result);
    }
} 