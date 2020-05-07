package com.mupei.assistant.service;

import com.mupei.assistant.model.Student;
import com.mupei.assistant.model.StudentInfo;

import java.util.ArrayList;

public interface StudentService {
    ArrayList<Student> getStudentInfos(Long classId);

    StudentInfo getStudentInfo(Long stuId);
}
