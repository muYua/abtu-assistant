package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.*;
import com.mupei.assistant.model.Course;
import com.mupei.assistant.model.StuClass;
import com.mupei.assistant.model.StuClass_Message;
import com.mupei.assistant.model.StuClass_UploadFile;
import com.mupei.assistant.service.StuClassService;
import com.mupei.assistant.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
public class StuClassServiceImpl implements StuClassService {
    @Autowired
    private StuClassDao stuClassDao;
    @Autowired
    private StuClass_StudentDao stuClass_studentDao;
    @Autowired
    private StuClass_UploadFileDao stuClass_uploadFileDao;
    @Autowired
    private StuClass_MessageDao stuClass_messageDao;
    @Autowired
    private CourseDao courseDao;
    @Autowired
    private JsonUtil jsonUtil;

    public StuClassServiceImpl() {
    }

    @Override
    public ArrayList<StuClass> getClassList(Long courseId) {
        if (courseId == null) {
            log.error("【stuClass/getClassList】课程ID为NULL，课程ID：{}", courseId);
            return null;
        }
        ArrayList<StuClass> stuClassList = stuClassDao.findByCourseId(courseId);
        log.debug("【stuClass/getClassList】获取班级信息：{}，课程ID：{}", stuClassList, courseId);
        return stuClassList;
    }

    @Override
    @Transactional
    public HashMap<String, Object> insertClass(String className, Long courseId) {
        StuClass stuClass = new StuClass(className, courseDao.findById(courseId).orElse(null));
        StuClass save = stuClassDao.save(stuClass);
        Long id = save.getId();
        HashMap<String, Object> map = new HashMap<>();
        map.put("classId", id);
        map.put("className", className);
        log.debug("【stuClass/insertClass】插入班级信息,班级ID：{}，班级名称：{}，课程ID：{}", id, className, courseId);
        return map;
    }

    @Override
    public ArrayList<Object> getClassByPage(Long courseId, Integer pageNo, Integer pageSize) {
        if (courseId == null) {
            log.error("【stuClass/getClassByPage】课程ID为NULL，课程ID：{}", courseId);
            return null;
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.ASC, "id");//分页所需参数
        ArrayList<StuClass> stuClasses = stuClassDao.findByCourseId(courseId, pageable);
        Optional<Course> optional = courseDao.findById(courseId);
        String courseName;//课程名称
        if(optional.isPresent()){
            courseName = optional.get().getCourseName();
        } else {
            log.error("【stuClass/getClassByPage】课程信息不存在！");
            return null;
        }
        ArrayList<Object> list = null;
        try {
            //stuClasses => json => mapList
            @SuppressWarnings("unchecked")
            ArrayList<HashMap<String, Object>> mapList = jsonUtil.parse(jsonUtil.stringify(stuClasses), ArrayList.class);
            //map.put
            mapList.forEach(map -> map.put("courseName", courseName));
            //mapList => json => list
            //noinspection unchecked
            list = jsonUtil.parse(jsonUtil.stringify(mapList), ArrayList.class);
        } catch (IOException e) {
            log.debug("【stuClass/getClassByPage】数据类型转换出错！");
            e.printStackTrace();
        }
        log.debug("【stuClass/getClassByPage】分页获取班级信息：{}，课程ID：{}", stuClasses, courseId);
        return list;
    }

    @Override
    public Long getClassCount(Long courseId) {
        Sort sort = Sort.by(Sort.Order.asc("id"));//排序
        Long count = stuClassDao.countByCourseId(courseId, sort);
        log.debug("【stuClass/getClassCount】获取班级总数：{}，课程ID：{}", count, courseId);
        return count;
    }

    @Override
    @Transactional
    public void deleteClass(Long classId) {
        stuClass_messageDao.deleteByClassId(classId);
        stuClass_studentDao.deleteByClassId(classId);
        stuClass_uploadFileDao.deleteByClassId(classId);
        stuClassDao.deleteById(classId);
        log.debug("【stuClass/deleteClass】删除班级，班级ID：{}", classId);
    }

    @Override
    public Long getClassIdByCourseIdAndStuId(Long courseId, Long stuId) {
        return stuClassDao.findByCourseIdAndStuId(courseId, stuId).getId();
    }

}
