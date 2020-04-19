package com.mupei.assistant.controller;

import com.mupei.assistant.model.UsualPerformance;
import com.mupei.assistant.service.UsualPerformanceService;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RequestMapping("/usualPerformance")
@RestController
public class UsualPerformanceController {
    @Autowired
    private UsualPerformanceService usualPerformanceService;

    @GetMapping("/getUsualPerformances")
    public Json getUsualPerformances(@RequestParam Long courseId, @RequestParam Long stuId) {
        ArrayList<UsualPerformance> list = usualPerformanceService.getUsualPerformances(courseId, stuId);
        boolean success = true;
        int code = 0;
        String msg = "平时成绩";
        if (list == null) {
            success = false;
            code = -1;
            msg = "获取平时成绩数据失败！";
        }
        return new Json(success, code, list, 0L, msg);
    }
}
