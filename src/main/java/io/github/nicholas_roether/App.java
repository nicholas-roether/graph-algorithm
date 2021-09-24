package io.github.nicholas_roether;

import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.physics_graph.PhysicsGraph;
import io.github.nicholas_roether.physics_graph.PhysicsGraphNode;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;
import java.util.Set;

public class App extends PApplet {
	private PhysicsGraph graph;

	public void run() {
		runSketch();
	}

	@Override
	public void settings() {
		size(800, 600);
		pixelDensity(displayDensity());
	}

	@Override
	public void setup() {
		graph = new PhysicsGraph(new PVector(width / 2f, height / 2f), 100);
		final PhysicsGraphNode nodeA = new PhysicsGraphNode("A", graph);
		final PhysicsGraphNode nodeB = new PhysicsGraphNode("B", graph);
		final PhysicsGraphNode nodeC = new PhysicsGraphNode("C", graph);
		final PhysicsGraphNode nodeD = new PhysicsGraphNode("D", graph);
		final PhysicsGraphNode nodeE = new PhysicsGraphNode("E", graph);
		nodeA.setPosition(new PVector(width / 2f - 10, height / 2f));
		nodeB.setPosition(new PVector(width / 2f + 10, height / 2f));
		nodeC.setPosition(new PVector(width / 2f, height / 2f - 10));
		nodeD.setPosition(new PVector(width / 2f, height / 2f));
		nodeE.setPosition(new PVector(width / 2f - 30, height / 2f));
		graph.fillWithNodes(Set.of(
				nodeA,
				nodeB,
				nodeC,
				nodeD,
				nodeE
		), List.of(
				new Graph.EdgeInit("A", "B", 5),
				new Graph.EdgeInit("B", "C", 2),
				new Graph.EdgeInit("A", "C", -3),
				new Graph.EdgeInit("A", "D", 4),
				new Graph.EdgeInit("A", "E", 2),
				new Graph.EdgeInit("B", "E", 4)
		));

	}

	@Override
	public void draw() {
		background(0, 0, 0);

		graph.stepPhysicsEngine(1 / frameRate);
		drawPhysicsGraph(graph);
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

	private void drawEdge(double weight, float x1, float y1, float x2, float y2) {
		stroke(100, 100, 100);
		strokeWeight(3);
		line(x1, y1, x2, y2);

		final float xMid = (x1 + x2) / 2;
		final float yMid = (y1 + y2) / 2;
		fill(255, 255, 255);
		textAlign(CENTER);
		textSize(15);
		text(Double.toString(weight), xMid, yMid - 15);
	}

	private PVector getNodePos(int index, int numNodes) {
		final float radius = Math.min(width, height) / 3f;
		final double angle = 2 * Math.PI * ((double) index / (double) numNodes);
		final float x = radius * (float) Math.cos(angle) + width / 2f;
		final float y = radius * (float) Math.sin(angle) + height / 2f;
		return new PVector(x, y);
	}

	private void drawPhysicsGraph(PhysicsGraph graph) {
		final Set<Graph.IndependentEdge<PhysicsGraphNode>> edges = graph.getEdges();
		for (Graph.IndependentEdge<PhysicsGraphNode> edge : edges) {
			final PhysicsGraphNode from = edge.from;
			final PhysicsGraphNode to = edge.to;
			drawEdge(edge.weight, from.getPosition().x, from.getPosition().y, to.getPosition().x, to.getPosition().y);
		}
		final Set<PhysicsGraphNode> nodes = graph.getNodes();
		for (PhysicsGraphNode node : nodes) {
			drawNode(node.id, node.getPosition().x, node.getPosition().y);
		}
	}
}
