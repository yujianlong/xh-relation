package com.xinhuanet.relationship.lang;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

public class DateUtils {
	private static final String ISSUE_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String BIRTHDAY_PATTERN = "yyyy-MM-dd";

	public static Date parseIssueDate(String issueDate) {
		if (StringUtils.isEmpty(issueDate)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(ISSUE_DATE_PATTERN);
		try {
			return sdf.parse(issueDate);
		} catch (Exception e) {
			return null;
		}
	}

	public static String formatIssueDate(Date issueDate) {
		if (null == issueDate) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(ISSUE_DATE_PATTERN);
		try {
			return sdf.format(issueDate);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date parseBirthday(String birthday) {
		if (StringUtils.isEmpty(birthday)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(BIRTHDAY_PATTERN);
		try {
			return sdf.parse(birthday);
		} catch (Exception e) {
			return null;
		}
	}

	public static String formatBirthday(Date birthday) {
		if (birthday == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(BIRTHDAY_PATTERN);
		try {
			return sdf.format(birthday);
		} catch (Exception e) {
			return null;
		}
	}
}
