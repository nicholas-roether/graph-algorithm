package io.github.nicholas_roether.draw.bounded;

import processing.core.PVector;

/**
 * A component that is bounded within a circle.
 *
 * @see BoundedComponent
 */
public abstract class CircularComponent extends BoundedComponent {
	public CircularComponent() {
		super();
	}

	public CircularComponent(int zIndex) {
		super(zIndex);
	}

	/**
	 * Returns the component's bounding region's radius.
	 * @return the component's bounding region's radius
	 */
	public abstract float getRadius();

	/**
	 * Returns the x-position of the center of the component's
	 * bounding region.
	 * @return the component's bounding region's x-position
	 */
	public abstract float getX();

	/**
	 * Returns the y-position of the center of the component's
	 * bounding region.
	 * @return the component's bounding region's y-position
	 */
	public abstract float getY();

	@Override
	public boolean checkInBounds(float x, float y) {
		// Get the vector that points from this component's position
		// to the position that should be checked
		final PVector position = new PVector(getX(), getY());
		final PVector offset = new PVector(x, y);
		offset.sub(position);

		final float radius = getRadius();

		// check if the distance to the coordinates is less than the radius
		// (actually, the squared distance is used for performance)
		return offset.magSq() <= radius * radius;
	}
}
