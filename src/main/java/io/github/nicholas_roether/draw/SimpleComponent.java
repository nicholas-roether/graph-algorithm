package io.github.nicholas_roether.draw;

import processing.core.PApplet;

import java.util.Collection;

public abstract class SimpleComponent extends Component {
	public SimpleComponent() {
		super();
	}

	public SimpleComponent(Collection<? extends Component> children) {
		super(children);
	}

	@Override
	public boolean isContained(float x, float y) {
		return true;
	}
}
