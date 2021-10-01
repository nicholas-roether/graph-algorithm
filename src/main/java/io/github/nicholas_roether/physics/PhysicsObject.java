package io.github.nicholas_roether.physics;

import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

/**
 * A moving object in a physics context.
 */
public interface PhysicsObject {
	/**
	 * Returns the object's acceleration.
	 * @return the object's acceleration.
	 */
	PVector getAcceleration();

	/**
	 * Returns the object's velocity.
	 * @return the object's velocity.
	 */
	PVector getVelocity();

	/**
	 * Returns the object's position.
	 * @return the object's position.
	 */
	PVector getPosition();

	/**
	 * Sets the object's acceleration.
	 * @param acceleration the new acceleration
	 */
	void setAcceleration(@NotNull PVector acceleration);

	/**
	 * Sets the object's velocity.
	 * @param velocity the new velocity
	 */
	void setVelocity(@NotNull PVector velocity);

	/**
	 * Sets the object's position.
	 * @param position the new position
	 */
	void setPosition(@NotNull PVector position);

	/**
	 * Update the state of the physics object.
	 */
	void update();

	/**
	 * Called after the update call
	 */
	default void postUpdate() {}
}
