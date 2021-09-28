package io.github.nicholas_roether.draw;

import java.util.Collection;

public abstract class SquareComponent extends Component {
	public SquareComponent() {
		super();
	}

	public SquareComponent(Collection<? extends  Component> children) {
		super(children);
	}

	@Override
	public boolean isContained(float x, float y) {
		final float delX = x - getX();
		final float delY = y - getY();
		return 0 <= delX && delX <= getWidth() && 0 <= delY && delY <= getHeight();
	}

	public abstract float getX();

	public abstract float getY();

	public abstract float getWidth();

	public abstract float getHeight();

}
