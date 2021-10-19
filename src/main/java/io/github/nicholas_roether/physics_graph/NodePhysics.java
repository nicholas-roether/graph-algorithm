package io.github.nicholas_roether.physics_graph;

import io.github.nicholas_roether.components.NodeComponent;
import io.github.nicholas_roether.general.EdgeData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsObject;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * The physics object behind a NodeComponent.
 */
public class NodePhysics implements PhysicsObject {
	/**
	 * The constant that determines the strength of the repulsion between nodes.
	 */
	public static final float REPULSION_CONSTANT = 5000000f;

	/**
	 * The constant that determines the strength of friction.
	 */
	public static final float FRICTION_CONSTANT = 3f;

	public static final float LENGTH_SCALE_FACTOR = 30f;

	public static final float SPRING_STRENGTH_FACTOR = 10f;

	/**
	 * The radius of a node.
	 */
	public static final float RADIUS = 20;

	/**
	 * Whether the physics of this node is disabled (for example if the node is an anchor).
	 */
	private boolean disabled = false;

	private boolean collidingWithLeftBorder = false;

	private boolean collidingWithRightBorder = false;

	private boolean collidingWithTopBorder = false;

	private boolean collidingWithBottomBorder = false;

	private final HashSet<GraphNode<NodeData>> collisionNodes = new HashSet<>();

	/**
	 * The node whose position the physics applies to
	 */
	private final GraphNode<NodeData> node;

	/**
	 * The graph the node belongs to
	 */
	private final Graph<NodeData, EdgeData> graph;

	private float screenWidth = 0;

	private float screenHeight = 0;

	public NodePhysics(GraphNode<NodeData> node, Graph<NodeData, EdgeData> graph) {
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

	/**
	 * Gets whether the physics of this node is disabled.
	 * <br>
	 *
	 * @return {@code true} if the physics are disabled.
	 */
	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public void update(float time) {
		if (disabled) {
			// Make sure the velocity and acceleration are zero when the physics are disabled
			setAcceleration(new PVector(0, 0));
			setVelocity(new PVector(0, 0));
			return;
		}

		final PVector acc = new PVector(0, 0);


		for (GraphNode<NodeData> node : graph.getNodes()) {
			final float distance = getDistance(node);
			if (distance == 0) continue; // Ignore nodes that have 0 distance between them because that breaks the math
			final PVector normal = getNormalTo(node);

			if (distance < 2 * RADIUS) {
				if (!collisionNodes.contains(node)) {
					final PVector velocity = getVelocity().copy();
					getVelocity().sub(normal.copy().mult(velocity.dot(normal)));
					node.data.getVelocity().add(normal.copy().mult(velocity.dot(normal)));
					collisionNodes.add(node);
				}
				getPosition().add(normal.mult(distance - 2 * RADIUS));
			} else {
				collisionNodes.remove(node);
				final float repulsion = getRepulsion(distance);
				// Add a vector away from the other node with the appropriate length to the acceleration
				acc.add(normal.copy().mult(-repulsion));
			}
		}
		for (GraphNeighbor<NodeData, EdgeData> neighbor : graph.getNeighbors(node)) {
			final float distance = getDistance(neighbor.node);
			if (distance == 0) continue; // Ignore nodes that have 0 distance between them because that breaks the math
			final PVector normal = getNormalTo(neighbor.node);
			final float acceleration = getAccelerationTowards(distance, neighbor.edgeWeight);
			acc.add(normal.mult(acceleration));
		}
		acc.add(getFriction()); // Add the acceleration due to friction
		setAcceleration(acc);

		if (screenHeight == 0 || screenWidth == 0) return;

		if (getPosition().x <= RADIUS) {
			if (!collidingWithLeftBorder) getVelocity().x *= -0.5;
			collidingWithLeftBorder = true;
			getAcceleration().x = 0;
			getPosition().x = RADIUS;
		} else {
			collidingWithLeftBorder = false;
			if (getPosition().x >= screenWidth - RADIUS) {
				if (!collidingWithRightBorder) getVelocity().x *= -0.5;
				collidingWithRightBorder = true;
				getAcceleration().x = 0;
				getPosition().x = screenWidth - RADIUS;
			} else collidingWithRightBorder = false;
		}
		if (getPosition().y <= RADIUS) {
			if (!collidingWithTopBorder) getVelocity().y *= -0.5;
			collidingWithTopBorder = true;
			getAcceleration().y = 0;
			getPosition().y = RADIUS;
		} else {
			collidingWithTopBorder = false;
			if (getPosition().y >= screenHeight - RADIUS) {
				if (!collidingWithBottomBorder) getVelocity().y *= -0.5;
				collidingWithBottomBorder = true;
				getAcceleration().y = 0;
				getPosition().y = screenHeight - RADIUS;
			} else collidingWithBottomBorder = false;
		}
	}

	/**
	 * Get the distance from this node to another
	 *
	 * @param other The node to get the distance to
	 * @return the computed distance
	 */
	private float getDistance(@NotNull GraphNode<NodeData> other) {
		return getPosition().dist(other.data.getPosition());
	}

	/**
	 * Gets the normal vector from this node towards another, meaning a vector that points from this node
	 * in the direction of the other node, with length 1.
	 *
	 * @param other The node to get the normal vector to
	 * @return the computed normal vector
	 */
	private PVector getNormalTo(@NotNull GraphNode<NodeData> other) {
		return other.data.getPosition().copy().sub(getPosition()).normalize();
	}

	private static float getAccelerationTowards(float distance, double weight) {
		final float minmaxedWeight = (float) Math.min(Math.max(weight, 1.0), 20.0);
		final float targetLength = LENGTH_SCALE_FACTOR * minmaxedWeight;
		final float offsetFromTargetLength = distance - targetLength;
		return SPRING_STRENGTH_FACTOR * offsetFromTargetLength;
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
