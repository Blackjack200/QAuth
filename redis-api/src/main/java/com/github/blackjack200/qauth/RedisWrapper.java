package com.github.blackjack200.qauth;

import com.github.blackjack200.qauth.qauth.RedisImpl;
import com.github.blackjack200.qauth.utils.Promise;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.Jedis;

@UtilityClass
public class RedisWrapper {
	public Promise<Jedis, Void> bindAccount(final String name, Long qq) {
		final Promise<Jedis, Void> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			if (RedisImpl.bindAccountImpl(name, qq, jedis)) {
				promise.resolve(null);
			}
			promise.reject(null);
		});
		return promise;
	}
	public Promise<Jedis, Boolean> isAccountBound(final String name) {
		final Promise<Jedis, Boolean> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			promise.resolve(RedisImpl.isAccountBoundImpl(name, jedis));
		});
		return promise;
	}

	public Promise<Jedis, String> getBindAccount(final String name) {
		final Promise<Jedis, String> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			promise.resolve(RedisImpl.getBindAccountImpl(name, jedis));
		});
		return promise;
	}

	public Promise<Jedis, Boolean> hasBindAccount(final String name) {
		final Promise<Jedis, Boolean> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			promise.resolve(RedisImpl.hasBindAccountImpl(name, jedis));
		});
		return promise;
	}

	public Promise<Jedis, Void> unbindAccount(final String name) {
		final Promise<Jedis, Void> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			RedisImpl.unbindAccountImpl(name, jedis);
			promise.resolve(null);
		});
		return promise;
	}

	public Promise<Jedis, Void> setAuthCode(final String name, final String code, final long timeout) {
		final Promise<Jedis, Void> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			RedisImpl.setAuthCodeImpl(name, code, jedis, timeout);
			promise.resolve(null);
		});
		return promise;
	}

	public Promise<Jedis, String> getAuthCode(final String name) {
		final Promise<Jedis, String> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			promise.resolve(RedisImpl.getAuthCodeImpl(name, jedis));
		});
		return promise;
	}

	public Promise<Jedis, Boolean> hasAuthCode(final String name) {
		final Promise<Jedis, Boolean> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			promise.resolve(RedisImpl.hasAuthCode(name, jedis));
		});
		return promise;
	}

	public Promise<Jedis, Void> removeAuthCode(final String name) {
		final Promise<Jedis, Void> promise = new Promise<>();
		promise.then((Jedis jedis) -> {
			RedisImpl.removeAuthCodeImpl(name, jedis);
			promise.resolve(null);
		});
		return promise;
	}
}
