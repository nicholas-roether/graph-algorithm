package io.github.nicholas_roether.physics;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhysicsEngine {
	private List<PhysicsObject> objects;

	public PhysicsEngine() {
		objects = new ArrayList<>();
	}

	public void addObject(PhysicsObject object) {
		objects.add(object);
	}

	public void addObjects(PhysicsObject ...objects) {
		for (PhysicsObject object : objects) {
			addObject(object);
		}
	}

	public List<PhysicsObject> getObjects() {
		return Collections.unmodifiableList(objects);
	}

	public void step(float time) {
		for (PhysicsObject object : objects)
				stepObject(object, time);
	}

	private void stepObject(@org.jetbrains.annotations.NotNull PhysicsObject object, float time) {
		object.update();
		final PVector deltaVel = object.getAcceleration().copy().mult(time);
		final PVector deltaPos = object.getVelocity().copy().mult(time);
		object.getVelocity().add(deltaVel);
		object.getPosition().add(deltaPos);
	}
}
