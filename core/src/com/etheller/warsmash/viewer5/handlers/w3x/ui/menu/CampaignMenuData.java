package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.viewer5.FogSettings;

public class CampaignMenuData {
	private final String header;
	private final String name;
	private final boolean defaultOpen;
	private final String background;
	private final FogSettings backgroundFogSettings;
	private final int cursor;
	private final String ambientSound;
	private final CampaignMission introCinematic;
	private final CampaignMission openCinematic;
	private final CampaignMission endCinematic;
	private final List<CampaignMission> missions = new ArrayList<>();

	public CampaignMenuData(final Element element, final boolean oldFormatCampaignData) {
		this.header = element.getField("Header");
		this.name = element.getField("Name");
		this.defaultOpen = element.getFieldValue("DefaultOpen") == 1;
		this.background = element.getField("Background");
		this.backgroundFogSettings = new FogSettings();
		final int backgroundFogStyle = element.getFieldValue("BackgroundFogStyle") + 1;
		this.backgroundFogSettings.setStyleByIndex(backgroundFogStyle);
		this.backgroundFogSettings.color = new Color(element.getFieldFloatValue("BackgroundFogColor", 1) / 255f,
				element.getFieldFloatValue("BackgroundFogColor", 2) / 255f,
				element.getFieldFloatValue("BackgroundFogColor", 3) / 255f,
				element.getFieldFloatValue("BackgroundFogColor", 0) / 255f);
		this.backgroundFogSettings.density = element.getFieldFloatValue("BackgroundFogDensity");
		this.backgroundFogSettings.start = element.getFieldFloatValue("BackgroundFogStart");
		this.backgroundFogSettings.end = element.getFieldFloatValue("BackgroundFogEnd");
		this.cursor = element.getFieldValue("Cursor");
		this.ambientSound = element.getField("AmbientSound");
		if (oldFormatCampaignData) {
			// TODO we need an exterior element named "Label" to use InCinematic,
			// OpCinematic and EdCinematic here
			this.introCinematic = null;
			this.openCinematic = null;
			this.endCinematic = null;
			int missionIndex = 0;
			CampaignMission currentMission;
			while ((currentMission = readOldFormatMission(element, missionIndex)) != null) {
				this.missions.add(currentMission);
				missionIndex++;
			}
		}
		else {
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

	public FogSettings getBackgroundFogSettings() {
		return this.backgroundFogSettings;
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

	private CampaignMission readOldFormatMission(final Element element, final int missionIndex) {
		final String missionKey = "Mission" + missionIndex;
		if ("".equals(element.getField(missionKey, 0))) {
			return null;
		}
		final String titleKey = "Title" + missionIndex;
		final String fileKey = "File" + missionIndex;
		return new CampaignMission(element.getField(titleKey, 0), element.getField(missionKey, 0),
				"Maps\\Campaign\\" + element.getField(fileKey, 0) + ".w3m");
	}
}
