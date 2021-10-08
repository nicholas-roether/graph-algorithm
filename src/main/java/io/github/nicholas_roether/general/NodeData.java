package io.github.nicholas_roether.general;

import io.github.nicholas_roether.physics_graph.PhysicsNodeData;
import processing.core.PVector;

public class NodeData implements PhysicsNodeData {
	private PVector position = new PVector();
	private PVector velocity = new PVector();
	private PVector acceleration = new PVector();
	private State state = State.DEFAULT;
	private boolean start = false;
	private boolean goal = false;
	private boolean needsChecking = false;

	@Override
	public PVector getPosition() {
		return position;
	}

	@Override
	public PVector getVelocity() {
		return velocity;
	}

	@Override
	public PVector getAcceleration() {
		return acceleration;
	}

	public State getState() {
		return state;
	}

	public boolean isStart() {
		return start;
	}

	public boolean isGoal() {
		return goal;
	}

	public boolean needsChecking() {
		return needsChecking;
	}

	@Override
	public void setPosition(PVector position) {
		this.position = position;
	}

	@Override
	public void setVelocity(PVector velocity) {
		this.velocity = velocity;
	}

	@Override
	public void setAcceleration(PVector acceleration) {
		this.acceleration = acceleration;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public void setGoal(boolean goal) {
		this.goal = goal;
	}

	public void setNeedsChecking(boolean needsChecking) {
		this.needsChecking = needsChecking;
	}

	public enum State {
		DEFAULT,
		VISITED,
		CURRENT,
		CHECKING,
		FINAL
	}
}
