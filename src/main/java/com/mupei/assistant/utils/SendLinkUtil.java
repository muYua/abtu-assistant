package com.mupei.assistant.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendLinkUtil {
	@Autowired
	private VerifyCodeUtil verifyCodeUtil;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private EncryptUtil encryptUtil;
	@Autowired
	private EmailUtil emailUtil;
	@Value("${sendLinkUtil.website}")
	private String website;//主机网址

	/* 替换URL中的特殊符号 */
	private String replaceUrl(String url) {
		String s1 = url.replaceAll("\\+", "%2B");
		String s2 = s1.replaceAll("/", "%2F");
		return s2.replaceAll("=", "%3D");
	}

	/* 生成注册激活链接 */
	private String generateLink(String email, String verifyCode, String algorithm)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String KEY = encryptUtil.getKeyOfAES();
		String VI = encryptUtil.getViReg();
		String encryptedEmail = encryptUtil.encryptWithAES(email, KEY, VI);
		String encryptedVerifyCode = encryptUtil.encryptWithSHA(verifyCode, algorithm);
		String url = website + "/assistant/activateRegVerifyCode?" + "&email=" + replaceUrl(encryptedEmail)
				+ "&verifyCode=" + replaceUrl(encryptedVerifyCode);
		String message = "【阿师课堂助手】请点击该链接来进行账号激活：<a href='" + url + "' target='_blank'>" + url + "</a>"
				+ "<p>注：该链接将在10分钟后失效。如果无法打开该链接，请复制到浏览器打开。</p>";
		return message;
	}

	/* 发送注册激活链接 */
	public void sendLink(String email) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String verifyCode = verifyCodeUtil.getVerifyCode(6);
		redisUtil.set("verifyCode" + email, verifyCode, Long.valueOf(10L), TimeUnit.MINUTES);
		emailUtil.sendHtmlMail(email, "激活邮箱账号", generateLink(email, verifyCode, "SHA-256"));
	}
}
