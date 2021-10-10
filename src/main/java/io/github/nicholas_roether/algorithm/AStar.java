package io.github.nicholas_roether.algorithm;

import io.github.nicholas_roether.JSONSerializable;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics_graph.NodePhysics;

import java.util.*;

/**
 * The implementation of the A*-Algorithm.
 * <br>
 * This class is written in such a way that the algorithm can be executed both step-by-step for the purposes of
 * visualization, and, though unused in this project, all at once, as a proper and efficient breadth-first
 * path-finding algorithm.
 *
 * @param <ND> The type of data stored in the graph nodes used. Must extend AStarNodeData.
 * @param <ED> The type of data stored in the graph edges used.
 *
 * @see AStarNodeData
 */
public class AStar<ND extends AStarNodeData, ED extends JSONSerializable> {
	/**
	 * The graph the algorithm operates on.
	 */
	public final Graph<ND, ED> graph;

	/**
	 * The node the algorithm starts from.
	 */
	public final GraphNode<ND> start;

	/**
	 * The node the algorithm tries to reach.
	 */
	public final GraphNode<ND> goal;

	/**
	 * All the nodes that the algorithm has currently discovered and still needs to check, ordered by the estimated
	 * cost via that node to the goal.
	 * <br>
	 * The proper data structure to use for this would be an ordered set since nodes can't appear twice in this list,
	 * but the fact that the ordering isn't static breaks the standard implementation.
	 */
	private final ArrayList<GraphNode<ND>> currentNodes = new ArrayList<>();

	/**
	 * Maps all currently discovered nodes to the node via which they are reached most efficiently, according to the
	 * algorithm's current knowledge.
	 */
	private final HashMap<GraphNode<ND>, GraphNode<ND>> pathMap = new HashMap<>();

	/**
	 * Maps all currently discovered nodes to the cost to reach them via the most efficient path, according to the
	 * algorithm's current knowledge.
	 * <br>
	 * The aforementioned cost consists of the sum of the weights of all edges traversed along the path.
	 */
	private final HashMap<GraphNode<ND>, Double> costMap = new HashMap<>();

	/**
	 * Maps all currently discovered nodes to the estimated cost of the fastest path that goes through them to the goal.
	 * <br>
	 * This estimated cost consists of the known cost to reach the node, plus the estimated cost to reach the goal from
	 * it. For implementation details on this estimate see {@code AStar.estimateCostToGoal()}.
	 */
	private final HashMap<GraphNode<ND>, Double> estimateMap = new HashMap<>();

	/**
	 * The node that the algorithm is currently checking.
	 */
	private GraphNode<ND> current;

	/**
	 * Whether the algorithm has finished, meaning it has found a path to the goal.
	 */
	private boolean finished = false;

	/**
	 * Constructs an instance of the A*-Algorithm.
	 * <br>
	 * Note that removing the start or end node at any time after construction, or any node while the algorithm is
	 * running, from the graph will most likely cause the algorithm to break.
	 *
	 * @param graph The graph this algorithm acts on
	 * @param start The starting node
	 * @param goal The node the algorithm tries to reach
	 */
	public AStar(Graph<ND, ED> graph, GraphNode<ND> start, GraphNode<ND> goal) {
		this.graph = graph;
		if (!graph.getNodes().contains(start) || !graph.getNodes().contains(goal))
			throw new IllegalArgumentException("Start and end nodes must be contained in the graph");
		this.start = start;
		this.goal = goal;
		current = start;

		// Initialize the state of the algorithm
		currentNodes.add(start);
		costMap.put(start, 0.0);
		estimateMap.put(start, 0.0);
	}

	/**
	 * Gets the cost to reach the given node via the shortest path currently known to the algorithm.
	 *
	 * @param node The node to find the cost to go to
	 * @return The cost, or {@code null} if no path to the given node is known
	 */
	public double getCostFor(GraphNode<ND> node) {
		if (!costMap.containsKey(node)) return Double.MAX_VALUE;
		return costMap.get(node);
	}

