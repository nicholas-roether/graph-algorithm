package io.github.nicholas_roether.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EventSource<E> {
	private final Set<String> events;
	private HashMap<String, Set<Consumer<E>>> listenerMap;

	public EventSource(String ...events) {
		this.events = Set.of(events);
		listenerMap = new HashMap<>();
	}

	public void on(String event, Consumer<E> listener) {
		if (!events.contains(event)) return;
		if (!listenerMap.containsKey(event))
			listenerMap.put(event, new HashSet<>());
		Set<Consumer<E>> listeners = listenerMap.get(event);
		listeners.add(listener);
	}

	public void removeListener(String event, Consumer<E> listener) {
		final Set<Consumer<E>> listeners = listenerMap.get(event);
		if (listeners == null) return;
		listeners.remove(listener);
	}

	public void dispatch(String event, E evtObj) {
		final Set<Consumer<E>> listeners = listenerMap.get(event);
		if (listeners == null) return;
		for (Consumer<E> listener : listeners) listener.accept(evtObj);
	}
}
