package io.github.nicholas_roether.graph;

/**
 * An edge that connects a node to it's neighbor. The edge can also have
 * a weight. {@code GraphEdge}-objects always represent a one-directional
 * link between their parent node and a neighboring node, though in a
 * non-directed graph the neighboring node will always contain an edge
 * in the opposite direction with the same weight.
 * {@code GraphEdge}-Objects are immutable.
 *
 * @see GraphNode
 * @see Graph
 */
public class GraphEdge<N extends GraphNode> {
	/**
	 * The neighboring node this edge points to.
	 *
	 * @see GraphNode
	 */
	public final N neighbor;

	/**
	 * The weight of this edge. If no weight is provided in the constructor,
	 * this field will be initialized to {@code 1}.
	 */
	public final double weight;

	/**
	 * Constructs a new edge. The weight of this edge will be initialized
	 * to {@code 1}.
	 *
	 * @param neighbor The neighbor this edge points to
	 */
	public GraphEdge(N neighbor) {
		this.neighbor = neighbor;
		this.weight = 1;
	}

	/**
	 * Constructs a new edge.
	 *
	 * @param neighbor The neighbor this edge points to
	 * @param weight The weight of this edge
	 */
	public GraphEdge(N neighbor, double weight) {
		this.neighbor = neighbor;
		this.weight = weight;
	}
}
