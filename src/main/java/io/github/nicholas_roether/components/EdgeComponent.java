package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.graph.GraphEdge;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PVector;

public class EdgeComponent extends Component {
	public static final int Z_INDEX = 1;

	public final GraphEdge<PVector, Object> edge;

	public EdgeComponent(GraphEdge<PVector, Object> edge) {
		super(Z_INDEX);
		this.edge = edge;
	}

	@Override
	public void draw(@NotNull PApplet p) {
		final PVector nodePos0 = edge.nodes.getValue0().getData();
		final PVector nodePos1 = edge.nodes.getValue1().getData();

		p.stroke(100, 100, 100);
		p.strokeWeight(3);
		p.line(nodePos0.x, nodePos0.y, nodePos1.x, nodePos1.y);
	}
}
