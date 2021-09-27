package io.github.nicholas_roether.util;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

public class Notifier {
	private final Set<String> events;
	private HashMap<String, Set<Runnable>> listenerMap;

	public Notifier(String ...events) {
		this.events = Set.of(events);
		listenerMap = new HashMap<>();
	}

	public void on(String event, Runnable listener) {
		final Set<Runnable> listeners = listenerMap.get(event);
		if (listeners == null) return;
		listeners.add(listener);
	}

	public void removeListener(String event, Runnable listener) {
		final Set<Runnable> listeners = listenerMap.get(event);
		if (listeners == null) return;
		listeners.remove(listener);
	}

	public void dispatch(String event) {
		final Set<Runnable> listeners = listenerMap.get(event);
		if (listeners == null) return;
		for (Runnable listener : listeners) listener.run();
	}
}

