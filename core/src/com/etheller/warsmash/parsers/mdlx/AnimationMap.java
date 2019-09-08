package com.etheller.warsmash.parsers.mdlx;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.MdlUtils;
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
	KMTF(MdlUtils.TOKEN_TEXTURE_ID, TimelineDescriptor.UINT32_TIMELINE),
	KMTA(MdlUtils.TOKEN_ALPHA, TimelineDescriptor.FLOAT_TIMELINE),
	// TextureAnimation
	KTAT(MdlUtils.TOKEN_TRANSLATION, TimelineDescriptor.VECTOR3_TIMELINE),
	KTAR(MdlUtils.TOKEN_ROTATION, TimelineDescriptor.VECTOR4_TIMELINE),
	KTAS(MdlUtils.TOKEN_SCALING, TimelineDescriptor.VECTOR3_TIMELINE),
	// GeosetAnimation
	KGAO(MdlUtils.TOKEN_ALPHA, TimelineDescriptor.FLOAT_TIMELINE),
	KGAC(MdlUtils.TOKEN_COLOR, TimelineDescriptor.VECTOR3_TIMELINE),
	// Light
	KLAS(MdlUtils.TOKEN_ATTENUATION_START, TimelineDescriptor.FLOAT_TIMELINE),
	KLAE(MdlUtils.TOKEN_ATTENUATION_END, TimelineDescriptor.FLOAT_TIMELINE),
	KLAC(MdlUtils.TOKEN_COLOR, TimelineDescriptor.VECTOR3_TIMELINE),
	KLAI(MdlUtils.TOKEN_INTENSITY, TimelineDescriptor.FLOAT_TIMELINE),
	KLBI(MdlUtils.TOKEN_AMB_INTENSITY, TimelineDescriptor.FLOAT_TIMELINE),
	KLBC(MdlUtils.TOKEN_AMB_COLOR, TimelineDescriptor.VECTOR3_TIMELINE),
	KLAV(MdlUtils.TOKEN_VISIBILITY, TimelineDescriptor.FLOAT_TIMELINE),
	// Attachment
	KATV(MdlUtils.TOKEN_VISIBILITY, TimelineDescriptor.FLOAT_TIMELINE),
	// ParticleEmitter
	KPEE(MdlUtils.TOKEN_EMISSION_RATE, TimelineDescriptor.FLOAT_TIMELINE),
	KPEG(MdlUtils.TOKEN_GRAVITY, TimelineDescriptor.FLOAT_TIMELINE),
	KPLN(MdlUtils.TOKEN_LONGITUDE, TimelineDescriptor.FLOAT_TIMELINE),
	KPLT(MdlUtils.TOKEN_LATITUDE, TimelineDescriptor.FLOAT_TIMELINE),
	KPEL(MdlUtils.TOKEN_LIFE_SPAN, TimelineDescriptor.FLOAT_TIMELINE),
	KPES(MdlUtils.TOKEN_INIT_VELOCITY, TimelineDescriptor.FLOAT_TIMELINE),
	KPEV(MdlUtils.TOKEN_VISIBILITY, TimelineDescriptor.FLOAT_TIMELINE),
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
	KCTR(MdlUtils.TOKEN_TRANSLATION, TimelineDescriptor.VECTOR3_TIMELINE),
	KTTR(MdlUtils.TOKEN_TRANSLATION, TimelineDescriptor.VECTOR3_TIMELINE),
	KCRL(MdlUtils.TOKEN_ROTATION, TimelineDescriptor.UINT32_TIMELINE),
	// GenericObject
	KGTR(MdlUtils.TOKEN_TRANSLATION, TimelineDescriptor.VECTOR3_TIMELINE),
	KGRT(MdlUtils.TOKEN_ROTATION, TimelineDescriptor.VECTOR4_TIMELINE),
	KGSC(MdlUtils.TOKEN_SCALING, TimelineDescriptor.VECTOR3_TIMELINE);
	private final String mdlToken;
	private final TimelineDescriptor implementation;
	private final War3ID war3id;

	private AnimationMap(final String mdlToken, final TimelineDescriptor implementation) {
		this.mdlToken = mdlToken;
		this.implementation = implementation;
		this.war3id = War3ID.fromString(this.name());
	}

	public String getMdlToken() {
		return this.mdlToken;
	}

	public TimelineDescriptor getImplementation() {
		return this.implementation;
	}

	public War3ID getWar3id() {
		return this.war3id;
	}

	public static final Map<War3ID, AnimationMap> ID_TO_TAG = new HashMap<>();

	static {
		for (final AnimationMap tag : AnimationMap.values()) {
			ID_TO_TAG.put(tag.getWar3id(), tag);
		}
	}
}
