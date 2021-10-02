package io.github.nicholas_roether.draw.bounded;

import processing.core.PVector;

public abstract class CircularComponent extends BoundedComponent {
	public CircularComponent() {
		super();
	}

	public CircularComponent(int zIndex) {
		super(zIndex);
	}

	public abstract float getRadius();

	public abstract float getX();

	public abstract float getY();

	@Override
	public boolean checkInBounds(float x, float y) {
		final PVector position = new PVector(getX(), getY());
		final PVector offset = new PVector(x, y);
		offset.sub(position);

		final float radius = getRadius();

		return offset.magSq() <= radius * radius;
	}
}
