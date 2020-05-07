package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.*;
import com.mupei.assistant.model.*;
import com.mupei.assistant.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mupei.assistant.service.MessageService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private StuClassDao stuClassDao;
    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private StuClass_MessageDao stuClass_messageDao;
    @Autowired
    private TimeUtil timeUtil;

    @Override
    @Transactional
    public Boolean setMessage(String content, Long teacherId, Long classId, String sort) {
        String createTime = timeUtil.getTimeWithFormat("yyyy-MM-dd hh:mm:ss");
        Message message = new Message(content, sort, createTime, teacherDao.findById(teacherId).orElse(null));
        Message save = messageDao.save(message);
        log.debug("【message/setMessage】持久化消息内容{}，消息：{}，教师ID：{}，班级ID：：{}", sort, content, teacherId, classId);
        Long messageId = save.getId();
        //class <-> message
        StuClass_Message stuClass_message = new StuClass_Message(stuClassDao.findById(classId).orElse(null), messageDao.findById(messageId).orElse(null), createTime);
        stuClass_messageDao.save(stuClass_message);
        log.debug("【message/setMessage】维护关系：class <-> message，班级ID：{}， 消息ID：{}", classId, messageId);
        return true;
    }

    @Override
    public ArrayList<Message> getMessage(Long stuId, Long courseId, String date, String sort) {
        ArrayList<Message> messages = new ArrayList<>();
        StuClass stuClass = stuClassDao.findByCourseIdAndStuId(courseId, stuId);
        stuClass_messageDao.findByClassId(stuClass.getId()).forEach(stuClass_message -> {
            Message message = stuClass_message.getMessage();
            if (Objects.equals(message.getSort(), sort) && date.equals(message.getCreateTime().substring(0, 10))) {
                messages.add(message);
            }
        });
        log.debug("【message/getMessage】找到对应课程的消息{}，消息内容：{}，课程ID：{}，学生ID：{}", sort, messages, courseId, stuId);
        return  messages;
    }
}
