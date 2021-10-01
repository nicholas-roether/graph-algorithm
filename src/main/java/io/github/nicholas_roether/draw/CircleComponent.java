package io.github.nicholas_roether.draw;

import processing.core.PVector;

import java.util.Collection;

public abstract class CircleComponent<S> extends Component<S> {
	public CircleComponent() {
		super();
	}

	@Override
	public boolean isInBounds(float x, float y) {
		final float distance = new PVector(getX(), getY()).dist(new PVector(x, y));
		return distance <= getRadius();
	}

	public abstract float getX();

	public abstract float getY();

	public abstract float getRadius();
}
