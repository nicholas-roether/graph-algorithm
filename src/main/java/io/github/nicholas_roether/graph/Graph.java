package io.github.nicholas_roether.graph;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A graph. Graphs consist of nodes, and edges that connect those nodes. Each edge has a weight
 * associated with it; if no weight is specified it defaults to {@code 1}. Each node also has a name.
 * <br>
 * It is possible to associate custom data with both nodes and edges, using their constructors and
 * type parameters.
 * <br>
 * Graphs are considered equal if all their nodes and edges are equal.
 *
 * @param <ND> The node data type
 * @param <ED> The edge data type
 *
 * @see GraphNode
 * @see GraphEdge
 */
public class Graph<ND, ED> {
	private final Set<GraphNode<ND>> nodes;
	private final List<GraphEdge<ND, ED>> edges;

	/**
	 * Constructs an empty graph.
	 */
	public Graph() {
		this.nodes = new HashSet<>();
		this.edges = new ArrayList<>();
	}

	/**
	 * Adds a new node with the given name and value and returns it, if that node isn't already contained.
	 * <br>
	 * Note that nodes are considered equal if they have the same name, so each node in the graph
	 * needs a unique name.
	 *
	 * @param name The name of the new node
	 * @param value The value of the new node
	 * @return the created node if it was actually added, otherwise {@code null}.
	 *
	 * @see GraphNode
	 */
	public GraphNode<ND> addNode(@NotNull String name, ND value) {
		final GraphNode<ND> node = new GraphNode<>(name, value);
		if (addNode(node)) return node;
		return null;
	}

	/**
	 * Adds a new node with the given name and returns it, if that node isn't already contained.
	 * It will set the node's value to {@code null}.
	 * <br>
	 * Note that nodes are considered equal if they have the same name, so each node in the graph
	 * needs a unique name.
	 *
	 * @param name The name of the new node
	 * @return the created node if it was actually added, otherwise {@code null}.
	 *
	 * @see GraphNode
	 */
	public GraphNode<ND> addNode(@NotNull String name) {
		return addNode(name, null);
	}

	/**
	 * Adds the given node to the graph, if that node isn't already contained.
	 * <br>
	 * Note that nodes are considered equal if they have the same name, so each node in the graph
	 * needs a unique name.
	 *
	 * @param node The node to add
	 * @return true if the node was actually added to the graph
	 *
	 * @see GraphNode
	 */
	public boolean addNode(@NotNull GraphNode<ND> node) {
		return nodes.add(node);
	}

	/**
	 * Adds all nodes from the given collection that aren't already contained.
	 * <br>
	 * Note that nodes are considered equal if they have the same name, so each node in the graph
	 * needs a unique name.
	 *
	 * @param nodes The nodes to add
	 * @return {@code true} if any nodes were added to the graph
	 *
	 * @see GraphNode
	 */
	public boolean addNodes(@NotNull Collection<? extends GraphNode<ND>> nodes) {
		boolean ret = false;
		for(GraphNode<ND> node : nodes) {
			if (addNode(node)) ret = true;
		}
		return ret;
	}

	/**
	 * Gets a node out of the graph by name.
	 *
	 * @param name The name to search for
	 * @return The node, or {@code null} if none is found
	 *
	 * @see GraphNode
	 */
	public GraphNode<ND> getNode(@NotNull String name) {
		for (GraphNode<ND> node : nodes) {
			if (node.name.equals(name)) return node;
		}
		return null;
	}

	/**
	 * Returns all nodes in this graph.
	 *
	 * @return an immutable set of all nodes in this graph
	 */
	public Set<GraphNode<ND>> getNodes() {
		return Collections.unmodifiableSet(nodes);
	}

	/**
	 * Removes the given node from the graph if the graph contains it.
	 *
	 * @param node The node to remove
	 * @return {@code true} if the node was actually contained and has been removed
	 *
	 * @see GraphNode
	 */
	public boolean removeNode(@NotNull GraphNode<ND> node) {
		return nodes.remove(node);
	}

	/**
	 * Adds a new edge between the two given nodes - with the given weight and data - to the graph,
	 * and returns it.
	 *
	 * @param node1 The first of the nodes the edge connects
	 * @param node2 The second of the nodes the edge connects
	 * @param weight The weight of the edge
	 * @param data The data of the edge
	 * @return the created edge
	 *
	 * @see GraphEdge
	 * @see GraphNode
	 */
	public GraphEdge<ND, ED> addEdge(@NotNull GraphNode<ND> node1, @NotNull GraphNode<ND> node2, double weight, ED data) {
		final GraphEdge<ND, ED> edge = new GraphEdge<>(node1, node2, weight, data);
		if (this.addEdge(edge)) return edge;
		return null;
	}

