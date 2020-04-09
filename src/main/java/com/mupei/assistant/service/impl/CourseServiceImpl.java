package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.*;
import com.mupei.assistant.model.*;
import com.mupei.assistant.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mupei.assistant.service.CourseService;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseDao courseDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private StuClassDao stuClassDao;
    @Autowired
    private Student_CourseDao student_courseDao;
    @Autowired
    private StuClass_StudentDao stuClass_studentDao;
    @Autowired
    private StuClass_MessageDao stuClass_messageDao;
    @Autowired
    private StuClass_UploadFileDao stuClass_uploadFileDao;
    @Autowired
    private JsonUtil jsonUtil;

    @Override
    public ArrayList<Course> getCourseList(Long teacherId) {
        ArrayList<Course> courses = courseDao.findByTeacherId(teacherId);
        log.debug("【course/getCourseList】获取课程信息：{}，教师ID：{}", courses, teacherId);
        return courses;
    }

    @Override
    @Transactional
    public HashMap<String, Object> insertCourse(Long teacherId, String courseName) {
        Course course = new Course(courseName, teacherId);
        Course save = courseDao.save(course);
        Long id = save.getId();
        HashMap<String, Object> map = new HashMap<>();
        map.put("courseId", id);
        map.put("courseName", courseName);
        log.debug("【course/insertCourse】插入课程信息，课程ID：{}，教师ID：{}，课程名称：{}", id, teacherId, courseName);
        return map;
    }

    @Override
    public ArrayList<Object> getCourseByPage(Long teacherId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.ASC, "id");//分页所需参数
        ArrayList<Course> courses = courseDao.findByTeacherId(teacherId, pageable);
        Optional<Role> optional = roleDao.findById(teacherId);
        String name;//教师姓名
        if(optional.isPresent()){
            name = optional.get().getName();
        } else {
            log.error("【course/getCourseByPage】教师信息不存在！");
            return null;
        }
        ArrayList<Object> list = null;
        try {
            //course => json => mapList
            @SuppressWarnings("unchecked")
            ArrayList<HashMap<String, Object>> mapList = jsonUtil.parse(jsonUtil.stringify(courses), ArrayList.class);
            //map.put
            mapList.forEach(map -> map.put("teacherName", name));
            //mapList => json => list
            //noinspection unchecked
            list = jsonUtil.parse(jsonUtil.stringify(mapList), ArrayList.class);
        } catch (IOException e) {
            log.debug("【course/getCourseByPage】数据类型转换出错！");
            e.printStackTrace();
        }
        log.debug("【course/getCourseByPage】分页获取课程信息：{}，教师ID：{}", courses, teacherId);
        return list;
    }

    @Override
    public Long getCourseCount(Long teacherId) {
        Sort sort = Sort.by(Sort.Order.asc("id"));//排序
        Long count = courseDao.countByTeacherId(teacherId, sort);
        log.debug("【course/getCourseCount】获取课程总数：{}，教师ID：{}", count, teacherId);
        return count;
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        //student_course <-> course
        ArrayList<Student_Course> student_course = student_courseDao.findByCourseId(courseId);
        if (student_course != null && student_course.size() != 0) {
            student_courseDao.deleteAll(student_course);
            log.debug("【course/deleteCourse】删除相关的学生与课程关系表记录。");
        }
        //class_student <-> stuClass
        //class_message <-> stuClass
        //class_uploadFile <-> stuClass
        ArrayList<StuClass> classList = stuClassDao.findByCourseId(courseId);
        if (classList != null && classList.size() != 0) {
            classList.forEach(stuClass -> {
                Long classId = stuClass.getId();
                if (stuClass_studentDao.existsByClassId(classId)){
                    stuClass_studentDao.deleteByClassId(classId);
                }
                if(stuClass_messageDao.existsByClassId(classId)){
                    stuClass_messageDao.deleteByClassId(classId);
                }
                if(stuClass_uploadFileDao.existsByClassId(classId)){
                    stuClass_uploadFileDao.deleteByClassId(classId);
                }
            });
            log.debug("【course/deleteCourse】删除关联的班级的关系表：class_student <-> stuClass，" +
                    "class_message <-> stuClass，class_uploadFile <-> stuClass，课程ID：{}", courseId);
        } else {
            log.error("【course/deleteCourse】该课程查询不到班级信息！课程ID：{}", courseId);
        }
        //stuClass <-> course
        stuClassDao.deleteByCourseId(courseId);
        log.debug("【course/deleteCourse】删除关联班级：stuClass <-> course，课程ID：{}", courseId);
        courseDao.deleteById(courseId);
        log.debug("【course/deleteCourse】删除课程，课程ID：{}", courseId);
    }

    @Override
    public ArrayList<Course> getCourseListByStuId(Long stuId) {
        ArrayList<Course> courses = new ArrayList<>();
        //student <-> course
        student_courseDao.findByStuId(stuId).forEach(student_course -> {
            Long courseId = student_course.getCourseId();
            Course course = courseDao.findById(courseId).orElse(null);
            courses.add(course);
        });
        log.debug("【course/getCourseListByStuId】获取课程信息：{}，学生ID：{}", courses, stuId);
        return courses;
    }

    @Override
    @Transactional
    public HashMap<String, Object> addCourseOfStudent(Long courseId, Long classId, Long stuId) {
        HashMap<String, Object> map = new HashMap<>();
        Optional<String> courseNameOptional = courseDao.findById(courseId).map(Course::getCourseName);
        if (courseNameOptional.isPresent()){
            map.put("courseName", courseNameOptional.get());
        } else {
            //课程不存在
            log.error("【stuClass/addStuClassOfStudent】课程不存在，课程ID：{}", courseId);
        }
        //course <-> student
        Student_Course student_course = new Student_Course(stuId, courseId);
        student_courseDao.save(student_course);
        // class <-> student
        StuClass_Student stuClass_student = new StuClass_Student(classId, stuId);
        stuClass_studentDao.save(stuClass_student);
        return map;
    }
}
