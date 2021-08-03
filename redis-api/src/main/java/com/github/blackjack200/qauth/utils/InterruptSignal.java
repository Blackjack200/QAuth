package com.github.blackjack200.qauth.utils;

public class InterruptSignal extends RuntimeException {
	public static final InterruptSignal SIGNAL = new InterruptSignal();

	private InterruptSignal() {
		//noop
	}
}
