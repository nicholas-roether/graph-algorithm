package io.github.nicholas_roether.draw.bounded;

import processing.core.PVector;

public abstract class RectangularComponent extends BoundedComponent {
	public RectangularComponent() {
		super();
	}

	public RectangularComponent(int zIndex) {
		super(zIndex);
	}

	public abstract float getWidth();

	public abstract float getHeight();

	public abstract float getX();

	public abstract float getY();

	@Override
	public boolean checkInBounds(float x, float y) {
		final PVector position = new PVector(getX(), getY());
		final PVector offset = new PVector(x, y);
		offset.sub(position);

		float width = getWidth();
		float height = getHeight();

		return 0 <= offset.x && offset.x <= width && 0 <= offset.y && offset.y <= height;
	}
}
