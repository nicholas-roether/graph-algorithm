package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Animation;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.Element;
import io.github.nicholas_roether.draw.bounded.CircularComponent;
import io.github.nicholas_roether.elements.IconPlaque;
import io.github.nicholas_roether.elements.NodeElement;
import io.github.nicholas_roether.general.EdgeData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics_graph.NodePhysics;
import processing.core.PImage;
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

	private static final float PLAQUE_OFFSET = 20;

	private static PImage startIcon;
	private static PImage goalIcon;

	/**
	 * The radius of the nodes in pixels.
	 */
	public static final float NODE_RADIUS = NodePhysics.RADIUS;

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

	public final Graph<NodeData, EdgeData> graph;

	/**
	 * Whether this node is an anchor node, meaning it isn't affected by physics
	 */
	public final boolean anchor;

	/**
	 * The physics object for this node.
	 *
	 * @see NodePhysics
	 */
	private final NodePhysics physics;

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

	private final Animation animation = new Animation(0.25, Animation.EASE_IN, 2);

	private float screenWidth;
	private float screenHeight;

	public NodeComponent(GraphNode<NodeData> node, Graph<NodeData, EdgeData> graph, boolean anchor) {
		super(Z_INDEX);
		this.node = node;
		this.anchor = anchor;
		this.graph = graph;
		physics = new NodePhysics(node, graph);
		// Disable physics if the node is an anchor
		if (anchor) physics.setDisabled(true);
	}

	public NodePhysics getPhysics() {
		return physics;
	}

	@Override
	protected void init() {
		physics.setScreenWidth(p.width);
		physics.setScreenHeight(p.height);
		screenWidth = p.width;
		screenHeight = p.height;
		if (startIcon == null) startIcon = p.loadImage("start_icon.png");
		if (goalIcon == null) goalIcon = p.loadImage("goal_icon.png");
	}

	@Override
	public void frame() {
		if (dragging) {
			moveToMouse();
			node.data.setVelocity(new PVector());
			node.data.setVelocity(new PVector());
		}
		if (node.data.getState() == NodeData.State.CHECKING)
			animation.step(1 / p.frameRate);
		else animation.restart();
	}

	@Override
	public void draw() {
		final Element nodeElement = new NodeElement(
				node.data.getState(),
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

		if (node.data.getState() == NodeData.State.CHECKING) {
			float radius = Document.lerp(NODE_RADIUS * 1.2f, NODE_RADIUS * 1.8f, (float) animation.getProgress());
			float opacity = 255 * (1 - (float) animation.getProgress());
			p.stroke(85, 205, 244, opacity);
			p.strokeWeight(2);
			p.fill(0, 0, 0, 0);
			p.ellipseMode(RADIUS);
			p.circle(getX(), getY(), radius);
		}

		if (node.data.isStart()) {
			final Element plaque = new IconPlaque(startIcon, getX() + PLAQUE_OFFSET, getY() - PLAQUE_OFFSET);
			plaque.draw(p);
		} else if (node.data.isGoal()) {
			final Element plaque = new IconPlaque(goalIcon, getX() + PLAQUE_OFFSET, getY() - PLAQUE_OFFSET);
			plaque.draw(p);
		}
	}

	private void moveToMouse() {
		// Move the node to the mouse if it is being dragged and the new position isn't inside another node
		// or outside the screen
		PVector targetPos = new PVector(p.mouseX, p.mouseY);
		if (targetPos.x < NODE_RADIUS) targetPos.x = NODE_RADIUS;
		if (targetPos.x > screenWidth - NODE_RADIUS) targetPos.x = screenWidth - NODE_RADIUS;
		if (targetPos.y < NODE_RADIUS) targetPos.y = NODE_RADIUS;
		if (targetPos.y > screenHeight - NODE_RADIUS) targetPos.y = screenHeight - NODE_RADIUS;
		node.data.setPosition(targetPos);
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
			// The node has started to be dragged
			dragging = true;
		}
	}

	@Override
	public void mouseReleasedAnywhere(MouseEvent event) {
		// The left button is still being pressed; ignore the event.
		if (event.getButton() == LEFT) return;

		if (dragging) {
			// If the node used to be dragged, make sure it doesn't land inside another node.
			for (GraphNode<NodeData> other : graph.getNodes()) {
				if (other.equals(node)) continue;
				final PVector offset = node.data.getPosition().copy().sub(other.data.getPosition());
				final float distance = offset.mag();
				if (distance < 2 * NODE_RADIUS) {
					PVector delta = offset.copy().mult(2 * NODE_RADIUS / distance - 1);
					node.data.getPosition().add(delta);
				}
			}
			// If the node used to be dragged and there is a mouse velocity tracked, set the node's
			// velocity according to MOUSE_VEL_TRANSLATION
			if(mouseVelocity != null) physics.setVelocity(mouseVelocity.copy().mult(MOUSE_VEL_TRANSLATION));
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
		final PVector on = nodePos.copy().sub(otherPos);

		final float a = nm.magSq();
		final float b = 2 * on.dot(nm);
		final float c = on.magSq() - 4 * NODE_RADIUS * NODE_RADIUS;

		final float sqrt = (float) Math.sqrt(b * b - 4 * a * c);
		final float k = (-b - sqrt) / (2 * a);

		System.out.println("a: " + a + "; b: " + b + "; c: " + c);
		System.out.println("sqrt: " + sqrt + "; k: " + k);

		final PVector offset = nm.copy().mult(k);
		if (Float.isNaN(offset.x) || Float.isNaN(offset.y)) return nodePos; // something went wrong
		return nodePos.copy().add(offset);
	}
}
