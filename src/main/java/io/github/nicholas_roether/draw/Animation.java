package io.github.nicholas_roether.draw;

import java.util.function.Function;

public class Animation {
	public static final EasingFunction LINEAR = time -> time;
	public static final EasingFunction EASE_IN = time -> 1 - Math.cos(time * Math.PI / 2);
	public static final EasingFunction EASE_OUT = time -> Math.sin(time * Math.PI / 2);
	public static final EasingFunction EASE_IN_OUT = time -> (1 - Math.cos(time * Math.PI)) / 2;

	public final double duration;
	private final EasingFunction easingFunction;
	private final boolean repetition;
	private double time;

	public Animation(double duration, EasingFunction easingFunction, boolean repetition) {
		this.duration = duration;
		this.easingFunction = easingFunction;
		this.repetition = repetition;
		EasingFunction test;
	}

	public void step(double amount) {
		if (time <= 1) {
			time += amount / duration;
		} else if (repetition) {
			time %= 1;
		} else {
			time = 1;
		}
	}

	public void restart() {
		time = 0;
	}

	public double getProgress() {
		return Math.min(Math.max(easingFunction.apply(time), 0), 1);
	}

	public interface EasingFunction extends Function<Double, Double> {}
}
