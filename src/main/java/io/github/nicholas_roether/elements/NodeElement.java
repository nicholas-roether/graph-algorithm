package io.github.nicholas_roether.elements;

import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.Element;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NodeElement extends Element {
	private final boolean hovering;
	private final boolean anchor;
	private final float x;
	private final float y;
	private final float radius;
	private final String message;
	private final float textSize;
	private final float opacity;

	public NodeElement(boolean hovering, boolean anchor, float x, float y, float radius, String message, float textSize, float opacity) {
		this.hovering = hovering;
		this.anchor = anchor;
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.message = message;
		this.textSize = textSize;
		this.opacity = opacity;
	}

	@Override
	public void draw(@NotNull Document p) {
		// If the mouse is hovering over the node, fill it light blue
		if (hovering) p.fill(140, 245, 256, opacity);
			// If the node is an anchor, fill it with a darker ble
		else if (anchor) p.fill(85, 205, 244, opacity);
			// Otherwise, fill it with white.
		else p.fill(255, 255, 255, opacity);

		// Have a grey outline of 3px width around the node
		p.stroke(100, 100, 100, opacity);
		p.strokeWeight(3);

		// Draw a circle with the node's radius and the set filling and outline
		p.circle(x, y, 2 * radius);

		// Draw center-aligned text with the node's name on top
		p.fill(0, 0, 0);
		p.textAlign(CENTER, CENTER);
		p.textSize(getTextSizeForLength()); // The text is scaled down when longer
		p.textLeading(7); // Lower line height
		p.text(
				getMessage(message),
				x,
				y - p.textAscent() * 0.13f // A hack to fix text positioning, don't worry about it
		);
	}

	/**
	 * Gets the appropriate text size for the node name.
	 *
	 * @return the computed appropriate size, in pixels
	 */
	private float getTextSizeForLength() {
		// the size is TEXT_SIZE when the length is 1, otherwise it is lower up until
		// a minimum size which is reached at a length of four characters,
		// according to the somewhat arbitrary function below.
		return textSize / (0.8f * Math.min(message.length(), 4) + 0.2f);
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
