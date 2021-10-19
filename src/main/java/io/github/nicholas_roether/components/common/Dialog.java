package io.github.nicholas_roether.components.common;

import io.github.nicholas_roether.draw.ComponentRegistry;
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
	 * The height of the text box for the prompt (the content of the popup)
	 */
	private static final float CONTENT_TEXT_HEIGHT = 23;

	/**
	 * The padding around the content of the popup
	 */
	private static final float CONTENT_PADDING = 15;

	/**
	 * The content text of the dialog, prompting the user for the input.
	 */
	public final String prompt;

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
	 * @param title The title  of the dialog
	 * @param prompt The content text of the dialog, prompting the user for the input.
	 * @param allowedValues A regex string that all values that can be typed into the input need to match.
	 * @param allowedReturns A regex string that all values that can be returned from the dialog need to match.
	 *                       If the user enters a value into the dialog that this regex doesn't match, the "Ok"-Button
	 *                       will be disabled.
	 */
	public Dialog(String title, String prompt, String allowedValues, String allowedReturns) {
		super(title);
		this.prompt = prompt;
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
		// Set the current callback to one that accepts the callback as a parameter, and then resets itself.
		this.callback = () -> {
			callback.accept(getValue());
			this.callback = null;
		};
		// Show the popup
		setShowing(true);
	}

	@Override
	public void frame() {
		final String value = getValue();
		if (value == null) return; // If the value is still null, do nothing.
		// Disable or enable the popup return button depending on if the current value matches the regex for allowed
		// returns.
		setDisabled(!allowedReturns.matcher(value).find());
	}

	@Override
	public void build(ComponentRegistry registry) {
		super.build(registry);
		setDisabled(true); // Initially disable the return button
		if (input != null) input.setFocused(true); // Initially focus the text input
	}

	@Override
	protected Content buildContent() {
		// The content of the popup consists of the input element and the prompt text above it.
		return new Popup.Content(CONTENT_TEXT_HEIGHT + CONTENT_PADDING + 40) {
			@Override
			public void build(ComponentRegistry registry) {
				// The input component inside the popup
				input = new Input(
						1000,
						x,
						y + CONTENT_TEXT_HEIGHT + CONTENT_PADDING,
						width,
						40,
						allowedValues,
						0xFFFFFFFF
				);
				registry.register(input, id);
			}

			@Override
			public void draw() {
				// Draw the prompt text
				p.fill(255);
				p.textSize(CONTENT_TEXT_HEIGHT * 0.7f);
				p.text(prompt, x, y, width, CONTENT_TEXT_HEIGHT);
			}
		};
	}

	@Override
	protected List<Option> buildOptions() {
		// The only option on a dialog is the Ok-button.
		return List.of(new Popup.Option("Ok", 0xFF456990, popup -> {
			if (callback != null) callback.run(); // Run the current callback
			popup.setShowing(false); // Hide the popup
		}));
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// If enter is pressed, try to return from the dialog.
		if (event.getKey() == ENTER) {
			// This is a somewhat terrible hack to simulate a mouse press on the Ok-button.
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
