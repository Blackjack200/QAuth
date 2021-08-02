package com.github.blackjack200;

import com.github.blackjack200.utils.Promise;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

@UtilityClass
public class RedisWrapper {
	public Promise<Jedis, Void> bindAccount(final String name, Long qq) {
		final Promise<Jedis, Void> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			if (bindAccountImpl(name, qq, jedis)) {
				promise.resolve(null);
			}
			promise.reject(null);
		});
		return promise;
	}

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

	public Promise<Jedis, Boolean> isAccountBound(final String name) {
		final Promise<Jedis, Boolean> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			promise.resolve(isAccountBoundImpl(name, jedis));
		});
		return promise;
	}

	public boolean isAccountBoundImpl(String name, Jedis jedis) {
		return getBindAccountImpl(name, jedis) == null;
	}

	public Promise<Jedis, String> getBindAccount(final String name) {
		final Promise<Jedis, String> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			promise.resolve(getBindAccountImpl(name, jedis));
		});
		return promise;
	}

	public static String getBindAccountImpl(String name, Jedis jedis) {
		return jedis.hget("account_bind", name);
	}

	public Promise<Jedis, Void> unbindAccount(final String name) {
		final Promise<Jedis, Void> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			unbindAccountImpl(name, jedis);
			promise.resolve(null);
		});
		return promise;
	}

	public void unbindAccountImpl(String name, Jedis jedis) {
		jedis.hdel("account_bind", name);
	}

	public Promise<Jedis, Void> setAuthCode(final String name, final String code) {
		final Promise<Jedis, Void> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			setAuthCodeImpl(name, code, jedis);
			promise.resolve(null);
		});
		return promise;
	}

	public void setAuthCodeImpl(String name, String code, Jedis jedis) {
		final String key = "account_code:" + name;
		jedis.set(key, code);
		jedis.expire(key, 10 * 60L);
	}

	public Promise<Jedis, String> getAuthCode(final String name) {
		final Promise<Jedis, String> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			promise.resolve(getAuthCodeImpl(name, jedis));
		});
		return promise;
	}

	public String getAuthCodeImpl(String name, Jedis jedis) {
		return jedis.get("account_code:" + name);
	}

	public Promise<Jedis, Void> removeAuthCode(final String name) {
		final Promise<Jedis, Void> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			removeAuthCodeImpl(name, jedis);
			promise.resolve(null);
		});
		return promise;
	}

	public void removeAuthCodeImpl(String name, Jedis jedis) {
		jedis.del("account_code:" + name);
	}
}
