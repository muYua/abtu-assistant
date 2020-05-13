package com.mupei.assistant.service;

import com.mupei.assistant.model.UsualPerformance;
import com.mupei.assistant.model.UsualPerformanceInfo;

import java.util.ArrayList;

public interface UsualPerformanceService {
    ArrayList<UsualPerformance> getUsualPerformances(Long courseId, Long classId);

    ArrayList<UsualPerformanceInfo> getUsualPerformanceInfo(Long courseId, Long classId);

    boolean insertUsualPerformance(Long courseId, Long stuId, String date, Integer score);

    void deleteUsualPerformance(Long id);

    boolean updateUsualPerformance(Long id, Integer score);
}
