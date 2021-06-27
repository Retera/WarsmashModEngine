package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

public class SettableCommandErrorListener implements CommandErrorListener {
	private CommandErrorListener delegate;

	@Override
	public void showCommandError(final String message) {
		this.delegate.showCommandError(message);
	}

	@Override
	public void showCantPlaceError() {
		this.delegate.showCantPlaceError();
	}

	@Override
	public void showNoFoodError() {
		this.delegate.showNoFoodError();
	}

	@Override
	public void showInventoryFullError() {
		this.delegate.showInventoryFullError();
	}

	public void setDelegate(final CommandErrorListener delegate) {
		this.delegate = delegate;
	}

	@Override
	public void showUnableToFindCoupleTargetError() {
		this.delegate.showUnableToFindCoupleTargetError();
	}
}
