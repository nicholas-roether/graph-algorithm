package io.github.nicholas_roether.draw;

import java.util.Collection;

public abstract class SquareComponent<S> extends Component<S> {
	public SquareComponent() {
		super();
	}

	@Override
	public boolean isInBounds(float x, float y) {
		final float delX = x - getX();
		final float delY = y - getY();
		return 0 <= delX && delX <= getWidth() && 0 <= delY && delY <= getHeight();
	}

	public abstract float getX();

	public abstract float getY();

	public abstract float getWidth();

	public abstract float getHeight();

}
