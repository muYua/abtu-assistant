package com.mupei.assistant.service;

import com.mupei.assistant.model.Role;
import com.mupei.assistant.model.RoleInfo;

import java.util.HashMap;

public interface RoleService {

	HashMap<String, Object> loginCheck(String roleNumber, String password, String flag, String currentTime, String ipAddr);

	Boolean reg(Role role, String currentTime, String ipAddr, String stuNumber);

	Boolean activateReg(String encryptedEmail, String encryptedVerifyCode, String currentTime, String ipAddr, String stuNumber);

	Boolean getResetPasswordVerifyCode(String email, String currentTime, String ipAddr);

	Boolean resetPassword(String email, String password, String currentTime, String ipAddr);

	Boolean checkResetPasswordVerifyCode(String email, String verifyCode);

    RoleInfo getRoleInfo(Long roleId);
}
