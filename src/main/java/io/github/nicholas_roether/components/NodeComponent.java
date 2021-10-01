package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.CircleComponent;
import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsObject;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.List;

public class NodeComponent extends CircleComponent<NodeComponent.State> implements PhysicsObject {
	public static class State {
		public final PVector position;
		public final boolean hover;

		public State(PVector position, boolean hover) {
			this.position = position;
			this.hover = hover;
		}
	}

	private static final float REPULSION_CONSTANT = 1150000;
	private static final float ATTRACTION_CONSTANT = 4f;
	private static final float FRICTION_CONSTANT = 2f;
	public static final float RADIUS = 15;

	public final GraphNode<PVector> node;
	public final boolean anchor;

	private final Graph<PVector, ?> graph;
	private PVector velocity = new PVector(0, 0);
	private PVector acceleration = new PVector(0, 0);
	private boolean colliding = false;

	public NodeComponent(GraphNode<PVector> node, Graph<PVector, ?> graph, boolean anchor) {
		super();
		this.node = node;
		this.anchor = anchor;
		this.graph = graph;

		setState(new State(node.getData(), false));
	}


	@Override
	public List<Component<?>> build(PApplet proc) {
		return NO_CHILDREN;
	}

	@Override
	public void render(PApplet proc) {
		if (getState().hover) proc.fill(140, 245, 256);
		else if (anchor) proc.fill(85, 205, 244);
		else proc.fill(255, 255, 255);
		proc.stroke(100, 100, 100);
		proc.strokeWeight(3);
		proc.ellipseMode(CENTER);
		proc.ellipse(getX(), getY(), 2 * getRadius(), 2 * getRadius());

		final float textSize = 25;
		proc.fill(0, 0, 0);
		proc.textAlign(CENTER);
		proc.textSize(textSize);
		proc.text(node.name, getState().position.x, getState().position.y + textSize / 3);
	}

	@Override
	protected void onMouseEntered(MouseEvent event) {
		setState(new State(getState().position, true));
	}

	@Override
	protected void onMouseExited(MouseEvent event) {
		setState(new State(getState().position, false));
	}

	@Override
	public void onMouseDragged(MouseEvent event) {
		System.out.println(event.getX() + "; " + event.getY());
		velocity = new PVector(0, 0);
		acceleration = new PVector(0, 0);
		node.setData(new PVector(event.getX(), event.getY()));
		setState(new State(node.getData(), getState().hover));
	}

	@Override
	public float getX() {
		return node.getData().x;
	}

	@Override
	public float getY() {
		return node.getData().y;
	}

	@Override
	public float getRadius() {
		return RADIUS;
	}

	@Override
	public PVector getAcceleration() {
		return acceleration;
	}

	@Override
	public PVector getVelocity() {
		return velocity;
	}

	@Override
	public PVector getPosition() {
		return node.getData();
	}

	@Override
	public void setAcceleration(@NotNull PVector acceleration) {
		this.acceleration = acceleration;
	}

	@Override
	public void setVelocity(@NotNull PVector velocity) {
		this.velocity = velocity;
	}

	@Override
	public void setPosition(@NotNull PVector position) {
		node.setData(position);
	}

	@Override
	public void update() {
		if (anchor || getState().hover) {
			setAcceleration(new PVector(0, 0));
			setVelocity(new PVector(0, 0));
			return;
		}

		final PVector acc = new PVector(0, 0);
		for (GraphNode<PVector> node : graph.getNodes()) {
			final float distance = getDistance(node);
			if (distance == 0) continue;
			final PVector normal = getNormalTo(node);

			if (distance <= RADIUS) {
				if (!colliding) {
					colliding = true;
					velocity.sub(normal.copy().mult(2 * velocity.dot(normal.mult(-1))));
				}
			} else {
				colliding = false;
				final float repulsion = getRepulsion(distance);
				acc.add(normal.copy().mult(-repulsion));
			}
		}
		for (GraphNeighbor<PVector, ?> neighbor : graph.getNeighbors(node)) {
			final float distance = getDistance(neighbor.node);
			if (distance == 0) continue;
			final PVector normal = getNormalTo(neighbor.node);
			final double minmaxedWeight = Math.min(Math.max(neighbor.edgeWeight, 1), 20);
			final float attraction = getAttraction(distance / (float) minmaxedWeight);
			acc.add(normal.mult(attraction));
		}
		acc.add(getFriction());
		setAcceleration(acc);
	}

	@Override
	public void postUpdate() {
		setState(new State(node.getData(), getState().hover));
	}

	private float getDistance(@NotNull GraphNode<PVector> other) {
		return node.getData().dist(other.getData());
	}

	private PVector getNormalTo(@NotNull GraphNode<PVector> other) {
		return other.getData().copy().sub(node.getData()).normalize();
	}

	private float getRepulsion(float distance) {
		if (distance == 0) return 0;
		return REPULSION_CONSTANT / (distance * distance);
	}

	private float getAttraction(float distance) {
		return ATTRACTION_CONSTANT * distance;
	}

	private PVector getFriction() {
		return velocity.copy().mult(-FRICTION_CONSTANT);
	}
}
