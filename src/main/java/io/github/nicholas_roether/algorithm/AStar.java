package io.github.nicholas_roether.algorithm;

import io.github.nicholas_roether.general.NodePosition;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;

import java.util.*;

public class AStar<ND extends NodePosition, ED> {
	private static final double ESTIMATE_SCALE_FACTOR = 0.012;

	public final Graph<ND, ED> graph;
	public final GraphNode<ND> start;
	public final GraphNode<ND> goal;

	private final TreeSet<GraphNode<ND>> currentNodes = new TreeSet<>(Comparator.comparingDouble(this::getEstimateFor));
	private final HashMap<GraphNode<ND>, GraphNode<ND>> pathMap = new HashMap<>();
	private final HashMap<GraphNode<ND>, Double> costMap = new HashMap<>();
	private final HashMap<GraphNode<ND>, Double> estimateMap = new HashMap<>();
	private GraphNode<ND> current;
	private boolean finished = false;

	public AStar(Graph<ND, ED> graph, GraphNode<ND> start, GraphNode<ND> goal) {
		this.graph = graph;
		if (!graph.getNodes().contains(start) || !graph.getNodes().contains(goal))
			throw new IllegalArgumentException("Start and end nodes must be contained in the graph");
		this.start = start;
		this.goal = goal;
		current = start;

		currentNodes.add(start);
		costMap.put(start, 0.0);
		estimateMap.put(start, 0.0);
	}

	public double getCostFor(GraphNode<ND> node) {
		if (!costMap.containsKey(node)) return Double.MAX_VALUE;
		return costMap.get(node);
	}

	public double getEstimateFor(GraphNode<ND> node) {
		if (!estimateMap.containsKey(node)) return Double.MAX_VALUE;
		return estimateMap.get(node);
	}

	public double estimateCostToGoal(GraphNode<ND> node) {
		final double distance = node.data.getPosition().dist(goal.data.getPosition());
		return ESTIMATE_SCALE_FACTOR * distance;
	}

	public void step() {
		if (finished || currentNodes.size() == 0) return;
		current = currentNodes.first();
		if (current == goal) {
			finished = true;
			return;
		}
		currentNodes.remove(current);

		final double costToCurrent = getCostFor(current);
		for (GraphNeighbor<ND, ED> neighbor : graph.getNeighbors(current)) {
			final double newCost = costToCurrent + neighbor.edgeWeight;
			if (newCost >= getCostFor(neighbor.node)) continue;
			pathMap.put(neighbor.node, current);
			costMap.put(neighbor.node, newCost);
			estimateMap.put(neighbor.node, newCost + estimateCostToGoal(neighbor.node));
			currentNodes.add(neighbor.node);
		}
	}

	public List<GraphNode<ND>> execute() {
		while (!finished && currentNodes.size() > 0) step();
		if (!finished) return null;
		return getResult();
	}

	public List<GraphNode<ND>> getPathTo(GraphNode<ND> node) {
		final List<GraphNode<ND>> path = getMutablePathRecursively(node);
		if (path == null) return null;
		return Collections.unmodifiableList(path);
	}
	public List<GraphNode<ND>> getResult() {
		return getPathTo(goal);
	}

	private ArrayList<GraphNode<ND>> getMutablePathRecursively(GraphNode<ND> node) {
		if (node == start) {
			final ArrayList<GraphNode<ND>> list = new ArrayList<>(graph.getNodes().size());
			list.add(start);
			return list;
		}
		final GraphNode<ND> via = pathMap.get(node);
		if (via == null) return null;
		final ArrayList<GraphNode<ND>> list = getMutablePathRecursively(via);
		if (list == null) return null;
		list.add(node);
		return list;
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Getters


	public boolean isFinished() {
		return finished;
	}

	public GraphNode<ND> getCurrent() {
		return current;
	}

	public TreeSet<GraphNode<ND>> getCurrentNodes() {
		return currentNodes;
	}

	public HashMap<GraphNode<ND>, GraphNode<ND>> getPathMap() {
		return pathMap;
	}

	public HashMap<GraphNode<ND>, Double> getCostMap() {
		return costMap;
	}

	public HashMap<GraphNode<ND>, Double> getEstimateMap() {
		return estimateMap;
	}
}
