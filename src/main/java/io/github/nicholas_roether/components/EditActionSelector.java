package io.github.nicholas_roether.components;

import io.github.nicholas_roether.components.action_select.DeleteButton;
import io.github.nicholas_roether.components.action_select.EdgeButton;
import io.github.nicholas_roether.components.action_select.NodeButton;
import io.github.nicholas_roether.components.common.ToggleButton;
import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.bounded.RectangularComponent;
import processing.core.PApplet;

import java.util.List;

public class EditActionSelector extends RectangularComponent {
	public final float x;
	public final float y;

	private boolean visible = true;
	private boolean showing = false;

	private NodeButton nodeButton;
	private EdgeButton edgeButton;
	private DeleteButton deleteButton;

	private State state = State.NODE;

	public EditActionSelector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void build(ComponentRegistry registry, PApplet p) {
		showing = visible;
		if (!visible) return;
		nodeButton = new NodeButton(x, y);
		edgeButton = new EdgeButton(x, y + nodeButton.height + 10);
		deleteButton = new DeleteButton(x, y + nodeButton.height + edgeButton.height + 20);
		registry.register(List.of(
				nodeButton,
				edgeButton,
				deleteButton
		), id);
	}

	public State getState() {
		return state;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean shouldRebuild() {
		return showing != visible;
	}

	@Override
	public void frame(float frameRate) {
		switch (state) {
			case NODE -> {
				if (edgeButton.isPressed()) state = State.EDGE;
				if (deleteButton.isPressed()) state = State.DELETE;
			}
			case EDGE -> {
				if (nodeButton.isPressed()) state = State.NODE;
				if (deleteButton.isPressed()) state = State.DELETE;
			}
			case DELETE -> {
				if (nodeButton.isPressed()) state = State.NODE;
				if (edgeButton.isPressed()) state = State.EDGE;
			}
		}

		nodeButton.setPressed(false);
		edgeButton.setPressed(false);
		deleteButton.setPressed(false);

		switch (state) {
			case NODE -> nodeButton.setPressed(true);
			case EDGE -> edgeButton.setPressed(true);
			case DELETE -> deleteButton.setPressed(true);
		}
	}

	@Override
	public float getWidth() {
		return Math.max(Math.max(nodeButton.width, edgeButton.width), deleteButton.width);
	}

	@Override
	public float getHeight() {
		return nodeButton.height + edgeButton.height + deleteButton.height + 20;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	public enum State {
		NODE,
		EDGE,
		DELETE
	}
}
