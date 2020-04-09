package com.mupei.assistant.service;

import com.mupei.assistant.model.StuClass;

import java.util.ArrayList;
import java.util.HashMap;

public interface StuClassService {

    ArrayList<StuClass> getClassList(Long courseId);

    HashMap<String, Object> insertClass(String className, Long teacherId, Long courseId);
    
    ArrayList<Object> getClassByPage(Long courseId, Integer pageNo, Integer pageSize);

    Long getClassCount(Long courseId);

    void deleteClass(Long classId);
}
