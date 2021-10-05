package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.GraphEdge;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * The graphical component that represents the label of an edge, containing its weight.
 *
 * @see Component
 */
public class EdgeLabel extends Component {
	// Edge labels are drawn on layer 3.
	public static final int Z_INDEX = 3;

	public final GraphEdge<NodeData, Object> edge;

	public EdgeLabel(GraphEdge<NodeData, Object> edge) {
		super(Z_INDEX);
		this.edge = edge;
	}

	@Override
	public void draw(@NotNull PApplet p) {
		// Get the position vectors of the nodes the edge connects, and
		// find the center point between them.
		final PVector from = edge.nodes.getValue0().data.getPosition();
		final PVector to = edge.nodes.getValue1().data.getPosition();
		final PVector center = from.copy().add(to).div(2);

		// Draw white text of 15px size at the center point, displaying the
		// edge's weight.
		p.textSize(20);
		p.fill(255, 255, 255);
		p.textAlign(CENTER);
		p.text(Double.toString(edge.weight), center.x, center.y);
	}
}
