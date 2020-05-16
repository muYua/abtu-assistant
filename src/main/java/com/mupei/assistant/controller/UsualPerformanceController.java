package com.mupei.assistant.controller;

import com.mupei.assistant.model.UsualPerformance;
import com.mupei.assistant.model.UsualPerformanceInfo;
import com.mupei.assistant.service.UsualPerformanceService;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin
@RequestMapping("/usualPerformance")
@RestController
public class UsualPerformanceController {
    @Autowired
    private UsualPerformanceService usualPerformanceService;

    @GetMapping("/getUsualPerformances")
    public Json getUsualPerformances(@RequestParam Long courseId, @RequestParam Long stuId) {
        ArrayList<UsualPerformance> list = usualPerformanceService.getUsualPerformances(courseId, stuId);
        boolean success = list != null;
        return new Json(success, success ? 0 : -1, list, 0L, success ? null : "学生模块获取平时成绩数据失败!");
    }

    @GetMapping("/getUsualPerformanceInfo")
    public Json getUsualPerformanceInfo(@RequestParam Long courseId, @RequestParam Long classId) {
        ArrayList<UsualPerformanceInfo> list = usualPerformanceService.getUsualPerformanceInfo(courseId, classId);
        boolean success = list != null;
        return new Json(success, success ? 0 : -1, list, 0L, success ? null : "教师模块获取平时成绩数据失败!");
    }

    @PostMapping("/insertUsualPerformance")
    public Json insertUsualPerformance(@RequestParam Long courseId, @RequestParam Long stuId, @RequestParam String date, @RequestParam Integer score) {
        return new Json(usualPerformanceService.insertUsualPerformance(courseId, stuId, date, score));
    }

    @DeleteMapping("/deleteUsualPerformance")
    public Json deleteUsualPerformance(@RequestParam Long id) {
        usualPerformanceService.deleteUsualPerformance(id);
        return new Json(true);
    }

    @PutMapping("/updateUsualPerformance")
    public Json updateUsualPerformance(@RequestParam Long id, @RequestParam("value") Integer score) {
        return new Json(usualPerformanceService.updateUsualPerformance(id, score));
    }
}
