package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.general.EdgeData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.GraphEdge;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

/**
 * The graphical component representing an edge within the rendered graph
 *
 * @see Component
 */
public class EdgeComponent extends Component {
	// Edges are drawn on layer 1.
	public static final int Z_INDEX = 1;

	public final GraphEdge<NodeData, EdgeData> edge;

	public EdgeComponent(GraphEdge<NodeData, EdgeData> edge) {
		super(Z_INDEX);
		this.edge = edge;
	}

	@Override
	public void draw() {
		// Get the position vectors for the nodes the edge connects
		final PVector nodePos0 = edge.nodes.getValue0().data.getPosition();
		final PVector nodePos1 = edge.nodes.getValue1().data.getPosition();

		// Draw a grey (RGB 100/100/100) line with 3 pixels width between the nodes
		p.stroke(100, 100, 100);
		p.strokeWeight(3);
		p.line(nodePos0.x, nodePos0.y, nodePos1.x, nodePos1.y);
	}
}
