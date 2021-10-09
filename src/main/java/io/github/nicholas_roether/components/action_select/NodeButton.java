package io.github.nicholas_roether.components.action_select;

import io.github.nicholas_roether.components.common.ToggleButton;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.Element;
import io.github.nicholas_roether.elements.NodeElement;
import io.github.nicholas_roether.general.NodeData;

/**
 * The icon for adding nodes in the editing menu.
 */
public class NodeButton extends ToggleButton {
	public static final int Z_INDEX = 10;

	public NodeButton(float x, float y) {
		super(Z_INDEX, x, y, 40, 40, 0xFFA0A0A0);
	}

	@Override
	protected void drawLabel() {
		// Display a small node with no title inside the button
		final Element nodeElement = new NodeElement(
				NodeData.State.DEFAULT,
				false,
				false,
				x + width / 2f,
				y + height / 2f,
				width * 0.3f,
				"",
				1,
				255
		);
		nodeElement.draw(p);
	}
}
