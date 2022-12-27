package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class AbilityDataUI {
	// Standard ability icon fields
	private static final War3ID ICON_NORMAL_X = War3ID.fromString("abpx");
	private static final War3ID ICON_NORMAL_Y = War3ID.fromString("abpy");
	private static final War3ID ICON_NORMAL = War3ID.fromString("aart");
	private static final War3ID ICON_TURN_OFF = War3ID.fromString("auar");
	private static final War3ID ICON_TURN_OFF_X = War3ID.fromString("aubx");
	private static final War3ID ICON_TURN_OFF_Y = War3ID.fromString("auby");
	private static final War3ID ICON_RESEARCH = War3ID.fromString("arar");
	private static final War3ID ICON_RESEARCH_X = War3ID.fromString("arpx");
	private static final War3ID ICON_RESEARCH_Y = War3ID.fromString("arpy");
	private static final War3ID ABILITY_TIP = War3ID.fromString("atp1");
	private static final War3ID ABILITY_UBER_TIP = War3ID.fromString("aub1");
	private static final War3ID ABILITY_UN_TIP = War3ID.fromString("aut1");
	private static final War3ID ABILITY_UN_UBER_TIP = War3ID.fromString("auu1");
	private static final War3ID ABILITY_RESEARCH_TIP = War3ID.fromString("aret");
	private static final War3ID ABILITY_RESEARCH_UBER_TIP = War3ID.fromString("arut");
	private static final War3ID ABILITY_EFFECT_SOUND = War3ID.fromString("aefs");
	private static final War3ID ABILITY_EFFECT_SOUND_LOOPED = War3ID.fromString("aefl");

	private static final War3ID ABILITY_HOTKEY_NORMAL = War3ID.fromString("ahky");
	private static final War3ID ABILITY_HOTKEY_TURNOFF = War3ID.fromString("auhk");
	private static final War3ID ABILITY_HOTKEY_LEARN = War3ID.fromString("arhk");

	private static final War3ID CASTER_ART = War3ID.fromString("acat");
	private static final War3ID[] CASTER_ART_ATTACHMENT_POINT = { War3ID.fromString("acap"),
			War3ID.fromString("aca1") };
	private static final War3ID CASTER_ART_ATTACHMENT_COUNT = War3ID.fromString("acac");
	private static final War3ID TARGET_ART = War3ID.fromString("atat");
	private static final War3ID[] TARGET_ART_ATTACHMENT_POINT = { War3ID.fromString("ata0"), War3ID.fromString("ata1"),
			War3ID.fromString("ata2"), War3ID.fromString("ata3"), War3ID.fromString("ata4"),
			War3ID.fromString("ata5") };
	private static final War3ID TARGET_ART_ATTACHMENT_COUNT = War3ID.fromString("atac");
	private static final War3ID SPECIAL_ART = War3ID.fromString("asat");
	private static final War3ID SPECIAL_ART_ATTACHMENT_POINT = War3ID.fromString("aspt");
	private static final War3ID EFFECT_ART = War3ID.fromString("aeat");
	private static final War3ID AREA_EFFECT_ART = War3ID.fromString("aaea");
	private static final War3ID MISSILE_ART = War3ID.fromString("amat");
	private static final War3ID MISSILE_ARC = War3ID.fromString("amac");

	// Standard buff icon fields
	private static final War3ID BUFF_ICON_NORMAL = War3ID.fromString("fart");
	private static final War3ID BUFF_ABILITY_TIP = War3ID.fromString("ftip");
	private static final War3ID BUFF_ABILITY_UBER_TIP = War3ID.fromString("fube");
	private static final War3ID BUFF_ABILITY_EFFECT_SOUND = War3ID.fromString("fefs");
	private static final War3ID BUFF_ABILITY_EFFECT_SOUND_LOOPED = War3ID.fromString("fefl");

	private static final War3ID BUFF_TARGET_ART = War3ID.fromString("ftat");
	private static final War3ID[] BUFF_TARGET_ART_ATTACHMENT_POINT = { War3ID.fromString("fta0"),
			War3ID.fromString("fta1"), War3ID.fromString("fta2"), War3ID.fromString("fta3"), War3ID.fromString("fta4"),
			War3ID.fromString("fta5") };
	private static final War3ID BUFF_TARGET_ART_ATTACHMENT_COUNT = War3ID.fromString("ftac");
	private static final War3ID BUFF_SPECIAL_ART = War3ID.fromString("fsat");
	private static final War3ID BUFF_SPECIAL_ART_ATTACHMENT_POINT = War3ID.fromString("fspt");
	private static final War3ID BUFF_EFFECT_ART = War3ID.fromString("feat");
	private static final War3ID BUFF_EFFECT_ART_ATTACHMENT_POINT = War3ID.fromString("feft");
	private static final War3ID BUFF_MISSILE_ART = War3ID.fromString("fmat");

	private static final War3ID UNIT_ICON_NORMAL_X = War3ID.fromString("ubpx");
	private static final War3ID UNIT_ICON_NORMAL_Y = War3ID.fromString("ubpy");
	private static final War3ID UNIT_ICON_NORMAL = War3ID.fromString("uico");
	private static final War3ID UNIT_TIP = War3ID.fromString("utip");
	private static final War3ID UNIT_REVIVE_TIP = War3ID.fromString("utpr");
	private static final War3ID UNIT_AWAKEN_TIP = War3ID.fromString("uawt");
	private static final War3ID UNIT_UBER_TIP = War3ID.fromString("utub");
	private static final War3ID UNIT_HOTKEY = War3ID.fromString("uhot");

	private static final War3ID ITEM_ICON_NORMAL_X = War3ID.fromString("ubpx");
	private static final War3ID ITEM_ICON_NORMAL_Y = War3ID.fromString("ubpy");
	private static final War3ID ITEM_ICON_NORMAL = War3ID.fromString("iico");
	private static final War3ID ITEM_TIP = War3ID.fromString("utip");
	private static final War3ID ITEM_UBER_TIP = War3ID.fromString("utub");
	private static final War3ID ITEM_DESCRIPTION = War3ID.fromString("ides");
	private static final War3ID ITEM_HOTKEY = War3ID.fromString("uhot");

	private static final War3ID UPGRADE_ICON_NORMAL_X = War3ID.fromString("gbpx");
	private static final War3ID UPGRADE_ICON_NORMAL_Y = War3ID.fromString("gbpy");
	private static final War3ID UPGRADE_ICON_NORMAL = War3ID.fromString("gar1");
	private static final War3ID UPGRADE_LEVELS = War3ID.fromString("glvl");
	private static final War3ID UPGRADE_TIP = War3ID.fromString("gtp1");
	private static final War3ID UPGRADE_UBER_TIP = War3ID.fromString("gub1");
	private static final War3ID UPGRADE_HOTKEY = War3ID.fromString("ghk1");

	private final Map<War3ID, AbilityUI> rawcodeToUI = new HashMap<>();
	private final Map<War3ID, BuffUI> rawcodeToBuffUI = new HashMap<>();
	private final Map<War3ID, UnitIconUI> rawcodeToUnitUI = new HashMap<>();
	private final Map<War3ID, ItemUI> rawcodeToItemUI = new HashMap<>();
	private final Map<War3ID, List<IconUI>> rawcodeToUpgradeUI = new HashMap<>();
	private final IconUI moveUI;
	private final IconUI stopUI;
	private final IconUI holdPosUI;
	private final IconUI patrolUI;
	private final IconUI attackUI;
	private final IconUI attackGroundUI;
	private final IconUI buildHumanUI;
	private final IconUI buildOrcUI;
	private final IconUI buildNightElfUI;
	private final IconUI buildUndeadUI;
	private final IconUI buildNeutralUI;
	private final IconUI buildNagaUI;
	private final IconUI cancelUI;
	private final IconUI cancelBuildUI;
	private final IconUI cancelTrainUI;
	private final IconUI rallyUI;
	private final IconUI selectSkillUI;
	private final String disabledPrefix;

	public AbilityDataUI(final MutableObjectData abilityData, final MutableObjectData buffData,
			final MutableObjectData unitData, final MutableObjectData itemData, final MutableObjectData upgradeData,
			final GameUI gameUI, final War3MapViewer viewer) {
		this.disabledPrefix = gameUI.getSkinField("CommandButtonDisabledArtPath");
		for (final War3ID alias : abilityData.keySet()) {
			final MutableGameObject abilityTypeData = abilityData.get(alias);
			final String iconResearchPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_RESEARCH, 0));
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_NORMAL, 0));
			final String iconTurnOffPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_TURN_OFF, 0));
			final String iconTip = abilityTypeData.getFieldAsString(ABILITY_TIP, 1);
			final String iconUberTip = abilityTypeData.getFieldAsString(ABILITY_UBER_TIP, 1);
			final char iconHotkey = getHotkey(abilityTypeData, ABILITY_HOTKEY_NORMAL);
			final String iconTurnOffTip = abilityTypeData.getFieldAsString(ABILITY_UN_TIP, 1);
			final String iconTurnOffUberTip = abilityTypeData.getFieldAsString(ABILITY_UN_UBER_TIP, 1);
			final char iconTurnOffHotkey = getHotkey(abilityTypeData, ABILITY_HOTKEY_TURNOFF);
			final String iconResearchTip = abilityTypeData.getFieldAsString(ABILITY_RESEARCH_TIP, 0);
			final String iconResearchUberTip = abilityTypeData.getFieldAsString(ABILITY_RESEARCH_UBER_TIP, 0);
			final char iconResearchHotkey = getHotkey(abilityTypeData, ABILITY_HOTKEY_LEARN);
			final int iconResearchX = abilityTypeData.getFieldAsInteger(ICON_RESEARCH_X, 0);
			final int iconResearchY = abilityTypeData.getFieldAsInteger(ICON_RESEARCH_Y, 0);
			final int iconNormalX = abilityTypeData.getFieldAsInteger(ICON_NORMAL_X, 0);
			final int iconNormalY = abilityTypeData.getFieldAsInteger(ICON_NORMAL_Y, 0);
			final int iconTurnOffX = abilityTypeData.getFieldAsInteger(ICON_TURN_OFF_X, 0);
			final int iconTurnOffY = abilityTypeData.getFieldAsInteger(ICON_TURN_OFF_Y, 0);
			final Texture iconResearch = gameUI.loadTexture(iconResearchPath);
			final Texture iconResearchDisabled = gameUI.loadTexture(disable(iconResearchPath, this.disabledPrefix));
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
			final Texture iconTurnOff = gameUI.loadTexture(iconTurnOffPath);
			final Texture iconTurnOffDisabled = gameUI.loadTexture(disable(iconTurnOffPath, this.disabledPrefix));

			final List<EffectAttachmentUI> casterArt = new ArrayList<>();
			final List<String> casterArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(CASTER_ART, 0).split(","));
			final int casterAttachmentCount = abilityTypeData.getFieldAsInteger(CASTER_ART_ATTACHMENT_COUNT, 0);
			final int casterAttachmentIndexMax = Math.min(casterAttachmentCount - 1, casterArtPaths.size() - 1);
			final int casterIteratorCount = Math.max(casterAttachmentCount, casterArtPaths.size());
			for (int i = 0; i < casterIteratorCount; i++) {
				final String modelPath = casterArtPaths.get(Math.max(0, Math.min(i, casterAttachmentIndexMax)));
				final War3ID attachmentPointKey = tryGet(CASTER_ART_ATTACHMENT_POINT, i);
				final List<String> attachmentPoints = Arrays
						.asList(abilityTypeData.getFieldAsString(attachmentPointKey, 0).split(","));
				casterArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}
			final List<EffectAttachmentUI> targetArt = new ArrayList<>();
			final List<String> targetArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(TARGET_ART, 0).split(","));
			final int targetAttachmentCount = abilityTypeData.getFieldAsInteger(TARGET_ART_ATTACHMENT_COUNT, 0);
			final int targetAttachmentIndexMax = Math.min(targetAttachmentCount - 1, targetArtPaths.size() - 1);
			final int targetIteratorCount = Math.max(targetAttachmentCount, targetArtPaths.size());
			for (int i = 0; i < targetIteratorCount; i++) {
				final String modelPath = targetArtPaths.get(Math.max(0, Math.min(i, targetAttachmentIndexMax)));
				final War3ID attachmentPointKey = tryGet(TARGET_ART_ATTACHMENT_POINT, i);
				final List<String> attachmentPoints = Arrays
						.asList(abilityTypeData.getFieldAsString(attachmentPointKey, 0).split(","));
				targetArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}
			final List<EffectAttachmentUI> specialArt = new ArrayList<>();
			final List<String> specialArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(SPECIAL_ART, 0).split(","));
			for (int i = 0; i < specialArtPaths.size(); i++) {
				final String modelPath = specialArtPaths.get(i);
				final List<String> attachmentPoints = Arrays
						.asList(abilityTypeData.getFieldAsString(SPECIAL_ART_ATTACHMENT_POINT, 0).split(","));
				specialArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}
			final List<EffectAttachmentUI> effectArt = new ArrayList<>();
			final List<String> effectArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(EFFECT_ART, 0).split(","));
			for (int i = 0; i < effectArtPaths.size(); i++) {
				final String modelPath = effectArtPaths.get(i);
				// TODO so if this is used with buffs or whatever, it would break because of
				// using ability meta on buff meta, just bad in a lot of ways
				final String effectAttach = abilityTypeData.readSLKTag("Effectattach");
				final List<String> attachmentPoints = ((effectAttach == null) || effectAttach.isEmpty())
						? Collections.emptyList()
						: Arrays.asList(effectAttach);
				effectArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}
			final List<EffectAttachmentUI> areaEffectArt = new ArrayList<>();
			final List<String> areaEffectArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(AREA_EFFECT_ART, 0).split(","));
			for (final String areaEffectArtPath : areaEffectArtPaths) {
				areaEffectArt.add(new EffectAttachmentUI(areaEffectArtPath, Collections.emptyList()));
			}
			final List<EffectAttachmentUIMissile> missileArt = new ArrayList<>();
			final List<String> missileArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(MISSILE_ART, 0).split(","));

			float missileArc = abilityTypeData.getFieldAsFloat(MISSILE_ARC, 0);
			for (final String missileArtPath : missileArtPaths) {
				missileArt.add(new EffectAttachmentUIMissile(missileArtPath, Collections.emptyList(), missileArc));
			}

			final String effectSound = abilityTypeData.getFieldAsString(ABILITY_EFFECT_SOUND, 0);
			final String effectSoundLooped = abilityTypeData.getFieldAsString(ABILITY_EFFECT_SOUND_LOOPED, 0);

			this.rawcodeToUI.put(alias,
					new AbilityUI(
							new IconUI(iconResearch, iconResearchDisabled, iconResearchX, iconResearchY,
									iconResearchTip, iconResearchUberTip, iconResearchHotkey),
							new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY, iconTip, iconUberTip,
									iconHotkey),
							new IconUI(iconTurnOff, iconTurnOffDisabled, iconTurnOffX, iconTurnOffY, iconTurnOffTip,
									iconTurnOffUberTip, iconTurnOffHotkey),
							casterArt, targetArt, specialArt, effectArt, areaEffectArt, missileArt, effectSound,
							effectSoundLooped));
		}
		for (final War3ID alias : buffData.keySet()) {
			// TODO pretty sure that in WC3 the buffs and abilities are stored in the same
			// table, but I was already using an object editor tab emulator that I wrote
			// previously and so it has these divided...
			final MutableGameObject abilityTypeData = buffData.get(alias);
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(BUFF_ICON_NORMAL, 0));
			final String iconTip = abilityTypeData.getFieldAsString(BUFF_ABILITY_TIP, 1);
			final String iconUberTip = abilityTypeData.getFieldAsString(BUFF_ABILITY_UBER_TIP, 1);
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));

			final List<EffectAttachmentUI> targetArt = new ArrayList<>();
			final List<String> targetArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(BUFF_TARGET_ART, 0).split(","));
			final int targetAttachmentCount = abilityTypeData.getFieldAsInteger(BUFF_TARGET_ART_ATTACHMENT_COUNT, 0);
			final int targetAttachmentIndexMax = Math.min(targetAttachmentCount - 1, targetArtPaths.size() - 1);
			final int targetIteratorCount = Math.max(targetAttachmentCount, targetArtPaths.size());
			for (int i = 0; i < targetIteratorCount; i++) {
				final String modelPath = targetArtPaths.get(Math.max(0, Math.min(i, targetAttachmentIndexMax)));
				final War3ID attachmentPointKey = tryGet(BUFF_TARGET_ART_ATTACHMENT_POINT, i);
				final List<String> attachmentPoints = Arrays
						.asList(abilityTypeData.getFieldAsString(attachmentPointKey, 0).split(","));
				targetArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}
			final List<EffectAttachmentUI> specialArt = new ArrayList<>();
			final List<String> specialArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(BUFF_SPECIAL_ART, 0).split(","));
			for (int i = 0; i < specialArtPaths.size(); i++) {
				final String modelPath = specialArtPaths.get(i);
				final List<String> attachmentPoints = Arrays
						.asList(abilityTypeData.getFieldAsString(BUFF_SPECIAL_ART_ATTACHMENT_POINT, 0).split(","));
				specialArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}
			final List<EffectAttachmentUI> effectArt = new ArrayList<>();
			final List<String> effectArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(BUFF_EFFECT_ART, 0).split(","));
			for (int i = 0; i < effectArtPaths.size(); i++) {
				final String modelPath = effectArtPaths.get(i);
				// TODO so if this is used with buffs or whatever, it would break because of
				// using ability meta on buff meta, just bad in a lot of ways
				final String effectAttach = abilityTypeData.readSLKTag("Effectattach");
				final List<String> attachmentPoints = ((effectAttach == null) || effectAttach.isEmpty())
						? Collections.emptyList()
						: Arrays.asList(effectAttach);
				effectArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}
			final List<EffectAttachmentUI> missileArt = new ArrayList<>();
			final List<String> missileArtPaths = Arrays
					.asList(abilityTypeData.getFieldAsString(BUFF_MISSILE_ART, 0).split(","));
			for (final String missileArtPath : missileArtPaths) {
				missileArt.add(new EffectAttachmentUI(missileArtPath, Collections.emptyList()));
			}

			final String effectSound = abilityTypeData.getFieldAsString(BUFF_ABILITY_EFFECT_SOUND, 0);
			final String effectSoundLooped = abilityTypeData.getFieldAsString(BUFF_ABILITY_EFFECT_SOUND_LOOPED, 0);

			this.rawcodeToBuffUI.put(alias,
					new BuffUI(new IconUI(iconNormal, iconNormalDisabled, 0, 0, iconTip, iconUberTip, '\0'), targetArt,
							specialArt, effectArt, missileArt, effectSound, effectSoundLooped));
		}
		for (final War3ID alias : unitData.keySet()) {
			final MutableGameObject abilityTypeData = unitData.get(alias);
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(UNIT_ICON_NORMAL, 0));
			final int iconNormalX = abilityTypeData.getFieldAsInteger(UNIT_ICON_NORMAL_X, 0);
			final int iconNormalY = abilityTypeData.getFieldAsInteger(UNIT_ICON_NORMAL_Y, 0);
			final String iconTip = abilityTypeData.getFieldAsString(UNIT_TIP, 0);
			final String reviveTip = abilityTypeData.getFieldAsString(UNIT_REVIVE_TIP, 0);
			final String awakenTip = abilityTypeData.getFieldAsString(UNIT_AWAKEN_TIP, 0);
			final String iconUberTip = abilityTypeData.getFieldAsString(UNIT_UBER_TIP, 0);
			final char iconHotkey = getHotkey(abilityTypeData, UNIT_HOTKEY);
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
			this.rawcodeToUnitUI.put(alias, new UnitIconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY,
					iconTip, iconUberTip, iconHotkey, reviveTip, awakenTip));
		}
		for (final War3ID alias : itemData.keySet()) {
			final MutableGameObject abilityTypeData = itemData.get(alias);
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ITEM_ICON_NORMAL, 0));
			final int iconNormalX = abilityTypeData.getFieldAsInteger(ITEM_ICON_NORMAL_X, 0);
			final int iconNormalY = abilityTypeData.getFieldAsInteger(ITEM_ICON_NORMAL_Y, 0);
			final String iconTip = abilityTypeData.getFieldAsString(ITEM_TIP, 0);
			final String iconUberTip = abilityTypeData.getFieldAsString(ITEM_UBER_TIP, 0);
			final String iconDescription = abilityTypeData.getFieldAsString(ITEM_DESCRIPTION, 0);
			final char iconHotkey = getHotkey(abilityTypeData, ITEM_HOTKEY);
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
			this.rawcodeToItemUI
					.put(alias,
							new ItemUI(
									new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY, iconTip,
											iconUberTip, iconHotkey),
									abilityTypeData.getName(), iconDescription, iconNormalPath));
		}
		for (final War3ID alias : upgradeData.keySet()) {
			final MutableGameObject upgradeTypeData = upgradeData.get(alias);
			final int upgradeLevels = upgradeTypeData.getFieldAsInteger(UPGRADE_LEVELS, 0);
			final int iconNormalX = upgradeTypeData.getFieldAsInteger(UPGRADE_ICON_NORMAL_X, 0);
			final int iconNormalY = upgradeTypeData.getFieldAsInteger(UPGRADE_ICON_NORMAL_Y, 0);
			final List<IconUI> upgradeIconsByLevel = new ArrayList<>();
			for (int i = 0; i < upgradeLevels; i++) {
				int upgradeLevelValue = i + 1;
				final String iconTip = upgradeTypeData.getFieldAsString(UPGRADE_TIP, upgradeLevelValue);
				final String iconUberTip = upgradeTypeData.getFieldAsString(UPGRADE_UBER_TIP, upgradeLevelValue);
				final String iconNormalPath = gameUI
						.trySkinField(upgradeTypeData.getFieldAsString(UPGRADE_ICON_NORMAL, upgradeLevelValue));
				final char iconHotkey = getHotkey(upgradeTypeData, UPGRADE_HOTKEY, upgradeLevelValue);
				final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
				final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
				upgradeIconsByLevel.add(new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY, iconTip,
						iconUberTip, iconHotkey));
			}
			this.rawcodeToUpgradeUI.put(alias, upgradeIconsByLevel);
		}
		this.moveUI = createBuiltInIconUI(gameUI, "CmdMove", this.disabledPrefix);
		this.stopUI = createBuiltInIconUI(gameUI, "CmdStop", this.disabledPrefix);
		this.holdPosUI = createBuiltInIconUI(gameUI, "CmdHoldPos", this.disabledPrefix);
		this.patrolUI = createBuiltInIconUI(gameUI, "CmdPatrol", this.disabledPrefix);
		this.attackUI = createBuiltInIconUI(gameUI, "CmdAttack", this.disabledPrefix);
		this.buildHumanUI = createBuiltInIconUI(gameUI, "CmdBuildHuman", this.disabledPrefix);
		this.buildOrcUI = createBuiltInIconUI(gameUI, "CmdBuildOrc", this.disabledPrefix);
		this.buildNightElfUI = createBuiltInIconUI(gameUI, "CmdBuildNightElf", this.disabledPrefix);
		this.buildUndeadUI = createBuiltInIconUI(gameUI, "CmdBuildUndead", this.disabledPrefix);
		this.buildNagaUI = createBuiltInIconUISplit(gameUI, "CmdBuildNaga", "CmdBuildOrc",
				abilityData.get(War3ID.fromString("AGbu")), this.disabledPrefix);
		this.buildNeutralUI = createBuiltInIconUI(gameUI, "CmdBuild", this.disabledPrefix);
		this.attackGroundUI = createBuiltInIconUI(gameUI, "CmdAttackGround", this.disabledPrefix);
		this.cancelUI = createBuiltInIconUI(gameUI, "CmdCancel", this.disabledPrefix);
		this.cancelBuildUI = createBuiltInIconUI(gameUI, "CmdCancelBuild", this.disabledPrefix);
		this.cancelTrainUI = createBuiltInIconUI(gameUI, "CmdCancelTrain", this.disabledPrefix);
		this.rallyUI = createBuiltInIconUI(gameUI, "CmdRally", this.disabledPrefix);
		this.selectSkillUI = createBuiltInIconUI(gameUI, "CmdSelectSkill", this.disabledPrefix);
	}

	private char getHotkey(final MutableGameObject abilityTypeData, final War3ID abilityHotkeyNormal) {
		return getHotkey(abilityTypeData, abilityHotkeyNormal, 1);
	}

	private char getHotkey(final MutableGameObject abilityTypeData, final War3ID abilityHotkeyNormal, final int level) {
		final String iconHotkeyString = abilityTypeData.getFieldAsString(abilityHotkeyNormal, level);
		final char itemHotkey = getHotkeyChar(iconHotkeyString);
		return itemHotkey;
	}

	private IconUI createBuiltInIconUI(final GameUI gameUI, final String key, final String disabledPrefix) {
		final Element builtInAbility = gameUI.getSkinData().get(key);
		final String iconPath = gameUI.trySkinField(builtInAbility.getField("Art"));
		final Texture icon = gameUI.loadTexture(iconPath);
		final Texture iconDisabled = gameUI.loadTexture(disable(iconPath, disabledPrefix));
		final int buttonPositionX = builtInAbility.getFieldValue("Buttonpos", 0);
		final int buttonPositionY = builtInAbility.getFieldValue("Buttonpos", 1);
		final String tip = builtInAbility.getField("Tip");
		final String uberTip = builtInAbility.getField("UberTip");
		final String hotkeyString = builtInAbility.getField("Hotkey");
		final char hotkey = getHotkeyChar(hotkeyString);
		return new IconUI(icon, iconDisabled, buttonPositionX, buttonPositionY, tip, uberTip, hotkey);
	}

	private IconUI createBuiltInIconUISplit(final GameUI gameUI, final String key, String funckey,
			MutableGameObject worldEditorObject, final String disabledPrefix) {
		final Element builtInAbility = gameUI.getSkinData().get(key);
		final Element builtInAbilityFunc = gameUI.getSkinData().get(funckey);
		String iconPath = gameUI.trySkinField(builtInAbilityFunc.getField("Art"));
		String worldEditorValue = worldEditorObject.readSLKTag("Art");
		if (worldEditorValue.length() > 0) {
			iconPath = worldEditorValue;
		}
		final Texture icon = gameUI.loadTexture(iconPath);
		final Texture iconDisabled = gameUI.loadTexture(disable(iconPath, disabledPrefix));
		final int buttonPositionX = builtInAbilityFunc.getFieldValue("Buttonpos", 0);
		final int buttonPositionY = builtInAbilityFunc.getFieldValue("Buttonpos", 1);
		final String tip = builtInAbility.getField("Tip");
		final String uberTip = builtInAbility.getField("UberTip");
		final String hotkeyString = builtInAbility.getField("Hotkey");
		final char hotkey = getHotkeyChar(hotkeyString);
		return new IconUI(icon, iconDisabled, buttonPositionX, buttonPositionY, tip, uberTip, hotkey);
	}

	private char getHotkeyChar(final String hotkeyString) {
		if (hotkeyString.length() > 1) {
			final int hotkeyInt = Integer.parseInt(hotkeyString);
			if (hotkeyInt == 512) {
				return WarsmashConstants.SPECIAL_ESCAPE_KEYCODE;
			}
			return (char) hotkeyInt;
		}
		return hotkeyString.length() > 0 ? hotkeyString.charAt(0) : '\0';
	}

	public AbilityUI getUI(final War3ID rawcode) {
		return this.rawcodeToUI.get(rawcode);
	}

	public BuffUI getBuffUI(final War3ID rawcode) {
		return this.rawcodeToBuffUI.get(rawcode);
	}

	public UnitIconUI getUnitUI(final War3ID rawcode) {
		return this.rawcodeToUnitUI.get(rawcode);
	}

	public ItemUI getItemUI(final War3ID rawcode) {
		return this.rawcodeToItemUI.get(rawcode);
	}

	public IconUI getUpgradeUI(final War3ID rawcode, final int level) {
		final List<IconUI> upgradeUI = this.rawcodeToUpgradeUI.get(rawcode);
		if (upgradeUI != null) {
			if (level < upgradeUI.size()) {
				return upgradeUI.get(level);
			}
			else {
				return upgradeUI.get(upgradeUI.size() - 1);
			}
		}
		return null;
	}

	public static String disable(final String path, final String disabledPrefix) {
		final int slashIndex = path.lastIndexOf('\\');
		String name = path;
		if (slashIndex != -1) {
			name = path.substring(slashIndex + 1);
		}
		return disabledPrefix + "DIS" + name;
	}

	public IconUI getMoveUI() {
		return this.moveUI;
	}

	public IconUI getStopUI() {
		return this.stopUI;
	}

	public IconUI getHoldPosUI() {
		return this.holdPosUI;
	}

	public IconUI getPatrolUI() {
		return this.patrolUI;
	}

	public IconUI getAttackUI() {
		return this.attackUI;
	}

	public IconUI getAttackGroundUI() {
		return this.attackGroundUI;
	}

	public IconUI getBuildHumanUI() {
		return this.buildHumanUI;
	}

	public IconUI getBuildNightElfUI() {
		return this.buildNightElfUI;
	}

	public IconUI getBuildOrcUI() {
		return this.buildOrcUI;
	}

	public IconUI getBuildUndeadUI() {
		return this.buildUndeadUI;
	}

	public IconUI getBuildNagaUI() {
		return this.buildNagaUI;
	}

	public IconUI getBuildNeutralUI() {
		return this.buildNeutralUI;
	}

	public IconUI getCancelUI() {
		return this.cancelUI;
	}

	public IconUI getCancelBuildUI() {
		return this.cancelBuildUI;
	}

	public IconUI getCancelTrainUI() {
		return this.cancelTrainUI;
	}

	public IconUI getRallyUI() {
		return this.rallyUI;
	}

	public IconUI getSelectSkillUI() {
		return this.selectSkillUI;
	}

	public String getDisabledPrefix() {
		return this.disabledPrefix;
	}

	private War3ID tryGet(final War3ID[] ids, final int index) {
		if ((index >= 0) && (index < ids.length)) {
			return ids[index];
		}
		return ids[ids.length - 1];
	}
}
