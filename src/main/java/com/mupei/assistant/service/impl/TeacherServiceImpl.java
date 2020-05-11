package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.TeacherDao;
import com.mupei.assistant.model.Teacher;
import com.mupei.assistant.model.TeacherInfo;
import com.mupei.assistant.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherDao teacherDao;

    @Override
    public TeacherInfo getTeacherInfo(Long roleId) {
        return teacherDao.findTeacherInfoById(roleId);
    }

    @Override
    public boolean updateTeacherInfo(Teacher teacher) {
        return teacherDao.update(teacher) > 0;
    }
}
