package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapRuntimeObjectData;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.COrderButton;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class AbilityDataUI {
	// Standard ability icon fields
	private static final String ICON_NORMAL_XY = "Buttonpos"; // replaced from 'abpx'
	private static final String ICON_NORMAL = "Art"; // replaced from 'aart'
	private static final String ICON_TURN_OFF = "Unart"; // replaced from 'auar'
	private static final String ICON_TURN_OFF_XY = "UnButtonpos"; // replaced from 'aubx'
	private static final String ICON_RESEARCH = "ResearchArt"; // replaced from 'arar'
	private static final String ICON_RESEARCH_XY = "Researchbuttonpos"; // replaced from 'arpx'
	private static final String ABILITY_TIP = "Tip"; // replaced from 'atp1'
	private static final String ABILITY_UBER_TIP = "Ubertip"; // replaced from 'aub1'
	private static final String ABILITY_UN_TIP = "Untip"; // replaced from 'aut1'
	private static final String ABILITY_UN_UBER_TIP = "Unubertip"; // replaced from 'auu1'
	private static final String ABILITY_RESEARCH_TIP = "Researchtip"; // replaced from 'aret'
	private static final String ABILITY_RESEARCH_UBER_TIP = "Researchubertip"; // replaced from 'arut'
	private static final String ABILITY_EFFECT_SOUND = "Effectsound"; // replaced from 'aefs'
	private static final String ABILITY_EFFECT_SOUND_LOOPED = "Effectsoundlooped"; // replaced from 'aefl'

	private static final String ABILITY_HOTKEY_NORMAL = "Hotkey"; // replaced from 'ahky'
	private static final String ABILITY_HOTKEY_TURNOFF = "Unhotkey"; // replaced from 'auhk'
	private static final String ABILITY_HOTKEY_LEARN = "Researchhotkey"; // replaced from 'arhk'

	private static final String CASTER_ART = "CasterArt"; // replaced from 'acat'
	private static final String[] CASTER_ART_ATTACHMENT_POINT = { "Casterattach", // replaced from 'acap'
			"Casterattach1" }; // replaced from 'aca1'
	private static final String CASTER_ART_ATTACHMENT_COUNT = "Casterattachcount"; // replaced from 'acac'
	private static final String TARGET_ART = "TargetArt"; // replaced from 'atat'
	private static final String[] TARGET_ART_ATTACHMENT_POINT = { "Targetattach", "Targetattach1",
			// replaced from 'ata0'
			"Targetattach2", "Targetattach3", "Targetattach4", // replaced from 'ata2'
			"Targetattach5" }; // replaced from 'ata5'
	private static final String TARGET_ART_ATTACHMENT_COUNT = "Targetattachcount"; // replaced from 'atac'
	private static final String SPECIAL_ART = "SpecialArt"; // replaced from 'asat'
	private static final String SPECIAL_ART_ATTACHMENT_POINT = "Specialattach"; // replaced from 'aspt'
	private static final String EFFECT_ART = "EffectArt"; // replaced from 'aeat'
	private static final String AREA_EFFECT_ART = "Areaeffectart"; // replaced from 'aaea'
	private static final String MISSILE_ART = "Missileart"; // replaced from 'amat'
	private static final String MISSILE_ARC = "Missilearc"; // replaced from 'amac'
	private static final String LIGHTNING_EFFECTS = "LightningEffect";

	// Standard buff icon fields
	private static final String BUFF_ICON_NORMAL = "Buffart"; // replaced from 'fart'
	private static final String BUFF_ABILITY_TIP = "Bufftip"; // replaced from 'ftip'
	private static final String BUFF_ABILITY_UBER_TIP = "Buffubertip"; // replaced from 'fube'
	private static final String BUFF_ABILITY_EFFECT_SOUND = "Effectsound"; // replaced from 'fefs'
	private static final String BUFF_ABILITY_EFFECT_SOUND_LOOPED = "Effectsoundlooped"; // replaced from 'fefl'

	private static final String BUFF_TARGET_ART = "TargetArt"; // replaced from 'ftat'
	private static final String[] BUFF_TARGET_ART_ATTACHMENT_POINT = { "Targetattach", // replaced from 'fta0'
			"Targetattach1", "Targetattach2", "Targetattach3", "Targetattach4", // replaced from 'fta1'
			"Targetattach5" }; // replaced from 'fta5'
	private static final String BUFF_TARGET_ART_ATTACHMENT_COUNT = "Targetattachcount"; // replaced from 'ftac'
	private static final String BUFF_SPECIAL_ART = "SpecialArt"; // replaced from 'fsat'
	private static final String BUFF_SPECIAL_ART_ATTACHMENT_POINT = "Specialattach"; // replaced from 'fspt'
	private static final String BUFF_EFFECT_ART = "EffectArt"; // replaced from 'feat'
	private static final String BUFF_EFFECT_ART_ATTACHMENT_POINT = "Effectattach"; // replaced from 'feft'
	private static final String BUFF_MISSILE_ART = "Missileart"; // replaced from 'fmat'

	private static final String UNIT_ICON_NORMAL_XY = "Buttonpos"; // replaced from 'ubpx'
	private static final String UNIT_ICON_NORMAL = "Art"; // replaced from 'uico'
	private static final String UNIT_TIP = "Tip"; // replaced from 'utip'
	private static final String UNIT_REVIVE_TIP = "Revivetip"; // replaced from 'utpr'
	private static final String UNIT_AWAKEN_TIP = "Awakentip"; // replaced from 'uawt'
	private static final String UNIT_UBER_TIP = "Ubertip"; // replaced from 'utub'
	private static final String UNIT_HOTKEY = "Hotkey"; // replaced from 'uhot'

	private static final String ITEM_ICON_NORMAL_XY = "Buttonpos"; // replaced from 'ubpx'
	private static final String ITEM_ICON_NORMAL = "Art"; // replaced from 'iico'
	private static final String ITEM_TIP = "Tip"; // replaced from 'utip'
	private static final String ITEM_UBER_TIP = "Ubertip"; // replaced from 'utub'
	private static final String ITEM_DESCRIPTION = "Description"; // replaced from 'ides'
	private static final String ITEM_HOTKEY = "Hotkey"; // replaced from 'uhot'

	private static final String UPGRADE_ICON_NORMAL_XY = "Buttonpos"; // replaced from 'gbpx'
	private static final String UPGRADE_ICON_NORMAL = "Art"; // replaced from 'gar1'
	private static final String UPGRADE_LEVELS = "maxlevel"; // replaced from 'glvl'
	private static final String UPGRADE_TIP = "Tip"; // replaced from 'gtp1'
	private static final String UPGRADE_UBER_TIP = "Ubertip"; // replaced from 'gub1'
	private static final String UPGRADE_HOTKEY = "Hotkey"; // replaced from 'ghk1'

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
	private final IconUI neutralInteractUI;
	private final String disabledPrefix;
	private final Map<COrderButton, OrderButtonUI> buttonToRenderPeer = new HashMap<>();

	public AbilityDataUI(final Warcraft3MapRuntimeObjectData allObjectData, final GameUI gameUI,
			final War3MapViewer viewer) {
		final ObjectData abilityData = allObjectData.getAbilities();
		final ObjectData buffData = allObjectData.getBuffs();
		final ObjectData unitData = allObjectData.getUnits();
		final ObjectData itemData = allObjectData.getItems();
		final ObjectData upgradeData = allObjectData.getUpgrades();
		this.disabledPrefix = gameUI.getSkinField("CommandButtonDisabledArtPath");
		for (final String alias : abilityData.keySet()) {
			final GameObject abilityTypeData = abilityData.get(alias);
			final String iconResearchPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_RESEARCH, 0));
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_NORMAL, 0));
			final String iconTurnOffPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_TURN_OFF, 0));
			final char iconHotkey = getHotkey(abilityTypeData, ABILITY_HOTKEY_NORMAL);
			final char iconTurnOffHotkey = getHotkey(abilityTypeData, ABILITY_HOTKEY_TURNOFF);
			final String iconResearchTip = abilityTypeData.getFieldAsString(ABILITY_RESEARCH_TIP, 0);
			final String iconResearchUberTip = parseUbertip(allObjectData,
					abilityTypeData.getFieldAsString(ABILITY_RESEARCH_UBER_TIP, 0));
			final char iconResearchHotkey = getHotkey(abilityTypeData, ABILITY_HOTKEY_LEARN);
			final int iconResearchX = abilityTypeData.getFieldAsInteger(ICON_RESEARCH_XY, 0);
			final int iconResearchY = abilityTypeData.getFieldAsInteger(ICON_RESEARCH_XY, 1);
			final int iconNormalX = abilityTypeData.getFieldAsInteger(ICON_NORMAL_XY, 0);
			final int iconNormalY = abilityTypeData.getFieldAsInteger(ICON_NORMAL_XY, 1);
			final int iconTurnOffX = abilityTypeData.getFieldAsInteger(ICON_TURN_OFF_XY, 0);
			final int iconTurnOffY = abilityTypeData.getFieldAsInteger(ICON_TURN_OFF_XY, 1);
			final Texture iconResearch = gameUI.loadTexture(iconResearchPath);
			final Texture iconResearchDisabled = gameUI.loadTexture(disable(iconResearchPath, this.disabledPrefix));
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
			final Texture iconTurnOff = gameUI.loadTexture(iconTurnOffPath);
			final Texture iconTurnOffDisabled = gameUI.loadTexture(disable(iconTurnOffPath, this.disabledPrefix));

			final List<IconUI> turnOffIconUIs = new ArrayList<>();
			final List<IconUI> normalIconUIs = new ArrayList<>();
			final int levels = Math.max(1, abilityTypeData.getFieldAsInteger(AbilityFields.LEVELS, 0));
			for (int i = 0; i < levels; i++) {
				final String iconTip = abilityTypeData.getFieldAsString(ABILITY_TIP, i);
				final String iconUberTip = parseUbertip(allObjectData,
						abilityTypeData.getFieldAsString(ABILITY_UBER_TIP, i));
				final String iconTurnOffTip = abilityTypeData.getFieldAsString(ABILITY_UN_TIP, i);
				final String iconTurnOffUberTip = parseUbertip(allObjectData,
						abilityTypeData.getFieldAsString(ABILITY_UN_UBER_TIP, i));

				normalIconUIs.add(new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY, iconTip,
						iconUberTip, iconHotkey));
				turnOffIconUIs.add(new IconUI(iconTurnOff, iconTurnOffDisabled, iconTurnOffX, iconTurnOffY,
						iconTurnOffTip, iconTurnOffUberTip, iconTurnOffHotkey));
			}

			final List<EffectAttachmentUI> casterArt = new ArrayList<>();
			final List<String> casterArtPaths = abilityTypeData.getFieldAsList(CASTER_ART);
			final int casterAttachmentCount = abilityTypeData.getFieldAsInteger(CASTER_ART_ATTACHMENT_COUNT, 0);
			final int casterAttachmentIndexMax = Math.min(casterAttachmentCount - 1, casterArtPaths.size() - 1);
			final int casterIteratorCount = Math.max(casterAttachmentCount, casterArtPaths.size());
			for (int i = 0; i < casterIteratorCount; i++) {
				final String modelPath = casterArtPaths.isEmpty() ? ""
						: casterArtPaths.get(Math.max(0, Math.min(i, casterAttachmentIndexMax)));
				final String attachmentPointKey = tryGet(CASTER_ART_ATTACHMENT_POINT, i);
				final List<String> attachmentPoints = abilityTypeData.getFieldAsList(attachmentPointKey);
				casterArt.add(new EffectAttachmentUI(modelPath, attachmentPoints));
			}
			final List<EffectAttachmentUI> targetArt = new ArrayList<>();
			final List<String> targetArtPaths = abilityTypeData.getFieldAsList(TARGET_ART);
			final int targetAttachmentCount = abilityTypeData.getFieldAsInteger(TARGET_ART_ATTACHMENT_COUNT, 0);
			final int targetAttachmentIndexMax = Math.min(targetAttachmentCount - 1, targetArtPaths.size() - 1);
			final int targetIteratorCount = Math.max(targetAttachmentCount, targetArtPaths.size());
			for (int i = 0; i < targetIteratorCount; i++) {
				final String modelPath = targetArtPaths.get(Math.max(0, Math.min(i, targetAttachmentIndexMax)));
				final String attachmentPointKey = tryGet(TARGET_ART_ATTACHMENT_POINT, i);
				final List<String> attachmentPoints = abilityTypeData.getFieldAsList(attachmentPointKey);
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

			final float missileArc = abilityTypeData.getFieldAsFloat(MISSILE_ARC, 0);
			for (final String missileArtPath : missileArtPaths) {
				missileArt.add(new EffectAttachmentUIMissile(missileArtPath, Collections.emptyList(), missileArc));
			}

			final List<String> LightningEffectList = Arrays
					.asList(abilityTypeData.getFieldAsString(LIGHTNING_EFFECTS, 0).split(","));
			final List<War3ID> LightningEffects = new ArrayList<>();

			for (final String lightning : LightningEffectList) {
				if ((lightning != null) && !lightning.isBlank()) {
					LightningEffects.add(War3ID.fromString(lightning));
				}
			}

			final String effectSound = abilityTypeData.getFieldAsString(ABILITY_EFFECT_SOUND, 0);
			final String effectSoundLooped = abilityTypeData.getFieldAsString(ABILITY_EFFECT_SOUND_LOOPED, 0);

			this.rawcodeToUI.put(War3ID.fromString(alias),
					new AbilityUI(
							new IconUI(iconResearch, iconResearchDisabled, iconResearchX, iconResearchY,
									iconResearchTip, iconResearchUberTip, iconResearchHotkey),
							normalIconUIs, turnOffIconUIs, casterArt, targetArt, specialArt, effectArt, areaEffectArt,
							missileArt, LightningEffects, effectSound, effectSoundLooped));
		}
		for (final String alias : buffData.keySet()) {
			// TODO pretty sure that in WC3 the buffs and abilities are stored in the same
			// table, but I was already using an object editor tab emulator that I wrote
			// previously and so it has these divided...
			final GameObject abilityTypeData = buffData.get(alias);
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(BUFF_ICON_NORMAL, 0));
			final String iconTip = abilityTypeData.getFieldAsString(BUFF_ABILITY_TIP, 0);
			final String iconUberTip = parseUbertip(allObjectData,
					abilityTypeData.getFieldAsString(BUFF_ABILITY_UBER_TIP, 0));
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));

			final List<EffectAttachmentUI> targetArt = new ArrayList<>();
			final List<String> targetArtPaths = abilityTypeData.getFieldAsList(BUFF_TARGET_ART);
			final int targetAttachmentCount = abilityTypeData.getFieldAsInteger(BUFF_TARGET_ART_ATTACHMENT_COUNT, 0);
			final int targetAttachmentIndexMax = Math.min(targetAttachmentCount - 1, targetArtPaths.size() - 1);
			final int targetIteratorCount = Math.max(targetAttachmentCount, targetArtPaths.size());
			for (int i = 0; i < targetIteratorCount; i++) {
				final String modelPath = targetArtPaths.isEmpty() ? ""
						: targetArtPaths.get(Math.max(0, Math.min(i, targetAttachmentIndexMax)));
				final String attachmentPointKey = tryGet(BUFF_TARGET_ART_ATTACHMENT_POINT, i);
				final List<String> attachmentPoints = abilityTypeData.getFieldAsList(attachmentPointKey);
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

			this.rawcodeToBuffUI.put(War3ID.fromString(alias),
					new BuffUI(new IconUI(iconNormal, iconNormalDisabled, 0, 0, iconTip, iconUberTip, '\0'), targetArt,
							specialArt, effectArt, missileArt, effectSound, effectSoundLooped));
		}
		for (final String alias : unitData.keySet()) {
			final GameObject abilityTypeData = unitData.get(alias);
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(UNIT_ICON_NORMAL, 0));
			final int iconNormalX = abilityTypeData.getFieldAsInteger(UNIT_ICON_NORMAL_XY, 0);
			final int iconNormalY = abilityTypeData.getFieldAsInteger(UNIT_ICON_NORMAL_XY, 1);
			final String iconTip = abilityTypeData.getFieldAsString(UNIT_TIP, 0);
			final String reviveTip = abilityTypeData.getFieldAsString(UNIT_REVIVE_TIP, 0);
			final String awakenTip = abilityTypeData.getFieldAsString(UNIT_AWAKEN_TIP, 0);
			final String iconUberTip = parseUbertip(allObjectData, abilityTypeData.getFieldAsString(UNIT_UBER_TIP, 0));
			final char iconHotkey = getHotkey(abilityTypeData, UNIT_HOTKEY);
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
			this.rawcodeToUnitUI.put(War3ID.fromString(alias), new UnitIconUI(iconNormal, iconNormalDisabled,
					iconNormalX, iconNormalY, iconTip, iconUberTip, iconHotkey, reviveTip, awakenTip));
		}
		for (final String alias : itemData.keySet()) {
			final GameObject abilityTypeData = itemData.get(alias);
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ITEM_ICON_NORMAL, 0));
			final int iconNormalX = abilityTypeData.getFieldAsInteger(ITEM_ICON_NORMAL_XY, 0);
			final int iconNormalY = abilityTypeData.getFieldAsInteger(ITEM_ICON_NORMAL_XY, 1);
			final String iconTip = abilityTypeData.getFieldAsString(ITEM_TIP, 0);
			final String iconUberTip = parseUbertip(allObjectData, abilityTypeData.getFieldAsString(ITEM_UBER_TIP, 0));
			final String iconDescription = abilityTypeData.getFieldAsString(ITEM_DESCRIPTION, 0);
			final char iconHotkey = getHotkey(abilityTypeData, ITEM_HOTKEY);
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
			this.rawcodeToItemUI
					.put(War3ID.fromString(alias),
							new ItemUI(
									new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY, iconTip,
											iconUberTip, iconHotkey),
									abilityTypeData.getName(), iconDescription, iconNormalPath));
		}
		for (final String alias : upgradeData.keySet()) {
			final GameObject upgradeTypeData = upgradeData.get(alias);
			final int upgradeLevels = upgradeTypeData.getFieldAsInteger(UPGRADE_LEVELS, 0);
			final int iconNormalX = upgradeTypeData.getFieldAsInteger(UPGRADE_ICON_NORMAL_XY, 0);
			final int iconNormalY = upgradeTypeData.getFieldAsInteger(UPGRADE_ICON_NORMAL_XY, 1);
			final List<IconUI> upgradeIconsByLevel = new ArrayList<>();
			for (int upgradeLevelValue = 0; upgradeLevelValue < upgradeLevels; upgradeLevelValue++) {
				final String iconTip = upgradeTypeData.getFieldAsString(UPGRADE_TIP, upgradeLevelValue);
				final String iconUberTip = parseUbertip(allObjectData,
						upgradeTypeData.getFieldAsString(UPGRADE_UBER_TIP, upgradeLevelValue));
				final String iconNormalPath = gameUI
						.trySkinField(upgradeTypeData.getFieldAsString(UPGRADE_ICON_NORMAL, upgradeLevelValue));
				final char iconHotkey = getHotkey(upgradeTypeData, UPGRADE_HOTKEY, upgradeLevelValue);
				final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
				final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, this.disabledPrefix));
				upgradeIconsByLevel.add(new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY, iconTip,
						iconUberTip, iconHotkey));
			}
			this.rawcodeToUpgradeUI.put(War3ID.fromString(alias), upgradeIconsByLevel);
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
		this.neutralInteractUI = getUI(War3ID.fromString("Anei")).getOnIconUI(0);
	}

	private static String parseUbertip(final Warcraft3MapRuntimeObjectData allObjectData, final String originalText) {
		String tooltipText = originalText;
		int openBracketIndex = tooltipText.indexOf('<');
		int closeBracketIndex = tooltipText.indexOf('>');
		while ((openBracketIndex < closeBracketIndex) && (openBracketIndex != -1)) {
			final String textBefore = tooltipText.substring(0, openBracketIndex);
			final String textAfter = tooltipText.substring(closeBracketIndex + 1);
			final String codeText = tooltipText.substring(openBracketIndex + 1, closeBracketIndex);
			final String[] codeTextParts = codeText.split(",");
			String valueText = "";
			boolean percent = false;
			if (((codeTextParts.length == 2)
					|| ((codeTextParts.length == 3) && (percent = "%".equals(codeTextParts[2]))))) {
				final String rawcode = codeTextParts[0];
				GameObject unit = allObjectData.getUnits().get(rawcode);
				if (unit == null) {
					unit = allObjectData.getItems().get(rawcode);
				}
				if (unit == null) {
					unit = allObjectData.getAbilities().get(rawcode);
				}
				if (unit != null) {
					if (percent) {
						valueText = Integer.toString((int) (unit.readSLKTagFloat(codeTextParts[1]) * 100f));
					}
					else {
						valueText = unit.readSLKTag(codeTextParts[1]);
					}
				}
				else {
					valueText = codeText + "{missing}";
				}
			}

			// TODO less java.lang.String memory allocation here could be achieved using one
			// string builder for all loop iterations
			tooltipText = textBefore + valueText + textAfter;

			openBracketIndex = tooltipText.indexOf('<');
			closeBracketIndex = tooltipText.indexOf('>');
		}
		return tooltipText;
	}

	private char getHotkey(final GameObject abilityTypeData, final String abilityHotkeyNormal) {
		return getHotkey(abilityTypeData, abilityHotkeyNormal, 0);
	}

	private char getHotkey(final GameObject abilityTypeData, final String abilityHotkeyNormal, final int index) {
		final String iconHotkeyString = abilityTypeData.getFieldAsString(abilityHotkeyNormal, index);
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

	private IconUI createBuiltInIconUISplit(final GameUI gameUI, final String key, final String funckey,
			final GameObject worldEditorObject, final String disabledPrefix) {
		final Element builtInAbility = gameUI.getSkinData().get(key);
		final Element builtInAbilityFunc = gameUI.getSkinData().get(funckey);
		String iconPath = gameUI.trySkinField(builtInAbilityFunc.getField("Art"));
		final String worldEditorValue = worldEditorObject.getField("Art");
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
			boolean anyNonDigit = false;
			for (int i = 0; i < hotkeyString.length(); i++) {
				if (!Character.isDigit(hotkeyString.charAt(i))) {
					anyNonDigit = true;
				}
			}
			if (!anyNonDigit) {
				final int hotkeyInt = Integer.parseInt(hotkeyString);
				if (hotkeyInt == 512) {
					return WarsmashConstants.SPECIAL_ESCAPE_KEYCODE;
				}
				return (char) hotkeyInt;
			}
			else {
				String resultStr;
				try {
					resultStr = new String(hotkeyString.getBytes(), "utf-8");
				}
				catch (final UnsupportedEncodingException e) {
					e.printStackTrace();
					return '\0';
				}
				final char result = resultStr.charAt(0);
				System.out.println("weird hotkey: " + result);
				return result;
			}
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

	public IconUI getNeutralInteractUI() {
		return this.neutralInteractUI;
	}

	public String getDisabledPrefix() {
		return this.disabledPrefix;
	}

	private String tryGet(final String[] ids, final int index) {
		if ((index >= 0) && (index < ids.length)) {
			return ids[index];
		}
		return ids[ids.length - 1];
	}

	public OrderButtonUI getRenderPeer(final COrderButton orderButton) {
		return this.buttonToRenderPeer.get(orderButton);
	}

	public void createRenderPeer(final COrderButton orderButton) {
		this.buttonToRenderPeer.put(orderButton, new OrderButtonUI());
	}

	public void removeRenderPeer(final COrderButton orderButton) {
		this.buttonToRenderPeer.remove(orderButton);
	}
}
