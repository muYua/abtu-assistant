package com.mupei.assistant.service;

import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

import com.mupei.assistant.model.Message;
import com.mupei.assistant.model.UploadFile;

public interface TeacherService {

	void sendMessage(Long courseId, Message message, String sort);

	void uploadFiles(MultipartFile[] files, Long courseId, String sort, String relativePath);

	ArrayList<UploadFile> getFiles(Long courseId, String createDate, String sort, Integer pageNo, Integer pageSize);

	Long getFilesCount(Long courseId, String createDate, String sort);

}
