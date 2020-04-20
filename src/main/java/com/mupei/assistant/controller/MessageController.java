package com.mupei.assistant.controller;

import com.mupei.assistant.model.Message;
import com.mupei.assistant.service.MessageService;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping("/message")
@RestController //@Controller + @ResponseBody
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/sendMessage")
    public Json sendMessage(@RequestParam String content, @RequestParam Long teacherId, @RequestParam Long classId, @RequestParam String sort) {
        Boolean isTrue = messageService.setMessage(content, teacherId, classId, sort);
        Json json = new Json(isTrue);
        if(!isTrue) json.setMsg("没有学生添加该课程。");
        return json;
    }

    @GetMapping("/getMessage")
    public Json getMessage(@RequestParam Long stuId, @RequestParam Long courseId, @RequestParam String date, @RequestParam String sort){
        ArrayList<Message> messages = messageService.getMessage(stuId, courseId, date, sort);
        if(messages == null)
            return new Json(false);
        return new Json(true, messages);
    }
}
