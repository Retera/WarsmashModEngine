package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.MdxAssetLoader;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSoundset;

public class RenderUnitTypeData extends RenderWidgetTypeData<RenderUnitType> {
	private static final String UNIT_FILE = "file"; // replaced from 'umdl'
	private static final String UNIT_PATHING = "pathTex"; // replaced from 'upat'
	private static final String UNIT_SPECIAL = "Specialart"; // replaced from 'uspa'
	private static final String UNIT_SHADOW = "unitShadow"; // replaced from 'ushu'
	private static final String UNIT_SHADOW_X = "shadowX"; // replaced from 'ushx'
	private static final String UNIT_SHADOW_Y = "shadowY"; // replaced from 'ushy'
	private static final String UNIT_SHADOW_W = "shadowW"; // replaced from 'ushw'
	private static final String UNIT_SHADOW_H = "shadowH"; // replaced from 'ushh'
	private static final String BUILDING_SHADOW = "buildingShadow"; // replaced from 'ushb'
	public static final String UNIT_SELECT_SCALE = "scale"; // replaced from 'ussc'
	private static final String UNIT_SOUNDSET = "unitSound"; // replaced from 'usnd'
	private static final String UBER_SPLAT = "uberSplat"; // replaced from 'uubs'
	private static final String ELEVATION_SAMPLE_RADIUS = "elevRad"; // replaced from 'uerd'
	private static final String MAX_PITCH = "maxPitch"; // replaced from 'umxp'
	private static final String ALLOW_CUSTOM_TEAM_COLOR = "customTeamColor"; // replaced from 'utcc'
	private static final String TEAM_COLOR = "teamColor"; // replaced from 'utco'
	private static final String MAX_ROLL = "maxRoll"; // replaced from 'umxr'
	private static final String ANIMATION_RUN_SPEED = "run"; // replaced from 'urun'
	private static final String ANIMATION_WALK_SPEED = "walk"; // replaced from 'uwal'
	private static final String MODEL_SCALE = "modelScale"; // replaced from 'usca'
	private static final String RED = "red"; // replaced from 'uclr'
	private static final String GREEN = "green"; // replaced from 'uclg'
	private static final String BLUE = "blue"; // replaced from 'uclb'
	private static final String MOVE_HEIGHT = "moveHeight"; // replaced from 'umvh'
	private static final String ORIENTATION_INTERPOLATION = "orientInterp"; // replaced from 'uori'
	public static final String ANIM_PROPS = "animProps"; // replaced from 'uani'
	public static final String ATTACHMENT_ANIM_PROPS = "Attachmentanimprops"; // replaced from 'uaap'
	private static final String BLEND_TIME = "blend"; // replaced from 'uble'
	private static final String BUILD_SOUND_LABEL = "BuildingSoundLabel"; // replaced from 'ubsl'
	private static final String UNIT_SELECT_HEIGHT = "selZ"; // replaced from 'uslz'

	private final DataTable unitAckSoundsTable;
	private final DataTable uberSplatTable;
	private final Map<String, UnitSoundset> soundsetNameToSoundset = new HashMap<>();

	public RenderUnitTypeData(ObjectData unitObjectData, DataSource dataSource, MdxAssetLoader mapViewer,
			DataTable unitAckSoundsTable, DataTable uberSplatTable) {
		super(unitObjectData, dataSource, mapViewer);
		this.unitAckSoundsTable = unitAckSoundsTable;
		this.uberSplatTable = uberSplatTable;
	}

	private String getUnitModelPath(final GameObject row) {
		String path;
		path = row.getFieldAsString(UNIT_FILE, 0);

		if (path.toLowerCase().endsWith(".mdl") || path.toLowerCase().endsWith(".mdx")) {
			path = path.substring(0, path.length() - 4);
		}
		if ((row.readSLKTagInt("fileVerFlags") == 2) && this.dataSource.has(path + "_V1.mdx")) {
			path += "_V1";
		}
		else if ((row.readSLKTagInt("fileVerFlags") == 1) && this.dataSource.has(path + "_V0.mdx")) {
			path += "_V0";
		}

		path += ".mdx";
		return path;
	}

	private BufferedImage getBuildingPathingPixelMap(final GameObject row) {
		final String pathingTexture = row.getFieldAsString(UNIT_PATHING, 0);
		final BufferedImage buildingPathingPixelMap = this.mapViewer.loadPathingTexture(pathingTexture);
		return buildingPathingPixelMap;
	}

