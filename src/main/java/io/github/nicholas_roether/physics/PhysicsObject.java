package io.github.nicholas_roether.physics;

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
	void setAcceleration(PVector acceleration);

	/**
	 * Sets the object's velocity.
	 * @param velocity the new velocity
	 */
	void setVelocity(PVector velocity);

	/**
	 * Sets the object's position.
	 * @param position the new position
	 */
	void setPosition(PVector position);

	/**
	 * Update the state of the physics object.
	 */
	void update();
}
