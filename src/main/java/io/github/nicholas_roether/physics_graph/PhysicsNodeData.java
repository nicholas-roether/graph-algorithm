package io.github.nicholas_roether.physics_graph;

import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;

import io.github.nicholas_roether.physics.PhysicsObject;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

public class PhysicsNodeData implements PhysicsObject {
	private static final float REPULSION_CONSTANT = 1150000;
	private static final float ATTRACTION_CONSTANT = 4f;
	private static final float FRICTION_CONSTANT = 2f;
	public static final float RADIUS = 15;

	public boolean dragging = false;
	private final PhysicsGraph<PhysicsNodeData, ?> graph;
	private final GraphNode<PhysicsNodeData> node;
	private PVector acceleration;
	private PVector velocity;
	private PVector position;
	private boolean colliding = false;

	public PhysicsNodeData(
			@NotNull GraphNode<PhysicsNodeData> node,
			@NotNull PhysicsGraph<PhysicsNodeData, ?> graph,
			@NotNull PVector position
	) {
		acceleration = new PVector(0, 0, 0);
		velocity = new PVector(0, 0, 0);
		this.position = position;
		this.graph = graph;
		this.node = node;
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
		return position;
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
		this.position = position;
	}

	@Override
	public void update() {
		if (graph.getAnchors().contains(node)) {
			setAcceleration(new PVector(0, 0));
			setVelocity(new PVector(0, 0));
			return;
		}

		if (dragging) return;

		final PVector acc = new PVector(0, 0);
		for (GraphNode<PhysicsNodeData> node : graph.getNodes()) {
			final PhysicsNodeData nodeData = node.getData();
			final float distance = getDistance(nodeData);
			if (distance == 0) continue;
			final PVector normal = getNormalTo(nodeData);

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
		for (GraphNeighbor<PhysicsNodeData, ?> neighbor : graph.getNeighbors(node)) {
			final PhysicsNodeData neighborData = neighbor.node.getData();
			final float distance = getDistance(neighborData);
			if (distance == 0) continue;
			final PVector normal = getNormalTo(neighborData);
			final double minmaxedWeight = Math.min(Math.max(neighbor.edgeWeight, 1), 20);
			final float attraction = getAttraction(distance / (float) minmaxedWeight);
			acc.add(normal.mult(attraction));
		}
		acc.add(getFriction());
		setAcceleration(acc);
	}

	private float getDistance(@NotNull PhysicsNodeData other) {
		return position.dist(other.position);
	}

	private PVector getNormalTo(@NotNull PhysicsNodeData other) {
		return other.position.copy().sub(position).normalize();
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
