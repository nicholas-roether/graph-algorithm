package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.graph.Graph;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics_graph.NodePhysics;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;


public class NodeComponent extends Component {
	public static final int Z_INDEX = 2;
	public static final float RADIUS = 15;
	public static final float TEXT_SIZE = 25;

	public final GraphNode<PVector> node;
	public final boolean anchor;

	public final NodePhysics physics;

	private boolean hover = false;
	private boolean dragging = false;

	public NodeComponent(GraphNode<PVector> node, Graph<PVector, Object> graph, boolean anchor) {
		super(Z_INDEX);
		this.node = node;
		this.anchor = anchor;
		this.physics = new NodePhysics(node, graph);
		if (anchor) physics.setDisabled(true);
	}

	@Override
	public void draw(@NotNull PApplet p) {
		if (hover) p.fill(140, 245, 256);
		else if (anchor) p.fill(85, 205, 244);
		else p.fill(255, 255, 255);
		p.stroke(100, 100, 100);
		p.strokeWeight(3);
		p.ellipse(physics.getPosition().x, physics.getPosition().y, 2 * RADIUS, 2 * RADIUS);

		p.fill(0, 0, 0);
		p.textAlign(CENTER);
		p.textSize(TEXT_SIZE);
		p.text(node.name, physics.getPosition().x, physics.getPosition().y + TEXT_SIZE / 3);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		hover = checkInBounds(event.getX(), event.getY());
		if (dragging) {
			physics.setPosition(new PVector(event.getX(), event.getY()));
			physics.setDisabled(true);
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (checkInBounds(event.getX(), event.getY()))
			dragging = true;
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		dragging = false;
		physics.setDisabled(anchor);
	}

	@Override
	public int instructCursor(float x, float y) {
		if (dragging) return MOVE;
		if (checkInBounds(x, y)) {
			return HAND;
		}
		return super.instructCursor(x, y);
	}

	private boolean checkInBounds(float x, float y) {
		final PVector dist = new PVector(x, y);
		dist.sub(physics.getPosition());

		return dist.magSq() <= RADIUS * RADIUS;
	}
}
