package io.github.nicholas_roether.draw.cursor;

import io.github.nicholas_roether.draw.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CursorManager {
	public static final int NO_CURSOR_INSTRUCT = -1;
	private final List<CursorInstruction> instructions;

	public CursorManager() {
		this.instructions = new ArrayList<>();
	}

	public void addInstruction(int zIndex, int cursor) {
		instructions.add(new CursorInstruction(zIndex, cursor));
	}

	public void reset() {
		instructions.clear();
	}

	public int getCurrentCursor() {
		instructions.sort(Comparator.comparingInt(i -> i.zIndex));
		return instructions.get(instructions.size() - 1).cursor;
	}
}
