package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.general.GraphWithData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.GraphNode;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

import java.util.List;

public class MainView extends Component {
	private final GraphWithData graph = new GraphWithData();
	private State state = State.EDITING;

	private GraphComponent graphComponent;
	private Background background;
	private EditingButton editingButton;

	private enum State {
		EDITING,
		SHOWING,
		RUNNING
	}

	@Override
	protected void init(PApplet p) {
		final GraphNode<NodeData> startNode = graph.addNode("A", 60f, p.height / 2f);
		final GraphNode<NodeData> endNode = graph.addNode("Z", p.width - 60f, p.height / 2f);
		final GraphNode<NodeData> centerNode = graph.addNode("C", p.width / 2f, 40);

		graph.addEdge(startNode, centerNode, 1f);
		graph.addEdge(endNode, centerNode, 1f);
	}

	@Override
	public void build(ComponentRegistry registry) {
		graphComponent = new GraphComponent(graph, List.of("A", "Z"));
		background = new Background();
		editingButton = new EditingButton(10, 10, 80, 40);
		registry.register(List.of(
				background,
				graphComponent,
				editingButton
		), id);
	}

	@Override
	public void draw(@NotNull PApplet p) {
		if (state == State.EDITING) {
			if (!editingButton.isPressed()) state = State.SHOWING;
		} else {
			if (editingButton.isPressed()) state = State.EDITING;
		}
		graphComponent.setRunning(state != State.EDITING);
		background.setEditing(state == State.EDITING);
	}
}
