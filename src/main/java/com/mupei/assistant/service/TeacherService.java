package com.mupei.assistant.service;

import com.mupei.assistant.model.Teacher;
import com.mupei.assistant.model.TeacherInfo;

public interface TeacherService {

    TeacherInfo getTeacherInfo(Long roleId);

    boolean updateTeacherInfo(Teacher teacher);
}
