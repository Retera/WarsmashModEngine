package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;

public class AbilityBuilderDupe {
	private String id;

	private String castId;
	private String uncastId;
	private String autoCastOnId;
	private String autoCastOffId;
	private AutocastType autoCastType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCastId() {
		return castId;
	}

	public void setCastId(String castId) {
		this.castId = castId;
	}

	public String getUncastId() {
		return uncastId;
	}

	public void setUncastId(String uncastId) {
		this.uncastId = uncastId;
	}

	public String getAutoCastOnId() {
		return autoCastOnId;
	}

	public void setAutoCastOnId(String autoCastOnId) {
		this.autoCastOnId = autoCastOnId;
	}

	public String getAutoCastOffId() {
		return autoCastOffId;
	}

	public void setAutoCastOffId(String autoCastOffId) {
		this.autoCastOffId = autoCastOffId;
	}

	/**
	 * @return the autoCastType
	 */
	public AutocastType getAutoCastType() {
		return autoCastType;
	}

	/**
	 * @param autoCastType the autoCastType to set
	 */
	public void setAutoCastType(AutocastType autoCastType) {
		this.autoCastType = autoCastType;
	}
}
