package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentGroup;
import io.github.nicholas_roether.draw.SimpleComponent;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsEngine;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class GraphComponent extends SimpleComponent {
	public final Graph<PVector, ?> graph;

	private final List<NodeComponent> nodeComponents;
	private final List<EdgeComponent> edgeComponents;
	private final PhysicsEngine<NodeComponent> engine;
	private final List<String> anchors;

	public GraphComponent(Graph<PVector, ?> graph, List<String> anchors) {
		super();
		engine = new PhysicsEngine<>();
		nodeComponents = new ArrayList<>();
		edgeComponents = new ArrayList<>();
		this.graph = graph;
		this.anchors = anchors;
	}

	@Override
	public void render(PApplet proc) {
		if (graph.getNodes().size() != nodeComponents.size())
			syncNodes();
		if (graph.getEdges().size() != edgeComponents.size())
			syncEdges();

		final List<Component> labels = new ArrayList<>();
		for (EdgeComponent edgeComponent : edgeComponents) {
			final PVector edgeCenter = edgeComponent.getCenter();
			labels.add(new Label(Double.toString(edgeComponent.edge.weight), edgeCenter.x, edgeCenter.y));
		}

		replaceChildren(List.of(
			new ComponentGroup(edgeComponents),
			new ComponentGroup(nodeComponents),
			new ComponentGroup(labels)
		));
	}

	public void stepPhysicsEngine(float time) {
		engine.step(time);
	}

	private void syncNodes() {
		nodeComponents.clear();
		engine.getObjects().clear();
		for (GraphNode<PVector> node : graph.getNodes()) {
			final NodeComponent component = new NodeComponent(node, graph, anchors.contains(node.name));
			nodeComponents.add(component);
			engine.addObject(component);
		}
	}

	private void syncEdges() {
		edgeComponents.clear();
		for (GraphEdge<PVector, ?> edge : graph.getEdges()) {
			final EdgeComponent component = new EdgeComponent(edge);
			edgeComponents.add(component);
		}
	}
}
