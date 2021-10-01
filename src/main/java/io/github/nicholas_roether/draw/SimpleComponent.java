package io.github.nicholas_roether.draw;

import processing.core.PApplet;

import java.util.Collection;

public abstract class SimpleComponent<S> extends Component<S> {
	public SimpleComponent() {
		super();
	}

	@Override
	public boolean isInBounds(float x, float y) {
		return true;
	}
}
