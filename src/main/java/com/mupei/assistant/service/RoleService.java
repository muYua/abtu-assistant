package com.mupei.assistant.service;

import com.mupei.assistant.model.Role;

public interface RoleService {
	Boolean loginCheck(String paramString1, String paramString2, String paramString3, String paramString4,
			String paramString5);

	Boolean reg(Role paramRole, String paramString1, String paramString2);

	Boolean activateReg(String paramString1, String paramString2, String paramString3, String paramString4);

	Boolean getResetPasswordVerifyCode(String paramString1, String paramString2, String paramString3);

	Boolean resetPassword(String paramString1, String paramString2, String paramString3, String paramString4);

	Boolean checkResetPasswordVerifyCode(String email, String verifyCode);
}
