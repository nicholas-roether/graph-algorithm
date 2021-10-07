package io.github.nicholas_roether.components.common;

import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.Document;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Dialog extends Popup {
	private final String allowedValues;
	private final Pattern allowedReturns;

	private Runnable callback;

	private Input input;

	public Dialog(String prompt, String allowedValues, String allowedReturns) {
		super(prompt);
		this.allowedValues = allowedValues;
		this.allowedReturns = Pattern.compile(allowedReturns);
	}

	public String getValue() {
		if (input == null) return null;
		return input.getValue();
	}

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
}
