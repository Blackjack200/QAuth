package com.github.blackjack200.qauth.utils;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.function.Consumer;

public interface PromiseInterface<T, RT> {
	PromiseInterface<T, RT> then(@NonNull Consumer<T> call);

	PromiseInterface<T, RT> onReject(@NonNull Consumer<RT> call);

	PromiseInterface<T, RT> onFulfill(@NonNull Consumer<RT> call);

	ArrayList<Consumer<RT>> getFulfillCallbacks();

	ArrayList<Consumer<RT>> getRejectCallbacks();

	Consumer<T> getAsyncCall();

	void start();

	void resolve(RT reason);

	void reject(RT reason);
}
