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
import java.util.Optional;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private StuClassDao stuClassDao;
    @Autowired
    private StuClass_StudentDao stuClass_studentDao;
    @Autowired
    private StuClass_MessageDao stuClass_messageDao;
    @Autowired
    private TimeUtil timeUtil;

    @Override
    @Transactional
    public Boolean setMessage(String content, Long teacherId, Long classId, String sort) {
        String createTime = timeUtil.getTimeWithFormat("yyyy-MM-dd hh:mm:ss");
        Message message = new Message(content, sort, createTime, teacherId);
        Message save = messageDao.save(message);
        log.debug("【message/setMessage】持久化消息内容{}，消息：{}，教师ID：{}，班级ID：：{}", sort, content, teacherId, classId);
        Long messageId = save.getId();
        //class <-> message
        StuClass_Message stuClass_message = new StuClass_Message(classId, messageId, createTime);
        stuClass_messageDao.save(stuClass_message);
        log.debug("【message/setMessage】维护关系：class <-> message，班级ID：{}， 消息ID：{}", classId, messageId);
        return true;
    }

    @Override
    public ArrayList<Message> getMessage(Long stuId, Long courseId, String sort) {
        ArrayList<StuClass_Student> stuStuClass_students = stuClass_studentDao.findByStuId(stuId);
        if(stuStuClass_students != null && stuStuClass_students.size() != 0){
            //student <-> stuClass => stuClass
            for (StuClass_Student stuClass_student : stuStuClass_students) {
                Long classId = stuClass_student.getClassId();
                Optional<StuClass> stuClassOptional = stuClassDao.findById(classId);
                if (stuClassOptional.isPresent()) {
                    StuClass stuClass = stuClassOptional.get();
                    // stuClass => course
                    if (Objects.equals(stuClass.getCourseId(), courseId)) { //找到对应course的stuClass
                        //course => stuClass <-> message => message
                        ArrayList<Message> messages = new ArrayList<>();
                        stuClass_messageDao.findByClassId(classId).forEach(stuClass_message -> {
                            Long messageId = stuClass_message.getMessageId();
                            Message message = messageDao.findById(messageId).orElse(null);
                            if(Objects.equals(Objects.requireNonNull(message).getSort(), sort)) messages.add(message); //将消息分类（通知、作业）
                        });
                        log.debug("【message/getMessage】找到对应课程的消息{}，消息内容：{}，课程ID：{}，班级ID：{}", sort, messages, courseId, classId);
                        return  messages;
                    }
                } else {
                    log.error("【message/getMessage】班级不存在，班级ID：{}", classId);
                    return null;
                }
            }
        } else {
            log.error("【message/getMessage】class <-> student关系不存在，学生ID：{}", stuId);
            return null;
        }
        return null;
    }
}
