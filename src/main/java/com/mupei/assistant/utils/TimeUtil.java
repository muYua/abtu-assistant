package com.mupei.assistant.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TimeUtil {
	@Value("${timeUtil.dataFormat}")
	private String dataFormat;

	public String getCurrentTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.dataFormat);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return simpleDateFormat.format(new Date());
	}
}