package com.mupei.assistant.controller;

import com.mupei.assistant.model.Role;
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

@RestController
public class RoleController<V> {
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
	 * @return
	 */
	@PostMapping({ "/login" })
	public Json login(@RequestParam String roleNumber, @RequestParam String password, @RequestParam String flag,
			HttpServletRequest request) {
		String currentTime = timeUtil.getCurrentTime();
		String ipAddr = ipAddressUtil.getIpAddr(request);
		Boolean loginCheck = roleService.loginCheck(roleNumber, password, flag, currentTime, ipAddr);
		Json json = new Json();
		json.setSuccess(loginCheck);
		return json;
	}

	/**
	 * 注册，发送注册激活链接
	 * 
	 * @param role    用户基础信息，password、nickname、email、sort('s','t','a')
	 * @param request
	 * @return
	 */
	@PostMapping({ "/reg" })
	public Json reg(Role role, HttpServletRequest request) {
		String currentTime = timeUtil.getCurrentTime();
		String ipAddr = ipAddressUtil.getIpAddr(request);
		Boolean reg = roleService.reg(role, currentTime, ipAddr);
		Json json = new Json();
		json.setSuccess(reg);
		return json;
	}

	/**
	 * 激活注册链接
	 * 
	 * @param encryptedEmail      链接里已加密的电子邮箱
	 * @param encryptedVerifyCode 链接里的已加密的验证码
	 * @param request
	 * @return
	 */
	@RequestMapping({ "/activateRegVerifyCode" })
	public Json activateVerifyCode(@RequestParam String encryptedEmail, @RequestParam String encryptedVerifyCode,
			HttpServletRequest request) {
		String currentTime = timeUtil.getCurrentTime();
		String ipAddr = ipAddressUtil.getIpAddr(request);
		Boolean activateReg = roleService.activateReg(encryptedEmail, encryptedVerifyCode, currentTime, ipAddr);
		Json json = new Json();
		json.setSuccess(activateReg);
		return json;
	}

	/**
	 * 获取重置密码的验证码
	 * 
	 * @param email   电子邮箱
	 * @param request
	 * @return
	 */
	@GetMapping({ "/getResetPasswordVerifyCode" })
	public Json getResetPasswordVerifyCode(@RequestParam String email, HttpServletRequest request) {
		String currentTime = timeUtil.getCurrentTime();
		String ipAddr = ipAddressUtil.getIpAddr(request);
		Boolean flag = roleService.getResetPasswordVerifyCode(email, currentTime, ipAddr);
		Json json = new Json();
		json.setSuccess(flag);
		return json;
	}

	/**
	 * 校验重置验证码
	 * 
	 * @param email      电子邮箱
	 * @param verifyCode 验证码
	 * @return
	 */
	@PostMapping({ "/checkResetPasswordVerifyCode" })
	public Json checkResetPasswordVerifyCode(@RequestParam String email, @RequestParam String verifyCode) {
		Boolean flag = roleService.checkResetPasswordVerifyCode(email, verifyCode);
		Json json = new Json();
		json.setSuccess(flag);
		return json;
	}
	
	/**
	 * 重置密码
	 * 
	 * @param email    电子邮箱
	 * @param password 新密码
	 * @param request
	 * @return
	 */
	@PutMapping({ "/resetPassword" })
	public Json resetPassword(@RequestParam String email, @RequestParam String password, HttpServletRequest request) {
		String currentTime = timeUtil.getCurrentTime();
		String ipAddr = ipAddressUtil.getIpAddr(request);
		Boolean resetPassword = roleService.resetPassword(email, password, currentTime, ipAddr);
		Json json = new Json();
		json.setSuccess(resetPassword);
		return json;
	}
}
