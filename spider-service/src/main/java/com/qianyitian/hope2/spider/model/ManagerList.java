/**
 * Copyright 2021 jb51.net
 */
package com.qianyitian.hope2.spider.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Auto-generated: 2021-01-27 10:33:18
 *
 * @author jb51.net (i@jb51.net)
 * @website http://tools.jb51.net/code/json2javabean
 */
public class ManagerList {

    private String name;
    private String resume;
    @JSONField(name = "work_year")
    private String workYear;
    @JSONField(name = "achievement_list")
    private List<AchievementList> achievementList;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getResume() {
        return resume;
    }

    public void setWorkYear(String workYear) {
        this.workYear = workYear;
    }

    public String getWorkYear() {
        return workYear;
    }

    public void setAchievementList(List<AchievementList> achievementList) {
        this.achievementList = achievementList;
    }

    public List<AchievementList> getAchievementList() {
        return achievementList;
    }

}