package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CWeaponSoundTypeJass implements CHandle {
	WHOKNOWS(null),
	METAL_LIGHT_CHOP("MetalLightChop"),
	METAL_MEDIUM_CHOP("MetalMediumChop"),
	METAL_HEAVY_CHOP("MetalHeavyChop"),
	METAL_LIGHT_SLICE("MetalLightSlice"),
	METAL_MEDIUM_SLICE("MetalMediumSlice"),
	METAL_HEAVY_SLICE("MetalHeavySlice"),
	METAL_MEDIUM_BASH("MetalMediumBash"),
	METAL_HEAVY_BASH("MetalHeavyBash"),
	METAL_MEDIUM_STAB("MetalMediumStab"),
	METAL_HEAVY_STAB("MetalHeavyStab"),
	WOOD_LIGHT_SLICE("WoodLightSlice"),
	WOOD_MEDIUM_SLICE("WoodMediumSlice"),
	WOOD_HEAVY_SLICE("WoodHeavySlice"),
	WOOD_LIGHT_BASH("WoodLightBash"),
	WOOD_MEDIUM_BASH("WoodMediumBash"),
	WOOD_HEAVY_BASH("WoodHeavyBash"),
	WOOD_LIGHT_STAB("WoodLightStab"),
	WOOD_MEDIUM_STAB("WoodMediumStab"),
	CLAW_LIGHT_SLICE("ClawLightSlice"),
	CLAW_MEDIUM_SLICE("ClawMediumSlice"),
	CLAW_HEAVY_SLICE("ClawHeavySlice"),
	AXE_MEDIUM_CHOP("AxeMediumChop"),
	ROCK_HEAVY_BASH("RockHeavyBash");

	private final String soundKey;

	CWeaponSoundTypeJass(final String soundKey) {
		this.soundKey = soundKey;
	}

	public String getSoundKey() {
		return this.soundKey;
	}

	public static CWeaponSoundTypeJass[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
