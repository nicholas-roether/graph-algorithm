package io.github.nicholas_roether.components;

import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.Element;
import io.github.nicholas_roether.draw.bounded.BoundedComponent;
import io.github.nicholas_roether.elements.NodeElement;
import io.github.nicholas_roether.general.GraphWithData;
import io.github.nicholas_roether.general.NodeData;

import io.github.nicholas_roether.graph.GraphNode;
import org.jetbrains.annotations.NotNull;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.function.BiFunction;


public class NodeAdder extends BoundedComponent {
	public static final int Z_INDEX = 100;
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public final GraphWithData graph;

	private final BiFunction<Float, Float, Boolean> bounds;

	private boolean enabled = false;
	private int nameIndex = 0;

	public NodeAdder(GraphWithData graph, BiFunction<Float, Float, Boolean> bounds) {
		super(Z_INDEX);
		this.graph = graph;
		this.bounds = bounds;
	}


	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void draw(@NotNull Document p) {
		if (!enabled || !checkInBounds(p.mouseX, p.mouseY)) return;
		final Element nodeElement = new NodeElement(
				false,
				false,
				p.mouseX,
				p.mouseY,
				NodeComponent.NODE_RADIUS,
				"",
				1,
				100
		);
		nodeElement.draw(p);
	}

	@Override
	public void mouseClickedInBounds(MouseEvent event) {
		if (!enabled) return;
		addNode(event.getX(), event.getY());
	}

	@Override
	public boolean checkInBounds(float x, float y) {
		for (GraphNode<NodeData> node : graph.getNodes()) {
			final float distance = node.data.getPosition().dist(new PVector(x, y));
			if (distance <= 2 * NodeComponent.NODE_RADIUS) return false;
		}
		return bounds.apply(x, y);
	}

	private void addNode(float x, float y) {
		graph.addNode(getNextName(), x, y);
	}

	private String getNextName() {
		final String name = generateNextName();
		for (GraphNode<NodeData> node : graph.getNodes()) {
			if (node.name.equals(name)) return getNextName();
		}
		return name;
	}

	private String generateNextName() {
		final StringBuilder nameBuilder = new StringBuilder();
		final int base = ALPHABET.length();
		int number = nameIndex;
		do {
			int index = number % base;
			if (nameIndex >= base && number < base) index -= 1;
			nameBuilder.append(ALPHABET.charAt(index));
			number /= base;
		} while (number > 0);
		nameIndex++;
		return nameBuilder.reverse().toString();
	}
}
