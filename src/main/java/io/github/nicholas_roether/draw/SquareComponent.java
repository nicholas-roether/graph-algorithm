package io.github.nicholas_roether.draw;

public abstract class SquareComponent extends Component {
	protected float x;
	protected float y;
	protected float width;
	protected float height;

	public SquareComponent() {
		super();
	}

	public SquareComponent(float x, float y, float width, float height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean isContained(float x, float y) {
		final float delX = x - this.x;
		final float delY = y - this.y;
		return 0 <= delX && delX <= width && 0 <= delY && delY <= height;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

}
