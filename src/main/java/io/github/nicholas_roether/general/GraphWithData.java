package io.github.nicholas_roether.general;

import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

public class GraphWithData extends Graph<NodeData, EdgeData> {
	@Override
	public GraphNode<NodeData> addNode(@NotNull String name) {
		return addNode(name, 0, 0);
	}

	public GraphNode<NodeData> addNode(@NotNull String name, float x , float y) {
		final GraphNode<NodeData> node = addNode(name, new NodeData());
		if (node == null) return null;
		node.data.setPosition(new PVector(x, y));
		return node;
	}

	@Override
	public GraphEdge<NodeData, EdgeData> addEdge(
			@NotNull GraphNode<NodeData> node1,
			@NotNull GraphNode<NodeData> node2,
			double weight
	) {
		return addEdge(node1, node2, weight, new EdgeData());
	}
}
