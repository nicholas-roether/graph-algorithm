package io.github.nicholas_roether.graph;


public class UnknownGraphNodeException extends Exception {
	public UnknownGraphNodeException(GraphNode node) {
		super("Node with id '" + node.id + "' is unknown.");
	}

	public UnknownGraphNodeException(String id) {
		super("Node with id '" + id + "' is unknown.");
	}
}
