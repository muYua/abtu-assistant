package com.mupei.assistant.controller;

import com.mupei.assistant.model.UploadFile;
import com.mupei.assistant.service.UploadFileService;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RequestMapping("/uploadFile")
@RestController //@Controller + @ResponseBody
public class UploadFileController {
    @Autowired
    private UploadFileService uploadFileService;

    @PostMapping("/uploadFiles")
    public Json uploadFiles(@RequestParam("file") MultipartFile[] files, @RequestParam Long roleId,
                            @RequestParam Long courseId, @RequestParam Long classId, @RequestParam String sort) {
        uploadFileService.uploadFiles(files, roleId, courseId, classId, sort);
        return new Json(true);
    }

    @GetMapping("/getSignInFiles")
    public Json getSignInFiles(@RequestParam Long courseId, @RequestParam Long classId, @RequestParam String date,
                            @RequestParam String sort, @RequestParam Integer pageNo, @RequestParam Integer pageSize){
        ArrayList<Object> signInInfo = uploadFileService.getSignInInfoByDate(courseId, classId, date, sort, pageNo, pageSize);
        Long count = uploadFileService.getFilesCountByDate(courseId, classId, date, sort);
        return new Json(true, 0, signInInfo, count, "考勤记录");
    }

    @GetMapping("/getHomeworkFiles")
    public Json getFiles(@RequestParam Long courseId, @RequestParam Long classId, @RequestParam String date,
                               @RequestParam String sort, @RequestParam Integer pageNo, @RequestParam Integer pageSize){
        ArrayList<Object> files = uploadFileService.getHomeworkFilesByDate(courseId, classId, date, sort, pageNo, pageSize);
        Long count = uploadFileService.getFilesCountByDate(courseId, classId, date, sort);
        return new Json(true, 0, files, count, "文件");
    }

    /* 批阅作业文件 */
//	checkHomework

    /* 下载作业文件 */
//	downloadHomework

}