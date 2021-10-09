package io.github.nicholas_roether.physics_graph;

import io.github.nicholas_roether.general.NodePosition;
import processing.core.PVector;

public interface PhysicsNodeData extends NodePosition {
	PVector getPosition();

	PVector getVelocity();

	PVector getAcceleration();

	void setPosition(PVector position);

	void setVelocity(PVector velocity);

	void setAcceleration(PVector acceleration);
}
