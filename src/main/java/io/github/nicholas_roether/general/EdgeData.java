package io.github.nicholas_roether.general;

import io.github.nicholas_roether.JSONSerializable;
import processing.data.JSONObject;

public class EdgeData implements JSONSerializable {
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

	@Override
	public JSONObject toJSON() {
		final JSONObject obj = new JSONObject();
		obj.put("state", state.name());
		return obj;
	}

	public static EdgeData fromJSON(JSONObject object) {
		final State state = State.valueOf(object.getString("state", "DEFAULT"));
		final EdgeData edgeData = new EdgeData();
		edgeData.setState(state);
		return edgeData;
	}
}
