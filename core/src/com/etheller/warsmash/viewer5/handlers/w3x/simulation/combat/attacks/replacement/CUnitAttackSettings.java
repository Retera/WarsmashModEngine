package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPostDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListener;

public class CUnitAttackSettings {

	private int areaOfEffectFullDamage;
	private int areaOfEffectMediumDamage;
	private int areaOfEffectSmallDamage;
	private float damageFactorMedium;
	private float damageFactorSmall;
	private int projectileSpeed = 0;
	private String projectileArt = "";
	private float projectileArc = 0;
	private boolean isHomingEnabled = false;
	private boolean isApplyEffectsOnMiss = false;
	private Float z = null;
	private Float impactZ = null;
	private Float deathTime = null;
	
	private EnumSet<SecondaryTag> animationNames = null;
	
	private List<CUnitAttackPreDamageListener> listeners = null;
	private List<CUnitAttackPostDamageListener> postDamageListeners = null;
	private float baseDamage; // This is a hack, as Barrage needs the damage early

	public CUnitAttackSettings(float projectileArc, String projectileArt, boolean projectileHomingEnabled, int projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
		this.projectileArt = projectileArt;
		this.projectileArc = projectileArc;
		this.isHomingEnabled = projectileHomingEnabled;
	}

	public CUnitAttackSettings() {
	}

	public CUnitAttackSettings(float projectileArc, String projectileArt, boolean projectileHomingEnabled,
			int projectileSpeed, int areaOfEffectFullDamage, int areaOfEffectMediumDamage,
			int areaOfEffectSmallDamage, float damageFactorMedium, float damageFactorSmall) {
		this.projectileSpeed = projectileSpeed;
		this.projectileArt = projectileArt;
		this.projectileArc = projectileArc;
		this.isHomingEnabled = projectileHomingEnabled;
		this.areaOfEffectFullDamage = areaOfEffectFullDamage;
		this.areaOfEffectMediumDamage = areaOfEffectMediumDamage;
		this.areaOfEffectSmallDamage = areaOfEffectSmallDamage;
		this.damageFactorMedium = damageFactorMedium;
		this.damageFactorSmall = damageFactorSmall;
	}

	public List<CUnitAttackPreDamageListener> getPreDamageListeners() {
		return listeners;
	}
	
	public void addPreDamageListener(CUnitAttackPreDamageListener listener) {
		if (this.listeners == null) {
			this.listeners = new ArrayList<>();
		}
		this.listeners.add(listener);
	}
	
	public void removePreDamageListener(CUnitAttackPreDamageListener listener) {
		if (this.listeners != null) {
			this.listeners.remove(listener);
		}
	}

	public void setEmptyPreDamageListeners() {
		this.listeners = new ArrayList<>();
	}

	public List<CUnitAttackPostDamageListener> getPostDamageListeners() {
		return postDamageListeners;
	}
	
	public void addPostDamageListener(CUnitAttackPostDamageListener listener) {
		if (this.postDamageListeners == null) {
			this.postDamageListeners = new ArrayList<>();
		}
		this.postDamageListeners.add(0, listener);
	}
	
	public void removePostDamageListener(CUnitAttackPostDamageListener listener) {
		if (this.postDamageListeners != null) {
			this.postDamageListeners.remove(listener);
		}
	}

	public float getProjectileArc() {
		return projectileArc;
	}

	public void setProjectileArc(float projectileArc) {
		this.projectileArc = projectileArc ;
	}

	public boolean isProjectileHomingEnabled() {
		return isHomingEnabled;
	}

	public void setProjectileHomingEnabled(boolean isHomingEnabled) {
		this.isHomingEnabled = isHomingEnabled ;
	}

	public boolean isApplyEffectsOnMiss() {
		return isApplyEffectsOnMiss;
	}

	public void setApplyEffectsOnMiss(boolean isApplyEffectsOnMiss) {
		this.isApplyEffectsOnMiss = isApplyEffectsOnMiss;
	}

	public int getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(int projectileSpeed) {
		this.projectileSpeed = projectileSpeed ;
	}

	public String getProjectileArt() {
		return projectileArt;
	}

	public void setProjectileArt(String projectileArt) {
		this.projectileArt = projectileArt ;
	}

	public int getAreaOfEffectFullDamage() {
		return areaOfEffectFullDamage;
	}

	public void setAreaOfEffectFullDamage(int areaOfEffectFullDamage) {
		this.areaOfEffectFullDamage = areaOfEffectFullDamage;
	}

	public int getAreaOfEffectMediumDamage() {
		return areaOfEffectMediumDamage;
	}

	public void setAreaOfEffectMediumDamage(int areaOfEffectMediumDamage) {
		this.areaOfEffectMediumDamage = areaOfEffectMediumDamage;
	}

	public int getAreaOfEffectSmallDamage() {
		return areaOfEffectSmallDamage;
	}

	public void setAreaOfEffectSmallDamage(int areaOfEffectSmallDamage) {
		this.areaOfEffectSmallDamage = areaOfEffectSmallDamage;
	}

	public float getDamageFactorMedium() {
		return damageFactorMedium;
	}

	public void setDamageFactorMedium(float damageFactorMedium) {
		this.damageFactorMedium = damageFactorMedium;
	}

	public float getDamageFactorSmall() {
		return damageFactorSmall;
	}

	public void setDamageFactorSmall(float damageFactorSmall) {
		this.damageFactorSmall = damageFactorSmall;
	}

	public EnumSet<SecondaryTag> getAnimationNames() {
		if (this.animationNames == null) {
			return SequenceUtils.EMPTY;
		}
		return animationNames;
	}

	public void addAnimationName(SecondaryTag animationName) {
		if (this.animationNames == null) {
			this.animationNames = EnumSet.of(animationName);
		} else {
			this.animationNames.add(animationName);
		}
	}

	public void addAnimationNames(EnumSet<SecondaryTag> animationNames) {
		if (this.animationNames == null) {
			this.animationNames = EnumSet.copyOf(animationNames);
		} else {
			this.animationNames.addAll(animationNames);
		}
	}

	public void removeAnimationName(SecondaryTag animationName) {
		if (this.animationNames != null) {
			this.animationNames.remove(animationName);
		}
	}

	public Float getZ() {
		return this.z;
	}
	
	public void setZ(Float z) {
		this.z = z;
	}

	public Float getImpactZ() {
		return this.impactZ;
	}
	
	public void setImpactZ(Float impactZ) {
		this.impactZ = impactZ;
	}

	public Float getArtDeathTime() {
		return this.deathTime;
	}
	
	public void setArtDeathTime(Float deathTime) {
		this.deathTime = deathTime;
	}

	public void setBaseDamage(int damage) {
		this.baseDamage = damage;
	}

	public float getBaseDamage() {
		return this.baseDamage;
	}
}
