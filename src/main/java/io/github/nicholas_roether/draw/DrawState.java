package io.github.nicholas_roether.draw;

import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

import java.util.function.Consumer;

public class DrawState {
	private final Consumer<PApplet> applier;

	public DrawState(@NotNull Consumer<PApplet> applier) {
		this.applier = applier;
	}

	public void apply(PApplet p) {
		applier.accept(p);
	}
}
