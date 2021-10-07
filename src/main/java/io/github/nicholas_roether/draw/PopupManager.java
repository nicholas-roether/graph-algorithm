package io.github.nicholas_roether.draw;

import io.github.nicholas_roether.draw.cursor.CursorManager;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class PopupManager implements WindowEventReceiver {
	private final ComponentRegistry registry;
	private List<Component> popupStack = new ArrayList<>(5);

	public PopupManager(ComponentRegistry registry) {
		this.registry = registry;
	}

	public void pushPopup(Component popup) {
		popupStack.add(popup);
	}

	public void removePopup(Component popup) {
		popupStack.remove(popup);
	}

	public Component getPopup() {
		if (popupStack.size() == 0) return null;
		return popupStack.get(popupStack.size() - 1);
	}

	public boolean hasPopup() {
		return getPopup() != null;
	}

	public void instructCursor(CursorManager manager, float x, float y) {
		if (!hasPopup()) return;
		addCursorInstructionsFor(getPopup(), manager, x, y);
	}

	public void addCursorInstructionsFor(Component component, CursorManager manager, float x, float y) {
		final int cursor = component.instructCursor(x, y);
		manager.addInstruction(component.zIndex, cursor);
		for (Component child : registry.getChildren(component)) {
			addCursorInstructionsFor(child, manager, x, y);
		}
	}

	private void passMousePress(Component component, MouseEvent event) {
		component.mousePressed(event);
		registry.getChildren(component).forEach(child -> passMousePress(child, event));
	}

	private void passMouseRelease(Component component, MouseEvent event) {
		component.mouseReleased(event);
		registry.getChildren(component).forEach(child -> passMouseRelease(child, event));
	}

	private void passMouseClick(Component component, MouseEvent event) {
		component.mouseClicked(event);
		registry.getChildren(component).forEach(child -> passMouseClick(child, event));
	}

	private void passMouseMove(Component component, MouseEvent event) {
		component.mouseMoved(event);
		registry.getChildren(component).forEach(child -> passMouseMove(child, event));
	}

	private void passMouseDrag(Component component, MouseEvent event) {
		component.mouseDragged(event);
		registry.getChildren(component).forEach(child -> passMouseDrag(child, event));
	}

	private void passMouseEnter(Component component, MouseEvent event) {
		component.mouseEntered(event);
		registry.getChildren(component).forEach(child -> passMouseEnter(child, event));
	}

	private void passMouseExit(Component component, MouseEvent event) {
		component.mouseExited(event);
		registry.getChildren(component).forEach(child -> passMouseExit(child, event));
	}

	private void passMouseWheel(Component component, MouseEvent event) {
		component.mouseWheel(event);
		registry.getChildren(component).forEach(child -> passMouseWheel(child, event));
	}

	private void passKeyPress(Component component, KeyEvent event) {
		component.keyPressed(event);
		registry.getChildren(component).forEach(child -> passKeyPress(child, event));
	}

	private void passKeyRelease(Component component, KeyEvent event) {
		component.keyReleased(event);
		registry.getChildren(component).forEach(child -> passKeyRelease(child, event));
	}

	private void passKeyType(Component component, KeyEvent event) {
		component.keyTyped(event);
		registry.getChildren(component).forEach(child -> passKeyType(child, event));
	}

	private void passFocusGain(Component component) {
		component.focusGained();
		registry.getChildren(component).forEach(this::passFocusGain);
	}

	private void passFocusLoss(Component component) {
		component.focusLost();
		registry.getChildren(component).forEach(this::passFocusLoss);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (hasPopup()) passMousePress(getPopup(), event);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (hasPopup()) passMouseRelease(getPopup(), event);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (hasPopup()) passMouseClick(getPopup(), event);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		if (hasPopup()) passMouseMove(getPopup(), event);
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (hasPopup()) passMouseDrag(getPopup(), event);
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		if (hasPopup()) passMouseEnter(getPopup(), event);
	}

	@Override
	public void mouseExited(MouseEvent event) {
		if (hasPopup()) passMouseExit(getPopup(), event);
	}

	@Override
	public void mouseWheel(MouseEvent event) {
		if (hasPopup()) passMouseWheel(getPopup(), event);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (hasPopup()) passKeyPress(getPopup(), event);
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (hasPopup()) passKeyRelease(getPopup(), event);
	}

	@Override
	public void keyTyped(KeyEvent event) {
		if (hasPopup()) passKeyType(getPopup(), event);
	}

	@Override
	public void focusGained() {
		if (hasPopup()) passFocusGain(getPopup());
	}

	@Override
	public void focusLost() {
		if (hasPopup()) passFocusLoss(getPopup());
	}
}
