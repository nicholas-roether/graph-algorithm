package io.github.nicholas_roether;

import io.github.nicholas_roether.components.GraphComponent;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNode;
import processing.core.PVector;

import java.util.List;


public class App extends Document {
	public static void main(String[] args) {
		final App app = new App();
		app.runSketch(args);
	}

	@Override
	public void settings() {
		size(800, 600);
		pixelDensity(displayDensity());
	}

	@Override
	protected void build(ComponentRegistry registry) {
		frameRate(30);
		surface.setTitle("A* Algorithm");

		Graph<PVector, Object> graph = new Graph<>();

		final GraphNode<PVector> nodeA = graph.addNode("A", new PVector(width / 2f, height / 2f));
		final GraphNode<PVector> nodeB = graph.addNode("B", new PVector(width / 2f + 100, height / 2f));
		final GraphNode<PVector> nodeC = graph.addNode("C", new PVector(width / 2f, height / 2f - 100));
		final GraphNode<PVector> nodeD = graph.addNode("D", new PVector(width / 2f, height / 2f - 69));
		final GraphNode<PVector> nodeE = graph.addNode("E", new PVector(width / 2f - 300, height / 2f));
		final GraphNode<PVector> nodeF = graph.addNode("F", new PVector(width / 2f - 100, height / 2f + 50));
		final GraphNode<PVector> nodeG = graph.addNode("G", new PVector(width / 2f + 100, height / 2f - 34));
		final GraphNode<PVector> nodeH = graph.addNode("H", new PVector(width / 2f + 32, height / 2f - 100));
		final GraphNode<PVector> nodeI = graph.addNode("I", new PVector(width / 2f - 56, height / 2f + 200));
		final GraphNode<PVector> nodeJ = graph.addNode("J", new PVector(width / 2f - 200, height / 2f - 14));

		graph.addEdge(nodeA, nodeB, 1);
		graph.addEdge(nodeB, nodeC, 2);
		graph.addEdge(nodeA, nodeC, 3);
		graph.addEdge(nodeA, nodeD, 0.2);
		graph.addEdge(nodeA, nodeE, 2);
		graph.addEdge(nodeB, nodeE, 4);
		graph.addEdge(nodeF, nodeI, 0.5);
		graph.addEdge(nodeE, nodeG, 4);
		graph.addEdge(nodeH, nodeA, 20);
		graph.addEdge(nodeJ, nodeD, 0.6);
		graph.addEdge(nodeJ, nodeF, 11);
		graph.addEdge(nodeB, nodeH, 2);
		graph.addEdge(nodeC, nodeI, 1);
		graph.addEdge(nodeF, nodeI, 0.8);
		graph.addEdge(nodeJ, nodeI, 17);
		graph.addEdge(nodeD, nodeG, 2);

		GraphComponent graphComponent = new GraphComponent(graph, List.of("A"));
		registry.register(graphComponent, ComponentRegistry.NO_PARENT);
	}
}
