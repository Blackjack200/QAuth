package com.github.blackjack200.qauth.utils;

import com.github.blackjack200.qauth.qauth.GlobalJedisConfig;
import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.MainLogger;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PromiseTask<T> extends AsyncTask {
	private final Jedis jedis;
	private final Promise<Jedis, T> promise;

	public PromiseTask(Promise<Jedis, T> promise) {
		this.jedis = GlobalJedisConfig.newJedis();
		this.promise = promise;
	}

	@Override
	public void onRun() {
		try {
			if (this.jedis == null) {
				this.promise.reject(null);
			}
			this.promise.getAsyncCall().accept(this.jedis);
		} catch (Throwable throwable) {
			if (!(throwable instanceof InterruptSignal)) {
				MainLogger.getLogger().logException(throwable);
			}
		}
		if (this.jedis != null) {
			this.jedis.close();
		}
	}

	@Override
	public void onCompletion(Server server) {
		ArrayList<Consumer<T>> callbacks = this.promise.getRejectCallbacks();
		if (this.promise.isResolved()) {
			callbacks = this.promise.getFulfillCallbacks();
		}
		for (Consumer<T> consumer : callbacks) {
			consumer.accept(this.promise.getResult());
		}
	}
}