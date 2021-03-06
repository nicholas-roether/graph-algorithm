package io.github.nicholas_roether.graph;

import io.github.nicholas_roether.JSONSerializable;
import org.jetbrains.annotations.NotNull;
import processing.data.JSONObject;

import java.util.Objects;

/**
 * A node in a graph.
 * <br>
 * Nodes have a name, as well as optionally a value. The latter will default to {@code null}
 * if none is provided.
 * <br>
 * Two nodes are considered equal
 * if their names are equal.
 *
 * @param <D> The type of the node data.
 */
public class GraphNode<D extends JSONSerializable> implements JSONSerializable {
	/**
	 * The name of this node. Nodes are considered equal if their names are equal.
	 */
	public final String name;

	/**
	 * The value of this node. Will default to {@code null}.
	 */
	public final D data;


	/**
	 * Constructs a node with the given name and value.
	 *
	 * @param name The name of the node
	 * @param data The value of the node
	 */
	public GraphNode(@NotNull String name, D data) {
		this.name = name;
		this.data = data;
	}

	/**
	 * Creates a string for this node representation that can, for example, be printed to the console.
	 *
	 * @return The string representation of this node
	 */
	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder("GraphNode (");
		strBuilder.append(name);
		strBuilder.append(")");
		if (data != null) {
			strBuilder.append(" {\n   ");
			strBuilder.append(data);
			strBuilder.append("\n}");
		}
		return strBuilder.toString();
	}

	@Override
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("data", data.toJSON());
		return obj;
	}

	/**
	 * Checks if the node equals the given object. For objects that aren't nodes, this
	 * will always return {@code false}.
	 * <br>
	 * Two nodes are considered equal if their names are equal.
	 *
	 * @param o the object to compare the edge to
	 * @return {@code true} if the node and the object are considered equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GraphNode<?> graphNode = (GraphNode<?>) o;
		return Objects.equals(name, graphNode.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
