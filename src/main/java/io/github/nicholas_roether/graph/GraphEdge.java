package io.github.nicholas_roether.graph;

import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An edge in a graph.
 * <br>
 * Edges connect two nodes, and have a numeric weight and optionally custom data
 * associated with them. If no values are provided, the weight will be set to {@code 1} and the
 * data to {@code null}.
 *
 * @param <ND> The node data type of the node the edge connects
 * @param <D> The type of the custom data of the edge
 */
public class GraphEdge<ND, D> {
	/**
	 * The nodes this edge connects.
	 *
	 * @see Pair
	 */
	public final Pair<GraphNode<ND>, GraphNode<ND>> nodes;

	/**
	 * The weight of this edge. Set to {@code 1} by default.
	 */
	public final double weight;

	/**
	 * The custom data associated to this edge. Set to {@code null} by default.
	 */
	public final D data;

	/**
	 * Creates an edge between the two provided nodes, with the given weight and data.
	 *
	 * @param node1 The first of the nodes the edge connects
	 * @param node2 The second of the nodes the edge connects
	 * @param weight The weight of the edge
	 * @param data The custom data associated with the edge
	 */
	public GraphEdge(@NotNull GraphNode<ND> node1, @NotNull GraphNode<ND> node2, double weight, D data) {
		nodes = Pair.with(node1, node2);
		this.weight = weight;
		this.data = data;
	}

	/**
	 * Creates a string for this edge representation that can, for example, be printed to the console.
	 *
	 * @return The string representation of this edge.
	 */
	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder("GraphEdge (");
		strBuilder.append(nodes.getValue0().name);
		strBuilder.append(" -> ");
		strBuilder.append(nodes.getValue1().name);
		strBuilder.append(")");
		if (data != null) {
			strBuilder.append(" {\n   ");
			strBuilder.append(data);
			strBuilder.append("\n}");
		}
		return strBuilder.toString();
	}

	/**
	 * Checks if the edge equals the given object. For objects that aren't edges, this
	 * will always return {@code false}.
	 * <br>
	 * Two edges are considered equal if the nodes they connect, their weight and their data are equal.
	 *
	 * @param o the object to compare the edge to
	 * @return whether the edge and the object are considered equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GraphEdge<?, ?> graphEdge = (GraphEdge<?, ?>) o;
		return Double.compare(graphEdge.weight, weight) == 0 && Objects.equals(nodes, graphEdge.nodes) && Objects.equals(data, graphEdge.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodes, weight, data);
	}
}
