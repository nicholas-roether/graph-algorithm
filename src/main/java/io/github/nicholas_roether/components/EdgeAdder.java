package io.github.nicholas_roether.components;

import io.github.nicholas_roether.components.common.Dialog;
import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.bounded.BoundedComponent;
import io.github.nicholas_roether.general.GraphWithData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.GraphNode;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.List;
import java.util.function.BiFunction;

public class EdgeAdder extends BoundedComponent {
	public static final int Z_INDEX = 100;

	public final GraphWithData graph;

	private final BiFunction<Float, Float, Boolean> bounds;
	private GraphNode<NodeData> hoveredNode;
	private GraphNode<NodeData> linkedNode;
	private boolean enabled = false;

	private Dialog weightDialog;


	public EdgeAdder(GraphWithData graph, BiFunction<Float, Float, Boolean> bounds) {
		super(Z_INDEX);
		this.graph = graph;
		this.bounds = bounds;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void build(ComponentRegistry registry, Document p) {
		weightDialog = new Dialog(
				"Enter the Edge's Weight",
				"^[0-9]+(?:\\.[0-9]*)?$",
				"^(?:[1-9]+)|(?:[0-9]\\.[0-9]+)$"
		);
		final Component edgePreview = new Component(EdgeComponent.Z_INDEX) {
			@Override
			public void draw(@NotNull Document p) {
				if (linkedNode == null) return;
				p.stroke(100);
				p.strokeWeight(3);
				p.line(linkedNode.data.getPosition().x, linkedNode.data.getPosition().y, p.mouseX, p.mouseY);
			}
		};
		registry.register(List.of(edgePreview, weightDialog), id);
	}

	@Override
	public void frame(Document p) {
		hoveredNode = null;
		for (GraphNode<NodeData> node : graph.getNodes()) {
			if (linkedNode != null) {
				if (node == linkedNode) continue;
				if(graph.areConnected(node, linkedNode)) continue;
			}
			final float distance = node.data.getPosition().dist(new PVector(p.mouseX, p.mouseY));
			if (distance <= NodeComponent.NODE_RADIUS) {
				hoveredNode = node;
				break;
			}
		}
	}

	@Override
	public void draw(@NotNull Document p) {
		if (!enabled || hoveredNode == null || p.popupManager.hasPopup()) return;
		p.fill(0x6011A032);
		p.circle(hoveredNode.data.getPosition().x, hoveredNode.data.getPosition().y, NodeComponent.NODE_RADIUS * 2.4f);
	}

	@Override
	protected int instructCursorInBounds() {
		if (enabled && hoveredNode != null) return HAND;
		return super.instructCursorInBounds();
	}

	@Override
	protected void mousePressedInBounds(MouseEvent event) {
		if (!enabled || event.getButton() != LEFT || hoveredNode == null) return;
		if (linkedNode == null) {
			linkedNode = hoveredNode;
		} else {
			linkNodes(linkedNode, hoveredNode);
			linkedNode = null;
			hoveredNode = null;
		}
	}

	@Override
	protected void mousePressedAnywhere(MouseEvent event) {
		if (hoveredNode == null) linkedNode = null;
	}

	@Override
	public boolean checkInBounds(float x, float y) {
		return bounds.apply(x, y);
	}

	private void linkNodes(GraphNode<NodeData> node1, GraphNode<NodeData> node2) {
		weightDialog.prompt(weightString -> {
			final double weight = Double.parseDouble(weightString);
			graph.addEdge(node1, node2, weight);
		});
	}
}
