package io.github.nicholas_roether.physics_graph;

import processing.core.PVector;

public interface PhysicsNodeData {
	PVector getPosition();

	PVector getVelocity();

	PVector getAcceleration();

	void setPosition(PVector position);

	void setVelocity(PVector velocity);

	void setAcceleration(PVector acceleration);
}
