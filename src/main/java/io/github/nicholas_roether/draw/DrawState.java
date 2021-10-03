package io.github.nicholas_roether.draw;

import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

import java.util.function.Consumer;

/**
 * A DrawState contains a set of instructions that can be applied to a PApplet at any time,
 * resetting it to some desired state.
 */
public class DrawState {
	/**
	 * The callback function that applies the instructions
	 */
	private final Consumer<PApplet> applier;

	public DrawState(@NotNull Consumer<PApplet> applier) {
		this.applier = applier;
	}

	/**
	 * Applies the instructed state to the given PApplet.
	 *
	 * @param p The PApplet to apply the state to
	 */
	public void apply(PApplet p) {
		applier.accept(p);
	}
}
