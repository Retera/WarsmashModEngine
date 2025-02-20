package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.FocusableFrame;

public class EditBoxFrame extends AbstractRenderableFrame implements FocusableFrame {

	private UIFrame controlBackdrop;
	private final float editBorderSize;
	private final Color editCursorColor;
	private StringFrame editTextFrame;
	private boolean focused = false;
	private int cursorIndex;

	// TODO design in such a way that references to these are not held !! very bad
	// code design here
	private GameUI gameUI;
	private Viewport viewport;
	private GlyphLayout glyphLayout;
	private Runnable onChange;
	private Runnable onEnter;
	private CharacterFilter filter = CharacterFilter.DEFAULT;

	public EditBoxFrame(final String name, final UIFrame parent, final float editBorderSize,
			final Color editCursorColor) {
		super(name, parent);
		this.editBorderSize = editBorderSize;
		this.editCursorColor = editCursorColor;
	}

	public void setControlBackdrop(final UIFrame controlBackdrop) {
		this.controlBackdrop = controlBackdrop;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		this.gameUI = gameUI;
		this.viewport = viewport;
		this.controlBackdrop.positionBounds(gameUI, viewport);
		if (this.editTextFrame != null) {
			this.editTextFrame.positionBounds(gameUI, viewport);
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		this.glyphLayout = glyphLayout;
		this.controlBackdrop.render(batch, baseFont, glyphLayout);
		this.editTextFrame.render(batch, baseFont, glyphLayout);
		if (this.focused) {
			final long time = TimeUtils.millis();
			if ((time % 500) > 250) {
				final BitmapFont frameFont = this.editTextFrame.getFrameFont();
				frameFont.setColor(this.editCursorColor);
				final int cursorRenderPosition = Math.min(this.cursorIndex,
						this.editTextFrame.getDisplayText().length());
				this.cursorIndex = cursorRenderPosition;
				glyphLayout.setText(frameFont, this.editTextFrame.getDisplayText().substring(0, cursorRenderPosition));
				final float cursorXOffset = glyphLayout.width;
				glyphLayout.setText(frameFont, "|");
				frameFont.draw(batch, "|",
						(this.editTextFrame.getFramePointX(FramePoint.LEFT) + cursorXOffset) - (glyphLayout.width / 2),
						this.editTextFrame.getFramePointY(FramePoint.LEFT) + ((frameFont.getCapHeight()) / 2));
			}
		}
	}

	public void setEditTextFrame(final StringFrame editTextFrame) {
		this.editTextFrame = editTextFrame;
	}

	@Override
	public boolean isFocusable() {
		return true;
	}

	@Override
	public void onFocusGained() {
		this.focused = true;
	}

	@Override
	public void onFocusLost() {
		this.focused = false;
	}

	@Override
	public boolean keyDown(final int keycode) {
		switch (keycode) {
		case Input.Keys.LEFT: {
			this.cursorIndex = Math.max(0, this.cursorIndex - 1);
			break;
		}
		case Input.Keys.RIGHT: {
			final String text = this.editTextFrame.getText();
			this.cursorIndex = Math.min(text.length(), this.cursorIndex + 1);
			break;
		}
		case Input.Keys.BACKSPACE: {
			final String prevText = this.editTextFrame.getText();
			final int prevTextLength = prevText.length();
			final int cursorIndex = Math.min(this.cursorIndex, prevTextLength);
			if (cursorIndex >= 1) {
				this.cursorIndex = cursorIndex - 1;
				final String newText = prevText.substring(0, cursorIndex - 1)
						+ prevText.substring(cursorIndex, prevTextLength);
				this.editTextFrame.setText(newText, this.gameUI, this.viewport);
				if (this.onChange != null) {
					this.onChange.run();
				}
			}
			break;
		}
		case Input.Keys.ENTER: {
			if (this.onEnter != null) {
				this.onEnter.run();
			}
			break;
		}
		case Input.Keys.V: {
			if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
				final String contents = Gdx.app.getClipboard().getContents();
				for (int i = 0; i < contents.length(); i++) {
					keyTyped(contents.charAt(i));
				}
			}
			break;
		}
		}
		return false;
	}

	@Override
	public boolean keyUp(final int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(final char character) {
		if (this.filter.allow(character)) {
			final String prevText = this.editTextFrame.getText();
			final int prevTextLength = prevText.length();
			final int cursorIndex = Math.min(this.cursorIndex, prevTextLength);
			final String newText = prevText.substring(0, cursorIndex) + character
					+ prevText.substring(cursorIndex, prevTextLength);
			this.editTextFrame.setText(newText, this.gameUI, this.viewport);
			this.cursorIndex++;
			if (this.onChange != null) {
				this.onChange.run();
			}
		}
		return false;
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {

			final String text = this.editTextFrame.getText();
			int indexFound = -1;
			final float fpXOfEditText = this.editTextFrame.getFramePointX(FramePoint.LEFT);
			float lastX = 0;
			for (int i = 0; i < text.length(); i++) {
				final BitmapFont frameFont = this.editTextFrame.getFrameFont();
				this.glyphLayout.setText(frameFont, this.editTextFrame.getText().substring(0, i));
				final float x = fpXOfEditText + this.glyphLayout.width;
				if (((x + lastX) / 2) > screenX) {
					indexFound = i - 1;
					break;
				}
				lastX = x;
			}
			if (indexFound == -1) {
				indexFound = text.length();
			}
			this.cursorIndex = indexFound;

			return this;
		}
		return super.touchDown(screenX, screenY, button);
	}

	public String getText() {
		return this.editTextFrame.getText();
	}

	public void setText(final String text, final GameUI gameUI, final Viewport uiViewport) {
		this.editTextFrame.setText(text, gameUI, uiViewport);
	}

	public void setOnChange(final Runnable onChange) {
		this.onChange = onChange;
	}

	public void setOnEnter(final Runnable onEnter) {
		this.onEnter = onEnter;
	}

	public void setFilter(final CharacterFilter filter) {
		this.filter = filter;
	}

	public void setFilterAllowAny() {
		this.filter = new CharacterFilter() {
			@Override
			public boolean allow(final char character) {
				return EditBoxFrame.this.editTextFrame.getFrameFont().getData().hasGlyph(character)
						&& (character >= 32);
			}
		};
	}

	public static interface CharacterFilter {
		boolean allow(char character);

		CharacterFilter DEFAULT = new CharacterFilter() {
			@Override
			public boolean allow(final char character) {
				return Character.isAlphabetic(character) || Character.isDigit(character) || (character == ' ');
			}
		};

		CharacterFilter ACCOUNT = new CharacterFilter() {
			@Override
			public boolean allow(final char character) {
				return Character.isAlphabetic(character) || Character.isDigit(character);
			}
		};
	}
}
