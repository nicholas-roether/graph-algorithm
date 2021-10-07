package io.github.nicholas_roether;

import io.github.nicholas_roether.components.GraphComponent;
import io.github.nicholas_roether.components.MainView;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.general.GraphWithData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.GraphNode;

import java.util.List;

/**
 * The main class of this app.
 */
public class App extends Document {
	public App() {
		// create an 800x800 window with the below title
		super(1280, 900, "A* Algorithm");
	}

	public static void main(String[] args) {
		final App app = new App();
		System.setProperty("sun.java2d.uiScale.enabled", "false"); // disable ui scaling to fix blurry graphics on high-dpi screens
		app.runSketch(args); // start the application
	}

	@Override
	protected void create() {
//		fullScreen();
	}

	@Override
	protected void init() {
		frameRate(30);
	}

	@Override
	protected void build(ComponentRegistry registry) {
		GraphWithData graph = new GraphWithData();

		final GraphNode<NodeData> nodeA = graph.addNode("A", width / 2f, height / 2f);
		final GraphNode<NodeData> nodeB = graph.addNode("B", width / 2f + 100, height / 2f);
		final GraphNode<NodeData> nodeC = graph.addNode("C", width / 2f, height / 2f - 100);
		final GraphNode<NodeData> nodeD = graph.addNode("D", width / 2f, height / 2f - 69);
		final GraphNode<NodeData> nodeE = graph.addNode("E", width / 2f - 300, height / 2f);
		final GraphNode<NodeData> nodeF = graph.addNode("F", width / 2f - 100, height / 2f + 50);
		final GraphNode<NodeData> nodeG = graph.addNode("G", width / 2f + 100, height / 2f - 34);
		final GraphNode<NodeData> nodeH = graph.addNode("H", width / 2f + 32, height / 2f - 100);
		final GraphNode<NodeData> nodeI = graph.addNode("I", width / 2f - 56, height / 2f + 200);
		final GraphNode<NodeData> nodeJ = graph.addNode("J", width / 2f - 200, height / 2f - 14);

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
		registry.register(new MainView(), ComponentRegistry.NO_PARENT);
	}
}
