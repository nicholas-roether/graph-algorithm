package io.github.nicholas_roether.physics_graph;

import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsEngine;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

import java.util.HashSet;
import java.util.Set;


public class PhysicsGraph<ND extends PhysicsNodeData, ED> extends Graph<ND, ED> {
	private final PhysicsEngine engine;
	private final Set<GraphNode<ND>> anchors;

	public PhysicsGraph() {
		super();
		this.engine = new PhysicsEngine();
		this.anchors = new HashSet<>();
	}

	public void initPhysicsEngine() {
		for (GraphNode<ND> node : getNodes()) {
			final ND data = node.getData();
			if (data == null)
				throw new IllegalStateException("Can't init physics engine due to not properly initialized node: " + node);
			engine.addObject(data);
		}
	}

	public void stepPhysicsEngine(float time) {
		engine.step(time);
	}

	public boolean addAnchor(GraphNode<ND> node) {
		return anchors.add(node);
	}

	public Set<GraphNode<ND>> getAnchors() {
		return anchors;
	}
}
