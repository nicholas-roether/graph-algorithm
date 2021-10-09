package io.github.nicholas_roether.components;

import io.github.nicholas_roether.algorithm.AStar;
import io.github.nicholas_roether.draw.Animation;
import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.general.EdgeData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AStarVisualization extends Component {
	private static final float STAGE_DURATION = 0.6f;

	private final Graph<NodeData, EdgeData> graph;
	private final GraphNode<NodeData> start;
	private final GraphNode<NodeData> goal;
	private AStar<NodeData, EdgeData> aStar;

	private boolean running = false;
	private final HashSet<GraphNode<NodeData>> visitedNodes = new HashSet<>();
	private final Animation animation = new Animation(2 * STAGE_DURATION, Animation.LINEAR, false);

	public AStarVisualization(Graph<NodeData, EdgeData> graph, GraphNode<NodeData> start, GraphNode<NodeData> goal) {
		this.graph = graph;
		this.start = start;
		this.goal = goal;
	}

	public void start() {
		if (running) return;
		reset();
		aStar.step();
		running = true;
	}

	public void stop() {
		reset();
		resetGraphState();
		running = false;
	}

	private void setStateAlongPath(GraphNode<NodeData> node, NodeData.State nodeState, EdgeData.State edgeState) {
		node.data.setState(nodeState);
		final GraphNode<NodeData> pathFrom = aStar.getPathMap().get(node);
		if (pathFrom == null) return;
		graph.getNeighbors(node).forEach(neighbor -> {
			if (neighbor.node.equals(pathFrom)) neighbor.edgeData.setState(edgeState);
		});
		setStateAlongPath(pathFrom, nodeState, edgeState);
	}

	@Override
	public void frame() {
		if (!running) return;
		resetGraphState();
		start.data.setStart(true);
		goal.data.setGoal(true);

		visitedNodes.add(aStar.getCurrent());

		Stage stage = Stage.SELECTING;
		if (aStar.isFinished()) stage = Stage.FINISHED;
		else if (animation.getProgress() > 0.5f) stage = Stage.SCANNING;

		for (GraphNode<NodeData> visitedNode : visitedNodes) {
			if (visitedNode.data.getState() != NodeData.State.VISITED)
				setStateAlongPath(visitedNode, NodeData.State.VISITED, EdgeData.State.CHOSEN);
		}

		setStateAlongPath(aStar.getCurrent(), NodeData.State.CURRENT, EdgeData.State.CURRENT);

		if (stage == Stage.SCANNING) {
			aStar.getCurrent().data.setState(NodeData.State.CHECKING);
			graph.getNeighbors(aStar.getCurrent()).forEach(neighbor -> {
				if (!neighbor.node.equals(aStar.getPathMap().get(aStar.getCurrent())))
					neighbor.edgeData.setState(EdgeData.State.CHECKING);
			});
		} else if (stage == Stage.FINISHED) {
			setStateAlongPath(goal, NodeData.State.FINAL, EdgeData.State.FINAL);
			running = false;
		}

		animation.step(1.0 / p.frameRate);
		if (animation.getProgress() == 1) {
			aStar.step();
			animation.restart();
		}
	}

	public void reset() {
		aStar = new AStar<>(graph, start, goal);
		visitedNodes.clear();
	}

	public void resetGraphState() {
		graph.getNodes().forEach(node -> node.data.setState(NodeData.State.DEFAULT));
		graph.getEdges().forEach(edge -> edge.data.setState(EdgeData.State.DEFAULT));
		start.data.setStart(false);
		goal.data.setGoal(false);
	}

	public boolean isRunning() {
		return running;
	}

	private List<GraphNeighbor<NodeData, EdgeData>> getNeighborsAlongPath(List<GraphNode<NodeData>> path) {
		if (path == null || path.size() == 0) return List.of();
		final ArrayList<GraphNeighbor<NodeData, EdgeData>> neighbors = new ArrayList<>(path.size() - 1);
		for (int i = 0; i < path.size() - 1; i++) {
			final GraphNode<NodeData> node1 = path.get(i);
			final GraphNode<NodeData> node2 = path.get(i + 1);

			for (GraphNeighbor<NodeData, EdgeData> neighbor : graph.getNeighbors(node1)) {
				if (neighbor.node == node2) {
					neighbors.add(neighbor);
					break;
				}
			}
		}
		return neighbors;
	}

	private enum Stage {
		SELECTING,
		SCANNING,
		FINISHED
	}
}
