package io.github.nicholas_roether.general;

import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.Arrays;
import java.util.List;

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

	public static GraphWithData fromJSON(JSONObject json) {
		final JSONArray nodesArr = json.getJSONArray("nodes");
		final JSONArray edgesArr = json.getJSONArray("edges");

		final GraphWithData graph = new GraphWithData();
		for (int i = 0; i < nodesArr.size(); i++) {
			addNodeFromJSON(graph, nodesArr.getJSONObject(i));
		}
		for (int i = 0; i < edgesArr.size(); i++) {
			addEdgeFromJSON(graph, edgesArr.getJSONObject(i));
		}
		return graph;
	}

	private static void addNodeFromJSON(GraphWithData graph, JSONObject json) {
		final String name = json.getString("name");
		final NodeData nodeData = NodeData.fromJSON(json.getJSONObject("data"));

		graph.addNode(name, nodeData);
	}

	private static void addEdgeFromJSON(GraphWithData graph, JSONObject json) {
		final String[] nodeNames = json.getJSONArray("nodes").getStringArray();
		final double weight = json.getDouble("weight", 1.0);
		final EdgeData data = EdgeData.fromJSON(json.getJSONObject("data"));

		graph.addEdge(graph.getNode(nodeNames[0]), graph.getNode(nodeNames[1]), weight, data);
	}
}
