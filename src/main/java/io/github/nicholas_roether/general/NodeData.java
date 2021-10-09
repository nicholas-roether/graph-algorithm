package io.github.nicholas_roether.general;

import io.github.nicholas_roether.algorithm.AStarNodeData;
import io.github.nicholas_roether.physics_graph.PhysicsNodeData;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class NodeData extends AStarNodeData implements PhysicsNodeData {
	private PVector position = new PVector();
	private PVector velocity = new PVector();
	private PVector acceleration = new PVector();
	private State state = State.DEFAULT;
	private boolean start = false;
	private boolean goal = false;

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

	@Override
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("position", vectorToJSON(position));
		obj.put("velocity", vectorToJSON(velocity));
		obj.put("acceleration", vectorToJSON(acceleration));
		obj.put("state", state.name());
		obj.put("start", start);
		obj.put("goal", goal);
		return obj;
	}

	public enum State {
		DEFAULT,
		VISITED,
		CURRENT,
		CHECKING,
		FINAL
	}

	public static NodeData fromJSON(JSONObject json) {
		final PVector position = vectorFromJSON(json.getJSONArray("position"));
		final PVector velocity = vectorFromJSON(json.getJSONArray("velocity"));
		final PVector acceleration = vectorFromJSON(json.getJSONArray("acceleration"));
		final State state = State.valueOf(json.getString("state", "DEFAULT"));
		final boolean start = json.getBoolean("start", false);
		final boolean goal = json.getBoolean("goal", false);

		final NodeData nodeData = new NodeData();
		nodeData.setPosition(position);
		nodeData.setVelocity(velocity);
		nodeData.setAcceleration(acceleration);
		nodeData.setState(state);
		nodeData.setStart(start);
		nodeData.setGoal(goal);

		return nodeData;
	}

	private static JSONArray vectorToJSON(PVector vector) {
		JSONArray array = new JSONArray();
		array.append(vector.x);
		array.append(vector.y);
		return array;
	}

	private static PVector vectorFromJSON(JSONArray array) {
		final float x = array.getFloat(0, 0.0f);
		final float y = array.getFloat(1, 0.0f);
		return new PVector(x, y);
	}
}
