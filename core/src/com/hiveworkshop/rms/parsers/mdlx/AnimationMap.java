package com.hiveworkshop.rms.parsers.mdlx;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;

/**
 * A map from MDX animation tags to their equivalent MDL tokens, and the
 * implementation objects.
 *
 * <p>
 * Based on the works of Chananya Freiman.
 */
public enum AnimationMap {
	// Layer
	/**
	 * Layer texture ID
	 */
	KMTF(MdlUtils.TOKEN_TEXTURE_ID, MdlxTimelineDescriptor.UINT32_TIMELINE),
	/**
	 * Layer alpha
	 */
	KMTA(MdlUtils.TOKEN_ALPHA, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Layer emissive gain
	 */
	KMTE("EmissiveGain", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Layer fresnel color
	 */
	KFC3("FresnelColor", MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	/**
	 * Layer fresnel opacity
	 */
	KFCA("FresnelOpacity", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Layer fresnel team color
	 */
	KFTC("FresnelTeamColor", MdlxTimelineDescriptor.UINT32_TIMELINE),
	// TextureAnimation
	/**
	 * Texture animation translation
	 */
	KTAT(MdlUtils.TOKEN_TRANSLATION, MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	/**
	 * Texture animation rotation
	 */
	KTAR(MdlUtils.TOKEN_ROTATION, MdlxTimelineDescriptor.VECTOR4_TIMELINE),
	/**
	 * Texture animation scaling
	 */
	KTAS(MdlUtils.TOKEN_SCALING, MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	// GeosetAnimation
	/**
	 * Geoset animation alpha
	 */
	KGAO(MdlUtils.TOKEN_ALPHA, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Geoset animation color
	 */
	KGAC(MdlUtils.TOKEN_COLOR, MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	// Light
	/**
	 * Light attenuation start
	 */
	KLAS(MdlUtils.TOKEN_ATTENUATION_START, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Light attenuation end
	 */
	KLAE(MdlUtils.TOKEN_ATTENUATION_END, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Light color
	 */
	KLAC(MdlUtils.TOKEN_COLOR, MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	/**
	 * Light intensity
	 */
	KLAI(MdlUtils.TOKEN_INTENSITY, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Light ambient intensity
	 */
	KLBI(MdlUtils.TOKEN_AMB_INTENSITY, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Light ambient color
	 */
	KLBC(MdlUtils.TOKEN_AMB_COLOR, MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	/**
	 * Light visibility
	 */
	KLAV(MdlUtils.TOKEN_VISIBILITY, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	// Attachment
	/**
	 * Attachment visibility
	 */
	KATV(MdlUtils.TOKEN_VISIBILITY, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	// ParticleEmitter
	/**
	 * Particle emitter emission rate
	 */
	KPEE(MdlUtils.TOKEN_EMISSION_RATE, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter gravity
	 */
	KPEG(MdlUtils.TOKEN_GRAVITY, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter longitude
	 */
	KPLN(MdlUtils.TOKEN_LONGITUDE, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter latitude
	 */
	KPLT(MdlUtils.TOKEN_LATITUDE, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter lifespan
	 */
	KPEL(MdlUtils.TOKEN_LIFE_SPAN, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter initial velocity
	 */
	KPES(MdlUtils.TOKEN_INIT_VELOCITY, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter visibility
	 */
	KPEV(MdlUtils.TOKEN_VISIBILITY, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	// ParticleEmitter2
	/**
	 * Particle emitter 2 speed
	 */
	KP2S("Speed", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter 2 variation
	 */
	KP2R("Variation", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter 2 latitude
	 */
	KP2L("Latitude", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter 2 gravity
	 */
	KP2G("Gravity", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter 2 emission rate
	 */
	KP2E("EmissionRate", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter 2 length
	 */
	KP2N("Length", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter 2 width
	 */
	KP2W("Width", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Particle emitter 2 visibility
	 */
	KP2V("Visibility", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	// ParticleEmitterCorn
	/**
	 * Popcorn emitter alpha
	 */
	KPPA("Alpha", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Popcorn emitter color
	 */
	KPPC("Color", MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	/**
	 * Popcorn emitter emission rate
	 */
	KPPE("EmissionRate", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Popcorn emitter lifespan
	 */
	KPPL("LifeSpan", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Popcorn emitter speed
	 */
	KPPS("Speed", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Popcorn emitter visibility
	 */
	KPPV("Visibility", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	// RibbonEmitter
	/**
	 * Ribbon emitter height above
	 */
	KRHA("HeightAbove", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Ribbon emitter height below
	 */
	KRHB("HeightBelow", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Ribbon emitter alpha
	 */
	KRAL("Alpha", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	/**
	 * Ribbon emitter color
	 */
	KRCO("Color", MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	/**
	 * Ribbon emitter texture slot
	 */
	KRTX("TextureSlot", MdlxTimelineDescriptor.UINT32_TIMELINE),
	/**
	 * Ribbon emitter visibility
	 */
	KRVS("Visibility", MdlxTimelineDescriptor.FLOAT_TIMELINE),
	// Camera
	/**
	 * Camera source translation
	 */
	KCTR(MdlUtils.TOKEN_TRANSLATION, MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	/**
	 * Camera target translation
	 */
	KTTR(MdlUtils.TOKEN_TRANSLATION, MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	/**
	 * Camera source rotation
	 */
	KCRL(MdlUtils.TOKEN_ROTATION, MdlxTimelineDescriptor.FLOAT_TIMELINE),
	// GenericObject
	/**
	 * Generic object translation
	 */
	KGTR(MdlUtils.TOKEN_TRANSLATION, MdlxTimelineDescriptor.VECTOR3_TIMELINE),
	/**
	 * Generic object rotation
	 */
	KGRT(MdlUtils.TOKEN_ROTATION, MdlxTimelineDescriptor.VECTOR4_TIMELINE),
	/**
	 * Generic object scaling
	 */
	KGSC(MdlUtils.TOKEN_SCALING, MdlxTimelineDescriptor.VECTOR3_TIMELINE);

	private final String mdlToken;
	private final MdlxTimelineDescriptor implementation;
	private final War3ID war3id;

	AnimationMap(final String mdlToken, final MdlxTimelineDescriptor implementation) {
		this.mdlToken = mdlToken;
		this.implementation = implementation;
		this.war3id = War3ID.fromString(name());
	}

	public String getMdlToken() {
		return this.mdlToken;
	}

	public MdlxTimelineDescriptor getImplementation() {
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
