package io.github.nicholas_roether.physics_graph;

import io.github.nicholas_roether.components.NodeComponent;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsObject;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

public class NodePhysics implements PhysicsObject {
	private static final float REPULSION_CONSTANT = 1150000;
	private static final float ATTRACTION_CONSTANT = 4f;
	private static final float FRICTION_CONSTANT = 2f;

	private PVector acceleration = new PVector(0, 0);
	private PVector velocity = new PVector(0, 0);

	private boolean disabled = false;
	private boolean colliding = false;

	private final GraphNode<PVector> node;
	private final Graph<PVector, ?> graph;

	public NodePhysics(GraphNode<PVector> node, Graph<PVector, ?> graph) {
		this.node = node;
		this.graph = graph;
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
		this.acceleration = acceleration.copy();
	}

	@Override
	public void setVelocity(@NotNull PVector velocity) {
		this.velocity = velocity.copy();
	}

	@Override
	public void setPosition(@NotNull PVector position) {
		node.setData(position.copy());
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public void update(float time) {
		if (disabled) {
			setAcceleration(new PVector(0, 0));
			setVelocity(new PVector(0, 0));
			return;
		}

		final PVector acc = new PVector(0, 0);
		for (GraphNode<PVector> node : graph.getNodes()) {
			final float distance = getDistance(node);
			if (distance == 0) continue;
			final PVector normal = getNormalTo(node);

			if (distance <= NodeComponent.NODE_RADIUS) {
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
