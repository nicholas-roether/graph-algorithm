package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.general.GraphWithData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.GraphNode;

import java.util.List;
import java.util.function.BiFunction;

public class MainView extends Component {
	private static final float INITIAL_NODE_INSET = 200f;

	private final GraphWithData graph = new GraphWithData();
	private State state = State.EDITING;

	private GraphComponent graphComponent;
	private Background background;
	private EditingButton editingButton;
	private EditActionSelector editActionSelector;
	private NodeAdder nodeAdder;
	private EdgeAdder edgeAdder;
	private GraphElementDeleter graphElementDeleter;

	private enum State {
		EDITING,
		SHOWING,
		RUNNING
	}

	@Override
	protected void init() {
		final GraphNode<NodeData> startNode = graph.addNode("A", INITIAL_NODE_INSET, p.height / 2f);
		final GraphNode<NodeData> endNode = graph.addNode("Z", p.width - INITIAL_NODE_INSET, p.height / 2f);
		final GraphNode<NodeData> centerNode = graph.addNode("B", p.width / 2f, 40);

		graph.addEdge(startNode, centerNode, 1f);
		graph.addEdge(endNode, centerNode, 1f);
	}

	@Override
	public void build(ComponentRegistry registry) {
		graphComponent = new GraphComponent(graph, List.of("A", "Z"));
		background = new Background();
		editingButton = new EditingButton(10, 10);
		editActionSelector = new EditActionSelector(10, 30 + editingButton.height);
		final BiFunction<Float, Float, Boolean> bounds = (x, y) -> !editActionSelector.checkInBounds(x, y)
				&& !editingButton.checkInBounds(x, y);
		nodeAdder = new NodeAdder(graph, bounds);
		edgeAdder = new EdgeAdder(graph, bounds);
		graphElementDeleter = new GraphElementDeleter(graph, bounds, graphComponent.anchors);
		registry.register(List.of(
				background,
				graphComponent,
				editingButton,
				editActionSelector,
				nodeAdder,
				edgeAdder,
				graphElementDeleter
		), id);
	}

	@Override
	public void frame() {
		if (state == State.EDITING) {
			if (!editingButton.isPressed()) state = State.SHOWING;
		} else {
			if (editingButton.isPressed()) state = State.EDITING;
		}
		editActionSelector.setVisible(state == State.EDITING);
		graphComponent.setRunning(state != State.EDITING);
		graphComponent.setDraggingEnabled(
						state != State.EDITING
						|| editActionSelector.getState() == EditActionSelector.State.MOVE
				);
		background.setEditing(state == State.EDITING);
		nodeAdder.setEnabled(state == State.EDITING && editActionSelector.getState() == EditActionSelector.State.NODE);
		edgeAdder.setEnabled(state == State.EDITING && editActionSelector.getState() == EditActionSelector.State.EDGE);
		graphElementDeleter.setEnabled(state == State.EDITING && editActionSelector.getState() == EditActionSelector.State.DELETE);
	}
}
