package io.github.nicholas_roether.graph;

import java.util.List;
import java.util.Set;

/**
 * A simple, non-directional graph with weighted edges and named nodes.
 */
public class NamedGraph extends Graph<GraphNode> {
	/**
	 * Creates an empty named graph.
	 */
	public NamedGraph() {
		super();
	}

	/**
	 * Creates a named graph, filled with the provided nodes.
	 * It is recommended to use {@code NamedGraph.fillWithNodes} on an empty named graph
	 * instead of this constructor in most circumstances.
	 *
	 * @param nodes The nodes to add
	 * @throws UnknownGraphNodeException if a node references an unregistered node as a neighbor
	 */
	public NamedGraph(Set<GraphNode> nodes) throws UnknownGraphNodeException {
		super(nodes);
	}
}
