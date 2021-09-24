package io.github.nicholas_roether.physics_graph;

import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.UnknownGraphNodeException;
import io.github.nicholas_roether.physics.PhysicsEngine;
import processing.core.PVector;


public class PhysicsGraph extends Graph<PhysicsGraphNode> {
	public final PVector origin;
	public final float initialDispersion;

	private final PhysicsEngine engine;

	public PhysicsGraph(PVector origin, float initialDispersion) {
		super();
		this.origin = origin;
		this.initialDispersion = initialDispersion;
		this.engine = new PhysicsEngine();
	}

	@Override
	public boolean addNode(PhysicsGraphNode node) throws UnknownGraphNodeException {
		this.engine.addObject(node);
		return super.addNode(node);
	}

	public void stepPhysicsEngine(float time) {
		this.engine.step(time);
	}
}
