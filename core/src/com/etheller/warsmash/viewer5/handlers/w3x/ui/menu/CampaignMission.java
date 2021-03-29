package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

public class CampaignMission {
	private final String header;
	private final String missionName;
	private final String mapFilename;

	public CampaignMission(final String header, final String missionName, final String mapFilename) {
		this.header = header;
		this.missionName = missionName;
		this.mapFilename = mapFilename;
	}

	public String getHeader() {
		return this.header;
	}

	public String getMissionName() {
		return this.missionName;
	}

	public String getMapFilename() {
		return this.mapFilename;
	}
}
