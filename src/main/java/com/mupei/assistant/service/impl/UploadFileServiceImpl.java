package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.*;
import com.mupei.assistant.model.*;
import com.mupei.assistant.utils.EncryptUtil;
import com.mupei.assistant.utils.JsonUtil;
import com.mupei.assistant.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mupei.assistant.service.UploadFileService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class
UploadFileServiceImpl implements UploadFileService {
    @Autowired
    private UploadFileDao uploadFileDao;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private StuClassDao stuClassDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private StuClass_UploadFileDao stuClass_uploadFileDao;
    @Autowired
    private TimeUtil timeUtil;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private EncryptUtil encryptUtil;
    @Value("${file.uploadFolder}")
    private String uploadFolder;
    @Value("${domainName}")
    private String DomainName;

    @Override
    @Transactional
    public void uploadFiles(MultipartFile[] files, Long roleId, Long courseId, Long classId, String sort) {
        //处理文件
        for(MultipartFile file : files) {
            String fileName = file.getOriginalFilename();// 获取到上传文件的名字
//            String filePath = request.getSession().getServletContext().getRealPath("upload") + File.separator + "file";//存储在"项目名/upload/file"
            String sortName, filePath;
            switch (sort) {
                case "l":
                    sortName = "TeachingFiles";
                    break;
                case "ht":
                    sortName = "HomeworkFiles"+ File.separator + "teacher";
                    break;
                case "hs":
                    sortName = "HomeworkFiles"+ File.separator + "student";
                    break;
                case "i":
                    sortName = "ImageFiles";
                    break;
                case "s":
                    sortName = "SignInFiles";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + sort);
            }
            if("ImageFiles".equals(sortName)){
                filePath = uploadFolder + roleId + File.separator + sortName;
            } else {
                filePath = uploadFolder + roleId + File.separator + sortName + File.separator +
                        courseId + File.separator + classId; //存储上传文件的路径
            }
            String currentTime = timeUtil.getCurrentTime();
            /*根据路径，文件名判重*/
            String checkMd5;
            try {
                checkMd5 = encryptUtil.encryptWithSHA(filePath + File.separator + fileName, "MD5");
            } catch (NoSuchAlgorithmException e) {
                log.error("【uploadFile/uploadFiles】上传文件时，获取MD5序列失败！");
                e.printStackTrace();
                return;
            }
            if(uploadFileDao.existsByMd5(checkMd5)){
                /*文件名重复，改名*/
                int index = Objects.requireNonNull(fileName).lastIndexOf(".");
                if (index == -1) {
                    return;
                }
                //后缀名
                String suffix = fileName.substring(index + 1);
                try {
                    fileName = fileName.substring(0, index) +"("+ encryptUtil.getMd5Str(currentTime).substring(8, 24) +")." + suffix;
                } catch (NoSuchAlgorithmException e) {
                    log.error("【uploadFile/uploadFiles】上传文件时，获取创建时间的MD5序列失败！");
                    e.printStackTrace();
                    return;
                }
            }
            File dir = new File(filePath, Objects.requireNonNull(fileName));
            if (!dir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }
            //存储到本地
            String directory;
            try {
                file.transferTo(dir);
                directory = dir.getCanonicalPath();//获取文档路径（完全路径）
                log.debug("【uploadFile/uploadFiles】文件上传成功，路径directory：{}", directory);
            } catch (IllegalStateException | IOException e) {
                log.error("【uploadFile/uploadFiles】上传文件时，本地存储失败！");
                e.printStackTrace();
                return;
            }
            String md5;
            try {
                md5 = encryptUtil.encryptWithSHA(directory, "MD5");
            } catch (NoSuchAlgorithmException e) {
                log.error("【uploadFile/uploadFiles】上传文件时，获取MD5序列失败！");
                e.printStackTrace();
                return;
            }
            Long fileSize = file.getSize();//文件大小
            //记录文件(实例化uploadFile)
            UploadFile uploadFile = new UploadFile(fileName, fileSize, filePath, md5, sort, currentTime, roleDao.findById(roleId).orElse(null));
            UploadFile save = uploadFileDao.save(uploadFile);
            //stuClass <-> uploadFile
            StuClass_UploadFile stuClass_uploadFile = new StuClass_UploadFile(stuClassDao.findById(classId).orElse(null), save, currentTime);
            stuClass_uploadFileDao.save(stuClass_uploadFile);
        }

    }

    @Override
    public ArrayList<Object> getSignInInfoByDate(Long courseId, Long classId, String date, String sort, Integer pageNo,
                                                 Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.ASC, "id");//分页所需参数
        //course<->class => class<->file => files
        ArrayList<UploadFile> files = uploadFileDao.findUploadFiles(courseId, classId, date, sort, pageable);
        ArrayList<Object> list = new ArrayList<>();
        for(UploadFile file : files) {
            //file<->roleId<->student => stuNumber
            String stuNumber = studentDao.findById(file.getId()).map(Student::getStuNumber).orElse(null);
            String roleName = file.getRole().getName();
            //fileUrl
//            String fileUrl = DomainName + "/static/" + file.getRole().getId() + "/SignInFiles/" + courseId + "/" + classId + "/" +file.getFileName();
            try { //存入数据(通过数据类型转换完成)
                //file => json => map
                @SuppressWarnings("unchecked")
                HashMap<String, Object> map = jsonUtil.parse(jsonUtil.stringify(file), HashMap.class);
                //map.put
                map.put("stuNumber", stuNumber);
				map.put("roleName", roleName);
//				map.put("fileUrl", fileUrl);
                //map => json => object => list.add(object)
                list.add(jsonUtil.parse(jsonUtil.stringify(map), Object.class));
            } catch (IOException e) {
                e.printStackTrace();
                log.error("【uploadFile/getSignInInfoByDate】数据转换出错！");
            }
        }
        log.debug("【uploadFile/getSignInInfoByDate】获取签到文件信息，课程：{}，文件类型：{}，日期：{}, 信息：{}", courseId, sort, date, list);
        return list;
    }

    @Override
    public Long getFilesCountByDate(Long courseId, Long classId, String date, String sort) {
        return uploadFileDao.countUploadFiles(courseId, classId, date, sort);
    }

    @Override
    public ArrayList<UploadFile> getFilesByDate(Long courseId, Long classId, String date, String sort, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.ASC, "id");//分页所需参数
        //course<->class => class<->file => files
        return uploadFileDao.findUploadFiles(courseId, classId, date, sort, pageable);
    }

    @Override
    public ArrayList<Object> getHomeworkFilesByDate(Long courseId, Long classId, String date, String sort, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.ASC, "id");//分页所需参数
        ArrayList<UploadFile> files = uploadFileDao.findUploadFiles(courseId, classId, date, sort, pageable);
        ArrayList<Object> list = new ArrayList<>();
        files.forEach(file -> {
            String stuNumber;
            String roleName = file.getRole().getName();
            if ("hs".equals(sort)) {
//                sortName = "HomeworkFiles/student";
                //file<-> roleId -> student => stuNumber
                stuNumber = studentDao.findById(file.getRole().getId()).map(Student::getStuNumber).orElse(null);
                //fileUrl
//                String fileUrl = DomainName + "/static/" + file.getRole().getId() + "/" + sortName + "/" + courseId + "/" + classId + "/" +file.getFileName();
                try { //存入数据(通过数据类型转换完成)
                    //file => json => map
                    //noinspection unchecked
                    HashMap<String, Object> map = jsonUtil.parse(jsonUtil.stringify(file), HashMap.class);
                    //map.put
                    map.put("stuNumber", stuNumber);
                    map.put("roleName", roleName);
//                    map.put("fileUrl", fileUrl);
                    //map => json => object => list.add(object)
                    list.add(jsonUtil.parse(jsonUtil.stringify(map), Object.class));
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("【uploadFile/getHomeworkFilesByDate】数据转换出错！");
                }
            } else if ("ht".equals(sort)) {
//                sortName = "HomeworkFiles/teacher";
                //fileUrl
//                String fileUrl = DomainName + "/static/" + file.getRole().getId() + "/" + sortName + "/" + courseId + "/" + classId + "/" +file.getFileName();
                try { //存入数据(通过数据类型转换完成)
                    //file => json => map
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> map = jsonUtil.parse(jsonUtil.stringify(file), HashMap.class);
                    //map.put
                    map.put("roleName", roleName);
//                    map.put("fileUrl", fileUrl);
                    //map => json => object => list.add(object)
                    list.add(jsonUtil.parse(jsonUtil.stringify(map), Object.class));
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("【uploadFile/getHomeworkFilesByDate】数据转换出错！");
                }
            }
        });
        log.debug("【uploadFile/getHomeworkFilesByDate】获取作业文件，文件类型：{}，课程：{}，日期：{}, 信息：{}", sort, courseId, date, list);
        return list;
    }

    @Override
    public ArrayList<Object> getTeachingFilesByDate(Long courseId, Long classId, String date, String sort, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.ASC, "id");//分页所需参数
        //course<->class => class<->file => files
        ArrayList<UploadFile> files = uploadFileDao.findUploadFiles(courseId, classId, date, sort, pageable);
        ArrayList<Object> list = new ArrayList<>();
        for(UploadFile file : files) {
            //file<->roleId => roleName
            String roleName = file.getRole().getName();
            //fileUrl
//            String fileUrl = DomainName + "/static/" + file.getRole().getId() + "TeachingFiles/" + courseId + "/" + classId + "/" +file.getFileName();
            try { //存入数据(通过数据类型转换完成)
                //file => json => map
                //noinspection unchecked
                HashMap<String, Object> map = jsonUtil.parse(jsonUtil.stringify(file), HashMap.class);
                //map.put
                map.put("roleName", roleName);
//                map.put("fileUrl", fileUrl);
                //map => json => object => list.add(object)
                list.add(jsonUtil.parse(jsonUtil.stringify(map), Object.class));
            } catch (IOException e) {
                e.printStackTrace();
                log.error("【uploadFile/getTeachingFilesByDate】数据转换出错！");
            }
        }
        return list;
    }

    @Override
    public Boolean downloadFile(HttpServletResponse response, Long fileId) {
        Optional<UploadFile> fileOptional = uploadFileDao.findById(fileId);
        String filePath, fileName;
        if (fileOptional.isPresent()) {
            UploadFile uploadFile = fileOptional.get();
            fileName = uploadFile.getFileName();
            filePath = uploadFile.getFilePath() + File.separator + fileName; //文件绝对路径
            File file = new File(filePath);
            // 如果文件存在，则进行下载
            if (file.exists()) {
                // 配置文件下载
                response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                // 下载文件能正常显示中文
                try {
                    response.setHeader("Content-Disposition",
                            "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    log.error("【uploadFile/downloadFile】编码不支持，导致文件下载失败！，文件Id：{}", fileId);
                    return false;
                }
                // 实现文件下载
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                OutputStream os;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        os.flush();
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("【uploadFile/downloadFile】文件下载失败！，文件Id：{}", fileId);
                    return false;
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return true;
        } else {
            //已不存在该文件
            return false;
        }
    }

    @Override
    public UploadFile findById(Long fileId) {
        return uploadFileDao.findById(fileId).orElse(null);
    }

    @Override
    @Transactional
    public UploadFile save(UploadFile file) {
        return uploadFileDao.save(file);
    }

    @Override
    @Transactional
    public void delFile(Long fileId) {
        stuClass_uploadFileDao.deleteByFileId(fileId);
        uploadFileDao.deleteById(fileId);
        //删除物理文件
    }

    //获取头像
    @Override
    public String getImageFileUrl(Long roleId, Long fileId) {
        Optional<UploadFile> optional = uploadFileDao.findById(fileId);
        return optional.map(uploadFile -> DomainName + "/static/" + fileId + "ImageFiles/" + uploadFile.getFileName()).orElse(null);
    }
}
