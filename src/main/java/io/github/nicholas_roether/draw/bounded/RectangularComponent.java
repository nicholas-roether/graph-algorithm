package io.github.nicholas_roether.draw.bounded;

import processing.core.PVector;

/**
 * A component that is bounded within a rectange.
 *
 * @see BoundedComponent
 */
public abstract class RectangularComponent extends BoundedComponent {
	public RectangularComponent() {
		super();
	}

	public RectangularComponent(int zIndex) {
		super(zIndex);
	}

	/**
	 * Returns the component's bounding region's width.
	 * @return the component's bounding region's width
	 */
	public abstract float getWidth();

	/**
	 * Returns the component's bounding region's width.
	 * @return the component's bounding region's width
	 */
	public abstract float getHeight();

	/**
	 * Returns the x-position of the upper-left corner of the
	 * components bounding region.
	 *
	 * @return the x-position of the component's bounding region
	 */
	public abstract float getX();

	/**
	 * Returns the y-position of the upper-left corner of the
	 * components bounding region.
	 *
	 * @return the y-position of the component's bounding region
	 */
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
