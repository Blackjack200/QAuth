package com.github.blackjack200.qauth.qauth;

import com.github.blackjack200.qauth.qauth.utils.AssumptionFailedException;
import com.github.blackjack200.qauth.qauth.utils.Utils;
import lombok.NonNull;
import redis.clients.jedis.Jedis;

public final class GlobalJedisConfig {
	private static JedisConfig config;

	private GlobalJedisConfig() {
		//noop
	}

	@NonNull
	public static JedisConfig get() {
		if (GlobalJedisConfig.config == null) {
			throw new AssumptionFailedException("Global JedisConfig should not be null when access");
		}
		return GlobalJedisConfig.config;
	}

	public static void set(@NonNull JedisConfig config) {
		GlobalJedisConfig.config = config;
	}

	public static Jedis newJedis() {
		final ConfigBean info = GlobalJedisConfig.get().getInfo();
		Jedis jedis = new Jedis(Utils.notNullString(info.host), info.port);
		try {
			jedis.auth(Utils.notNullString(info.password));
			return jedis;
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
		return null;
	}
}
