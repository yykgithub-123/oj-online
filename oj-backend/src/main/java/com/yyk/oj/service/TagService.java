package com.yyk.oj.service;

import java.util.List;

/**
 * 标签服务
 */
public interface TagService {

    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<String> getAllTags();

    /**
     * 获取热门标签
     * @param limit 限制数量
     * @return 标签列表
     */
    List<String> getPopularTags(int limit);
}