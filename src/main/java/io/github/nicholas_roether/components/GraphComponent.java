package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.general.EdgeData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsEngine;
import io.github.nicholas_roether.physics_graph.NodePhysics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The graphical component representing a graph with physics.
 *
 * @see Component
 */
public class GraphComponent extends Component {
	public final Graph<NodeData, EdgeData> graph;

	/**
	 * A list of the names of the nodes that are anchors, meaning they
	 * are unaffected by physics and stay in place
	 */
	public final List<String> anchors;

	/**
	 * A set of the nodes that are currently being rendered. Updated
	 * whenever the component is rebuilt.
	 */
	private Set<GraphNode<NodeData>> nodes;

	/**
	 * A list of all node components that are children to this graph component.
	 */
	private final List<NodeComponent> nodeComponents = new ArrayList<>();

	/**
	 * A set of the nodes that are currently being rendered. Updated
	 * whenever the component is rebuilt.
	 */
	private List<GraphEdge<NodeData, EdgeData>> edges;

	/**
	 * The physics engine in charge of simulating the nodes.
	 *
	 * @see PhysicsEngine
	 */
	private final PhysicsEngine<NodePhysics> physicsEngine;

	/**
	 * Whether the physics simulation is currently running.
	 */
	private boolean running = true;

	/**
	 * Whether the user should be able to drag nodes.
	 */
	private boolean draggingEnabled = true;

	public GraphComponent(Graph<NodeData, EdgeData> graph, List<String> anchors) {
		this.graph = graph;
		this.anchors = anchors;
		this.nodes = new HashSet<>();
		this.physicsEngine = new PhysicsEngine<>();
	}

	@Override
	public void build(ComponentRegistry registry) {
		// Reset the physics engine since all the nodes will be rebuilt.
		physicsEngine.reset();

		// Empty the node component list
		nodeComponents.clear();

		// Get current nodes and edges from the graph.
		nodes = Set.copyOf(graph.getNodes());
		edges = List.copyOf(graph.getEdges());

		final List<Component> components = new ArrayList<>();

		for (GraphNode<NodeData> node : nodes) {
			// Create a NodeComponent for each node
			final NodeComponent nodeComponent = new NodeComponent(node, graph, anchors.contains(node.name));

			// Add the NodePhysics object corresponding to each node to the engine
			physicsEngine.addObject(nodeComponent.getPhysics());

			nodeComponents.add(nodeComponent);
			components.add(nodeComponent);
		}

		for (GraphEdge<NodeData, EdgeData> edge : edges) {
			// Create an EdgeComponent for each edge
			components.add(new EdgeComponent(edge));

			// Create an EdgeLabel for each edge
			components.add(new EdgeLabel(edge));
		}

		registry.register(components, id);
	}

	public void setRunning(boolean running) {
		if (running != this.running) {
			// Reset velocities and accelerations when starting / stopping the simulation
			nodes.forEach(node -> {
				node.data.setVelocity(new PVector());
				node.data.setAcceleration(new PVector());
			});
			this.running = running;
		}
	}

	public void setDraggingEnabled(boolean draggingEnabled) {
		this.draggingEnabled = draggingEnabled;
		nodeComponents.forEach(nodeComponent -> nodeComponent.setDraggingEnabled(draggingEnabled));
	}

	public boolean isDraggingEnabled() {
		return draggingEnabled;
	}

	@Override
	public boolean shouldRebuild() {
		// Rebuild if the graph contains new nodes or edges
		return !nodes.equals(graph.getNodes()) || !edges.equals(graph.getEdges());
	}

	@Override
	public void frame() {
		// step the physics engine on each frame if the simulation is running
		if (running) physicsEngine.step(1 / p.frameRate);
	}
}
