package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.bounded.BoundedComponent;
import io.github.nicholas_roether.general.EdgeData;
import io.github.nicholas_roether.general.GraphWithData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.List;
import java.util.function.BiFunction;

public class GraphElementDeleter extends BoundedComponent {
	public static final int Z_INDEX = 100;
	private static final float EDGE_HOVER_THICKNESS = 15;

	public final GraphWithData graph;

	private final BiFunction<Float, Float, Boolean> bounds;
	private final List<String> undeletableNodes;
	private boolean enabled;
	private GraphNode<NodeData> hoveredNode;
	private GraphEdge<NodeData, EdgeData> hoveredEdge;

	public GraphElementDeleter(GraphWithData graph, BiFunction<Float, Float, Boolean> bounds, List<String> undeletableNodes) {
		super(Z_INDEX);
		this.graph = graph;
		this.bounds = bounds;
		this.undeletableNodes = undeletableNodes;
	}

	@Override
	public void frame() {
		if (!enabled) return;
		hoveredNode = null;
		hoveredEdge = null;
		for (GraphNode<NodeData> node : graph.getNodes()) {
			if (undeletableNodes.contains(node.name)) continue;
			final float distance = node.data.getPosition().dist(new PVector(p.mouseX, p.mouseY));
			if (distance <= NodeComponent.NODE_RADIUS) {
				hoveredNode = node;
				break;
			}
		}
		if (hoveredNode != null) return;
		for (GraphEdge<NodeData, EdgeData> edge : graph.getEdges()) {
			float distance = distanceToLine(
					new PVector(p.mouseX, p.mouseY),
					edge.nodes.getValue0().data.getPosition().copy(),
					edge.nodes.getValue1().data.getPosition().copy()
			);
			if (distance <= EDGE_HOVER_THICKNESS) {
				hoveredEdge = edge;
				break;
			}
		}
	}

	@Override
	public void draw() {
		if (!enabled) return;
		if (hoveredNode != null) {
			p.fill(0x60FF0000);
			p.circle(
					hoveredNode.data.getPosition().x,
					hoveredNode.data.getPosition().y,
					NodeComponent.NODE_RADIUS * 2.4f
			);
		} else if (hoveredEdge != null) {
			p.stroke(0x60FF0000);
			p.strokeWeight(EDGE_HOVER_THICKNESS);
			p.line(
					hoveredEdge.nodes.getValue0().data.getPosition().x,
					hoveredEdge.nodes.getValue0().data.getPosition().y,
					hoveredEdge.nodes.getValue1().data.getPosition().x,
					hoveredEdge.nodes.getValue1().data.getPosition().y
			);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	protected void mousePressedInBounds(MouseEvent event) {
		if (!enabled || event.getButton() != LEFT) return;
		if (hoveredNode != null) {
			graph.removeNode(hoveredNode);
		} else if (hoveredEdge != null) {
			graph.removeEdge(hoveredEdge);
		}
	}

	@Override
	protected int instructCursorInBounds() {
		if (enabled && (hoveredNode != null || hoveredEdge != null)) return HAND;
		return super.instructCursorInBounds();
	}

	@Override
	public boolean checkInBounds(float x, float y) {
		return bounds.apply(x, y);
	}

	private static float distanceToLine(PVector point, PVector lineStart, PVector lineEnd) {
		final PVector lineVector = lineEnd.copy().sub(lineStart);
		final PVector startToPoint = point.copy().sub(lineStart);

		final float unboundedK = startToPoint.dot(lineVector) / lineVector.magSq();
		final float k = Math.min(Math.max(unboundedK, 0), 1);

		final PVector closestPointOnLine = lineStart.copy().add(lineVector.copy().mult(k));
		return point.dist(closestPointOnLine);
	}
}
