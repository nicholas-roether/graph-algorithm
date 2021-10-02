package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.bounded.CircularComponent;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics_graph.NodePhysics;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;


public class NodeComponent extends CircularComponent {
	public static final int Z_INDEX = 2;
	public static final float NODE_RADIUS = 15;
	public static final float TEXT_SIZE = 25;

	public final GraphNode<PVector> node;
	public final boolean anchor;

	public final NodePhysics physics;

	private boolean hover = false;
	private boolean dragging = false;

	public NodeComponent(GraphNode<PVector> node, Graph<PVector, Object> graph, boolean anchor) {
		super(Z_INDEX);
		this.node = node;
		this.anchor = anchor;
		this.physics = new NodePhysics(node, graph);
		if (anchor) physics.setDisabled(true);
	}

	@Override
	public void draw(@NotNull PApplet p) {
		if (hover) p.fill(140, 245, 256);
		else if (anchor) p.fill(85, 205, 244);
		else p.fill(255, 255, 255);
		p.stroke(100, 100, 100);
		p.strokeWeight(3);
		p.ellipse(physics.getPosition().x, physics.getPosition().y, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

		p.fill(0, 0, 0);
		p.textAlign(CENTER);
		p.textSize(TEXT_SIZE);
		p.text(node.name, physics.getPosition().x, physics.getPosition().y + TEXT_SIZE / 3);
	}

	@Override
	public void mouseMovedAnywhere(MouseEvent event) {
		hover = checkInBounds(event.getX(), event.getY());
		if (dragging) {
			physics.setPosition(new PVector(event.getX(), event.getY()));
			physics.setDisabled(true);
		}
	}

	@Override
	public void mousePressedInBounds(MouseEvent event) {
		dragging = true;
	}

	@Override
	public void mouseReleasedAnywhere(MouseEvent event) {
		dragging = false;
		physics.setDisabled(anchor);
	}

	@Override
	protected int instructCursorAnywhere(float x, float y) {
		if (dragging) return MOVE;
		return NO_CURSOR_INSTRUCT;
	}

	@Override
	protected int instructCursorInBounds() {
		if (dragging) return MOVE;
		return HAND;
	}

	@Override
	public float getRadius() {
		return NODE_RADIUS;
	}

	@Override
	public float getX() {
		return physics.getPosition().x;
	}

	@Override
	public float getY() {
		return physics.getPosition().y;
	}
}