	/**
	 * Gets the estimated cost to reach the goal via a given node.
	 * <br>
	 * Note that this estimate only takes into account the shortest known path up until the node, and estimates the
	 * cost from there to the goal without any consideration for the actual graph structure afterwards, known or not.
	 *
	 * @param node The node to find the estimate for.
	 * @return The estimated cost, or {@code null} if no path to the given node is known.
	 */
	public double getEstimateFor(GraphNode<ND> node) {
		if (!estimateMap.containsKey(node)) return Double.MAX_VALUE;
		return estimateMap.get(node);
	}

	/**
	 * Estimates the cost to reach the goal from the given node, without any knowledge of the actual graph structure.
	 *
	 * @param node The node to estimate the cost from
	 * @return the estimated cost
	 */
	public double estimateCostToGoal(GraphNode<ND> node) {
		/*
		The way the algorithm estimates the cost is the following: it takes the positions of both the given node and the
		goal, which are known, and computes the weight an edge would need to have to be in equilibrium at that distance,
		assuming no outside forces and no repulsion between nodes (repulsion is ignored because the computation gets
		very complicated and quite expensive otherwise).

		Since the force produced by the edges is calculated precisely in such a way that they, by themselves, reach an
		equilibrium at a certain factor times their weight, this computation is quite straightforwardly the distance
		between the node and the goal divided by said factor.

		Because the algorithm assumes no repulsion between nodes, it will usually overestimate the edge weight slightly.
		This doesn't actually matter though, since usually the given node isn't actually connected directly to the goal
		anyway, making the cost higher.
		 */
		final double distance = node.data.getPosition().dist(goal.data.getPosition());
		final double scaleFactor = NodePhysics.LENGTH_SCALE_FACTOR;

		return distance / scaleFactor;
	}

	/**
	 * Advances the execution of the algorithm by one step.
	 */
	public void step() {
		if (hasHalted()) return;

		// Make sure the nodes are correctly sorted.
		currentNodes.sort(this::compareNodes);
		// Check the node with the lowest estimated path cost.
		current = currentNodes.get(0);
		// If the current node is the goal, halt the algorithm; it has found a path.
		if (current == goal) {
			finished = true;
			return;
		}
		// Remove the current node from the node list since it will be checked now.
		currentNodes.remove(current);

		final double costToCurrent = getCostFor(current);
		// Loop through all neighbors of the current node.
		for (GraphNeighbor<ND, ED> neighbor : graph.getNeighbors(current)) {
			// Compute the cost to reach the neighboring node via the current one.
			final double newCost = costToCurrent + neighbor.edgeWeight;
			// If the previously computed cost isn't lower that the currently known one, skip this neighbor.
			// The algorithm already knows a more efficient path to reach it.
			if (newCost >= getCostFor(neighbor.node)) continue;
			// Otherwise, update the path map so that the neighbor is reached via the current node, and update the
			// neighbors cost in the cost map.
			pathMap.put(neighbor.node, current);
			costMap.put(neighbor.node, newCost);
			// Compute a new estimate for reaching the goal via the neighbor and store it.
			estimateMap.put(neighbor.node, newCost + estimateCostToGoal(neighbor.node));
			// If it isn't already contained, add the neighboring node to the node list to be checked later.
			if (!currentNodes.contains(neighbor.node)) currentNodes.add(neighbor.node);
		}
	}

	/**
	 * Executes the algorithm and returns the path found.
	 *
	 * @return the found path in form of an ordered list of the nodes it goes along, or {@code null} if no path to the
	 * 		   goal was found.
	 */
	public List<GraphNode<ND>> execute() {
		/*
		This function isn't used in this project because it focuses on visualizing the A*-Algorithm instead of actually
		using it, but I wanted to make sure that there was an actually usable implementation of the algorithm somewhere
		in here.
		 */
		while (!hasHalted()) step(); // Step through the algorithm until it halts.
		if (!finished) return null; // No path was found, return null.
		return getResult(); // Reconstruct the path and return it.
	}

	/**
	 * Reconstructs the best known path to the given node and returns it.
	 *
	 *
	 * @param node The node to find the path to
	 * @return the path in the form of an ordered list of the nodes it visits, or {@code null} if no path to the node is
	 * 		   known.
	 */
	public List<GraphNode<ND>> getPathTo(GraphNode<ND> node) {
		final List<GraphNode<ND>> path = getMutablePathRecursively(node);
		if (path == null) return null;
		return Collections.unmodifiableList(path);
	}