	@Override
	protected final RenderUnitType createTypeData(War3ID key, GameObject row) {
		final String path = getUnitModelPath(row);
		final String unitSpecialArtPath = row.getFieldAsString(UNIT_SPECIAL, 0);
		MdxModel specialArtModel;
		if ((unitSpecialArtPath != null) && !unitSpecialArtPath.isEmpty()) {
			try {
				specialArtModel = this.mapViewer.loadModelMdx(unitSpecialArtPath);
			}
			catch (final Exception exc) {
				exc.printStackTrace();
				specialArtModel = null;
			}
		}
		else {
			specialArtModel = null;
		}
		final MdxModel model = this.mapViewer.loadModelMdx(path);
		final MdxModel portraitModel = getPortraitModel(path, model);

		final BufferedImage buildingPathingPixelMap = getBuildingPathingPixelMap(row);

		final String unitShadow = row.getFieldAsString(UNIT_SHADOW, 0);
		RenderShadowType renderShadowType = null;
		if ((unitShadow != null) && !"_".equals(unitShadow)) {
			final float shadowX = row.getFieldAsFloat(UNIT_SHADOW_X, 0);
			final float shadowY = row.getFieldAsFloat(UNIT_SHADOW_Y, 0);
			final float shadowWidth = row.getFieldAsFloat(UNIT_SHADOW_W, 0);
			final float shadowHeight = row.getFieldAsFloat(UNIT_SHADOW_H, 0);
			final String shadowTexture = getShadowTexture(unitShadow);
			if (this.dataSource.has(shadowTexture)) {
				renderShadowType = new RenderShadowType(shadowTexture, shadowX, shadowY, shadowWidth, shadowHeight);
			}
		}

		final String soundName = row.getFieldAsString(UNIT_SOUNDSET, 0);
		UnitSoundset unitSoundset = this.soundsetNameToSoundset.get(soundName);
		if (unitSoundset == null) {
			unitSoundset = new UnitSoundset(this.dataSource, this.unitAckSoundsTable, soundName);
			this.soundsetNameToSoundset.put(soundName, unitSoundset);
		}
		final UnitSoundset soundset = unitSoundset;
		final String uberSplat = row.getFieldAsString(UBER_SPLAT, 0);
		String uberSplatTexturePath = null;
		float uberSplatScaleValue = 0.0f;
		if (uberSplat != null) {
			final Element uberSplatInfo = this.uberSplatTable.get(uberSplat);
			if (uberSplatInfo != null) {
				uberSplatTexturePath = uberSplatInfo.getField("Dir") + "\\" + uberSplatInfo.getField("file") + ".blp";
				uberSplatScaleValue = uberSplatInfo.getFieldFloatValue("Scale");
			}
		}
		String buildingShadow = row.getFieldAsString(BUILDING_SHADOW, 0);
		if ("_".equals(buildingShadow)) {
			buildingShadow = null;
		}
		final String requiredAnimationNamesForAttachmentsString = row.getFieldAsString(ATTACHMENT_ANIM_PROPS, 0);
		final EnumSet<SecondaryTag> requiredAnimationNamesForAttachments = parseSecondaryTags(
				requiredAnimationNamesForAttachmentsString);
		final String requireAnimationNamesString = row.getFieldAsString(ANIM_PROPS, 0);
		final EnumSet<SecondaryTag> requiredAnimationNames = parseSecondaryTags(requireAnimationNamesString);

		final float red = row.getFieldAsInteger(RED, 0) / 255f;
		final float green = row.getFieldAsInteger(GREEN, 0) / 255f;
		final float blue = row.getFieldAsInteger(BLUE, 0) / 255f;

		final float selectScale = row.getFieldAsFloat(RenderUnitTypeData.UNIT_SELECT_SCALE, 0);
		final float selectHeight = row.getFieldAsFloat(UNIT_SELECT_HEIGHT, 0);

		final int orientationInterpolation = row.getFieldAsInteger(ORIENTATION_INTERPOLATION, 0);

		final float blendTime = row.getFieldAsFloat(BLEND_TIME, 0);

		return new RenderUnitType(model, portraitModel, specialArtModel, buildingPathingPixelMap,
				row.getFieldAsFloat(MAX_PITCH, 0), row.getFieldAsFloat(MAX_ROLL, 0),
				row.getFieldAsFloat(ELEVATION_SAMPLE_RADIUS, 0), row.getFieldAsBoolean(ALLOW_CUSTOM_TEAM_COLOR, 0),
				row.getFieldAsInteger(TEAM_COLOR, 0), row.getFieldAsFloat(ANIMATION_RUN_SPEED, 0),
				row.getFieldAsFloat(ANIMATION_WALK_SPEED, 0), row.getFieldAsFloat(MODEL_SCALE, 0), buildingShadow,
				uberSplatTexturePath, uberSplatScaleValue, requiredAnimationNamesForAttachments, requiredAnimationNames,
				renderShadowType, soundset, new Vector3(red, green, blue), selectScale, selectHeight,
				orientationInterpolation, blendTime);
	}

	private EnumSet<SecondaryTag> parseSecondaryTags(final String requiredAnimationNamesForAttachmentsString) {
		final EnumSet<SecondaryTag> requiredAnimationNamesForAttachments = EnumSet.noneOf(SecondaryTag.class);
		TokenLoop: for (final String animationName : requiredAnimationNamesForAttachmentsString.split(",")) {
			final String upperCaseToken = animationName.toUpperCase();
			for (final SecondaryTag secondaryTag : SecondaryTag.values()) {
				if (upperCaseToken.equals(secondaryTag.name())) {
					requiredAnimationNamesForAttachments.add(secondaryTag);
					continue TokenLoop;
				}
			}
		}
		return requiredAnimationNamesForAttachments;
	}
}
