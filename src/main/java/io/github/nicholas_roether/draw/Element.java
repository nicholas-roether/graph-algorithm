package io.github.nicholas_roether.draw;

import org.jetbrains.annotations.NotNull;
import processing.core.PConstants;

/**
 * An element is a drawable that also contains the constants necessary for a comfortable interaction
 * with the drawing functionality.
 * <br>
 * Classes that choose to extend {@code Element} over {@code Component} act as a helper to draw more complicated
 * structures within a component, but that don't require their own component to themselves.
 * <br>
 * Note that pure elements will need to be drawn explicitly within the draw function of some component, and that
 * the draw state will not be automatically reset.
 */
public abstract class Element implements PConstants, Drawable {}
