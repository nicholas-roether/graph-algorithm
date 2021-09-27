package io.github.nicholas_roether.draw;

import processing.core.PVector;

public abstract class CircleComponent extends Component {
	protected float x;
	protected float y;
	protected float radius;

	public CircleComponent() {
		super();
	}

	public CircleComponent(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	@Override
	public boolean isContained(float x, float y) {
		final float distanceSqr = new PVector(this.x, this.y).dist(new PVector(x, y));
		return distanceSqr <= radius * radius;
	}
}
