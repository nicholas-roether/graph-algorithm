package io.github.nicholas_roether.components;

import io.github.nicholas_roether.components.action_select.DeleteButton;
import io.github.nicholas_roether.components.action_select.EdgeButton;
import io.github.nicholas_roether.components.action_select.MoveButton;
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

	private MoveButton moveButton;
	private NodeButton nodeButton;
	private EdgeButton edgeButton;
	private DeleteButton deleteButton;

	private State state = State.MOVE;

	public EditActionSelector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void build(ComponentRegistry registry, PApplet p) {
		showing = visible;
		if (!visible) return;
		moveButton = new MoveButton(x, y);
		nodeButton = new NodeButton(x, y + moveButton.height + 10);
		edgeButton = new EdgeButton(x, y + moveButton.height + nodeButton.height + 20);
		deleteButton = new DeleteButton(x, y + moveButton.height + nodeButton.height + edgeButton.height + 30);
		registry.register(List.of(
				moveButton,
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
	public void frame(PApplet p) {
		switch (state) {
			case MOVE -> {
				if (nodeButton.isPressed()) state = State.NODE;
				if (edgeButton.isPressed()) state = State.EDGE;
				if (deleteButton.isPressed()) state = State.DELETE;
			}
			case NODE -> {
				if (moveButton.isPressed()) state = State.MOVE;
				if (edgeButton.isPressed()) state = State.EDGE;
				if (deleteButton.isPressed()) state = State.DELETE;
			}
			case EDGE -> {
				if (moveButton.isPressed()) state = State.MOVE;
				if (nodeButton.isPressed()) state = State.NODE;
				if (deleteButton.isPressed()) state = State.DELETE;
			}
			case DELETE -> {
				if (moveButton.isPressed()) state = State.MOVE;
				if (nodeButton.isPressed()) state = State.NODE;
				if (edgeButton.isPressed()) state = State.EDGE;
			}
		}

		moveButton.setPressed(false);
		nodeButton.setPressed(false);
		edgeButton.setPressed(false);
		deleteButton.setPressed(false);

		switch (state) {
			case MOVE -> moveButton.setPressed(true);
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
		return moveButton.height + nodeButton.height + edgeButton.height + deleteButton.height + 30;
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
		MOVE,
		NODE,
		EDGE,
		DELETE
	}
}
