package com.mupei.assistant.controller;

import com.mupei.assistant.model.Role;
import com.mupei.assistant.model.RoleInfo;
import com.mupei.assistant.model.Student;
import com.mupei.assistant.service.RoleService;
import com.mupei.assistant.utils.IpAddressUtil;
import com.mupei.assistant.utils.TimeUtil;
import com.mupei.assistant.vo.Json;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Objects;

@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private TimeUtil timeUtil;
    @Autowired
    private IpAddressUtil ipAddressUtil;

    /**
     * 登录
     *
     * @param roleNumber 用户账号，电子邮箱或手机号码
     * @param password   用户密码
     * @param flag       判别用户账号，电子邮箱或手机号码
     * @param request
     * @return map(roleId,roleSort)
     */
    @PostMapping("/login")
    public Json login(@RequestParam String roleNumber, @RequestParam String password, @RequestParam String flag,
                      HttpServletRequest request) {
        String currentTime = timeUtil.getCurrentTime();
        String ipAddr = ipAddressUtil.getIpAddr(request);
        HashMap<String, Object> map = roleService.loginCheck(roleNumber, password, flag, currentTime, ipAddr);
        if(map == null) {
            return new Json(false);
        }
        if(map.get("msg") != null) {
            return new Json(false, map.get("msg").toString());
        }
        return new Json(true, map);
    }

    /**
     * 注册，发送注册激活链接
     *
     * @param role    用户基础信息，password、nickname、email、sort('s','t','a')
     * @param request
     * @return
     */
    @PostMapping("/reg")
    public Json reg(Role role, @RequestParam(required = false) String stuNumber, HttpServletRequest request) {
        String currentTime = timeUtil.getCurrentTime();
        String ipAddr = ipAddressUtil.getIpAddr(request);
        Boolean reg;
        reg = roleService.reg(role, currentTime, ipAddr, stuNumber);
        return new Json(reg);
    }

    /**
     * 激活注册链接
     *
     * @param encryptedEmail      链接里已加密的电子邮箱
     * @param encryptedVerifyCode 链接里的已加密的验证码
     * @param request
     * @return
     */
    @RequestMapping("/activateRegVerifyCode")
    public Json activateVerifyCode(@RequestParam String encryptedEmail, @RequestParam String encryptedVerifyCode,
                                   HttpServletRequest request, @RequestParam(required = false) String stuNumber) {
        String currentTime = timeUtil.getCurrentTime();
        String ipAddr = ipAddressUtil.getIpAddr(request);
        Boolean activateReg = roleService.activateReg(encryptedEmail, encryptedVerifyCode, currentTime, ipAddr, stuNumber);
        return new Json(activateReg);
    }

    /**
     * 获取重置密码的验证码
     *
     * @param email   电子邮箱
     * @param request
     * @return
     */
    @GetMapping("/getResetPasswordVerifyCode")
    public Json getResetPasswordVerifyCode(@RequestParam String email, HttpServletRequest request) {
        String currentTime = timeUtil.getCurrentTime();
        String ipAddr = ipAddressUtil.getIpAddr(request);
        Boolean flag = roleService.getResetPasswordVerifyCode(email, currentTime, ipAddr);
        return new Json(flag);
    }

    /**
     * 校验重置验证码
     *
     * @param email      电子邮箱
     * @param verifyCode 验证码
     * @return
     */
    @PostMapping("/checkResetPasswordVerifyCode")
    public Json checkResetPasswordVerifyCode(@RequestParam String email, @RequestParam String verifyCode) {
        Boolean flag = roleService.checkResetPasswordVerifyCode(email, verifyCode);
        return new Json(flag);
    }

    /**
     * 重置密码
     *
     * @param email    电子邮箱
     * @param password 新密码
     * @param request
     * @return
     */
    @PutMapping("/resetPassword")
    public Json resetPassword(@RequestParam String email, @RequestParam String password, HttpServletRequest request) {
        String currentTime = timeUtil.getCurrentTime();
        String ipAddr = ipAddressUtil.getIpAddr(request);
        Boolean resetPassword = roleService.resetPassword(email, password, currentTime, ipAddr);
        return new Json(resetPassword);
    }

    @GetMapping("/getRoleInfo")
    public Json getStudentInfo(@RequestParam Long roleId) {
        RoleInfo role = roleService.getRoleInfo(roleId);
        return new Json(role != null, role);
    }
}
