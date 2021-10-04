package io.github.nicholas_roether.general;

import io.github.nicholas_roether.physics_graph.PhysicsNodeData;
import processing.core.PVector;

public class NodeData implements PhysicsNodeData {
	private PVector position = new PVector();
	private PVector velocity = new PVector();
	private PVector acceleration = new PVector();

	@Override
	public PVector getPosition() {
		return position;
	}

	@Override
	public PVector getVelocity() {
		return velocity;
	}

	@Override
	public PVector getAcceleration() {
		return acceleration;
	}

	@Override
	public void setPosition(PVector position) {
		this.position = position;
	}

	@Override
	public void setVelocity(PVector velocity) {
		this.velocity = velocity;
	}

	@Override
	public void setAcceleration(PVector acceleration) {
		this.acceleration = acceleration;
	}
}
