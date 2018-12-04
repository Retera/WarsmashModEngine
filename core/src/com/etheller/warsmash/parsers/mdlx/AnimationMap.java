package com.etheller.warsmash.parsers.mdlx;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;

/**
 * A map from MDX animation tags to their equivalent MDL tokens, and the
 * implementation objects.
 *
 * <p>
 * Based on the works of Chananya Freiman.
 *
 */
public enum AnimationMap {
	// Layer
	KMTF("TextureId", TimelineDescriptor.UINT32_TIMELINE),
	KMTA("Alpha", TimelineDescriptor.FLOAT_TIMELINE),
	// TextureAnimation
	KTAT("Translation", TimelineDescriptor.VECTOR3_TIMELINE),
	KTAR("Rotation", TimelineDescriptor.VECTOR4_TIMELINE),
	KTAS("Scaling", TimelineDescriptor.VECTOR3_TIMELINE),
	// GeosetAnimation
	KGAO("Alpha", TimelineDescriptor.FLOAT_TIMELINE),
	KGAC("Color", TimelineDescriptor.VECTOR3_TIMELINE),
	// Light
	KLAS("AttenuationStart", TimelineDescriptor.FLOAT_TIMELINE),
	KLAE("AttenuationEnd", TimelineDescriptor.FLOAT_TIMELINE),
	KLAC("Color", TimelineDescriptor.VECTOR3_TIMELINE),
	KLAI("Intensity", TimelineDescriptor.FLOAT_TIMELINE),
	KLBI("AmbientIntensity", TimelineDescriptor.FLOAT_TIMELINE),
	KLBC("AmbientColor", TimelineDescriptor.VECTOR3_TIMELINE),
	KLAV("Visibility", TimelineDescriptor.FLOAT_TIMELINE),
	// Attachment
	KATV("Visibility", TimelineDescriptor.FLOAT_TIMELINE),
	// ParticleEmitter
	KPEE("EmissionRate", TimelineDescriptor.FLOAT_TIMELINE),
	KPEG("Gravity", TimelineDescriptor.FLOAT_TIMELINE),
	KPLN("Longitude", TimelineDescriptor.FLOAT_TIMELINE),
	KPLT("Latitude", TimelineDescriptor.FLOAT_TIMELINE),
	KPEL("LifeSpan", TimelineDescriptor.FLOAT_TIMELINE),
	KPES("Speed", TimelineDescriptor.FLOAT_TIMELINE),
	KPEV("Visibility", TimelineDescriptor.FLOAT_TIMELINE),
	// ParticleEmitter2
	KP2S("Speed", TimelineDescriptor.FLOAT_TIMELINE),
	KP2R("Variation", TimelineDescriptor.FLOAT_TIMELINE),
	KP2L("Latitude", TimelineDescriptor.FLOAT_TIMELINE),
	KP2G("Gravity", TimelineDescriptor.FLOAT_TIMELINE),
	KP2E("EmissionRate", TimelineDescriptor.FLOAT_TIMELINE),
	KP2N("Length", TimelineDescriptor.FLOAT_TIMELINE),
	KP2W("Width", TimelineDescriptor.FLOAT_TIMELINE),
	KP2V("Visibility", TimelineDescriptor.FLOAT_TIMELINE),
	// RibbonEmitter
	KRHA("HeightAbove", TimelineDescriptor.FLOAT_TIMELINE),
	KRHB("HeightBelow", TimelineDescriptor.FLOAT_TIMELINE),
	KRAL("Alpha", TimelineDescriptor.FLOAT_TIMELINE),
	KRCO("Color", TimelineDescriptor.VECTOR3_TIMELINE),
	KRTX("TextureSlot", TimelineDescriptor.UINT32_TIMELINE),
	KRVS("Visibility", TimelineDescriptor.FLOAT_TIMELINE),
	// Camera
	KCTR("Translation", TimelineDescriptor.VECTOR3_TIMELINE),
	KTTR("Translation", TimelineDescriptor.VECTOR3_TIMELINE),
	KCRL("Rotation", TimelineDescriptor.UINT32_TIMELINE),
	// GenericObject
	KGTR("Translation", TimelineDescriptor.VECTOR3_TIMELINE),
	KGRT("Rotation", TimelineDescriptor.VECTOR4_TIMELINE),
	KGSC("Scaling", TimelineDescriptor.VECTOR3_TIMELINE);
	private final String mdlToken;
	private final TimelineDescriptor implementation;

	private AnimationMap(final String mdlToken, final TimelineDescriptor implementation) {
		this.mdlToken = mdlToken;
		this.implementation = implementation;
	}

	public String getMdlToken() {
		return this.mdlToken;
	}

	public TimelineDescriptor getImplementation() {
		return this.implementation;
	}

	public static final Map<War3ID, AnimationMap> ID_TO_TAG = new HashMap<>();

	static {
		for (final AnimationMap tag : AnimationMap.values()) {
			ID_TO_TAG.put(War3ID.fromString(tag.name()), tag);
		}
	}
}
