package io.github.nicholas_roether.general;

public class EdgeData {
	private State state = State.DEFAULT;

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public enum State {
		DEFAULT,
		CHECKING,
		CHOSEN,
		CURRENT,
		FINAL
	}
}
