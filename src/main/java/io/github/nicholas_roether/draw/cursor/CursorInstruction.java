package io.github.nicholas_roether.draw.cursor;

import io.github.nicholas_roether.draw.Component;

public class CursorInstruction {
	public final int zIndex;
	public final int cursor;

	public CursorInstruction(int zIndex, int cursor) {
		this.zIndex = zIndex;
		this.cursor = cursor;
	}
}
