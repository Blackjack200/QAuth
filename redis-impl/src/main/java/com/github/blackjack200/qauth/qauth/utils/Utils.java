package com.github.blackjack200.qauth.qauth.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
	public String notNullString(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}
}
