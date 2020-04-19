package com.mupei.assistant.service;

import com.mupei.assistant.model.UsualPerformance;

import java.util.ArrayList;

public interface UsualPerformanceService {
    ArrayList<UsualPerformance> getUsualPerformances(Long courseId, Long classId);
}
