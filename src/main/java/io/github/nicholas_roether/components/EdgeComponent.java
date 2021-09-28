package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.SimpleComponent;
import io.github.nicholas_roether.graph.GraphEdge;
import processing.core.PApplet;
import processing.core.PVector;

public class EdgeComponent extends SimpleComponent {
	public final GraphEdge<PVector, ?> edge;

	public EdgeComponent(GraphEdge<PVector, ?> edge) {
		super();
		this.edge = edge;
	}

	@Override
	public void render(PApplet proc) {
		final PVector nodePos0 = edge.nodes.getValue0().getData();
		final PVector nodePos1 = edge.nodes.getValue1().getData();

		proc.stroke(100, 100, 100);
		proc.strokeWeight(3);
		proc.line(nodePos0.x, nodePos0.y, nodePos1.x, nodePos1.y);
	}

	public PVector getCenter() {
		final PVector nodePos0 = edge.nodes.getValue0().getData();
		final PVector nodePos1 = edge.nodes.getValue1().getData();
		return nodePos0.copy().add(nodePos1).mult(0.5f);
	}
}
