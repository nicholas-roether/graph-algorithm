package io.github.nicholas_roether.draw.cursor;

/**
 * A cursor instruction used internally by the CursorManager,
 * associating some cursor type with the z-index of the component
 * that requested it.
 */
public class CursorInstruction {
	/**
	 * The z-index of the component that requested the cursor type.
	 */
	public final int zIndex;

	/**
	 * The requested cursor type.
	 */
	public final int cursor;

	public CursorInstruction(int zIndex, int cursor) {
		this.zIndex = zIndex;
		this.cursor = cursor;
	}
}
