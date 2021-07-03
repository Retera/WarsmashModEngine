package com.etheller.warsmash.util;

import java.util.HashSet;
import java.util.Set;

public abstract class SubscriberSetNotifier<LISTENER_TYPE> {
	protected final Set<LISTENER_TYPE> set; // bad for iteration but there
											// should never be a dude subscribed
											// 2x

	public SubscriberSetNotifier() {
		this.set = new HashSet<>();
	}

	public final void subscribe(final LISTENER_TYPE listener) {
		this.set.add(listener);
	}

	public final void unsubscribe(final LISTENER_TYPE listener) {
		this.set.remove(listener);
	}

}
