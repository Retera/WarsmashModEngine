package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class NonStackingFx {
	private String stackingKey;
	private War3ID id;
	private SimulationRenderComponent art;
	
	public NonStackingFx(String stackingKey, War3ID id) {
		super();
		this.stackingKey = stackingKey;
		this.id = id;
	}
	
	public String getStackingKey() {
		return stackingKey;
	}

	public void setStackingKey(String stackingKey) {
		this.stackingKey = stackingKey;
	}

	public War3ID getId() {
		return id;
	}
	public void setId(War3ID id) {
		this.id = id;
	}

	public SimulationRenderComponent getArt() {
		return art;
	}

	public void setArt(SimulationRenderComponent art) {
		this.art = art;
	}
}
