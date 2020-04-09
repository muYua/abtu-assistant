package com.mupei.assistant.service;

import com.mupei.assistant.model.Message;

import java.util.ArrayList;

public interface MessageService {

    Boolean setMessage(String content, Long teacherId, Long classId, String sort);

    ArrayList<Message> getMessage(Long stuId, Long courseId, String sort);
}