	/**
	 * Adds a new edge between the two given nodes - with the given weight - to the graph,
	 * and returns it.
	 * <br>
	 * The data of the edge will be set to {@code null}.
	 *
	 * @param node1 The first of the nodes the edge connects
	 * @param node2 The second of the nodes the edge connects
	 * @param weight The weight of the edge
	 * @return the created edge
	 *
	 * @see GraphEdge
	 * @see GraphNode
	 */
	public GraphEdge<ND, ED> addEdge(@NotNull GraphNode<ND> node1, @NotNull GraphNode<ND> node2, double weight) {
		return addEdge(node1, node2, weight, null);
	}

	/**
	 * Adds a new edge between the two given nodes to the graph and returns it.
	 * <br>
	 * The weight of the edge will be set to {@code 1}, and it's data to {@code null}.
	 *
	 * @param node1 The first of the nodes the edge connects
	 * @param node2 The second of the nodes the edge connects
	 * @return the created edge
	 *
	 * @see GraphEdge
	 * @see GraphNode
	 */
	public GraphEdge<ND, ED> addEdge(@NotNull GraphNode<ND> node1, @NotNull GraphNode<ND> node2) {
		return addEdge(node1, node2, 1);
	}

	/**
	 * Adds the given edge to the graph.
	 *
	 * @param edge The edge to add
	 * @return {@code true}
	 * @throws UnknownNodeException if the edge attempts to connect to a node that isn't contained in the graph
	 *
	 * @see GraphEdge
	 */
	public boolean addEdge(GraphEdge<ND, ED> edge) {
		assertKnownNode(edge.nodes.getValue0());
		assertKnownNode(edge.nodes.getValue1());
		return this.edges.add(edge);
	}

	/**
	 * Adds all the given edges to the graph.
	 *
	 * @param edges The edge to add.
	 * @return {@code true}
	 * @throws UnknownNodeException if an edge attempts to connect to a node that isn't contained in the graph
	 *
	 * @see GraphEdge
	 */
	public boolean addEdges(@NotNull Collection<? extends GraphEdge<ND, ED>> edges) {
		boolean ret = false;
		for(GraphEdge<ND, ED> edge : edges) {
			if (addEdge(edge)) ret = true;
		}
		return ret;
	}

	/**
	 * Removes the given edge from the graph, given it is a part of it.
	 *
	 * @param edge The edge to remove
	 * @return {@code true} if the edge was actually a part of the graph and was removed
	 *
	 * @see GraphEdge
	 */
	public boolean removeEdge(@NotNull GraphEdge<ND, ED> edge) {
		return edges.remove(edge);
	}

	/**
	 * Returns all edges in the graph.
	 *
	 * @return an immutable list of all edges in the graph.
	 */
	public List<GraphEdge<ND, ED>> getEdges() {
		return Collections.unmodifiableList(edges);
	}

	/**
	 * Gets the neighbors of a node within the graph.
	 *
	 * @param node The node whose neighbors to get.
	 * @return The neighbors of the node, as {@code GraphNeighbor}-objects.
	 * @throws UnknownNodeException if the given node isn't contained in the graph.
	 *
	 * @see GraphNode
	 */
	public List<GraphNeighbor<ND, ED>> getNeighbors(@NotNull GraphNode<ND> node) {
		assertKnownNode(node);
		final List<GraphNeighbor<ND, ED>> neighbors = new ArrayList<>();
		for (GraphEdge<ND, ED> edge : edges) {
			GraphNode<ND> neighbor = null;
			if (edge.nodes.getValue0().equals(node)) {
				neighbor = edge.nodes.getValue1();
			} else if (edge.nodes.getValue1().equals(node)) {
				neighbor = edge.nodes.getValue0();
			}
			if (neighbor != null)
					neighbors.add(new GraphNeighbor<>(neighbor, edge.weight, edge.data()));
		}
		return neighbors;
	}

	/**
	 * Checks if the two given nodes are connected by an edge.
	 *
	 * @param node1 The first of the nodes to check
	 * @param node2 The second of the nodes to check
	 * @return {@code true} if the nodes are connected
	 */
	public boolean areConnected(GraphNode<ND> node1, GraphNode<ND> node2) {
		for (GraphNeighbor<ND, ED> neighbor : getNeighbors(node1)) {
			if (neighbor.node == node2) return true;
		}
		return false;
	}

	/**
	 * Checks if the graph equals the given object. For objects that aren't graphs, this
	 * will always return {@code false}.
	 * <br>
	 * Two graphs are considered equal if all their nodes and edges are equal.
	 *
	 * @param o the object to compare the graph to
	 * @return whether the graph and the object are considered equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Graph<?, ?> graph = (Graph<?, ?>) o;
		return Objects.equals(nodes, graph.nodes) && Objects.equals(edges, graph.edges);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodes, edges);
	}

	/**
	 * Creates a string for this graph representation that can, for example, be printed to the console.
	 *
	 * @return The string representation of this graph.
	 */
	@Override
	public String toString() {
		// TODO somehow visualize graphs in the console
		return super.toString();
	}

	private void assertKnownNode(GraphNode<ND> node) {
		if (node == null || !nodes.contains(node)) {
			throw new UnknownNodeException(node);
		}
	}
}
