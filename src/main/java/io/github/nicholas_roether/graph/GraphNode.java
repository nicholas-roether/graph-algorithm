package io.github.nicholas_roether.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A node within a graph. {@code GraphNode}s keep track of the edges going out from them, and each
 * one has a unique id. Two {@code GraphNode}s are considered equal if and only if their {@code id}s
 * are equal.
 *
 * @see GraphEdge
 * @see Graph
 */
public class GraphNode {
	/**
	 * A unique identifier for this node within the graph. Two {@code GraphNode}s
	 * are considered equal if and only if their {@code id}s are equal.
	 *
	 * @see Graph
	 */
	public final String id;
	private final List<GraphEdge> edges;

	/**
	 * Creates a new node without any connections.
	 *
	 * @param id A unique identifier for this node
	 */
	public GraphNode(String id) {
		this.id = id;
		this.edges = new ArrayList<>();
	}

	/**
	 * Creates a new node.
	 *
	 * @param id A unique identifier for this node
	 * @param edges The edges that go out from this node
	 */
	public GraphNode(String id, Collection<GraphEdge> edges) {
		this.id = id;
		this.edges = new ArrayList<>();
		this.edges.addAll(edges);
	}

	/**
	 * Returns an immutable set of all neighbors this node has.
	 *
	 * @return an immutable set of all neighbors this node has.
	 */
	public Set<GraphNode> getNeighbors() {
		final Set<GraphNode> neighbors = new HashSet<>(edges.size());
		for (GraphEdge edge : edges) {
			neighbors.add(edge.neighbor);
		}
		return neighbors;
	}

	/**
	 * Checks whether the given node is a neighbor to this node.
	 *
	 * @param other The node to check
	 * @return whether the node is a neighbor
	 */
	public boolean isNeighbor(GraphNode other) {
		for (GraphEdge edge : edges) {
			if (edge.neighbor == other) return true;
		}
		return false;
	}

	/**
	 * Returns the weight of the edge to the given node
	 * @param other The node to which the connection weight should be checked
	 * @return the weight of the connection
	 * @throws UnknownGraphNodeException if this node isn't connected to the given node
	 */
	public double getEdgeWeightTo(GraphNode other) throws UnknownGraphNodeException {
		for (GraphEdge edge : edges) {
			if (edge.neighbor == other) return edge.weight;
		}
		throw new UnknownGraphNodeException(other);
	}

	/**
	 * Add an edge to this node. This method should not be used directly; use {@code Graph.addEdge} instead.
	 *
	 * @see Graph
	 *
	 * @param edge The edge to add
	 * @param graph The graph this node is a part of
	 */
	public void addEdge(GraphEdge edge, Graph<GraphNode> graph) throws UnknownGraphNodeException, GraphHierarchyException {
		if (!graph.hasNode(this))
			throw new GraphHierarchyException("This node is not part of the provided graph");
		if (!graph.hasNode(edge.neighbor))
			throw new UnknownGraphNodeException(edge.neighbor);
		edges.add(edge);
	}

	/**
	 * Add an edge to this node. This method should not be used directly; use {@code Graph.removeEdge} instead.
	 *
	 * @param edge The edge to remove
	 * @param graph The graph this node is a part of
	 */
	public void removeEdge(GraphEdge edge, Graph<GraphNode> graph) throws GraphHierarchyException {
		if (!graph.hasNode(this))
			throw new GraphHierarchyException("This node is not part of the provided graph");
		edges.remove(edge);
	}

	/**
	 * Removes all edges from this node. This method should not be used directly; use {@code Graph.disconnect} instead.
	 *
	 * @param graph The graph this node is a part of
	 */
	public void disconnect(Graph<GraphNode> graph) throws GraphHierarchyException {
		if (!graph.hasNode(this))
			throw new GraphHierarchyException("This node is not part of the provided graph");
		this.edges.clear();
	}

	/**
	 * Returns the edges that go out from this node.
	 *
	 * @return the edges that go out from this node.
	 */
	public List<GraphEdge> getEdges() {
		return edges;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GraphNode) {
			GraphNode n = (GraphNode) obj;
			return n.id.equals(id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}
