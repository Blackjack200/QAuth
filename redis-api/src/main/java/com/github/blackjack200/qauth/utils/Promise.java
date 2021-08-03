package com.github.blackjack200.qauth.utils;

import cn.nukkit.Server;
import lombok.Getter;
import lombok.NonNull;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Promise<T, RT> implements PromiseInterface<T, RT> {
	private Consumer<T> asyncCall;
	@NonNull
	private ArrayList<Consumer<RT>> onFulfill = new ArrayList<>(16);
	@NonNull
	private ArrayList<Consumer<RT>> onReject = new ArrayList<>(16);
	@Getter
	private RT result;
	@Getter
	private boolean resolved = false;

	public Promise() {
	}

	public static <TT, RTT> Promise<TT, RTT> newPromise(Class<TT> klass, Class<RTT> klass2) {
		return new Promise<>();
	}

	@Override
	public Promise<T, RT> then(@NonNull Consumer<T> call) {
		this.asyncCall = call;
		return this;
	}

	@Override
	public Promise<T, RT> onReject(@NonNull Consumer<RT> call) {
		this.onReject.add(call);
		return this;
	}

	@Override
	public Promise<T, RT> onFulfill(@NonNull Consumer<RT> call) {
		this.onFulfill.add(call);
		return this;
	}

	@Override
	public ArrayList<Consumer<RT>> getFulfillCallbacks() {
		return onFulfill;
	}

	@Override
	public ArrayList<Consumer<RT>> getRejectCallbacks() {
		return onReject;
	}

	@Override
	public Consumer<T> getAsyncCall() {
		return this.asyncCall;
	}

	@Override
	public void start() {
		//ffs
		PromiseTask<RT> task = new PromiseTask<RT>((Promise<Jedis, RT>) this);
		Server.getInstance().getScheduler().scheduleAsyncTask(null, task);
	}

	@Override
	public void resolve(RT reason) {
		this.result = reason;
		this.resolved = true;
		throw InterruptSignal.SIGNAL;
	}

	@Override
	public void reject(RT reason) {
		this.result = reason;
		this.resolved = false;
		throw InterruptSignal.SIGNAL;
	}
}