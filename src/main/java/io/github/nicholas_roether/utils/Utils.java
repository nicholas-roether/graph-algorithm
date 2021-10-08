package io.github.nicholas_roether.utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;

public class Utils {
	private static Clipboard clipboard;

	public static Clipboard getClipboard() {
		if (clipboard == null) {
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}
		return clipboard;
	}
}
