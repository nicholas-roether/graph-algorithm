package io.github.nicholas_roether.graph;

/**
 * An edge that connects a node to it's neighbor. The edge can also have
 * a weight. {@code GraphEdge}-objects always represent a one-directional
 * link between their parent node an a neighboring node, though in a
 * non-directed graph the neighboring node will always contain an edge
 * in the opposite direction with the same weight.
 * {@code GraphEdge}-Objects are immutable.
 *
 * @see GraphNode
 * @see Graph
 */
public class GraphEdge {
	/**
	 * The neighboring node this edge points to.
	 *
	 * @see GraphNode
	 */
	public final GraphNode neighbor;

	/**
	 * The weight of this edge. If no weight is provided in the constructor,
	 * this field will be initialized to {@code 0}.
	 */
	public final double weight;

	/**
	 * Constructs a new edge. The weight of this edge will be initialized
	 * to {@code 0}.
	 *
	 * @param neighbor The neighbor this edge points to
	 */
	public GraphEdge(GraphNode neighbor) {
		this.neighbor = neighbor;
		this.weight = 0;
	}

	/**
	 * Constructs a new edge.
	 *
	 * @param neighbor The neighbor this edge points to
	 * @param weight The weight of this edge
	 */
	public GraphEdge(GraphNode neighbor, double weight) {
		this.neighbor = neighbor;
		this.weight = weight;
	}
}
