package io.github.nicholas_roether.components.common;

import io.github.nicholas_roether.draw.Component;
import io.github.nicholas_roether.draw.ComponentRegistry;
import io.github.nicholas_roether.draw.Document;
import io.github.nicholas_roether.draw.bounded.RectangularComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Popup extends Component {
	public static final int Z_INDEX = 200;
	public static Option CLOSE = new Option("Close", 0xFF456990, popup -> popup.setShowing(false));

	private static final float PADDING = 20;
	private static final float TITLE_HEIGHT = 30;
	private static final float ACTIONS_HEIGHT = 40;
	private static final float BUTTON_WIDTH = 180;

	public final String title;
	public float popupHeight;
	public float width = 0;

	private boolean showing = false;
	private boolean childrenShowing = false;
	protected List<Option> options;
	private final List<TextActionButton> buttons = new ArrayList<>();

	public Popup(String title) {
		super(Z_INDEX);
		this.title = title;
	}

	protected abstract Content buildContent(Document p);

	protected abstract List<Option> buildOptions(Document p);

	@Override
	public void build(ComponentRegistry registry, Document p) {
		if (!showing) return;

		options = buildOptions(p);
		Content content = buildContent(p);
		popupHeight = content.height + TITLE_HEIGHT + ACTIONS_HEIGHT + 6 * PADDING;
		content.setX((p.width - width) / 2f + PADDING * 2);
		content.setY((p.height - popupHeight) / 2f + TITLE_HEIGHT + 3 * PADDING);
		content.setWidth(width - 4 * PADDING);

		List<Component> children = new ArrayList<>(options.size() + 1);
		buttons.clear();
		final float buttonY = (p.height + popupHeight) / 2f - ACTIONS_HEIGHT - PADDING;
		for (int i = 0; i < options.size(); i++) {
			final float buttonX = (p.width + width) / 2f - (i + 1) * (BUTTON_WIDTH + PADDING);
			final Option option = options.get(i);
			final TextActionButton button = new TextActionButton(
					Z_INDEX + 2,
					() -> option.action.accept(this),
					option.name,
					buttonX,
					buttonY,
					BUTTON_WIDTH,
					ACTIONS_HEIGHT,
					option.color
			);
			buttons.add(button);
			children.add(button);
		}


		children.add(content);
		registry.register(children, id);
	}

	@Override
	public boolean shouldRebuild() {
		final boolean shouldRebuild = childrenShowing != showing;
		childrenShowing = showing;
		return shouldRebuild;
	}

	@Override
	protected void init(Document p) {
		width = 0.34f * p.width;
	}

	@Override
	public void draw(@NotNull Document p) {
		if (!showing) return;
		p.fill(0, 0, 0, 150);
		p.rect(0, 0, p.width, p.height);
		p.fill(50);
		p.rectMode(CENTER);
		p.rect(p.width / 2f, p.height / 2f, width, popupHeight, 10, 10, 10, 10);

		p.fill(255);
		p.textSize(TITLE_HEIGHT);
		p.text(title, (p.width - width) / 2f + PADDING * 2, (p.height - popupHeight) / 2f + PADDING + TITLE_HEIGHT);
	}

	public boolean isShowing() {
		return showing;
	}

	public void setShowing(boolean showing) {
		this.showing = showing;
	}


	public void setDisabled(boolean disabled) {
		buttons.forEach(button -> button.setDisabled(disabled));
	}

	public static class Option {
		public final String name;
		public final int color;
		public final Consumer<Popup> action;

		public Option(String name, int color, Consumer<Popup> action) {
			this.name = name;
			this.color = color;
			this.action = action;
		}
	}

	public static abstract class Content extends RectangularComponent {
		public static final int Z_INDEX = Popup.Z_INDEX + 1;

		public float x;
		public float y;
		public float width;
		public final float height;

		public Content(float height) {
			super(Z_INDEX);
			this.height = height;
		}

		@Override
		public float getX() {
			return x;
		}

		@Override
		public float getY() {
			return y;
		}

		@Override
		public float getWidth() {
			return width;
		}

		@Override
		public float getHeight() {
			return height;
		}

		private void setX(float x) {
			this.x = x;
		}

		private void setY(float y) {
			this.y = y;
		}

		private void setWidth(float width) {
			this.width = width;
		}
	}
}
