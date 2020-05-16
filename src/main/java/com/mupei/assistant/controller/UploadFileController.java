package com.mupei.assistant.controller;

import com.mupei.assistant.service.StuClassService;
import com.mupei.assistant.service.UploadFileService;
import com.mupei.assistant.vo.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@CrossOrigin
@RequestMapping("/uploadFile")
@RestController //@Controller + @ResponseBody
public class UploadFileController {
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private StuClassService stuClassService;

    @PostMapping("/uploadFilesByTeacher")
    public Json uploadFiles(@RequestParam("file") MultipartFile[] files, @RequestParam Long roleId,
                            @RequestParam Long courseId, @RequestParam Long classId, @RequestParam String sort) {
        uploadFileService.uploadFiles(files, roleId, courseId, classId, sort);
        return new Json(true);
    }

    @PostMapping("/uploadFilesByStudent")
    public Json uploadFilesByStudent(@RequestParam("file") MultipartFile[] files, @RequestParam Long roleId,
                                     @RequestParam Long courseId, @RequestParam String sort) {
        Long classId = stuClassService.getClassIdByCourseIdAndStuId(courseId, roleId);
        uploadFileService.uploadFiles(files, roleId, courseId, classId, sort);
        return new Json(true);
    }

    @GetMapping("/getSignInFiles")
    public Json getSignInFiles(@RequestParam Long courseId, @RequestParam Long classId, @RequestParam String date,
                               @RequestParam String sort, @RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        ArrayList<Object> signInInfo = uploadFileService.getSignInInfoByDate(courseId, classId, date, sort, pageNo, pageSize);
        Long count = uploadFileService.getFilesCountByDate(courseId, classId, date, sort);
        return new Json(true, 0, signInInfo, count, "考勤记录");
    }

    @GetMapping("/getHomeworkFiles")
    public Json getHomeworkFiles(@RequestParam Long courseId, @RequestParam Long classId, @RequestParam String date,
                                 @RequestParam String sort, @RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        ArrayList<Object> files = uploadFileService.getHomeworkFilesByDate(courseId, classId, date, sort, pageNo, pageSize);
        Long count = uploadFileService.getFilesCountByDate(courseId, classId, date, sort);
        return new Json(true, 0, files, count, "作业文件");
    }

    @GetMapping("/getHomeworkFilesByStudent")
    public Json getHomeworkFilesByStudent(@RequestParam Long courseId, @RequestParam Long stuId, @RequestParam String date,
                                          @RequestParam String sort, @RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        Long classId = stuClassService.getClassIdByCourseIdAndStuId(courseId, stuId);
        ArrayList<Object> files = uploadFileService.getHomeworkFilesByDate(courseId, classId, date, sort, pageNo, pageSize);
        Long count = uploadFileService.getFilesCountByDate(courseId, classId, date, sort);
        return new Json(true, 0, files, count, "作业文件");
    }

    @GetMapping("/getTeachingFiles")
    public Json getTeachingFiles(@RequestParam Long courseId, @RequestParam Long stuId, @RequestParam String date,
                                 @RequestParam String sort, @RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        Long classId = stuClassService.getClassIdByCourseIdAndStuId(courseId, stuId);
        ArrayList<Object> files = uploadFileService.getTeachingFilesByDate(courseId, classId, date, sort, pageNo, pageSize);
        Long count = uploadFileService.getFilesCountByDate(courseId, classId, date, sort);
        return new Json(true, 0, files, count, "课堂文件");
    }

    @RequestMapping(value = "/downloadFile/{fileId}")
    public void downloadFile(HttpServletResponse response, @PathVariable("fileId") Long fileId) {
        uploadFileService.downloadFile(response, fileId);
    }

    @DeleteMapping(value = "/delFile/{fileId}")
    public Json delFile(@PathVariable Long fileId) {
        uploadFileService.delFile(fileId);
        return new Json(true);
    }

    @GetMapping("getImageUrl/{roleId}")
    public Json getImageUrl(@PathVariable Long roleId){
        String url = uploadFileService.getImageFileUrl(roleId);
        return new Json(url!= null, (Object)url);
    }

    @PostMapping("/uploadImage/{roleId}")
    public Json uploadImage(@RequestParam("file") MultipartFile file, @PathVariable Long roleId){
        System.out.println("============"+roleId+"========");
        uploadFileService.uploadImageFile(file, roleId);
        return new Json(true);
    }
}