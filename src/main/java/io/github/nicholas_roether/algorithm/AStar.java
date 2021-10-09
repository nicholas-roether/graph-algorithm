package io.github.nicholas_roether.algorithm;

import io.github.nicholas_roether.JSONSerializable;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics_graph.NodePhysics;

import java.util.*;

public class AStar<ND extends AStarNodeData, ED extends JSONSerializable> {
	private static final double ESTIMATE_SCALE_FACTOR = NodePhysics.ATTRACTION_CONSTANT / NodePhysics.REPULSION_CONSTANT;

	public final Graph<ND, ED> graph;
	public final GraphNode<ND> start;
	public final GraphNode<ND> goal;

	private final ArrayList<GraphNode<ND>> currentNodes = new ArrayList<>();
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
		final double estimate = ESTIMATE_SCALE_FACTOR * distance * distance * distance;
		System.out.println("node: " + node.name + "; distance: " + distance + "; estimate: " + estimate);
		return estimate;
	}

	public void step() {
		if (finished || currentNodes.size() == 0) return;
		currentNodes.sort(this::compareNodes);
		current = currentNodes.get(0);
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
			if (!currentNodes.contains(neighbor.node)) currentNodes.add(neighbor.node);
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

	private int compareNodes(GraphNode<ND> node1, GraphNode<ND> node2) {
		if (node1.equals(node2)) return 0;
		return Double.compare(getEstimateFor(node1), getEstimateFor(node2));
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Getters

	public boolean isFinished() {
		return finished;
	}

	public GraphNode<ND> getCurrent() {
		return current;
	}

	public List<GraphNode<ND>> getCurrentNodes() {
		return Collections.unmodifiableList(currentNodes);
	}

	public Map<GraphNode<ND>, GraphNode<ND>> getPathMap() {
		return Collections.unmodifiableMap(pathMap);
	}

	public Map<GraphNode<ND>, Double> getCostMap() {
		return Collections.unmodifiableMap(costMap);
	}

	public Map<GraphNode<ND>, Double> getEstimateMap() {
		return Collections.unmodifiableMap(estimateMap);
	}
}
