package io.github.nicholas_roether.physics_graph;

import io.github.nicholas_roether.components.NodeComponent;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsObject;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

/**
 * The physics object behind a NodeComponent.
 */
public class NodePhysics<D extends PhysicsNodeData> implements PhysicsObject {
	/**
	 * The constant that determines the strength of the repulsion between nodes.
	 */
	private static final float REPULSION_CONSTANT = 1200000f;

	/**
	 * The constant that determines the strength of the attraction between connected nodes.
	 */
	private static final float ATTRACTION_CONSTANT = 4f;

	/**
	 * The constant that determines the strength of friction.
	 */
	private static final float FRICTION_CONSTANT = 2f;

	/**
	 * The radius of a node.
	 */
	private static final float RADIUS = NodeComponent.NODE_RADIUS;

	/**
	 * Whether the physics of this node is disabled (for example if the node is being dragged).
	 */
	private boolean disabled = false;

	/**
	 * Whether this node is currently colliding with another node.
	 */
	private boolean colliding = false;

	/**
	 * The node whose position the physics applies to
	 */
	private final GraphNode<D> node;

	/**
	 * The graph the node belongs to
	 */
	private final Graph<D, Object> graph;

	private float screenWidth = 0;

	private float screenHeight = 0;

	public NodePhysics(GraphNode<D> node, Graph<D, Object> graph) {
		this.node = node;
		this.graph = graph;
	}

	public void setScreenWidth(float screenWidth) {
		this.screenWidth = screenWidth;
	}

	public void setScreenHeight(float screenHeight) {
		this.screenHeight = screenHeight;
	}

	@Override
	public PVector getAcceleration() {
		return node.data.getAcceleration();
	}

	@Override
	public PVector getVelocity() {
		return node.data.getVelocity();
	}

	@Override
	public PVector getPosition() {
		return node.data.getPosition();
	}

	@Override
	public void setAcceleration(@NotNull PVector acceleration) {
		node.data.setAcceleration(acceleration.copy());
	}

	@Override
	public void setVelocity(@NotNull PVector velocity) {
		node.data.setVelocity(velocity.copy());
	}

	@Override
	public void setPosition(@NotNull PVector position) {
		node.data.setPosition(position.copy());
	}

	/**
	 * Set whether the physics of this node is disabled.
	 * <br>
	 * Calling {@code setDisabled(true)} causes the velocity and acceleration to be set to zero.
	 *
	 * @param disabled Whether the physics should be disabled
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
		if (disabled) {
			setAcceleration(new PVector(0, 0));
			setVelocity(new PVector(0, 0));
		}
	}

	@Override
	public void update(float time) {
		if (disabled) {
			// Make sure the velocity and acceleration are zero when the physics are disabled
			setAcceleration(new PVector(0, 0));
			setVelocity(new PVector(0, 0));
			return;
		}

		/*
		The physics more or less follows this differential equation:

		a = -R + A - f * v

		Where:
			- a is the node's acceleration vector
			- R is the sum of the acceleration due to repulsion from each node; see getRepulsion()
			- A is the sum of the acceleration due to attraction to each connected node; see getAttraction()
			- f is the friction constant
			- v is the node's velocity vector
		 */

		// The total acceleration is accumulated in this vector.
		final PVector acc = new PVector(0, 0);

		boolean collidingWithNode = false;
		boolean collidingWithVerticalBorder = false;
		boolean collidingWithHorizontalBorder = false;

