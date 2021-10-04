package io.github.nicholas_roether.general;

import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNode;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

public class GraphWithData extends Graph<NodeData, Object> {
	public GraphNode<NodeData> addNode(@NotNull String name, float x ,float y) {
		final GraphNode<NodeData> node = addNode(name, new NodeData());
		node.data.setPosition(new PVector(x, y));
		return node;
	}
}
