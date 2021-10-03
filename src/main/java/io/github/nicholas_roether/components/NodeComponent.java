package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.bounded.CircularComponent;
import io.github.nicholas_roether.graph.Graph;
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

	public final GraphNode<PVector> node;

	/**
	 * Whether this node is an anchor node, meaning it isn't affected by physics
	 */
	public final boolean anchor;

	/**
	 * The physics object for this node.
	 *
	 * @see NodePhysics
	 */
	public final NodePhysics physics;

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

	/**
	 * How many mouse buttons are pressed additionally to the left one while dragging.
	 * Useful for determining when a node has stopped being dragged.
	 */
	private int additionalMouseButtons = 0;

	public NodeComponent(GraphNode<PVector> node, Graph<PVector, Object> graph, boolean anchor) {
		super(Z_INDEX);
		this.node = node;
		this.anchor = anchor;
		this.physics = new NodePhysics(node, graph);
		// Disable physics if the node is an anchor
		if (anchor) physics.setDisabled(true);
	}

	@Override
	public void draw(@NotNull PApplet p) {
		// If the mouse is hovering over the node, fill it light blue
		if (checkInBounds(p.mouseX, p.mouseY)) p.fill(140, 245, 256);
		// If the node is an anchor, fill it with a darker ble
		else if (anchor) p.fill(85, 205, 244);
		// Otherwise, fill it with white.
		else p.fill(255, 255, 255);

		// Have a grey outline of 3px width around the node
		p.stroke(100, 100, 100);
		p.strokeWeight(3);

		// Draw a circle with the node's radius and the set filling and outline
		p.circle(physics.getPosition().x, physics.getPosition().y, 2 * NODE_RADIUS);

		// Draw center-aligned text with the node's name on top
		p.fill(0, 0, 0);
		p.textAlign(CENTER, CENTER);
		p.textSize(getTextSizeForLength(node.name.length())); // The text is scaled down when longer
		p.textLeading(7); // Lower line height
		p.text(
				getMessage(node.name),
				physics.getPosition().x,
				physics.getPosition().y - p.textAscent() * 0.13f // A hack to fix text positioning, don't worry about it
		);
	}

	@Override
	public void mouseMovedAnywhere(MouseEvent event) {
		if (dragging) {
			// Move the node to the mouse if it is being dragged
			physics.setPosition(new PVector(event.getX(), event.getY()));
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

	/**
	 * Gets the appropriate text size for a node name with a given length.
	 *
	 * @param length The node name length
	 * @return the computed appropriate size, in pixels
	 */
	private float getTextSizeForLength(int length) {
		// the size is TEXT_SIZE when the length is 1, otherwise it is lower up until
		// a minimum size which is reached at a length of four characters,
		// according to the somewhat arbitrary function below.
		return TEXT_SIZE / (0.8f * Math.min(node.name.length(), 4) + 0.2f);
	}

	/**
	 * Formats the given node name into a message to be displayed on the screen.
	 *
	 * This consists of inserting newlines every four characters, to more efficiently use the
	 * space within the node.
	 *
	 * @param name The node name
	 * @return the formatted message
	 */
	private String getMessage(String name) {
		// String formatting garbage that I can't be bothered explaining - It
		// really just inserts newlines every four characters
		final List<String> sections = new ArrayList<>((int) Math.ceil((double) name.length() / 4.0));
		final StringBuilder partBuilder = new StringBuilder(4);
		for (int i = 0; i < name.length(); i++) {
			partBuilder.append(name.charAt(i));
			if (i % 4 == 3 || i == name.length() - 1) {
				sections.add(partBuilder.toString());
				partBuilder.setLength(0);
			}
		}
		final StringBuilder messageBuilder = new StringBuilder(32);
		for (int i = 0; i < sections.size(); i++) {
			messageBuilder.append(sections.get(i));
			if (i < sections.size() - 1)
				messageBuilder.append("\n");
		}
		return messageBuilder.toString();
	}
}
