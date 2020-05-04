package com.mupei.assistant.controller;

import com.mupei.assistant.model.UploadFile;
import com.mupei.assistant.service.UploadFileService;
import com.zhuozhengsoft.pageoffice.FileSaver;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Map;

@RequestMapping("/po")
@RestController
public class PageOfficeController {
    @Value("${pageoffice.posyspath}")
    private String poSysPath;
    @Autowired
    private UploadFileService uploadFileService;

    /**
     * PageOffice授权
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        com.zhuozhengsoft.pageoffice.poserver.Server poserver = new com.zhuozhengsoft.pageoffice.poserver.Server();
        File dir = new File(poSysPath);
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        //设置PageOffice注册成功后,license.lic文件存放的目录
        poserver.setSysPath(poSysPath);
        ServletRegistrationBean srb = new ServletRegistrationBean(poserver);
        srb.addUrlMappings("/poserver.zz");
        srb.addUrlMappings("/posetup.exe");
        srb.addUrlMappings("/pageoffice.js");
        srb.addUrlMappings("/jquery.min.js");
        srb.addUrlMappings("/sealsetup.exe");
        srb.addUrlMappings("/pobstyle.css");
        return srb;
    }

    @GetMapping("/openWord/{fileId}")
    public ModelAndView openWord(HttpServletRequest request, Map<String, Object> map, @PathVariable Long fileId) {
        PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
        poCtrl.setServerPage("/poserver.zz");//设置授权程序
        poCtrl.setTitlebar(false); //隐藏标题栏
//        poCtrl.setOfficeToolbars(false);//隐藏office工具栏
        poCtrl.addCustomToolButton("手写批注", "StartHandDraw", 3);
//        poCtrl.addCustomToolButton("设置线宽", "SetPenWidth", 5);
//        poCtrl.addCustomToolButton("设置颜色", "SetPenColor", 5);
//        poCtrl.addCustomToolButton("设置笔型", "SetPenType", 5);
//        poCtrl.addCustomToolButton("设置缩放", "SetPenZoom", 5);
        poCtrl.addCustomToolButton("保存", "Save", 1); //添加自定义按钮
        poCtrl.addCustomToolButton("打印", "PrintFile", 6);
        poCtrl.addCustomToolButton("全屏/还原", "IsFullScreen", 4);
        poCtrl.addCustomToolButton("关闭", "CloseFile", 21);
        poCtrl.setSaveFilePage("/po/save/" + fileId);//设置保存的action
        UploadFile file = uploadFileService.findById(fileId);
        String filePath = file.getFilePath() + File.separator + file.getFileName();
        System.out.println(filePath+"-----------------------");
        poCtrl.webOpen(filePath, OpenModeType.docAdmin, "张三");
        map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        return new ModelAndView("word");
    }

    @RequestMapping("/save/{fileId}")
    public void saveFile(HttpServletRequest request, HttpServletResponse response, @PathVariable Long fileId) {
        UploadFile file = uploadFileService.findById(fileId);
        String filePath = file.getFilePath() + File.separator + file.getFileName();
        FileSaver fs = new FileSaver(request, response);
//        fs.saveToFile("D:\\ABTU-assistant\\" + fs.getFileName());
        fs.saveToFile(filePath);
        if (!file.getIsUpdate()) file.setIsUpdate(true);
        file.setUpdateCount(file.getUpdateCount()+1);
        uploadFileService.save(file);
        fs.close();
    }
}
