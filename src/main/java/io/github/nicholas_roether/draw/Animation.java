package io.github.nicholas_roether.draw;

import java.util.function.Function;

public class Animation {
	public static final EasingFunction LINEAR = time -> time;
	public static final EasingFunction EASE_IN = time -> 1 - Math.cos(time * Math.PI / 2);
	public static final EasingFunction EASE_OUT = time -> Math.sin(time * Math.PI / 2);
	public static final EasingFunction EASE_IN_OUT = time -> (1 - Math.cos(time * Math.PI)) / 2;

	public final double duration;
	private final EasingFunction easingFunction;
	private final int repetitions;
	private int currentReps = 0;
	private double time = 0;

	public Animation(double duration, EasingFunction easingFunction, int repetitions) {
		this.duration = duration;
		this.easingFunction = easingFunction;
		this.repetitions = repetitions;
	}

	public void step(double amount) {
		if (time < 1) {
			time += amount / duration;
		} else if (repetitions == 0 || currentReps < repetitions - 1) {
			time %= 1;
			currentReps++;
		} else {
			time = 1;
		}
	}

	public void restart() {
		currentReps = 0;
		time = 0;
	}

	public double getProgress() {
		return Math.min(Math.max(easingFunction.apply(time), 0), 1);
	}

	public interface EasingFunction extends Function<Double, Double> {}
}
