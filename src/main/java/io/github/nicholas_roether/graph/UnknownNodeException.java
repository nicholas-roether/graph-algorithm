package io.github.nicholas_roether.graph;

import org.jetbrains.annotations.NotNull;

/**
 * This exception indicates that a node that isn't contained in a graph was passed to
 * one of that graph's methods. This often occurs in one of the following cases:
 * <ul>
 *     <li>
 *         You have tried to add an edge to the graph that connects to a node that isn't
 *         contained in the graph.
 *     </li>
 *     <li>
 *         You have tried to get the neighbors of a node within a graph that that node isn't
 *         contained in.
 *     </li>
 * </ul>
 */
public class UnknownNodeException extends IllegalArgumentException {
	public UnknownNodeException(GraphNode<?> node) {
		super("This node isn't contained in the graph: " + node);
	}
}
