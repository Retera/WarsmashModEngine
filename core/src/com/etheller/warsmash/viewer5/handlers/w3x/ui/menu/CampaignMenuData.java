package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.etheller.warsmash.units.Element;

public class CampaignMenuData {
	private final String header;
	private final String name;
	private final boolean defaultOpen;
	private final String background;
	private final int backgroundFogStyle;
	private final Color backgroundFogColor;
	private final float backgroundFogDensity;
	private final float backgroundFogStart;
	private final float backgroundFogEnd;
	private final int cursor;
	private final String ambientSound;
	private final CampaignMission introCinematic;
	private final CampaignMission openCinematic;
	private final CampaignMission endCinematic;
	private final List<CampaignMission> missions = new ArrayList<>();

	public CampaignMenuData(final Element element) {
		this.header = element.getField("Header");
		this.name = element.getField("Name");
		this.defaultOpen = element.getFieldValue("DefaultOpen") == 1;
		this.background = element.getField("Background");
		this.backgroundFogStyle = element.getFieldValue("BackgroundFogStyle");
		this.backgroundFogColor = new Color(element.getFieldFloatValue("BackgroundFogColor", 1) / 255f,
				element.getFieldFloatValue("BackgroundFogColor", 2) / 255f,
				element.getFieldFloatValue("BackgroundFogColor", 3) / 255f,
				element.getFieldFloatValue("BackgroundFogColor", 0) / 255f);
		this.backgroundFogDensity = element.getFieldFloatValue("BackgroundFogDensity");
		this.backgroundFogStart = element.getFieldFloatValue("BackgroundFogStart");
		this.backgroundFogEnd = element.getFieldFloatValue("BackgroundFogEnd");
		this.cursor = element.getFieldValue("Cursor");
		this.ambientSound = element.getField("AmbientSound");
		this.introCinematic = readMission(element, "IntroCinematic");
		this.openCinematic = readMission(element, "OpenCinematic");
		this.endCinematic = readMission(element, "EndCinematic");
		int missionIndex = 0;
		CampaignMission currentMission;
		while ((currentMission = readMission(element, "Mission" + missionIndex)) != null) {
			this.missions.add(currentMission);
			missionIndex++;
		}
	}

	public String getHeader() {
		return this.header;
	}

	public String getName() {
		return this.name;
	}

	public boolean isDefaultOpen() {
		return this.defaultOpen;
	}

	public String getBackground() {
		return this.background;
	}

	public int getBackgroundFogStyle() {
		return this.backgroundFogStyle;
	}

	public Color getBackgroundFogColor() {
		return this.backgroundFogColor;
	}

	public float getBackgroundFogDensity() {
		return this.backgroundFogDensity;
	}

	public float getBackgroundFogStart() {
		return this.backgroundFogStart;
	}

	public float getBackgroundFogEnd() {
		return this.backgroundFogEnd;
	}

	public int getCursor() {
		return this.cursor;
	}

	public String getAmbientSound() {
		return this.ambientSound;
	}

	public CampaignMission getIntroCinematic() {
		return this.introCinematic;
	}

	public CampaignMission getOpenCinematic() {
		return this.openCinematic;
	}

	public CampaignMission getEndCinematic() {
		return this.endCinematic;
	}

	public List<CampaignMission> getMissions() {
		return this.missions;
	}

	private static CampaignMission readMission(final Element element, final String field) {
		if ("".equals(element.getField(field, 0))) {
			return null;
		}
		return new CampaignMission(element.getField(field, 0), element.getField(field, 1), element.getField(field, 2));
	}
}
