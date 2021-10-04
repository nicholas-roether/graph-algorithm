package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Element;
import io.github.nicholas_roether.draw.bounded.CircularComponent;
import io.github.nicholas_roether.elements.NodeElement;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNeighbor;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics_graph.NodePhysics;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * The graphical component representing a graph node within a rendered graph.
 * <br>
 * Graph nodes house the physics objects that are being simulated.
 *
 * @see CircularComponent
 */
public class NodeComponent extends CircularComponent {
	// Nodes are drawn on layer 2.
	public static final int Z_INDEX = 2;

	/**
	 * The radius of the nodes in pixels.
	 */
	public static final float NODE_RADIUS = 15;

	/**
	 * The default text size in pixels; text with more than one letter will be drawn
	 * smaller.
	 */
	private static final float TEXT_SIZE = 25;

	/**
	 * What proportion of the mouse velocity is translated to the node velocity after dragging a node
	 */
	private static final float MOUSE_VEL_TRANSLATION = 0.3f;

	public final GraphNode<NodeData> node;

	public final Graph<NodeData, Object> graph;

	/**
	 * Whether this node is an anchor node, meaning it isn't affected by physics
	 */
	public final boolean anchor;

	/**
	 * The physics object for this node.
	 *
	 * @see NodePhysics
	 */
	private NodePhysics<NodeData> physics;

	/**
	 * Whether this node is currently being dragged.
	 */
	private boolean dragging = false;

	/**
	 * The time at which the mouse was last moved, in milliseconds.
	 */
	private long lastMouseMove = 0;

	/**
	 * The last position of the mouse on the screen.
	 */
	private PVector lastMousePos;

	/**
	 * The current movement velocity of the mouse.
	 */
	private PVector mouseVelocity;

	public NodeComponent(GraphNode<NodeData> node, Graph<NodeData, Object> graph, boolean anchor) {
		super(Z_INDEX);
		this.node = node;
		this.anchor = anchor;
		this.graph = graph;
		physics = new NodePhysics<>(node, graph);
		// Disable physics if the node is an anchor
		if (anchor) physics.setDisabled(true);
	}

	public NodePhysics<NodeData> getPhysics() {
		return physics;
	}

	@Override
	protected void init(PApplet p) {
		physics.setScreenWidth(p.width);
		physics.setScreenHeight(p.height);
	}

	@Override
	public void draw(@NotNull PApplet p) {
		final Element nodeElement = new NodeElement(
				checkInBounds(p.mouseX, p.mouseY),
				anchor,
				physics.getPosition().x,
				physics.getPosition().y,
				NODE_RADIUS,
				node.name,
				TEXT_SIZE
		);
		nodeElement.draw(p);
	}

	@Override
	public void mouseMovedAnywhere(MouseEvent event) {
		if (dragging) {
			// Move the node to the mouse if it is being dragged and the new position isn't inside another node
			final PVector mousePos = new PVector(event.getX(), event.getY());
			boolean canMove = true;
			for (GraphNode<NodeData> other : graph.getNodes()) {
				if (other.equals(node)) continue;
				final float distance = mousePos.dist(other.data.getPosition());
				if (distance <= 2 * NODE_RADIUS) {
					canMove = false;
					break;
				}
			}
			if (canMove) physics.setPosition(mousePos);
		}

		// Update mouse velocity unless there was no tracked mouse event yet
		if (lastMouseMove != 0 && lastMousePos != null) {
			final long duration = event.getMillis() - lastMouseMove;
			final float durationSeconds = 0.001f * duration;

			final PVector velocity = new PVector(event.getX(), event.getY());
			velocity.sub(lastMousePos);
			velocity.div(durationSeconds);

			mouseVelocity = velocity.copy();
		}

		// Update variables tracking mouse movement & position
		lastMouseMove = event.getMillis();
		lastMousePos = new PVector(event.getX(), event.getY());
	}

	@Override
	public void mousePressedInBounds(MouseEvent event) {
		if (event.getButton() == LEFT) {
			// The node has started to be dragged; disable its physics
			dragging = true;
			physics.setDisabled(true);
		}
	}

	@Override
	public void mouseReleasedAnywhere(MouseEvent event) {
		// The left button is still being pressed; ignore the event.
		if (event.getButton() == LEFT) return;

		// Enable the physics if they were disabled because the node was being dragged,
		// unless the node is an anchor
		physics.setDisabled(anchor);

		// If the node used to be dragged and there is a mouse velocity tracked, set the node's
		// velocity according to MOUSE_VEL_TRANSLATION
		if (dragging && mouseVelocity != null) {
			physics.setVelocity(mouseVelocity.copy().mult(MOUSE_VEL_TRANSLATION));
		}

		// The node is no longer being dragged (if it was in the first place)
		dragging = false;
	}

	@Override
	protected int instructCursorAnywhere(float x, float y) {
		// If the node is being tracked, the cursor should be set to
		// MOVE anywhere on the screen
		if (dragging) return MOVE;
		return NO_CURSOR_INSTRUCT;
	}

	@Override
	protected int instructCursorInBounds() {
		// Inside the bounds of the node, the cursor should be MOVE if
		// the node is being dragged, otherwise it should be HAND.
		if (dragging) return MOVE;
		return HAND;
	}

	@Override
	public float getRadius() {
		return NODE_RADIUS;
	}

	@Override
	public float getX() {
		return physics.getPosition().x;
	}

	@Override
	public float getY() {
		return physics.getPosition().y;
	}

}
