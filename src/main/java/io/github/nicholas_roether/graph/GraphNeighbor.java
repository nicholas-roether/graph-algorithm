package io.github.nicholas_roether.graph;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a neighbor to a node in a graph. Contains the neighboring node
 * as well as information about the edge connecting them.
 *
 * @param <ND> The node data type of the neighboring node
 * @param <ED> The type of the custom data of the edge
 */
public class GraphNeighbor<ND, ED> {
	/**
	 * The neighboring node.
	 *
	 * @see GraphNode
	 */
	public final GraphNode<ND> node;

	/**
	 * The weight of the edge connecting to the neighbor.
	 */
	public final double edgeWeight;

	/**
	 * The custom data of the edge connecting to the neighbor.
	 */
	public final ED edgeData;

	/**
	 * Constructs a graph neighbor.
	 *
	 * @param node The neighboring node
	 * @param edgeWeight The weight of the connecting edge
	 * @param edgeData The custom data of the connecting edge
	 */
	public GraphNeighbor(@NotNull GraphNode<ND> node, double edgeWeight, ED edgeData) {
		this.node = node;
		this.edgeWeight = edgeWeight;
		this.edgeData = edgeData;
	}
}
