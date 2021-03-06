package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.general.GraphWithData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.GraphNode;
import processing.data.JSONObject;
import processing.event.KeyEvent;

import java.util.List;
import java.util.function.BiFunction;

public class MainView extends Component {
	private static final float INITIAL_NODE_INSET = 200f;

	private GraphWithData graph = new GraphWithData();
	private State state = State.EDITING;

	private GraphComponent graphComponent;
	private Background background;
	private EditingButton editingButton;
	private EditActionSelector editActionSelector;
	private RunningButton runningButton;
	private NodeAdder nodeAdder;
	private EdgeAdder edgeAdder;
	private GraphElementDeleter graphElementDeleter;
	private AStarVisualization aStarVisualization;

	private enum State {
		EDITING,
		SHOWING,
		RUNNING
	}

	@Override
	protected void init() {
		final List<String> jsonLines = List.of(p.loadStrings("default_graph.json"));
		final StringBuilder jsonText = new StringBuilder(255);
		jsonLines.forEach(jsonText::append);
		final JSONObject jsonObject = p.parseJSONObject(jsonText.toString());
		graph = GraphWithData.fromJSON(jsonObject);
	}

	@Override
	public void build(ComponentRegistry registry) {
		graphComponent = new GraphComponent(graph, List.of("A", "Z"));
		background = new Background();
		editingButton = new EditingButton(10, 10);
		editActionSelector = new EditActionSelector(10, 30 + editingButton.height);
		runningButton = new RunningButton(20 + editingButton.width, 10);
		final BiFunction<Float, Float, Boolean> bounds = (x, y) -> !editActionSelector.checkInBounds(x, y)
				&& !editingButton.checkInBounds(x, y) && !runningButton.checkInBounds(x, y);
		nodeAdder = new NodeAdder(graph, bounds);
		edgeAdder = new EdgeAdder(graph, bounds);
		graphElementDeleter = new GraphElementDeleter(graph, bounds, graphComponent.anchors);
		aStarVisualization = new AStarVisualization(graph, graph.getNode("A"), graph.getNode("Z"));
		registry.register(List.of(
				background,
				graphComponent,
				editingButton,
				editActionSelector,
				runningButton,
				nodeAdder,
				edgeAdder,
				graphElementDeleter,
				aStarVisualization
		), id);
	}

	@Override
	public void frame() {
		switch (state) {
			case EDITING -> {
				if (runningButton.isPressed()) {
					editingButton.setPressed(false);
					state = State.RUNNING;
					aStarVisualization.start();
				}
				else if (!editingButton.isPressed()) {
					state = State.SHOWING;
				}
			}
			case SHOWING -> {
				if (editingButton.isPressed()) state = State.EDITING;
				else if (runningButton.isPressed()) {
					state = State.RUNNING;
					aStarVisualization.start();
				}
			}
			case RUNNING -> {
				if (editingButton.isPressed()) {
					aStarVisualization.stop();
					runningButton.setPressed(false);
					state = State.EDITING;
				} else if (!runningButton.isPressed()) {
					aStarVisualization.stop();
					state = State.SHOWING;
				}
			}
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

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKey() == 's') {
			System.out.println(graph.toJSON());
		}
	}
}
