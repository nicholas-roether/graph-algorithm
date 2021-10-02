package io.github.nicholas_roether.draw;

import io.github.nicholas_roether.draw.cursor.CursorManager;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

import java.awt.event.MouseEvent;
import java.util.Objects;

public abstract class Component extends Element implements WindowEventReceiver {
	protected static final int NO_CURSOR_INSTRUCT = CursorManager.NO_CURSOR_INSTRUCT;

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

	public int instructCursor(float x, float y) {
		return NO_CURSOR_INSTRUCT;
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
