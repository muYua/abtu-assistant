package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.UsualPerformanceDao;
import com.mupei.assistant.model.UsualPerformance;
import com.mupei.assistant.model.UsualPerformanceInfo;
import com.mupei.assistant.service.UsualPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
public class UsualPerformanceServiceImpl implements UsualPerformanceService {
    @Autowired
    private UsualPerformanceDao usualPerformanceDao;

    @Override
    public ArrayList<UsualPerformance> getUsualPerformances(Long courseId, Long stuId) {
        return usualPerformanceDao.findByCourseIdAndStuId(courseId, stuId);
    }

    @Override
    public ArrayList<UsualPerformanceInfo> getUsualPerformanceInfo(Long courseId, Long classId) {
        return usualPerformanceDao.findByCourseIdAndClassId(courseId, classId);
    }

    @Override
    @Transactional
    public boolean insertUsualPerformance(Long courseId, Long stuId, String date, Integer score) {
        return usualPerformanceDao.insert(courseId, stuId, date, score) > 0;
    }

    @Override
    @Transactional
    public void deleteUsualPerformance(Long id) {
        usualPerformanceDao.deleteById(id);
    }

    @Override
    @Transactional
    public boolean updateUsualPerformance(Long id, Integer score) {
        return usualPerformanceDao.update(id, score) > 0;
    }

}
