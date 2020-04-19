package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.UsualPerformanceDao;
import com.mupei.assistant.model.UsualPerformance;
import com.mupei.assistant.service.UsualPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UsualPerformanceServiceImpl implements UsualPerformanceService {
    @Autowired
    private UsualPerformanceDao usualPerformanceDao;

    @Override
    public ArrayList<UsualPerformance> getUsualPerformances(Long courseId, Long stuId) {
        return usualPerformanceDao.findByCourseIdAndStuId(courseId, stuId);
    }
}
