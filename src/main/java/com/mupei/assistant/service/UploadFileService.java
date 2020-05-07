package com.mupei.assistant.service;

import com.mupei.assistant.model.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public interface UploadFileService {

    void uploadFiles(MultipartFile[] files, Long roleId, Long courseId, Long classId, String sort);

    ArrayList<Object> getSignInInfoByDate(Long courseId, Long classId, String date, String sort, Integer pageNo, Integer pageSize);

    Long getFilesCountByDate(Long courseId, Long classId, String date, String sort);

    ArrayList<UploadFile> getFilesByDate(Long courseId, Long classId, String date, String sort, Integer pageNo, Integer pageSize);

    ArrayList<Object> getHomeworkFilesByDate(Long courseId, Long classId, String date, String sort, Integer pageNo, Integer pageSize);

    ArrayList<Object> getTeachingFilesByDate(Long courseId, Long classId, String date, String sort, Integer pageNo, Integer pageSize);

    Boolean downloadFile(HttpServletResponse response, Long fileId);

    UploadFile findById(Long fileId);

    UploadFile save(UploadFile file);

    void delFile(Long fileId);
}
