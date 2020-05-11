package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.StuClass_StudentDao;
import com.mupei.assistant.dao.StudentDao;
import com.mupei.assistant.model.Student;
import com.mupei.assistant.model.StudentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mupei.assistant.service.StudentService;

import java.util.ArrayList;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StuClass_StudentDao stuClass_studentDao;
    @Autowired
    private StudentDao studentDao;

    @Override
    public ArrayList<Student> getStudentInfos(Long classId) {
        ArrayList<Student> students = new ArrayList<>();
        stuClass_studentDao.findByClassId(classId).forEach(stuClass_student -> {
            students.add(stuClass_student.getStudent());
        });
        return students;
    }

    @Override
    public StudentInfo getStudentInfo(Long stuId) {
        return studentDao.findStudentInfoById(stuId);
    }

    @Override
    public boolean updateStudentInfo(Student student) {
        return studentDao.update(student) > 0;
    }
}
