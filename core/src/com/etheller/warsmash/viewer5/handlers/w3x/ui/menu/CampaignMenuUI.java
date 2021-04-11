package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

public class CampaignMenuUI extends SimpleFrame {
	private final GameUI rootFrame;
	private final Viewport uiViewport;
	private final List<CampaignButtonUI> buttonUIs = new ArrayList<>();

	public CampaignMenuUI(final String name, final UIFrame parent, final GameUI rootFrame, final Viewport uiViewport) {
		super(name, parent);
		this.rootFrame = rootFrame;
		this.uiViewport = uiViewport;
	}

	public void addButton(final String header, final String name, final Runnable onClick) {
		final CampaignButtonUI campaignButtonUI = new CampaignButtonUI(null, this);
		final TextButtonFrame campaignArrowButton = (TextButtonFrame) this.rootFrame
				.createFrame("CampaignArrowButtonTemplate", campaignButtonUI, 0, 0);
		campaignButtonUI.setButtonArt(campaignArrowButton);
		campaignButtonUI.add(campaignArrowButton);
		campaignArrowButton.addSetPoint(new SetPoint(FramePoint.TOPLEFT, campaignButtonUI, FramePoint.TOPLEFT, 0, 0));
		campaignArrowButton.setOnClick(onClick);

		final StringFrame headerText = (StringFrame) this.rootFrame.createFrame("StandardSmallTextTemplate",
				campaignButtonUI, 0, 0);
		this.rootFrame.setDecoratedText(headerText, header);
		campaignButtonUI.add(headerText);
		final StringFrame nameText = (StringFrame) this.rootFrame.createFrame("StandardValueTextTemplate",
				campaignButtonUI, 0, 0);
		this.rootFrame.setDecoratedText(nameText, name);
		headerText.addSetPoint(new SetPoint(FramePoint.TOPLEFT, campaignArrowButton, FramePoint.TOPRIGHT, 0, 0));
		nameText.addSetPoint(new SetPoint(FramePoint.TOPLEFT, headerText, FramePoint.BOTTOMLEFT, 0, 0));
		campaignButtonUI.add(nameText);
		campaignButtonUI.setHeaderText(headerText);
		campaignButtonUI.setNameText(nameText);
		campaignButtonUI.setHeight(GameUI.convertY(this.uiViewport, 0.032f));

		add(campaignButtonUI);
		this.buttonUIs.add(campaignButtonUI);

	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		super.innerPositionBounds(gameUI, viewport);
		final float myHeight = this.renderBounds.height;
		final int buttonCount = this.buttonUIs.size();
		final float buttonSpacing = Math.min(myHeight / buttonCount, GameUI.convertY(this.uiViewport, 0.056f));
		final float buttonsHeight = buttonSpacing * buttonCount;
		final float expectedHeight = Math.min(buttonsHeight, myHeight);
		final float yOffset = (myHeight - expectedHeight) / 2;

		UIFrame lastButton = null;
		for (final CampaignButtonUI campaignButtonUI : this.buttonUIs) {
			if (lastButton == null) {
				campaignButtonUI.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this, FramePoint.TOPLEFT, 0, -yOffset));
				campaignButtonUI.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, this, FramePoint.TOPRIGHT, 0, -yOffset));
			}
			else {
				campaignButtonUI.addSetPoint(
						new SetPoint(FramePoint.TOPLEFT, lastButton, FramePoint.TOPLEFT, 0, -buttonSpacing));
				campaignButtonUI.addSetPoint(
						new SetPoint(FramePoint.TOPRIGHT, lastButton, FramePoint.TOPRIGHT, 0, -buttonSpacing));
			}
			lastButton = campaignButtonUI;
		}
		super.innerPositionBounds(gameUI, viewport);
	}

}
