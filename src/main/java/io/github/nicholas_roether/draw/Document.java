package io.github.nicholas_roether.draw;

import processing.core.PApplet;

import javax.lang.model.type.NullType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Document extends SimpleComponent<NullType> {
	private int bgColor;

	private final List<Component<?>> children;

	public Document() {
		super();
		children = new ArrayList<>();
	}

	public void fill(Collection<? extends Component<?>> components) {
		children.clear();
		children.addAll(components);
	}

	@Override
	public void setup(PApplet proc) {
		bgColor = proc.color(0, 0, 0);
	}

	@Override
	protected List<Component<?>> build(PApplet proc) {
		proc.background(bgColor);
		return children;
	}
}
