package com.mupei.assistant.service.impl;

import com.mupei.assistant.dao.RoleDao;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.service.RoleService;
import com.mupei.assistant.utils.EmailUtil;
import com.mupei.assistant.utils.EncryptUtil;
import com.mupei.assistant.utils.RedisUtil;
import com.mupei.assistant.utils.SendLinkUtil;
import com.mupei.assistant.utils.VerifyCodeUtil;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private SendLinkUtil sendLinkUtil;
	@Autowired
	private EncryptUtil encryptUtil;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private VerifyCodeUtil verifyCodeUtil;

	@Override
	public Boolean loginCheck(String roleNumber, String password, String flag, String currentTime, String ipAddr) {
		Boolean isExist = false;
		if ("email".equals(flag)) {
			isExist = roleDao.existsByEmailAndPassword(roleNumber, password);
		} else if ("phone".equals(flag)) {
			isExist = roleDao.existsByPhoneAndPassword(roleNumber, password);
		} else {
			log.error("【loginCheck】校验异常！");
			return false;
		}
		if (!isExist) {
			return false;
		}
		Role role = new Role();
		//更新登录时间、登录IP
		role.setLoginTime(currentTime);
		role.setLoginIP(ipAddr);
		roleDao.save(role);
		log.debug("【loginCheck】{}登录时间：{}", roleNumber, currentTime);
		log.debug("【loginCheck】{}登录IP：{}", roleNumber, ipAddr);
		return true;
	}

	@Override
	public Boolean reg(Role role, String currentTime, String ip) {
		String email = role.getEmail();
		//判重
		Optional<Role> optional = roleDao.findByEmail(email);
		if (optional.isPresent()) {
			Role tempRole = optional.get();
			if (tempRole.getActivated()) {
				log.error("【reg】账号已存在。");
				return false;
			}
			role.setId(tempRole.getId());//将未激活的账号投入使用
		}
		//密码加密
		String password = role.getPassword();
		String sha256 = "";
		try {
			sha256 = encryptUtil.encryptWithSHA(password, "SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		//插入实体
		role.setPassword(sha256);
		role.setRegTime(currentTime);
		roleDao.save(role);
		log.debug("【reg】{}账号密码：{}", email, sha256);
		log.debug("【reg】{}账号注册时间：{}", email, currentTime);
		log.debug("【reg】{} IP：{}", email, ip);
		//发送激活链接
		try {
			sendLinkUtil.sendLink(role.getEmail());
			log.debug("【reg】{}激活链接已发送。", email);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Boolean activateReg(String encryptedEmail, String encryptedVerifyCode, String currentTime, String ipAddr) {
		//验证数据合法性
		if (encryptedEmail == null || "".equals(encryptedEmail))
			return false;
		if (encryptedVerifyCode == null || "".equals(encryptedVerifyCode))
			return false;
		//解密
		String KEY = encryptUtil.getKeyOfAES();
		String VI = encryptUtil.getViReg();
		String email = encryptUtil.decryptWithAES(encryptedEmail, KEY, VI);
		//从Redis获取验证码
		String verifyCode = redisUtil.get("verifyCode" + email);
		if (StringUtils.isEmpty(verifyCode)) {
			log.error("【activateReg】邮箱{}注册链接已过期！", email);
			roleDao.deleteRoleByEmail(email);
			return false;
		}
		//校验验证码
		String checkVerifyCode = null;
		try {
			checkVerifyCode = encryptUtil.encryptWithSHA(verifyCode, "SHA-256");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			log.error("【activateReg】{}Redis内部验证码加密出错！", email);
			return false;
		}
		if (encryptedVerifyCode.equals(checkVerifyCode)) {
			Optional<Role> findByEmail = roleDao.findByEmail(email);
			if (findByEmail.isPresent())
				((Role) findByEmail.get()).setActivated(true);
			log.debug("【activateReg】{}账号激活时间：{}", email, currentTime);
			log.debug("【activateReg】{} IP：{}", email, ipAddr);
			log.debug("【activateReg】邮箱{}注册验证码链接成功激活", email);
		}
		return true;
	}

	@Override
	public String getResetPasswordVerifyCode(String email, String currentTime, String ipAddr) {
		Optional<Role> optional = roleDao.findByEmail(email);
		if (optional.isPresent()) {
			Role tempRole = optional.get();
			if (!tempRole.getActivated())
				return null;
		} else {
			return null;
		}
		String verifyCode = verifyCodeUtil.getVerifyCode(6);
		//将验证码存入Redis
		redisUtil.set("verifyCode" + email, verifyCode, 10L, TimeUnit.MINUTES);
		String message = "【阿师课堂助手】您的验证码：" + verifyCode + "。" + "<p>注：该链接将在10分钟后失效。如果无法打开该链接，请复制到浏览器打开。</p>";
		//发送验证码
		emailUtil.sendHtmlMail(email, "重置账号密码", message);
		log.debug("【getResetPasswordVerifyCode】{}获取重置验证码时间：{}", email, currentTime);
		log.debug("【getResetPasswordVerifyCode】{} IP：{}", email, ipAddr);
		log.debug("【getResetPasswordVerifyCode】邮箱{}密码重置链接发送成功！", email);
		return verifyCode;
	}

	@Override
	public Boolean resetPassword(String email, String password, String currentTime, String ipAddr) {
		Optional<Role> optional = roleDao.findByEmail(email);
		if (optional.isPresent()) {
			Role tempRole = optional.get();
			if (!tempRole.getActivated())
				return false;
			//重置密码
			tempRole.setPassword(password);
			roleDao.save(tempRole);
			return true;
		}
		return false;
	}
}
