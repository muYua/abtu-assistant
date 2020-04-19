package com.mupei.assistant.service;

import com.mupei.assistant.model.Student;

import java.util.ArrayList;

public interface StudentService {
    ArrayList<Student> getStudentInfos(Long classId);
}
