package io.github.nicholas_roether.draw;

import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

import java.util.Objects;

public abstract class Component extends Element implements WindowEventReceiver {
	private static int idCounter = 0;

	public final int zIndex;
	public final int id;

	public Component() {
		this.zIndex = 0;
		id = nextId();
	}

	public Component(int zIndex) {
		this.zIndex = zIndex;
		id = nextId();
	}

	public void draw(@NotNull PApplet p) {}

	public void build(ComponentRegistry registry) {}

	public boolean shouldRebuild() {
		return false;
	}

	private int nextId() {
		return ++idCounter;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Component component = (Component) o;
		return id == component.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
