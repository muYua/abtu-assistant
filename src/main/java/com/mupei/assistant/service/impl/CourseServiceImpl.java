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

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseDao courseDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private StuClassDao stuClassDao;
    @Autowired
    private Student_CourseDao student_courseDao;
    @Autowired
    private StuClass_StudentDao stuClass_studentDao;
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
        Course course = new Course(courseName, teacherDao.findById(teacherId).orElse(null));
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
            //noinspection unchecked
            ArrayList<HashMap<String, Object>> mapList = jsonUtil.parse(jsonUtil.stringify(courses), ArrayList.class);
            //map.put
            mapList.forEach(map -> map.put("teacherName", name));
            //mapList => json => list
            //noinspection unchecked
            list = jsonUtil.parse(jsonUtil.stringify(mapList), ArrayList.class);
        } catch (IOException e) {
            log.debug("【course/getCourseByPage】教师{}获取课程信息失败，数据类型转换出错！", teacherId);
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
        student_courseDao.deleteByCourseId(courseId);
        courseDao.deleteById(courseId);
        log.debug("【course/deleteCourse】删除课程，课程ID：{}", courseId);
    }

    @Override
    public ArrayList<Course> getCourseListByStuId(Long stuId) {
        ArrayList<Course> courses = new ArrayList<>();
        //student <-> course
        student_courseDao.findByStuId(stuId).forEach(student_course -> {
            Course course = student_course.getCourse();
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
            log.error("【course/addStuClassOfStudent】课程不存在，课程ID：{}", courseId);
        }
        //course <-> student
        if(student_courseDao.countByCourseIdAndStuId(courseId, stuId) > 0){
            map.put("msg", "已经选择了该课程。");
            return map;
        }
        Student_Course student_course = new Student_Course(studentDao.findById(stuId).orElse(null), courseDao.findById(courseId).orElse(null));
        student_courseDao.save(student_course);
        //class <-> student
        StuClass_Student stuClass_student = new StuClass_Student(stuClassDao.findById(classId).orElse(null), studentDao.findById(stuId).orElse(null));
        stuClass_studentDao.save(stuClass_student);
        return map;
    }

    //return course、teacherName、className
    @Override
    public ArrayList<Object> getCourseInfoOfStudent(Long stuId) {
        ArrayList<Object> courses = new ArrayList<>();
        //student <-> course
        student_courseDao.findByStuId(stuId).forEach(student_course -> {
            Course course = student_course.getCourse();
            String teacherName = Objects.requireNonNull(course).getTeacher().getName();
            String className = stuClassDao.findByCourseIdAndStuId(course.getId(), stuId).getClassName();
            try {
                //course => json => map
                //noinspection unchecked
                HashMap<String, Object> map = jsonUtil.parse(jsonUtil.stringify(course), HashMap.class);
                //map.put
                map.put("teacherName", teacherName);
                map.put("className", className);
                //map => json => object
                Object obj = jsonUtil.parse(jsonUtil.stringify(map), Object.class);
                courses.add(obj);
            } catch (IOException e) {
                log.error("【course/getCourseInfoOfStudent】获取学生{}课程信息失败，数据转换出错！", stuId);
                e.printStackTrace();
            }
        });
        return courses;
    }

    @Override
    @Transactional
    public void deleteCourseOfStudent(Long stuId, Long courseId) {
        stuClass_studentDao.deleteByStuId(stuId);
        student_courseDao.deleteByStuIdAndCourseId(stuId,courseId);
        log.debug("【course/deleteCourseOfStudent】删除选课，stuId:{}, 课程ID：{}", stuId, courseId);
    }
}
