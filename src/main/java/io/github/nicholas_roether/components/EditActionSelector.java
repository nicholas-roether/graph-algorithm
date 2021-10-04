package io.github.nicholas_roether.components;

import io.github.nicholas_roether.components.action_select.EdgeButton;
import io.github.nicholas_roether.components.action_select.NodeButton;
import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import processing.core.PApplet;

import java.util.List;

public class EditActionSelector extends Component {
	public final float x;
	public final float y;

	private boolean visible = true;
	private boolean showing = false;

	private NodeButton nodeButton;
	private EdgeButton edgeButton;

	public EditActionSelector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void build(ComponentRegistry registry, PApplet p) {
		showing = visible;
		if (!visible) return;
		nodeButton = new NodeButton(x, y);
		edgeButton = new EdgeButton(x, y + nodeButton.height + 10);
		registry.register(List.of(
				nodeButton,
				edgeButton
		), id);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean shouldRebuild() {
		return showing != visible;
	}
}
