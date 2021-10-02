package io.github.nicholas_roether.physics;

import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

public class PhysicsPoint implements PhysicsObject {
	private PVector acceleration;
	private PVector velocity;
	private PVector position;

	public PhysicsPoint(PVector position, PVector velocity, PVector acceleration) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
	}

	public PhysicsPoint(PVector position, PVector velocity) {
		this.position = position;
		this.velocity = velocity;
		this.acceleration = new PVector(0, 0, 0);
	}

	public PhysicsPoint(PVector position) {
		this.position = position;
		this.velocity = new PVector(0, 0, 0);
		this.acceleration = new PVector(0, 0, 0);
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
		System.out.println(position);
		this.position = position;
	}

	@Override
	public void update(float time) {
		// do nothing
	}
}
