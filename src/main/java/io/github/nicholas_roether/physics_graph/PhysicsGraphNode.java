package io.github.nicholas_roether.physics_graph;

import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsObject;
import processing.core.PVector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PhysicsGraphNode extends GraphNode implements PhysicsObject {
	private static float REPULSION_CONSTANT = 30000;
	private static float ATTRACTION_CONSTANT = 0.08f;
	private static float FRICTION_CONSTANT = 0.3f;

	private final Graph<PhysicsGraphNode> graph;
	private PVector position = new PVector(0, 0);
	private PVector velocity = new PVector(0, 0);
	private PVector acceleration = new PVector(0, 0);

	public PhysicsGraphNode(String id, Graph<PhysicsGraphNode> graph) {
		super(id);
		this.graph = graph;
	}

	public PhysicsGraphNode(String id, Graph<PhysicsGraphNode> graph, Collection<GraphEdge> edges) {
		super(id, edges);
		this.graph = graph;
	}

	public Set<PhysicsGraphNode> getNeighboringPhysicsGraphNodes() {
		final Set<PhysicsGraphNode> physicsGraphNodes = new HashSet<>();
		for (GraphNode neighbor : super.getNeighbors()) {
			if (neighbor instanceof PhysicsGraphNode)
					physicsGraphNodes.add((PhysicsGraphNode) neighbor);
		}
		return physicsGraphNodes;
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
	public void setAcceleration(PVector acceleration) {
		this.acceleration = acceleration;
	}

	@Override
	public void setVelocity(PVector velocity) {
		this.velocity = velocity;
	}

	@Override
	public void setPosition(PVector position) {
		this.position = position;
	}

	@Override
	public void update() {
		final PVector acc = new PVector(0, 0);
		for (PhysicsGraphNode node : graph.getNodes()) {
			if (node == this) continue;
			final float distance = getDistance(node);
			final PVector normal = getNormalTo(node);
			final float repulsion = getRepulsion(distance);
			acc.add(normal.mult(-repulsion));
		}
		for (PhysicsGraphNode neighbor : getNeighboringPhysicsGraphNodes()) {
			final float distance = getDistance(neighbor);
			final PVector normal = getNormalTo(neighbor);
			final float attraction = getAttraction(distance);
			acc.add(normal.mult(attraction));
		}
		acc.add(getFriction());
		setAcceleration(acc);
	}

	private float getDistance(PhysicsGraphNode other) {
		return position.dist(other.position);
	}

	private PVector getNormalTo(PhysicsGraphNode other) {
		return other.position.copy().sub(position).normalize();
	}

	private float getRepulsion(float distance) {
		if (distance == 0) return 0;
		return REPULSION_CONSTANT / (distance * distance);
	}

	private float getAttraction(float distance) {
		return ATTRACTION_CONSTANT * distance;
	}

	private PVector getFriction() {
		return this.velocity.copy().mult(-FRICTION_CONSTANT);
	}
}
