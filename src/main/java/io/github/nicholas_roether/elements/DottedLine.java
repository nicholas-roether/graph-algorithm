package io.github.nicholas_roether.elements;

import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.Element;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;

public class DottedLine extends Element {
	private final float x1;
	private final float y1;
	private final float x2;
	private final float y2;

	public DottedLine(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}


	@Override
	public void draw(@NotNull Document p) {
		final PVector start = new PVector(x1, y1);
		final PVector end = new PVector(x2, y2);
		final PVector line = end.copy().sub(start);
		final float length = line.mag();
		for (float i = 0; i < length; i += 20) {
			final PVector position = start.copy().add(line.copy().mult(i / length));
			final PVector to = position.add(line.copy().mult(10 / length));
			p.line(position.x, position.y, to.x, to.y);
		}
	}
}
