package com.etheller.warsmash.parsers.fdf.frames;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;

public class StringFrame extends AbstractRenderableFrame {
	private final List<SingleStringFrame> internalFrames = new ArrayList<>();
	private Color color;
	private String text = "Default string";
	private final TextJustify justifyH;
	private final TextJustify justifyV;
	private final BitmapFont frameFont;
	private Color fontShadowColor;
	private float fontShadowOffsetX;
	private float fontShadowOffsetY;
	private float alpha = 1.0f;
	private final SimpleFrame internalFramesContainer;
	private float predictedViewportHeight;

	static ShapeRenderer shapeRenderer = new ShapeRenderer();

	public StringFrame(final String name, final UIFrame parent, final Color color, final TextJustify justifyH,
			final TextJustify justifyV, final BitmapFont frameFont, final String text) {
		super(name, parent);
		this.color = color;
		this.justifyH = justifyH;
		this.justifyV = justifyV;
		this.frameFont = frameFont;
		this.text = text;
		this.internalFramesContainer = new SimpleFrame(null, this);
	}

	public String getText() {
		return this.text;
	}

	public void setText(final String text, final GameUI gameUI, final Viewport viewport) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		this.text = text;
		positionBounds(gameUI, viewport);
	}

	public void setColor(final Color color) {
		this.color = color;
		for (final SingleStringFrame internalFrame : this.internalFrames) {
			internalFrame.setColor(color);
		}
	}

	public Color getColor() {
		return this.color;
	}

	public void setFontShadowColor(final Color fontShadowColor) {
		this.fontShadowColor = fontShadowColor;
		for (final SingleStringFrame internalFrame : this.internalFrames) {
			internalFrame.setFontShadowColor(fontShadowColor);
		}
	}

	public void setFontShadowOffsetX(final float fontShadowOffsetX) {
		this.fontShadowOffsetX = fontShadowOffsetX;
		for (final SingleStringFrame internalFrame : this.internalFrames) {
			internalFrame.setFontShadowOffsetX(fontShadowOffsetX);
		}
	}

	public void setFontShadowOffsetY(final float fontShadowOffsetY) {
		this.fontShadowOffsetY = fontShadowOffsetY;
		for (final SingleStringFrame internalFrame : this.internalFrames) {
			internalFrame.setFontShadowOffsetY(fontShadowOffsetY);
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		this.internalFramesContainer.render(batch, baseFont, glyphLayout);

		if (GameUI.DEBUG) {
			batch.end();
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.setColor(1f, 1f, 1f, 1f);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.rect(this.renderBounds.x, this.renderBounds.y, this.renderBounds.width,
					this.renderBounds.height);

			shapeRenderer.end();

			batch.begin();
		}
	}

	@Override
	public void positionBounds(final GameUI gameUI, final Viewport viewport) {
		createInternalFrames(gameUI.getGlyphLayout());
		if (this.renderBounds.height == 0) {
			this.renderBounds.height = getPredictedViewportHeight();
		}
		super.positionBounds(gameUI, viewport);
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		this.internalFramesContainer.positionBounds(gameUI, viewport);
	}

	private void createInternalFrames(final GlyphLayout glyphLayout) {
		for (final SingleStringFrame internalFrame : this.internalFrames) {
			this.internalFramesContainer.remove(internalFrame);
		}
		this.internalFrames.clear();
		final StringBuilder currentLine = new StringBuilder();
		final StringBuilder currentWord = new StringBuilder();
		float currentXCoordForWord = 0;
		float currentXCoordForFrames = 0;
		final float usedWidth = 0;
		float usedHeight = 0;
		float usedWidthMax = 0;
		final float startingBoundsWidth = this.renderBounds.width;
		final boolean firstInLine = false;
		Color currentColor = this.color;
		for (int i = 0; i < this.text.length(); i++) {
			final char c = this.text.charAt(i);
			switch (c) {
			case '|': {
				// special control character
				if ((i + 1) < this.text.length()) {
					final char escapedCharacter = this.text.charAt(i + 1);
					switch (escapedCharacter) {
					case 'c':
					case 'C':
						if ((i + 9) < this.text.length()) {
							int colorInt;
							try {
								final String upperCase = this.text.substring(i + 2, i + 10).toUpperCase();
								colorInt = (int) Long.parseLong(upperCase, 16);
							}
							catch (final NumberFormatException exc) {
								currentWord.append(c);
								break;
							}
							i += 9;
							{
								final String wordString = currentWord.toString();
								currentWord.setLength(0);
								glyphLayout.setText(this.frameFont, wordString);
								final float wordWidth = glyphLayout.width;
								if ((startingBoundsWidth > 0)
										&& ((currentXCoordForWord + wordWidth) >= startingBoundsWidth)) {
									final String currentLineString = currentLine.toString();
									currentLine.setLength(0);
									glyphLayout.setText(this.frameFont, currentLineString);
									usedWidthMax = Math.max(currentXCoordForFrames + glyphLayout.width, usedWidthMax);
									final SingleStringFrame singleStringFrame = new SingleStringFrame(currentLineString,
											this.internalFramesContainer, currentColor, TextJustify.LEFT,
											TextJustify.TOP, this.frameFont);
									singleStringFrame.setHeight(this.frameFont.getLineHeight());
									singleStringFrame.setWidth(glyphLayout.width);
									singleStringFrame.setAlpha(this.alpha);
									singleStringFrame.setFontShadowColor(this.fontShadowColor);
									singleStringFrame.setFontShadowOffsetX(this.fontShadowOffsetX);
									singleStringFrame.setFontShadowOffsetY(this.fontShadowOffsetY);
									singleStringFrame.addAnchor(new AnchorDefinition(FramePoint.TOPLEFT,
											currentXCoordForFrames, usedHeight));
									this.internalFrames.add(singleStringFrame);
									usedHeight += this.frameFont.getLineHeight();
									currentXCoordForWord = 0;
									currentXCoordForFrames = 0;
								}
								currentXCoordForWord += wordWidth;
								currentLine.append(wordString);

								final String currentLineString = currentLine.toString();
								currentLine.setLength(0);
								glyphLayout.setText(this.frameFont, currentLineString);
								usedWidthMax = Math.max(currentXCoordForFrames + glyphLayout.width, usedWidthMax);
								final SingleStringFrame singleStringFrame = new SingleStringFrame(currentLineString,
										this.internalFramesContainer, currentColor, TextJustify.LEFT, TextJustify.TOP,
										this.frameFont);
								singleStringFrame.setHeight(this.frameFont.getLineHeight());
								singleStringFrame.setWidth(glyphLayout.width);
								singleStringFrame.setAlpha(this.alpha);
								singleStringFrame.setFontShadowColor(this.fontShadowColor);
								singleStringFrame.setFontShadowOffsetX(this.fontShadowOffsetX);
								singleStringFrame.setFontShadowOffsetY(this.fontShadowOffsetY);
								singleStringFrame.addAnchor(
										new AnchorDefinition(FramePoint.TOPLEFT, currentXCoordForFrames, -usedHeight));
								this.internalFrames.add(singleStringFrame);
								currentXCoordForFrames = currentXCoordForWord;

								currentColor = new Color((colorInt << 8) | (colorInt >>> 24));
							}
						}
						break;
					case 'r':
					case 'R':
						i++; {
						final String wordString = currentWord.toString();
						currentWord.setLength(0);
						glyphLayout.setText(this.frameFont, wordString);
						final float wordWidth = glyphLayout.width;
						if ((startingBoundsWidth > 0) && ((currentXCoordForWord + wordWidth) >= startingBoundsWidth)) {
							final String currentLineString = currentLine.toString();
							currentLine.setLength(0);
							glyphLayout.setText(this.frameFont, currentLineString);
							usedWidthMax = Math.max(currentXCoordForFrames + glyphLayout.width, usedWidthMax);
							final SingleStringFrame singleStringFrame = new SingleStringFrame(currentLineString,
									this.internalFramesContainer, currentColor, TextJustify.LEFT, TextJustify.TOP,
									this.frameFont);
							singleStringFrame.setHeight(this.frameFont.getLineHeight());
							singleStringFrame.setWidth(glyphLayout.width);
							singleStringFrame.setAlpha(this.alpha);
							singleStringFrame.setFontShadowColor(this.fontShadowColor);
							singleStringFrame.setFontShadowOffsetX(this.fontShadowOffsetX);
							singleStringFrame.setFontShadowOffsetY(this.fontShadowOffsetY);
							singleStringFrame.addAnchor(
									new AnchorDefinition(FramePoint.TOPLEFT, currentXCoordForFrames, -usedHeight));
							this.internalFrames.add(singleStringFrame);
							usedHeight += this.frameFont.getLineHeight();
							currentXCoordForWord = 0;
							currentXCoordForFrames = 0;
						}
						currentXCoordForWord += wordWidth;
						currentLine.append(wordString);

						final String currentLineString = currentLine.toString();
						currentLine.setLength(0);
						glyphLayout.setText(this.frameFont, currentLineString);
						usedWidthMax = Math.max(currentXCoordForFrames + glyphLayout.width, usedWidthMax);
						final SingleStringFrame singleStringFrame = new SingleStringFrame(currentLineString,
								this.internalFramesContainer, currentColor, TextJustify.LEFT, TextJustify.TOP,
								this.frameFont);
						singleStringFrame.setHeight(this.frameFont.getLineHeight());
						singleStringFrame.setWidth(glyphLayout.width);
						singleStringFrame.setAlpha(this.alpha);
						singleStringFrame.setFontShadowColor(this.fontShadowColor);
						singleStringFrame.setFontShadowOffsetX(this.fontShadowOffsetX);
						singleStringFrame.setFontShadowOffsetY(this.fontShadowOffsetY);
						singleStringFrame.addAnchor(
								new AnchorDefinition(FramePoint.TOPLEFT, currentXCoordForFrames, -usedHeight));
						this.internalFrames.add(singleStringFrame);
						currentXCoordForFrames = currentXCoordForWord;
					}
						currentColor = this.color;
						break;
					case 'n':
					case 'N': {

						final String wordString = currentWord.toString();
						currentWord.setLength(0);
						glyphLayout.setText(this.frameFont, wordString);
						final float wordWidth = glyphLayout.width;
						if ((startingBoundsWidth > 0) && ((currentXCoordForWord + wordWidth) >= startingBoundsWidth)) {
							final String currentLineString = currentLine.toString();
							currentLine.setLength(0);
							glyphLayout.setText(this.frameFont, currentLineString);
							usedWidthMax = Math.max(currentXCoordForFrames + glyphLayout.width, usedWidthMax);
							final SingleStringFrame singleStringFrame = new SingleStringFrame(currentLineString,
									this.internalFramesContainer, currentColor, TextJustify.LEFT, TextJustify.TOP,
									this.frameFont);
							singleStringFrame.setHeight(this.frameFont.getLineHeight());
							singleStringFrame.setWidth(glyphLayout.width);
							singleStringFrame.setAlpha(this.alpha);
							singleStringFrame.setFontShadowColor(this.fontShadowColor);
							singleStringFrame.setFontShadowOffsetX(this.fontShadowOffsetX);
							singleStringFrame.setFontShadowOffsetY(this.fontShadowOffsetY);
							singleStringFrame.addAnchor(
									new AnchorDefinition(FramePoint.TOPLEFT, currentXCoordForFrames, -usedHeight));
							this.internalFrames.add(singleStringFrame);
							usedHeight += this.frameFont.getLineHeight();
							currentXCoordForWord = 0;
							currentXCoordForFrames = 0;
						}
						currentXCoordForWord += wordWidth;
						currentLine.append(wordString);

						final String currentLineString = currentLine.toString();
						currentLine.setLength(0);
						glyphLayout.setText(this.frameFont, currentLineString);
						usedWidthMax = Math.max(currentXCoordForFrames + glyphLayout.width, usedWidthMax);
						final SingleStringFrame singleStringFrame = new SingleStringFrame(currentLineString,
								this.internalFramesContainer, currentColor, TextJustify.LEFT, TextJustify.TOP,
								this.frameFont);
						singleStringFrame.setHeight(this.frameFont.getLineHeight());
						singleStringFrame.setWidth(glyphLayout.width);
						singleStringFrame.setAlpha(this.alpha);
						singleStringFrame.setFontShadowColor(this.fontShadowColor);
						singleStringFrame.setFontShadowOffsetX(this.fontShadowOffsetX);
						singleStringFrame.setFontShadowOffsetY(this.fontShadowOffsetY);
						singleStringFrame.addAnchor(
								new AnchorDefinition(FramePoint.TOPLEFT, currentXCoordForFrames, -usedHeight));
						this.internalFrames.add(singleStringFrame);
						usedHeight += this.frameFont.getLineHeight();
						currentXCoordForWord = 0;
						currentXCoordForFrames = 0;

					}
						i++;
						break;
					default:
						currentWord.append(c);
						break;
					}
				}
			}
				break;
			case ' ':
				currentWord.append(' ');
				final String wordString = currentWord.toString();
				currentWord.setLength(0);
				glyphLayout.setText(this.frameFont, wordString);
				final float wordWidth = glyphLayout.width;
				if ((startingBoundsWidth > 0) && ((currentXCoordForWord + wordWidth) >= startingBoundsWidth)) {
					final String currentLineString = currentLine.toString();
					currentLine.setLength(0);
					glyphLayout.setText(this.frameFont, currentLineString);
					usedWidthMax = Math.max(currentXCoordForFrames + glyphLayout.width, usedWidthMax);
					final SingleStringFrame singleStringFrame = new SingleStringFrame(currentLineString,
							this.internalFramesContainer, currentColor, TextJustify.LEFT, TextJustify.TOP,
							this.frameFont);
					singleStringFrame.setHeight(this.frameFont.getLineHeight());
					singleStringFrame.setWidth(glyphLayout.width);
					singleStringFrame.setAlpha(this.alpha);
					singleStringFrame.setFontShadowColor(this.fontShadowColor);
					singleStringFrame.setFontShadowOffsetX(this.fontShadowOffsetX);
					singleStringFrame.setFontShadowOffsetY(this.fontShadowOffsetY);
					singleStringFrame
							.addAnchor(new AnchorDefinition(FramePoint.TOPLEFT, currentXCoordForFrames, -usedHeight));
					this.internalFrames.add(singleStringFrame);
					usedHeight += this.frameFont.getLineHeight();
					currentXCoordForWord = 0;
					currentXCoordForFrames = 0;
				}
				currentXCoordForWord += wordWidth;
				currentLine.append(wordString);
				break;
			default:
				currentWord.append(c);
				break;
			}
		}

		{

			final String wordString = currentWord.toString();
			currentWord.setLength(0);
			glyphLayout.setText(this.frameFont, wordString);
			final float wordWidth = glyphLayout.width;
			if ((startingBoundsWidth > 0) && ((currentXCoordForWord + wordWidth) >= startingBoundsWidth)) {
				final String currentLineString = currentLine.toString();
				currentLine.setLength(0);
				glyphLayout.setText(this.frameFont, currentLineString);
				usedWidthMax = Math.max(currentXCoordForFrames + glyphLayout.width, usedWidthMax);
				final SingleStringFrame singleStringFrame = new SingleStringFrame(currentLineString,
						this.internalFramesContainer, currentColor, TextJustify.LEFT, TextJustify.TOP, this.frameFont);
				singleStringFrame.setHeight(this.frameFont.getLineHeight());
				singleStringFrame.setWidth(glyphLayout.width);
				singleStringFrame.setAlpha(this.alpha);
				singleStringFrame.setFontShadowColor(this.fontShadowColor);
				singleStringFrame.setFontShadowOffsetX(this.fontShadowOffsetX);
				singleStringFrame.setFontShadowOffsetY(this.fontShadowOffsetY);
				singleStringFrame
						.addAnchor(new AnchorDefinition(FramePoint.TOPLEFT, currentXCoordForFrames, -usedHeight));
				this.internalFrames.add(singleStringFrame);
				usedHeight += this.frameFont.getLineHeight();
				currentXCoordForWord = 0;
				currentXCoordForFrames = 0;
			}
			currentXCoordForWord += wordWidth;
			currentLine.append(wordString);

			final String currentLineString = currentLine.toString();
			currentLine.setLength(0);
			glyphLayout.setText(this.frameFont, currentLineString);
			usedWidthMax = Math.max(currentXCoordForFrames + glyphLayout.width, usedWidthMax);
			final SingleStringFrame singleStringFrame = new SingleStringFrame(currentLineString,
					this.internalFramesContainer, currentColor, TextJustify.LEFT, TextJustify.TOP, this.frameFont);
			singleStringFrame.setHeight(this.frameFont.getLineHeight());
			singleStringFrame.setWidth(glyphLayout.width);
			singleStringFrame.setAlpha(this.alpha);
			singleStringFrame.setFontShadowColor(this.fontShadowColor);
			singleStringFrame.setFontShadowOffsetX(this.fontShadowOffsetX);
			singleStringFrame.setFontShadowOffsetY(this.fontShadowOffsetY);
			singleStringFrame.addAnchor(new AnchorDefinition(FramePoint.TOPLEFT, currentXCoordForFrames, -usedHeight));
			this.internalFrames.add(singleStringFrame);
			currentXCoordForFrames = currentXCoordForWord;
			usedHeight += this.frameFont.getCapHeight();
		}

		this.internalFramesContainer.setWidth(usedWidthMax);
		this.internalFramesContainer.setHeight(usedHeight);
		this.predictedViewportHeight = (usedHeight - this.frameFont.getCapHeight()) + this.frameFont.getLineHeight();

		this.internalFramesContainer.clearFramePointAssignments();
		switch (this.justifyH) {
		case CENTER:
			switch (this.justifyV) {
			case MIDDLE:
				this.internalFramesContainer.addAnchor(new AnchorDefinition(FramePoint.CENTER, 0, 0));
				break;
			case BOTTOM:
				this.internalFramesContainer.addAnchor(new AnchorDefinition(FramePoint.BOTTOM, 0, 0));
				break;
			case TOP:
			default:
				this.internalFramesContainer.addAnchor(new AnchorDefinition(FramePoint.TOP, 0, 0));
				break;
			}
			break;
		case RIGHT:
			switch (this.justifyV) {
			case MIDDLE:
				this.internalFramesContainer.addAnchor(new AnchorDefinition(FramePoint.RIGHT, 0, 0));
				break;
			case BOTTOM:
				this.internalFramesContainer.addAnchor(new AnchorDefinition(FramePoint.BOTTOMRIGHT, 0, 0));
				break;
			case TOP:
			default:
				this.internalFramesContainer.addAnchor(new AnchorDefinition(FramePoint.TOPRIGHT, 0, 0));
				break;
			}
			break;
		case LEFT:
		default:
			switch (this.justifyV) {
			case MIDDLE:
				this.internalFramesContainer.addAnchor(new AnchorDefinition(FramePoint.LEFT, 0, 0));
				break;
			case BOTTOM:
				this.internalFramesContainer.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT, 0, 0));
				break;
			case TOP:
			default:
				this.internalFramesContainer.addAnchor(new AnchorDefinition(FramePoint.TOPLEFT, 0, 0));
				break;
			}
			break;
		}

		for (final SingleStringFrame internalFrame : this.internalFrames) {
			this.internalFramesContainer.add(internalFrame);
		}
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
		for (final SingleStringFrame internalFrame : this.internalFrames) {
			internalFrame.setAlpha(alpha);
		}

	}

	public float getPredictedViewportHeight() {
		return this.predictedViewportHeight;
	}

	public BitmapFont getFrameFont() {
		return this.frameFont;
	}
}
