package io.github.nicholas_roether.physics;

import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 * A rudimentary physics engine that simulates a set of {@code PhysicsObject}s.
 *
 * @see PhysicsObject
 */
public class PhysicsEngine<O extends PhysicsObject> {
	private final List<O> objects;

	/**
	 * Constructs a {@code PhysicsEngine} with no {@code PhysicsObject}s associated with it.
	 */
	public PhysicsEngine() {
		objects = new ArrayList<>();
	}

	/**
	 * Adds a new {@code PhysicsObject} to this engine.
	 *
	 * @param object The object to add.
	 */
	public void addObject(@NotNull O object) {
		objects.add(object);
	}

	/**
	 * Adds multiple new {@code PhysicsObject}s to this engine.
	 *
	 * @param objects The objects to add.
	 */
	@SafeVarargs
	public final void addObjects(O @NotNull ... objects) {
		for (O object : objects) {
			addObject(object);
		}
	}

	/**
	 * Returns all {@code PhysicsObject}s that are associated with this engine.
	 *
	 * @return all {@code PhysicsObject}s that are associated with this engine.
	 */
	public List<O> getObjects() {
		return objects;
	}

	/**
	 * Steps the simulation forward by the specified amount of time.
	 * <br>
	 * It is crucial that steps occur very frequently, as the more frequent they are
	 * the more accurate the simulation becomes. Depending on the use case, between 10 and 60
	 * steps per second are recommended.
	 *
	 * @param time The step time in seconds
	 */
	public void step(float time) {
		for (O object : objects)
				stepObject(object, time);
	}

	/**
	 * Resets the physics engine, removing all registered objects.
	 */
	public void reset() {
		objects.clear();
	}

	protected void stepObject(@NotNull O object, float time) {
		object.update(time);
		final PVector deltaVel = object.getAcceleration().copy().mult(time);
		final PVector deltaPos = object.getVelocity().copy().mult(time);
		object.getVelocity().add(deltaVel);
		object.getPosition().add(deltaPos);
		object.postUpdate();

		mendVector(object.getPosition());
		mendVector(object.getVelocity());
		mendVector(object.getAcceleration());
	}

	private static void mendVector(PVector vector) {
		if (Float.isNaN(vector.x)) vector.x = 0;
		if (Float.isNaN(vector.y)) vector.y = 0;
	}
}