		for (GraphNode<D> node : graph.getNodes()) {
			final float distance = getDistance(node);
			if (distance == 0) continue; // Ignore nodes that have 0 distance between them because that breaks the math
			final PVector normal = getNormalTo(node);

			if (distance <= RADIUS) {
				collidingWithNode = true;
				if (!colliding) {
					// Handle collisions by having the nodes bounce off of each other (once per collision)
					getVelocity().sub(normal.copy().mult(2 * getVelocity().dot(normal.mult(-1))));
					getPosition().add(normal.copy().mult((distance - RADIUS) / 2));
				}
			} else {
				final float repulsion = getRepulsion(distance);
				// Add a vector away from the other node with the appropriate length to the acceleration
				acc.add(normal.copy().mult(-repulsion));
			}
		}
		for (GraphNeighbor<D, Object> neighbor : graph.getNeighbors(node)) {
			final float distance = getDistance(neighbor.node);
			if (distance == 0) continue; // Ignore nodes that have 0 distance between them because that breaks the math
			final PVector normal = getNormalTo(neighbor.node);
			final float attraction = getAttraction(distance, neighbor.edgeWeight);
			// Add a vector towards the neighboring node with the appropriate length to the acceleration
			acc.add(normal.mult(attraction));
		}
		acc.add(getFriction()); // Add the acceleration due to friction
		setAcceleration(acc);

		if (screenHeight > 0 && (getPosition().x <= RADIUS || getPosition().x >= screenWidth - RADIUS)) {
			if (!colliding) setVelocity(new PVector(-getVelocity().x, getVelocity().y));
			collidingWithHorizontalBorder = true;
			if (getPosition().x <= RADIUS) getPosition().x = RADIUS;
			if (getPosition().x >= screenWidth - RADIUS) getPosition().x = screenWidth - RADIUS;
		}
		if (screenWidth > 0 && (getPosition().y <= RADIUS || getPosition().y >= screenHeight - RADIUS)) {
			if (!colliding) setVelocity(new PVector(getVelocity().x, -getVelocity().y));
			collidingWithVerticalBorder = true;
			if (getPosition().y <= RADIUS) getPosition().y = RADIUS;
			if (getPosition().y >= screenHeight - RADIUS) getPosition().y = screenHeight - RADIUS;
		}

		colliding = collidingWithNode || collidingWithHorizontalBorder || collidingWithVerticalBorder;
	}

	/**
	 * Get the distance from this node to another
	 *
	 * @param other The node to get the distance to
	 * @return the computed distance
	 */
	private float getDistance(@NotNull GraphNode<D> other) {
		return getPosition().dist(other.data.getPosition());
	}

	/**
	 * Gets the normal vector from this node towards another, meaning a vector that points from this node
	 * in the direction of the other node, with length 1.
	 *
	 * @param other The node to get the normal vector to
	 * @return the computed normal vector
	 */
	private PVector getNormalTo(@NotNull GraphNode<D> other) {
		return other.data.getPosition().copy().sub(getPosition()).normalize();
	}

	/**
	 * Gets the acceleration magnitude due to repulsion caused by a node of the given distance.
	 *
	 * @param distance The distance to the node causing the repulsion
	 * @return the magnitude of the computed acceleration
	 */
	private static float getRepulsion(float distance) {
		if (distance == 0) return 0; // Avoid dividing by 0
		// The repulsion strength is proportional to the inverse square of the distance.
		return REPULSION_CONSTANT / (distance * distance);
	}

	/**
	 * Gets the acceleration magnitude due to attraction caused by a connected node of the given distance,
	 * connected by an edge of the given weight.
	 * <br>
	 * Edges with higher weights will result in lower attraction, causing the nodes to be further away from each other.
	 *
	 * @param distance The distance to the node causing the attraction
	 * @param weight The weight of the connecting edge
	 * @return the magnitude of the computed acceleration
	 */
	private static float getAttraction(float distance, double weight) {
		/*
		In the most basic terms, the attraction is proportional to the distance and inversely proportional to the
		edge weight.

		The only complication is the fact that the edge weight used in the formula is bounded between 1 and 20; any
		inputted weights above that will result in the same attraction as a weight of 20, and any weights below in the
		same as a weight of 1. This is to prevent a too extreme distortion of the graph.
		 */
		final float minmaxedWeight = (float) Math.min(Math.max(weight, 1.0), 20.0);
		return ATTRACTION_CONSTANT * distance / minmaxedWeight;
	}

	/**
	 * Gets the acceleration vector due to friction.
	 *
	 * @return the computed acceleration vector
	 */
	private PVector getFriction() {
		// friction is simply proportional to the node's speed, and points in the opposite direction to
		// the node's velocity.
		return getVelocity().copy().mult(-FRICTION_CONSTANT);
	}
}
