package io.github.nicholas_roether;

import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics_graph.PhysicsGraph;
import io.github.nicholas_roether.physics_graph.PhysicsNodeData;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PVector;

import javax.lang.model.type.NullType;
import java.util.List;
import java.util.Set;


public class App extends PApplet {
	private PhysicsGraph<PhysicsNodeData, NullType> graph;

	public static void main(String[] args) {
		final App app = new App();
		app.runSketch();
	}

	@Override
	public void settings() {
		size(800, 600);
		pixelDensity(displayDensity());
	}

	@Override
	public void setup() {
		frameRate(30);

		graph = new PhysicsGraph<>();

		final GraphNode<PhysicsNodeData> nodeA = graph.addNode("A");
		nodeA.setData(new PhysicsNodeData(nodeA, graph, new PVector(width / 2f - 100, height / 2f)));

		final GraphNode<PhysicsNodeData> nodeB = graph.addNode("B");
		nodeB.setData(new PhysicsNodeData(nodeB, graph, new PVector(width / 2f + 100, height / 2f)));

		final GraphNode<PhysicsNodeData> nodeC = graph.addNode("C");
		nodeC.setData(new PhysicsNodeData(nodeC, graph, new PVector(width / 2f, height / 2f - 100)));

		final GraphNode<PhysicsNodeData> nodeD = graph.addNode("D");
		nodeD.setData(new PhysicsNodeData(nodeD, graph, new PVector(width / 2f, height / 2f)));

		final GraphNode<PhysicsNodeData> nodeE = graph.addNode("E");
		nodeE.setData(new PhysicsNodeData(nodeE, graph, new PVector(width / 2f - 300, height / 2f)));

		graph.addEdge(nodeA, nodeB, 5);
		graph.addEdge(nodeB, nodeC, 2);
		graph.addEdge(nodeA, nodeC, -3);
		graph.addEdge(nodeA, nodeD, 4);
		graph.addEdge(nodeA, nodeE, 2);
		graph.addEdge(nodeB, nodeE, 4);

		graph.addAnchor(nodeA);

		graph.initPhysicsEngine();
	}

	@Override
	public void draw() {
		background(0, 0, 0);

		graph.stepPhysicsEngine(1 / frameRate);
		drawPhysicsGraph(graph);
	}

	private void drawArrow(float x1, float y1, float x2, float y2, float headHeight, float headWidth) {
		final PVector vector = new PVector(x2 - x1, y2 - y1);
		final PVector normal = new PVector(-vector.y, vector.x).normalize();

		final PVector toHeadTop = vector.copy().add(vector.copy().normalize().mult(headHeight));
		final PVector toHeadBaseRight = vector.copy().add(normal.copy().mult(headWidth / 2));
		final PVector toHeadBaseLeft = vector.copy().sub(normal.copy().mult(headWidth / 2));


		line(x1, y1, x2, y2);
		triangle(
				x1 + toHeadTop.x, y1 + toHeadTop.y,
				x1 + toHeadBaseRight.x, y1 + toHeadBaseRight.y,
				x1 + toHeadBaseLeft.x, y1 + toHeadBaseLeft.y
		);
	}

	private void drawNode(String name, float x, float y) {
		fill(255, 255, 255);
		stroke(100, 100, 100);
		strokeWeight(3);
		ellipseMode(CENTER);
		ellipse(x, y, 30, 30);

		final float textSize = 25;
		fill(0, 0, 0);
		textAlign(CENTER);
		textSize(textSize);
		text(name, x, y + textSize / 3);
	}

	private void drawEdge(float x1, float y1, float x2, float y2) {
		stroke(100, 100, 100);
		strokeWeight(3);
		line(x1, y1, x2, y2);
	}

	private void drawEdgeLabel(double weight, float x1, float y1, float x2, float y2) {
		final float xMid = (x1 + x2) / 2;
		final float yMid = (y1 + y2) / 2;
		fill(255, 255, 255);
		textAlign(CENTER);
		textSize(15);
		text(Double.toString(weight), xMid, yMid);
	}

	private void drawPhysicsGraph(@NotNull PhysicsGraph<PhysicsNodeData, ?> graph) {
		final List<? extends GraphEdge<PhysicsNodeData, ?>> edges = graph.getEdges();
		final Set<GraphNode<PhysicsNodeData>> nodes = graph.getNodes();

		// Edges
		for (GraphEdge<PhysicsNodeData, ?> edge : edges) {
			final PhysicsNodeData from = edge.nodes.getValue0().getData();
			final PhysicsNodeData to = edge.nodes.getValue1().getData();
			if (from == null || to == null) continue;
			drawEdge(from.getPosition().x, from.getPosition().y, to.getPosition().x, to.getPosition().y);
		}

		// Nodes
		for (GraphNode<PhysicsNodeData> node : nodes) {
			final PhysicsNodeData nodeData = node.getData();
			if (nodeData == null) continue;
			drawNode(node.name, nodeData.getPosition().x, nodeData.getPosition().y);
		}

		// Labels
		for (GraphEdge<PhysicsNodeData, ?> edge : edges) {
			final PhysicsNodeData from = edge.nodes.getValue0().getData();
			final PhysicsNodeData to = edge.nodes.getValue1().getData();
			if (from == null || to == null) continue;
			drawEdgeLabel(edge.weight, from.getPosition().x, from.getPosition().y, to.getPosition().x, to.getPosition().y);
		}
	}
}
