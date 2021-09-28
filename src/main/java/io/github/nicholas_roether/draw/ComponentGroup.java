package io.github.nicholas_roether.draw;

import processing.core.PApplet;

import java.util.Collection;

public class ComponentGroup extends SimpleComponent {
	public ComponentGroup(Collection<? extends Component> children) {
		super(children);
	}

	@Override
	public void render(PApplet proc) {}
}
