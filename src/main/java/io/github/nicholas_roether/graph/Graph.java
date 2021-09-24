package io.github.nicholas_roether.graph;

import java.util.*;
import java.util.function.Function;

/**
 * A graph. Graphs will by default be non-directional, meaning edges will always point in both directions.
 * It is possible to specify your own class to serve as nodes, however this class has to extend GraphNode.
 *
 * @param <N> Which class should be used as nodes
 */
public class Graph<N extends GraphNode> {
	/**
	 * Whether or not this graph is directional, meaning whether or not edges should distinguish in
	 * which direction they're pointing
	 */
	public final boolean directional;

	private Set<N> nodes;

	/**
	 * Constructs a non-directional, empty graph.
	 */
	public Graph() {
		this.nodes = new HashSet<>();
		this.directional = false;
	}

	/**
	 * Constructs an empty graph.
	 *
	 * @param directional whether this graph is directional
	 */
	public Graph(boolean directional) {
		this.nodes = new HashSet<>();
		this.directional = directional;
	}

	/**
	 * Constructs a non-directional graph filled with the provided nodes.
	 * For convenience, in most cases it is recommended to construct an empty graph instead and
	 * fill it with {@code Graph.fillWithNodes}.
	 *
	 * @param nodes The nodes to fill this graph with
	 * @throws UnknownGraphNodeException if a node has an unregistered node registered as a connection.
	 *
	 * @see GraphNode
	 */
	public Graph(Set<N> nodes) throws UnknownGraphNodeException {
		this.nodes = new HashSet<>(nodes.size());
		for (N node : nodes) addNode(node);
		this.directional = false;
	}

	/**
	 * Constructs a graph filled with the provided nodes.
	 * For convenience, in most cases it is recommended to construct an empty graph instead and
	 * fill it with {@code Graph.fillWithNodes}.
	 *
	 * @param nodes The nodes to fill this graph with
	 * @param directional whether this graph is directional
	 * @throws UnknownGraphNodeException if a node has an unregistered node registered as a connection.
	 *
	 * @see GraphNode
	 */
	public Graph(Set<N> nodes, boolean directional) throws UnknownGraphNodeException {
		this.nodes = new HashSet<>(nodes.size());
		for (N node : nodes) addNode(node);
		this.directional = directional;
	}

	/**
	 * Adds a node to this graph, provided the graph doesn't already contain it.
	 * Note that nodes are considered equal if their ids are equal.
	 *
	 * @param node The node to add
	 * @return true if the node was added, false if this graph already contained the node
	 * @throws UnknownGraphNodeException if a node has an unregistered node registered as a connection.
	 *
	 * @see GraphNode
	 */
	public boolean addNode(N node) throws UnknownGraphNodeException {
		for (GraphNode neighbor : node.getNeighbors()) {
			assertKnownNode(neighbor);
		}
		return nodes.add(node);
	}

	/**
	 * Checks whether this graph contains a node.
	 * Note that nodes are considered equal if their ids are equal.
	 *
	 * @param node The node for which to check
	 * @return whether this graph contains the node
	 *
	 * @see GraphNode
	 */
	public boolean hasNode(N node) {
		return nodes.contains(node);
	}

	/**
	 * Returns a set of all nodes in this graph.
	 *
	 * @return a set of all nodes in this graph.
	 */
	public Set<N> getNodes() {
		final Set<N> immutableSet = Collections.unmodifiableSet(nodes);
		return immutableSet;
	}

	/**
	 * Gets a node out of this graph by id.
	 *
	 * @param id The id of the node to find
	 * @return the node with the given id
	 * @throws UnknownGraphNodeException if the graph doesn't contain a node with the given id
	 *
	 * @see GraphNode
	 */
	public N getNode(String id) throws UnknownGraphNodeException {
		for (N node : nodes) {
			if (node.id == id) return node;
		}
		throw new UnknownGraphNodeException(id);
	}

	/**
	 * Gets a set of all the edges of this graph.
	 * The edges will be returned in form of {@code IndependentEdge}-objects - this is not
	 * the same as the {@code GraphEdge}-objects contained by nodes, as {@code IndependentEdge}s
	 * contain both the node they originate from as well as the node they go to.
	 *
	 * @return a set of this graph's nodes
	 *
	 * @see IndependentEdge
	 */
	public Set<IndependentEdge<N>> getEdges() {
		final Set<IndependentEdge<N>> edges = new HashSet<>();
		for (N node : getNodes()) {
			for (GraphEdge edge : node.getEdges()) {
				final IndependentEdge independentEdge =
						new IndependentEdge(node, edge.neighbor, edge.weight, directional);
				edges.add(independentEdge);
			}
		}
		return edges;
	}

	/**
	 * Adds an edge to this graph. On a non-directional graph, the order
	 * of the {@code to} and {@code from} parameters does not matter.
	 *
	 * @param from The node the edge originates from
	 * @param to The node the edge goes to
	 * @param weight The weight of this edge
	 * @throws UnknownGraphNodeException If nodes are to be connected that aren't contained in this graph
	 *
	 * @see GraphNode
	 */
	@SuppressWarnings("unchecked")
	public void addEdge(N from, N to, double weight) throws UnknownGraphNodeException {
		try {
			from.addEdge(new GraphEdge(to, weight), (Graph<GraphNode>) this);
			if (!directional)
				to.addEdge(new GraphEdge(from, weight), (Graph<GraphNode>) this);
		} catch (GraphHierarchyException e) {
			System.out.println("Warning: Unexpected GraphHierarchyException (" + e.getMessage() + ")");
		}
	}

