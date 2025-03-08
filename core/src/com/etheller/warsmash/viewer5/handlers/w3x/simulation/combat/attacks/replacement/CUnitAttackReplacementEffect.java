package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListener;

public class CUnitAttackReplacementEffect {

	public List<CUnitAttackPreDamageListener> getPreDamageListeners() {
		return null;
	}

	public float getProjectileArc() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isProjectileHomingEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getProjectileSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getProjectileArt() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean chargeResources(CUnit unit) {
		
		return false;
	}

}
