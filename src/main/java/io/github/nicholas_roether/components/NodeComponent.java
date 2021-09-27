package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.CircleComponent;
import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.graph.GraphNode;
import io.github.nicholas_roether.physics_graph.PhysicsNodeData;
import processing.core.PApplet;
import processing.core.PVector;

public class NodeComponent extends CircleComponent {
	public static final float RADIUS = 15;
	public final GraphNode<PhysicsNodeData> node;
	public final boolean anchor;

	public NodeComponent(GraphNode<PhysicsNodeData> node, boolean anchor) {
		super(
				node.getData().getPosition().x,
				node.getData().getPosition().y,
				RADIUS
		);
		this.node = node;
		this.anchor = anchor;
	}

	public NodeComponent(GraphNode<PhysicsNodeData> node) {
		super(
				node.getData().getPosition().x,
				node.getData().getPosition().y,
				RADIUS
		);
		this.node = node;
		this.anchor = false;
	}

	@Override
	public void init(PApplet proc) {
		onMouse("drag", event -> node.getData().setPosition(new PVector(event.getX(), event.getY())));

		super.setup(proc);
	}

	@Override
	public void render(PApplet proc) {
		x = node.getData().getPosition().x;
		y = node.getData().getPosition().y;

		if (anchor) proc.fill(85, 205, 244);
		else proc.fill(255, 255, 255);
		proc.stroke(100, 100, 100);
		proc.strokeWeight(3);
		proc.ellipseMode(CENTER);
		proc.ellipse(x, y, 2 * radius, 2 * radius);

		final float textSize = 25;
		proc.fill(0, 0, 0);
		proc.textAlign(CENTER);
		proc.textSize(textSize);
		proc.text(node.name, x, y + textSize / 3);

		super.draw(proc);
	}
}