	/**
	 * Adds an edge with zero weight to the graph. On a non-directional graph, the order
	 * of the {@code to} and {@code from} parameters does not matter.
	 *
	 * @param from The node the edge originates from
	 * @param to The node the edge goes to
	 * @throws UnknownGraphNodeException If nodes are to be connected that aren't contained in this graph
	 */
	public void addEdge(N from, N to) throws UnknownGraphNodeException {
		addEdge(from, to, 0);
	}

	/**
	 * Removes the edge between two nodes if it exists. If there are multiple edges between the nodes,
	 * all of them will be removed. On a directional graph, only edges that go in the specified direction
	 * will be removed.
	 *
	 * @param from The node the edge to remove originates from
	 * @param to The node the edge to remove goes to
	 */
	@SuppressWarnings("unchecked")
	public void removeEdge(N from, N to) {
		try {
			for (GraphEdge edge : from.getEdges())
				if (edge.neighbor == to) from.removeEdge(edge, (Graph<GraphNode>) this);
			if (!directional) {
				for (GraphEdge edge : to.getEdges())
					if (edge.neighbor == from) to.removeEdge(edge, (Graph<GraphNode>) this);
			}
		} catch (GraphHierarchyException e) {
			System.out.println("Warning: Unexpected GraphHierarchyException! (" + e.getMessage() + ")");
		}
	}

	/**
	 * Disconnects a node from the network by removing all it's edges. On a directional graph, only edges that
	 * originate from this node will be removed.
	 *
	 * @param node The node to disconnect
	 */
	@SuppressWarnings("unchecked")
	public void disconnect(N node) {
		try {
			node.disconnect((Graph<GraphNode>) this);
			if (!directional) {
				for (GraphNode neighbor : node.getNeighbors()) {
					for (GraphEdge neighborEdge : neighbor.getEdges()) {
						if (neighborEdge.neighbor == node)
							neighbor.removeEdge(neighborEdge, (Graph<GraphNode>) this);
					}
				}
			}
		} catch (GraphHierarchyException e) {
			System.out.println("Warning: Unexpected GraphHierarchyException! (" + e.getMessage() + ")");
		}
	}

	/**
	 * Fills the graph with nodes (and edges).
	 *
	 * @param nodes The ids of the nodes to add
	 * @param edges The edges to add, in form of EdgeInit-Objects
	 *
	 * @see EdgeInit
	 * @see GraphNode
	 */
	public void fillWithNodes(
			Set<N> nodes,
			List<Graph.EdgeInit> edges
	) {
		try {
			for (N node : nodes) {
				if (node.getEdges().size() != 0)
					throw new IllegalArgumentException("Only unconnected nodes are allowed");
				this.addNode(node);
			}
		} catch (UnknownGraphNodeException e) {
			System.out.println("Warning: Unexpected UnknownGraphNodeException! (" + e.getMessage() + ")");
		}
		try {
			for (Graph.EdgeInit edge : edges) {
				N from = getNode(edge.from);
				N to = getNode(edge.to);
				addEdge(from, to, edge.weight);
			}
		} catch (UnknownGraphNodeException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void assertKnownNode(GraphNode node) throws UnknownGraphNodeException {
		if (!hasNode((N) node)) throw new UnknownGraphNodeException(node);
	}

	/**
	 * Contains data for the graph to create an edge between two nodes.
	 */
	public static class EdgeInit {
		/**
		 * The node the edge should originate from.
		 */
		public final String from;

		/**
		 * The node the edge should go to
		 */
		public final String to;

		/**
		 * The weight the edge should have
		 */
		public final double weight;

		/**
		 * Creates an {@code EdgeInit}-object.
		 *
		 * @param from The node the edge should originate from
		 * @param to The node the edge should go to
		 * @param weight The weight the edge should have
		 */
		public EdgeInit(String from, String to, double weight) {
			this.from = from;
			this.to = to;
			this.weight = weight;
		}

		/**
		 * Creates an {@code EdgeInit}-object for an edge with 0 weight.
		 *
		 * @param from The node the edge should originate from
		 * @param to The node the edge should go to
		 */
		public EdgeInit(String from, String to) {
			this.from = from;
			this.to = to;
			this.weight = 0;
		}
	}

	/**
	 * Represents an edge in the graph, independently of any graph node. It thereby contains
	 * the information on both nodes it is connected to.
	 * <br>
	 * Note that two {@code IndependentEdge}-objects are considered equal if they have the same
	 * weight and they are connected to the same nodes; additionally, if the graph this edge belongs to
	 * is directional, the edges need to have the same starting and ending node to be considered equal.
	 * <br>
	 * Note also that nodes are considered equal if their ids are equal.
	 *
	 *
	 * @param <N> The type of node used
	 *
	 * @see Graph
	 */
	public static class IndependentEdge<N extends GraphNode> {
		/**
		 * The node this edge originates from.
		 */
		public final N from;

		/**
		 * The node this edge goes to.
		 */
		public final N to;

		/**
		 * The weight of this node.
		 */
		public final double weight;

		private final boolean directional;

		private IndependentEdge(N from, N to, double weight, boolean directional) {
			this.from = from;
			this.to = to;
			this.weight = weight;
			this.directional = directional;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			IndependentEdge<?> that = (IndependentEdge<?>) o;
			return Double.compare(that.weight, weight) == 0 && (
					from.equals(that.from) && to.equals(that.to)
							|| (!directional && to.equals(that.from) && from.equals(that.to))
			);
		}

		@Override
		public int hashCode() {
			if (!directional) {
				final Set<N> nodeSet = Set.of(from, to);
				return Objects.hash(nodeSet, weight);
			}
			return Objects.hash(from, to, weight);
		}
	}
}
