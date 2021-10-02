package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.graph.GraphEdge;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PVector;

public class EdgeLabel extends Component {
	public static final int Z_INDEX = 3;

	public final GraphEdge<PVector, Object> edge;

	public EdgeLabel(GraphEdge<PVector, Object> edge) {
		super(Z_INDEX);
		this.edge = edge;
	}

	@Override
	public void draw(@NotNull PApplet p) {
		final PVector from = edge.nodes.getValue0().getData();
		final PVector to = edge.nodes.getValue1().getData();
		final PVector center = from.copy().add(to).div(2);

		p.textSize(15);
		p.fill(255, 255, 255);
		p.textAlign(CENTER);
		p.text(Double.toString(edge.weight), center.x, center.y);
	}
}
