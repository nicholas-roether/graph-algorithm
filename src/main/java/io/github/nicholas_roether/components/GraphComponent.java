package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics.PhysicsEngine;
import io.github.nicholas_roether.physics_graph.NodePhysics;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

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
	public final Graph<NodeData, Object> graph;

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
	 * A set of the nodes that are currently being rendered. Updated
	 * whenever the component is rebuilt.
	 */
	private List<GraphEdge<NodeData, Object>> edges;

	/**
	 * The physics engine in charge of simulating the nodes.
	 *
	 * @see PhysicsEngine
	 */
	private final PhysicsEngine<NodePhysics<NodeData>> physicsEngine;

	public GraphComponent(Graph<NodeData, Object> graph, List<String> anchors) {
		this.graph = graph;
		this.anchors = anchors;
		this.nodes = new HashSet<>();
		this.physicsEngine = new PhysicsEngine<>();
	}

	@Override
	public void build(ComponentRegistry registry) {
		// Reset the physics engine since all the nodes will be rebuilt.
		physicsEngine.reset();

		// Get current nodes and edges from the graph.
		nodes = Set.copyOf(graph.getNodes());
		edges = List.copyOf(graph.getEdges());

		final List<Component> components = new ArrayList<>();

		for (GraphNode<NodeData> node : nodes) {
			// Create a NodeComponent for each node
			final NodeComponent nodeComponent = new NodeComponent(node, graph, anchors.contains(node.name));

			// Add the NodePhysics object corresponding to each node to the engine
			physicsEngine.addObject(nodeComponent.physics);

			components.add(nodeComponent);
		}

		for (GraphEdge<NodeData, Object> edge : edges) {
			// Create an EdgeComponent for each edge
			components.add(new EdgeComponent(edge));

			// Create an EdgeLabel for each edge
			components.add(new EdgeLabel(edge));
		}

		registry.register(components, id);
	}

	@Override
	public boolean shouldRebuild() {
		// Rebuild if the graph contains new nodes or edges
		return !nodes.equals(graph.getNodes()) || !edges.equals(graph.getEdges());
	}

	@Override
	public void draw(@NotNull PApplet p) {
		// step the physics engine on each frame
		physicsEngine.step(1 / p.frameRate);
	}
}
