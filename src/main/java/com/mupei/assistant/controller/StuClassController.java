package com.mupei.assistant.controller;

import com.mupei.assistant.model.StuClass;
import com.mupei.assistant.service.StuClassService;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@RequestMapping("/stuClass")
@RestController //@Controller + @ResponseBody
public class StuClassController {
    @Autowired
    private StuClassService stuClassService;

    @GetMapping("/getClassList")
    public Json getClassList(@RequestParam Long courseId) {
        ArrayList<StuClass> clazz = stuClassService.getClassList(courseId);
        return new Json(true, clazz);
    }

    @GetMapping("/getClassByPage")
    public Json getClassByPage(@RequestParam Long courseId, @RequestParam Integer pageNo,
                                @RequestParam Integer pageSize) {
        ArrayList<Object> stuClass = stuClassService.getClassByPage(courseId, pageNo, pageSize);
        Long count = stuClassService.getClassCount(courseId);
        boolean success = true;
        int code = 0;
        String msg = "";
        if(stuClass == null){
            success =false;
            code = -1;
            msg = "获取班级数据失败！";
        }
        return new Json(success, code, stuClass, count, msg);
    }
    
    //map(classId、className)
    @PostMapping("/insertClass")
    public Json insertClass(@RequestParam String className, @RequestParam Long teacherId, @RequestParam Long courseId) {
        HashMap<String, Object> map = stuClassService.insertClass(className, teacherId, courseId);
        return new Json(true, map);
    }

    @DeleteMapping("/deleteClass")
    public Json deleteClass(@RequestParam Long classId){
        stuClassService.deleteClass(classId);
        return new Json(true);
    }

}
