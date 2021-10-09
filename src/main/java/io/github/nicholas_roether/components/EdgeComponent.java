package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Animation;
import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.general.EdgeData;
import io.github.nicholas_roether.general.NodeData;
import io.github.nicholas_roether.graph.GraphEdge;
import io.github.nicholas_roether.graph.GraphNode;
import processing.core.PVector;

/**
 * The graphical component representing an edge within the rendered graph
 *
 * @see Component
 */
public class EdgeComponent extends Component {
	// Edges are drawn on layer 1.
	public static final int Z_INDEX = 1;
	private static final double ANIMATION_PERIOD = 0.3;

	public final GraphEdge<NodeData, EdgeData> edge;

	private final Animation checkBeamStart = new Animation(ANIMATION_PERIOD, Animation.EASE_IN, 2);
	private final Animation checkBeamEnd = new Animation(ANIMATION_PERIOD, Animation.EASE_OUT, 2);
	private boolean checking = false;

	public EdgeComponent(GraphEdge<NodeData, EdgeData> edge) {
		super(Z_INDEX);
		this.edge = edge;
	}

	@Override
	public void frame() {
		if (edge.data.getState() == EdgeData.State.CHECKING) {
			if (!checking) {
				checkBeamStart.restart();
				checkBeamEnd.restart();
				checking = true;
			}
			checkBeamStart.step(1.0 / p.frameRate);
			checkBeamEnd.step(1.0 / p.frameRate);
		} else {
			checking = false;
		}
	}

	@SuppressWarnings("Unchecked")
	@Override
	public void draw() {
		// Get the position vectors for the nodes the edge connects
		final PVector nodePos0 = edge.nodes.getValue0().data.getPosition();
		final PVector nodePos1 = edge.nodes.getValue1().data.getPosition();

		switch (edge.data.getState()) {
			case DEFAULT, CHECKING -> p.stroke(100);
			case CHOSEN -> p.stroke(0xFFF28526);
			case CURRENT -> p.stroke(0xFFF2262D);
			case FINAL -> p.stroke(0xFF4FF226);
		}

		// Draw a line with 3 pixels width between the nodes
		p.strokeWeight(3);
		p.line(nodePos0.x, nodePos0.y, nodePos1.x, nodePos1.y);


		if (edge.data.getState() == EdgeData.State.CHECKING) {
			GraphNode<NodeData> from = edge.nodes.getValue0();
			GraphNode<NodeData> to = edge.nodes.getValue1();
			if (edge.nodes.getValue1().data.getState() == NodeData.State.CHECKING) {
				from = edge.nodes.getValue1();
				to = edge.nodes.getValue0();
			}

			final PVector fromPos = from.data.getPosition();
			final PVector toPos = to.data.getPosition();

			final float startX = Document.lerp(fromPos.x, toPos.x, (float) checkBeamStart.getProgress());
			final float startY = Document.lerp(fromPos.y, toPos.y, (float) checkBeamStart.getProgress());

			final float endX = Document.lerp(fromPos.x, toPos.x, (float) checkBeamEnd.getProgress());
			final float endY = Document.lerp(fromPos.y, toPos.y, (float) checkBeamEnd.getProgress());

			p.stroke(85, 205, 244);
			p.line(startX, startY, endX, endY);
		}
	}
}
