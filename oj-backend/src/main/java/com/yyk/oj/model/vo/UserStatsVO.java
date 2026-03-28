package com.yyk.oj.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户统计信息视图对象
 */
@Data
public class UserStatsVO implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 总完成题目数
     */
    private Integer totalSolved;

    /**
     * 简单题完成数
     */
    private Integer easySolved;

    /**
     * 中等题完成数
     */
    private Integer mediumSolved;

    /**
     * 困难题完成数
     */
    private Integer hardSolved;

    /**
     * 总提交次数
     */
    private Integer totalSubmissions;

    /**
     * 通过率
     */
    private Double acceptanceRate;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 周排名
     */
    private Integer weeklyRank;

    /**
     * 通过的提交次数
     */
    private Integer acceptedSubmissions;

    /**
     * 周完成题目数（用于周榜）
     */
    private Integer weeklySolved;

    /**
     * 最后提交时间
     */
    private Date lastSubmissionTime;

    private static final long serialVersionUID = 1L;
}