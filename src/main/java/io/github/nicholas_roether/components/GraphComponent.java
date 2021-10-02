package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsEngine;
import io.github.nicholas_roether.physics_graph.NodePhysics;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphComponent extends Component {
	public final Graph<PVector, Object> graph;
	public final List<String> anchors;

	private Set<GraphNode<PVector>> nodes;
	private List<GraphEdge<PVector, Object>> edges;
	private final PhysicsEngine<NodePhysics> physicsEngine;

	public GraphComponent(Graph<PVector, Object> graph, List<String> anchors) {
		this.graph = graph;
		this.anchors = anchors;
		this.nodes = new HashSet<>();
		this.physicsEngine = new PhysicsEngine<>();
	}

	@Override
	public void build(ComponentRegistry registry) {
		physicsEngine.reset();

		nodes = graph.getNodes();
		edges = graph.getEdges();

		final List<Component> components = new ArrayList<>();

		for (GraphNode<PVector> node : nodes) {
			final NodeComponent nodeComponent = new NodeComponent(node, graph, anchors.contains(node.name));
			physicsEngine.addObject(nodeComponent.physics);
			components.add(nodeComponent);
		}

		for (GraphEdge<PVector, Object> edge : edges) {
			components.add(new EdgeComponent(edge));
			components.add(new EdgeLabel(edge));
		}

		registry.register(components, id);
	}

	@Override
	public boolean shouldRebuild() {
		return !nodes.equals(graph.getNodes()) || !edges.equals(graph.getEdges());
	}

	@Override
	public void draw(@NotNull PApplet p) {
		physicsEngine.step(1 / p.frameRate);
	}
}
