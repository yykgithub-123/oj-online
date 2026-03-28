package com.yyk.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yyk.oj.model.entity.Question;
import com.yyk.oj.service.QuestionService;
import com.yyk.oj.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签服务实现
 */
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private QuestionService questionService;

    private final Gson gson = new Gson();

    @Override
    public List<String> getAllTags() {
        // 查询所有题目的标签
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("tags");
        queryWrapper.eq("isDelete", 0);
        queryWrapper.isNotNull("tags");
        queryWrapper.ne("tags", "");
        
        List<Question> questions = questionService.list(queryWrapper);
        
        Set<String> allTags = new HashSet<>();
        Type listType = new TypeToken<List<String>>(){}.getType();
        
        for (Question question : questions) {
            String tagsJson = question.getTags();
            if (StringUtils.isNotBlank(tagsJson)) {
                try {
                    List<String> tags = gson.fromJson(tagsJson, listType);
                    if (tags != null) {
                        allTags.addAll(tags);
                    }
                } catch (Exception e) {
                    // 如果JSON解析失败，尝试按逗号分割
                    String[] tagArray = tagsJson.split(",");
                    for (String tag : tagArray) {
                        if (StringUtils.isNotBlank(tag.trim())) {
                            allTags.add(tag.trim());
                        }
                    }
                }
            }
        }
        
        return new ArrayList<>(allTags);
    }

    @Override
    public List<String> getPopularTags(int limit) {
        // 查询所有题目的标签
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("tags");
        queryWrapper.eq("isDelete", 0);
        queryWrapper.isNotNull("tags");
        queryWrapper.ne("tags", "");
        
        List<Question> questions = questionService.list(queryWrapper);
        
        Map<String, Integer> tagCountMap = new HashMap<>();
        Type listType = new TypeToken<List<String>>(){}.getType();
        
        for (Question question : questions) {
            String tagsJson = question.getTags();
            if (StringUtils.isNotBlank(tagsJson)) {
                try {
                    List<String> tags = gson.fromJson(tagsJson, listType);
                    if (tags != null) {
                        for (String tag : tags) {
                            tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0) + 1);
                        }
                    }
                } catch (Exception e) {
                    // 如果JSON解析失败，尝试按逗号分割
                    String[] tagArray = tagsJson.split(",");
                    for (String tag : tagArray) {
                        if (StringUtils.isNotBlank(tag.trim())) {
                            String cleanTag = tag.trim();
                            tagCountMap.put(cleanTag, tagCountMap.getOrDefault(cleanTag, 0) + 1);
                        }
                    }
                }
            }
        }
        
        // 按使用频率排序并限制数量
        return tagCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}