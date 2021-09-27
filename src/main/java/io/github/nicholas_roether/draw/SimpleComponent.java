package io.github.nicholas_roether.draw;

import processing.core.PApplet;

public abstract class SimpleComponent extends Component {
	@Override
	public boolean isContained(float x, float y) {
		return true;
	}
}
