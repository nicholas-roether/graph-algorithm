package io.github.nicholas_roether.draw.cursor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The CursorManager is responsible for keeping track of the
 * cursor types that are requested by each component, and determining
 * which one takes precedence.
 * <br>
 * It does so by taking the request from the component with the highest
 * z-index.
 */
public class CursorManager {
	/**
	 * The cursor type to be specified when the component doesn't want to request
	 * any cursor type for itself.
	 */
	public static final int NO_CURSOR_INSTRUCT = -1;

	/**
	 * A list of all current instructions.
	 *
	 * @see CursorInstruction
	 */
	private final List<CursorInstruction> instructions;

	public CursorManager() {
		this.instructions = new ArrayList<>();
	}

	/**
	 * Add an instruction to the manager. If {@code CursorManager.NO_CURSOR_INSTRUCT} is
	 * specified as the cursor type, this call will be ignored.
	 *
	 * @param zIndex The z-index of the component requesting this cursor type
	 * @param cursor The cursor type requested
	 */
	public void addInstruction(int zIndex, int cursor) {
		if (cursor == NO_CURSOR_INSTRUCT) return;
		instructions.add(new CursorInstruction(zIndex, cursor));
	}

	/**
	 * Reset the cursor manager, removing all instructions.
	 */
	public void reset() {
		instructions.clear();
	}

	/**
	 * Returns the cursor currently taking precedence.
	 * @return the cursor currently taking precedence
	 */
	public int getCurrentCursor() {
		instructions.sort(Comparator.comparingInt(i -> i.zIndex));
		return instructions.get(instructions.size() - 1).cursor;
	}
}
