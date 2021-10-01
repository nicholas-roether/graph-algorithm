package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.SimpleComponent;
import processing.core.PApplet;

import javax.lang.model.type.NullType;
import java.util.List;

public class Label extends SimpleComponent<NullType> {
	public final String message;
	public final float x;
	public final float y;

	public Label(String message, float x, float y) {
		this.message = message;
		this.x = x;
		this.y = y;
	}

	@Override
	protected List<Component<?>> build(PApplet proc) {
		return NO_CHILDREN;
	}

	@Override
	protected void render(PApplet proc) {
		proc.textSize(15);
		proc.fill(255, 255, 255);
		proc.textAlign(CENTER);
		proc.text(message, x, y);
	}
}
