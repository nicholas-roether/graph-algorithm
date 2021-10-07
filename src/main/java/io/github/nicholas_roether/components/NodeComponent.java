package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.Element;
import io.github.nicholas_roether.draw.bounded.CircularComponent;
import io.github.nicholas_roether.elements.NodeElement;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics_graph.NodePhysics;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;
import processing.event.MouseEvent;


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
	public static final float NODE_RADIUS = 20;

	/**
	 * The default text size in pixels; text with more than one letter will be drawn
	 * smaller.
	 */
	private static final float TEXT_SIZE = 30;

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
	 * Whether the user should be able to drag this node.
	 */
	private boolean draggingEnabled = true;

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

	private float screenWidth;
	private float screenHeight;

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
	protected void init(Document p) {
		physics.setScreenWidth(p.width);
		physics.setScreenHeight(p.height);
		screenWidth = p.width;
		screenHeight = p.height;
	}

	@Override
	public void frame(Document p) {
		if (dragging) moveToMouse(p);
	}

	@Override
	public void draw(@NotNull Document p) {
		final Element nodeElement = new NodeElement(
				draggingEnabled && checkInBounds(p.mouseX, p.mouseY),
				anchor,
				physics.getPosition().x,
				physics.getPosition().y,
				NODE_RADIUS,
				node.name,
				TEXT_SIZE,
				255
		);
		nodeElement.draw(p);
	}

	private void moveToMouse(Document p) {
		// Move the node to the mouse if it is being dragged and the new position isn't inside another node
		// or outside the screen
		final PVector nodePos = node.data.getPosition();
		final PVector mousePos = new PVector(p.mouseX, p.mouseY);
		PVector targetPos = mousePos;
		if (targetPos.x < NODE_RADIUS) targetPos.x = NODE_RADIUS;
		if (targetPos.x > screenWidth - NODE_RADIUS) targetPos.x = screenWidth - NODE_RADIUS;
		if (targetPos.y < NODE_RADIUS) targetPos.y = NODE_RADIUS;
		if (targetPos.y > screenHeight - NODE_RADIUS) targetPos.y = screenHeight - NODE_RADIUS;
		for (GraphNode<NodeData> other : graph.getNodes()) {
			if (other.equals(node)) continue;
			final float distanceToMouse = mousePos.dist(other.data.getPosition());
			if (distanceToMouse < 2 * NODE_RADIUS) {
				targetPos = closestPointToOtherNode(targetPos, nodePos, other.data.getPosition());
			}
		}
		node.data.setPosition(targetPos);
//		boolean canMove = true;
//		//noinspection RedundantIfStatement
//		if (mousePos.x <= NODE_RADIUS || mousePos.x >= screenWidth - NODE_RADIUS) canMove = false;
//		if (mousePos.y <= NODE_RADIUS || mousePos.y >= screenHeight - NODE_RADIUS) canMove = false;
//		for (GraphNode<NodeData> other : graph.getNodes()) {
//			if (other.equals(node)) continue;
//			final float distance = mousePos.dist(other.data.getPosition());
//			if (distance <= 2 * NODE_RADIUS) {
//				canMove = false;
//				break;
//			}
//		}
//		if (canMove) physics.setPosition(mousePos);
	}

	@Override
	public void mouseMovedAnywhere(MouseEvent event) {
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
		if (event.getButton() == LEFT && draggingEnabled) {
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
		return super.instructCursorAnywhere(x, y);
	}

	@Override
	protected int instructCursorInBounds() {
		// Inside the bounds of the node, the cursor should be MOVE if
		// the node is being dragged, otherwise it should be HAND.
		if (dragging) return MOVE;
		else if (draggingEnabled) return HAND;
		return super.instructCursorInBounds();
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

	public boolean isDraggingEnabled() {
		return draggingEnabled;
	}

	public void setDraggingEnabled(boolean draggingEnabled) {
		this.draggingEnabled = draggingEnabled;
	}

	/**
	 * Find the first point at which, going along the path between the node position and the requested position,
	 * the two nodes at {@code nodePos} and otherPos intersect.
	 * <br>
	 * This function is used to figure out at which point the node at {@code otherPos} blocks the node at
	 * {@code nodePos} from moving further if it was to try to move in a straight line from {@code nodePos}
	 * to {@code requestedPos}.
	 * <br>
	 * If the node is never blocked along its path (or if the math somehow breaks terribly),
	 * the function will simply return {@code requestedPos}.
	 *
	 * @param requestedPos The position the node would like to move to
	 * @param nodePos The starting position of the node
	 * @param otherPos The position of the other node that collides.
	 * @return The computed position of intersection
	 */
	public static PVector closestPointToOtherNode(PVector requestedPos, PVector nodePos, PVector otherPos) {
		/*
		The vector maths for this took me hours to figure out, and I really don't think I can
		explain it within the confines of a comment like this. If necessary, I could provide an
		explanatory geogebra file (the same one I used to come up with the formula).
		 */
		final PVector nm = requestedPos.copy().sub(nodePos);
		final PVector on = otherPos.copy().sub(nodePos);

		final float a = nm.magSq();
		final float b = -2 * nm.dot(on);
		final float c = on.magSq() - 4 * NODE_RADIUS * NODE_RADIUS;

		final float sqrt = (float) Math.sqrt(b * b - 4 * a * c);
		if (Float.isNaN(sqrt)) return requestedPos;
		final float k = (-b - sqrt) / (2 * a);

		final PVector offset = nm.copy().mult(k);
		return nodePos.copy().add(offset);
	}
}
