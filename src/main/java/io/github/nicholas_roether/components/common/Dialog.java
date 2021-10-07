package io.github.nicholas_roether.components.common;

import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.Document;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * A simple popup that asks for user input.
 */
public class Dialog extends Popup {
	/**
	 * A regex string that all values that can be typed into the input need to match.
	 */
	private final String allowedValues;

	/**
	 * A regex pattern that all values that can be returned need to match.
	 */
	private final Pattern allowedReturns;

	/**
	 * The current callback for when the dialog is closed.
	 */
	private Runnable callback;

	/**
	 * The input component that is shown in the dialog.
	 */
	private Input input;

	/**
	 * Constructs a {@code Dialog}.
	 *
	 * @param prompt The title of the dialog, prompting the user for the input.
	 * @param allowedValues A regex string that all values that can be typed into the input need to match.
	 * @param allowedReturns A regex string that all values that can be returned from the dialog need to match.
	 *                       If the user enters a value into the dialog that this regex doesn't match, the "Ok"-Button
	 *                       will be disabled.
	 */
	public Dialog(String prompt, String allowedValues, String allowedReturns) {
		super(prompt);
		this.allowedValues = allowedValues;
		this.allowedReturns = Pattern.compile(allowedReturns);
	}

	/**
	 * Gets the current value of the dialog, meaning the text that has been typed into its input.
	 * <br>
	 * This method is only applicable when the dialog has been built. If this is not the case,
	 * it will return {@code null}.
	 *
	 * @return the current value of the dialog
	 */
	public String getValue() {
		if (input == null) return null;
		return input.getValue();
	}

	/**
	 * Displays the dialog, and calls the given callback function as soon as the user presses "Ok" and closes the
	 * popup, passing to it the value they typed into the input.
	 *
	 * @param callback The callback to call when the dialog is closed
	 */
	public void prompt(Consumer<String> callback) {
		this.callback = () -> {
			callback.accept(getValue());
			this.callback = null;
		};
		setShowing(true);
	}

	@Override
	public void frame(Document p) {
		final String value = getValue();
		if (value == null) return;
		setDisabled(!allowedReturns.matcher(value).find());
	}

	@Override
	public void build(ComponentRegistry registry, Document p) {
		super.build(registry, p);
		setDisabled(true);
		if (input != null) input.setFocused(true);
	}

	@Override
	protected Content buildContent(Document p) {
		return new Popup.Content(40) {
			@Override
			public void build(ComponentRegistry registry, Document p) {
				input = new Input(
						1000,
						x,
						y,
						width,
						height,
						allowedValues,
						0xFFFFFFFF
				);
				registry.register(input, id);
			}
		};
	}

	@Override
	protected List<Option> buildOptions(Document p) {
		return List.of(new Popup.Option("Ok", 0xFF456990, popup -> {
			if (callback != null) callback.run();
			popup.setShowing(false);
		}));
	}

	@Override
	public void keyTyped(KeyEvent event) {
		if (event.getKey() == ENTER) {
			final ActionButton button = buttons.get(0);
			if (button != null) button.mousePressed(
					new MouseEvent(
							new Object(),
							event.getMillis(),
							MouseEvent.PRESS,
							0,
							(int) Math.ceil(button.x),
							(int) Math.ceil(button.y),
							LEFT,
							1
					)
			);
		}
	}
}
