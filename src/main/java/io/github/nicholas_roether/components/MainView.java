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
//		final GraphNode<NodeData> startNode = graph.addNode("A", INITIAL_NODE_INSET, p.height / 2f);
//		final GraphNode<NodeData> endNode = graph.addNode("Z", p.width - INITIAL_NODE_INSET, p.height / 2f);
//		final GraphNode<NodeData> centerNode = graph.addNode("B", p.width / 2f, 40);
//
//		graph.addEdge(startNode, centerNode, 1f);
//		graph.addEdge(endNode, centerNode, 1f);

		final JSONObject jsonObject = p.parseJSONObject("{\"nodes\":[{\"data\":{\"acceleration\":[0,0],\"goal\":false,\"start\":true,\"position\":[200,450],\"velocity\":[0,0],\"state\":\"CURRENT\"},\"name\":\"A\"},{\"data\":{\"acceleration\":[-2.0372681319713593e-10,-5.093170329928398e-11],\"goal\":false,\"start\":false,\"position\":[202.29283142089844,362.4528503417969],\"velocity\":[0.0001678467815509066,-0.00003623959855758585],\"state\":\"VISITED\"},\"name\":\"B\"},{\"data\":{\"acceleration\":[4.0745362639427185e-10,4.0745362639427185e-10],\"goal\":false,\"start\":false,\"position\":[465.4858093261719,411.87261962890625],\"velocity\":[0.0003356931556481868,0.0003204343665856868],\"state\":\"VISITED\"},\"name\":\"C\"},{\"data\":{\"acceleration\":[8.149072527885437e-10,2.0372681319713593e-10],\"goal\":false,\"start\":false,\"position\":[692.421875,330.6361083984375],\"velocity\":[0.0006256099441088736,0.0002288817340740934],\"state\":\"DEFAULT\"},\"name\":\"D\"},{\"data\":{\"acceleration\":[4.0745362639427185e-10,4.0745362639427185e-10],\"goal\":false,\"start\":false,\"position\":[778.380126953125,273.7127380371094],\"velocity\":[0.0004653928626794368,0.0004425046790856868],\"state\":\"DEFAULT\"},\"name\":\"E\"},{\"data\":{\"acceleration\":[-2.0372681319713593e-10,4.0745362639427185e-10],\"goal\":false,\"start\":false,\"position\":[367.26837158203125,340.37066650390625],\"velocity\":[0.0001373292034259066,0.0003013608802575618],\"state\":\"VISITED\"},\"name\":\"F\"},{\"data\":{\"acceleration\":[-8.149072527885437e-10,-2.0372681319713593e-10],\"goal\":false,\"start\":false,\"position\":[640.5926513671875,159.8986358642578],\"velocity\":[0.0008239750168286264,0.0001678467815509066],\"state\":\"DEFAULT\"},\"name\":\"G\"},{\"data\":{\"acceleration\":[8.149072527885437e-10,8.149072527885437e-10],\"goal\":false,\"start\":false,\"position\":[528.037109375,532.5208129882812],\"velocity\":[0.0006027217605151236,0.0007858272292651236],\"state\":\"CURRENT\"},\"name\":\"H\"},{\"data\":{\"acceleration\":[8.149072527885437e-10,8.149072527885437e-10],\"goal\":false,\"start\":false,\"position\":[816.0855712890625,563.8177490234375],\"velocity\":[0.0008392329909838736,0.0008621211745776236],\"state\":\"DEFAULT\"},\"name\":\"I\"},{\"data\":{\"acceleration\":[0,0],\"goal\":true,\"start\":false,\"position\":[1080,450],\"velocity\":[0,0],\"state\":\"DEFAULT\"},\"name\":\"Z\"}],\"edges\":[{\"nodes\":[\"A\",\"B\"],\"data\":{\"state\":\"CHOSEN\"},\"weight\":1},{\"nodes\":[\"B\",\"C\"],\"data\":{\"state\":\"DEFAULT\"},\"weight\":5},{\"nodes\":[\"C\",\"D\"],\"data\":{\"state\":\"DEFAULT\"},\"weight\":4},{\"nodes\":[\"D\",\"E\"],\"data\":{\"state\":\"DEFAULT\"},\"weight\":1},{\"nodes\":[\"E\",\"Z\"],\"data\":{\"state\":\"DEFAULT\"},\"weight\":7},{\"nodes\":[\"A\",\"F\"],\"data\":{\"state\":\"CHOSEN\"},\"weight\":3},{\"nodes\":[\"F\",\"C\"],\"data\":{\"state\":\"CHOSEN\"},\"weight\":2},{\"nodes\":[\"F\",\"G\"],\"data\":{\"state\":\"DEFAULT\"},\"weight\":8},{\"nodes\":[\"G\",\"E\"],\"data\":{\"state\":\"DEFAULT\"},\"weight\":3},{\"nodes\":[\"A\",\"H\"],\"data\":{\"state\":\"CURRENT\"},\"weight\":6},{\"nodes\":[\"H\",\"I\"],\"data\":{\"state\":\"DEFAULT\"},\"weight\":4},{\"nodes\":[\"I\",\"Z\"],\"data\":{\"state\":\"DEFAULT\"},\"weight\":5},{\"nodes\":[\"H\",\"C\"],\"data\":{\"state\":\"DEFAULT\"},\"weight\":2}]}");
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
				&& !editingButton.checkInBounds(x, y);
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