	/**
	 * Reconstructs the found path to the goal and returns it.
	 *
	 * @return the path in the form of an ordered list of the nodes it visits, or {@code null} if no path to the goal is
	 * 		   known.
	 */
	public List<GraphNode<ND>> getResult() {
		return getPathTo(goal);
	}

	/**
	 * Reconstructs the best known path to the given node recursively.
	 *
	 * @param node The node to find the path to
	 * @return the path in form of a mutable (@code ArrayList), or {@code null} if none is known.
	 */
	private ArrayList<GraphNode<ND>> getMutablePathRecursively(GraphNode<ND> node) {
		// If the given node is the starting node, the end of the recursion is reached.
		if (node.equals(start)) {
			// Construct the mutable list that will be passed up the recursion; It will, at most, need as many slots as
			// nodes are contained in the graph.
			final ArrayList<GraphNode<ND>> list = new ArrayList<>(graph.getNodes().size());
			// Add the node to the list and terminate the recursion.
			list.add(node);
			return list;
		}
		// Get the previous node on the path.
		final GraphNode<ND> via = pathMap.get(node);
		if (via == null) return null; // No path to this node is known
		// Get the path to the previous node recursively
		final ArrayList<GraphNode<ND>> list = getMutablePathRecursively(via);
		if (list == null) return null; // No path to the previous node is known
		// Add the current node to the list and pass it further up the recursion.
		list.add(node);
		return list;
	}

	/**
	 * The comparison function used to sort the node list by the estimated cost of the paths via the nodes.
	 *
	 * @param node1 The first of the nodes to compare
	 * @param node2 The second of the nodes to compare
	 * @return {@code 0} if the nodes are equal, a value less than zero if the estimated cost via {@code node1} is lower
	 * 		   than {@code node2}, and a value higher than zero if the estimated cost via {@code node1} is higher than
	 * 		   {@code node2}.
	 */
	private int compareNodes(GraphNode<ND> node1, GraphNode<ND> node2) {
		if (node1.equals(node2)) return 0; // The nodes are the same, return 0.
		return Double.compare(getEstimateFor(node1), getEstimateFor(node2)); // Compare the nodes' cost estimates
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Getters

	/**
	 * Checks whether the algorithm has finished, meaning it has halted and has in fact found a path to the goal.
	 *
	 * @return {@code true} if the algorithm has finished
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Checks whether the algorithm has halted. This happens when it either finds a path to the goal, or runs out of
	 * nodes to check, meaning it couldn't find such a path.
	 *
	 * @return {@code true} if the algorithm has halted
	 */
	public boolean hasHalted() {
		return finished || currentNodes.size() == 0;
	}

	/**
	 * Gets the node the algorithm is currently checking.
	 *
	 * @return the current node
	 */
	public GraphNode<ND> getCurrent() {
		return current;
	}

	/**
	 * Gets the list of all nodes the algorithm has discovered and still needs to check.
	 *
	 * @return the list of the current nodes
	 */
	public List<GraphNode<ND>> getCurrentNodes() {
		return Collections.unmodifiableList(currentNodes);
	}

	/**
	 * Gets the map that maps each known node to the node via which they are, to the algorithm's current knowledge,
	 * reached most efficiently.
	 *
	 * @return the path map
	 */
	public Map<GraphNode<ND>, GraphNode<ND>> getPathMap() {
		return Collections.unmodifiableMap(pathMap);
	}

	/**
	 * Gets the map that maps each known node to the cost of the most efficient path to them, according to the
	 * algorithm's current knowledge.
	 *
	 * @return the cost map
	 */
	public Map<GraphNode<ND>, Double> getCostMap() {
		return Collections.unmodifiableMap(costMap);
	}

	/**
	 * Gets the map that maps each known node to the current estimated cost to reach the goal via the shortest path
	 * through them.
	 *
	 * @return the estimate map
	 */
	public Map<GraphNode<ND>, Double> getEstimateMap() {
		return Collections.unmodifiableMap(estimateMap);
	}
}
