package com.github.blackjack200.utils;

public class InterruptSignal extends RuntimeException {
	public static final InterruptSignal SIGNAL = new InterruptSignal();

	private InterruptSignal() {
		//noop
	}
}
