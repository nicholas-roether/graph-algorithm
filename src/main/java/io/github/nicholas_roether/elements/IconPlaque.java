package io.github.nicholas_roether.elements;

import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.Element;
import org.jetbrains.annotations.NotNull;
import processing.core.PImage;

public class IconPlaque extends Element {
	public static final float PADDING = -7;
	private final PImage icon;
	private final float x;
	private final float y;

	public IconPlaque(PImage icon, float x, float y) {
		this.icon = icon;
		this.x = x;
		this.y = y;
	}

	@Override
	public void draw(@NotNull Document p) {
		final float radius = Math.max(icon.width, icon.height) + PADDING;
		p.stroke(0, 0, 0, 0);
		p.ellipseMode(RADIUS);
		p.fill(50);
		p.circle(x, y, radius);
		p.imageMode(CENTER);
		p.image(icon, x, y);
	}
}
