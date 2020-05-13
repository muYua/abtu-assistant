package com.mupei.assistant.model;

public interface UsualPerformanceInfo {
    //根据数据库返回名称(别名)取值
    Long getStuId();
    String getStuNumber();
    String getName();
    Long getCourseId();
    Long getCount();
    Integer getAvg();
}
