package com.github.blackjack200.qauth.qauth;

import lombok.experimental.UtilityClass;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

@UtilityClass
public class RedisImpl {
	public boolean bindAccountImpl(String name, Long qq, Jedis jedis) {
		byte times = Byte.MAX_VALUE;
		while (times-- > 0) {
			jedis.watch("account_bind");
			Transaction transaction = jedis.multi();
			transaction.hset("account_bind", name, qq.toString());
			List<Object> result = transaction.exec();
			jedis.unwatch();
			if (!result.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public static String getBindAccountImpl(String name, Jedis jedis) {
		return jedis.hget("account_bind", name);
	}

	public static boolean hasBindAccountImpl(String name, Jedis jedis) {
		return getBindAccountImpl(name, jedis) != null;
	}

	public boolean isAccountBoundImpl(String name, Jedis jedis) {
		return getBindAccountImpl(name, jedis) == null;
	}

	public void unbindAccountImpl(String name, Jedis jedis) {
		jedis.hdel("account_bind", name);
	}

	public void setAuthCodeImpl(String name, String code, Jedis jedis, long timeout) {
		final String key = "account_captcha:" + name;
		jedis.set(key, code);
		jedis.expire(key, timeout);
	}

	public String getAuthCodeImpl(String name, Jedis jedis) {
		return jedis.get("account_captcha:" + name);
	}

	public boolean hasAuthCode(String name, Jedis jedis) {
		return getAuthCodeImpl(name, jedis) != null;
	}

	public void removeAuthCodeImpl(String name, Jedis jedis) {
		jedis.del("account_captcha:" + name);
	}
}
