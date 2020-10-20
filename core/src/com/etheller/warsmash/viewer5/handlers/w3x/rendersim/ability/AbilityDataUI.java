package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;

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

	private final Map<War3ID, AbilityIconUI> rawcodeToUI = new HashMap<>();
	private final IconUI moveUI;
	private final IconUI stopUI;
	private final IconUI holdPosUI;
	private final IconUI patrolUI;
	private final IconUI attackUI;
	private final IconUI attackGroundUI;

	public AbilityDataUI(final MutableObjectData abilityData, final GameUI gameUI) {
		final String disabledPrefix = gameUI.getSkinField("CommandButtonDisabledArtPath");
		for (final War3ID alias : abilityData.keySet()) {
			final MutableGameObject abilityTypeData = abilityData.get(alias);
			final String iconResearchPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_RESEARCH, 0));
			final String iconNormalPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_NORMAL, 0));
			final String iconTurnOffPath = gameUI.trySkinField(abilityTypeData.getFieldAsString(ICON_TURN_OFF, 0));
			final int iconResearchX = abilityTypeData.getFieldAsInteger(ICON_RESEARCH_X, 0);
			final int iconResearchY = abilityTypeData.getFieldAsInteger(ICON_RESEARCH_Y, 0);
			final int iconNormalX = abilityTypeData.getFieldAsInteger(ICON_NORMAL_X, 0);
			final int iconNormalY = abilityTypeData.getFieldAsInteger(ICON_NORMAL_Y, 0);
			final int iconTurnOffX = abilityTypeData.getFieldAsInteger(ICON_TURN_OFF_X, 0);
			final int iconTurnOffY = abilityTypeData.getFieldAsInteger(ICON_TURN_OFF_Y, 0);
			final Texture iconResearch = gameUI.loadTexture(iconResearchPath);
			final Texture iconResearchDisabled = gameUI.loadTexture(disable(iconResearchPath, disabledPrefix));
			final Texture iconNormal = gameUI.loadTexture(iconNormalPath);
			final Texture iconNormalDisabled = gameUI.loadTexture(disable(iconNormalPath, disabledPrefix));
			final Texture iconTurnOff = gameUI.loadTexture(iconTurnOffPath);
			final Texture iconTurnOffDisabled = gameUI.loadTexture(disable(iconTurnOffPath, disabledPrefix));
			this.rawcodeToUI.put(alias,
					new AbilityIconUI(new IconUI(iconResearch, iconResearchDisabled, iconResearchX, iconResearchY),
							new IconUI(iconNormal, iconNormalDisabled, iconNormalX, iconNormalY),
							new IconUI(iconTurnOff, iconTurnOffDisabled, iconTurnOffX, iconTurnOffY)));
		}
		this.moveUI = createBuiltInIconUI(gameUI, "CmdMove", disabledPrefix);
		this.stopUI = createBuiltInIconUI(gameUI, "CmdStop", disabledPrefix);
		this.holdPosUI = createBuiltInIconUI(gameUI, "CmdHoldPos", disabledPrefix);
		this.patrolUI = createBuiltInIconUI(gameUI, "CmdPatrol", disabledPrefix);
		this.attackUI = createBuiltInIconUI(gameUI, "CmdAttack", disabledPrefix);
		this.attackGroundUI = createBuiltInIconUI(gameUI, "CmdAttackGround", disabledPrefix);
	}

	private IconUI createBuiltInIconUI(final GameUI gameUI, final String key, final String disabledPrefix) {
		final Element builtInAbility = gameUI.getSkinData().get(key);
		final String iconPath = gameUI.trySkinField(builtInAbility.getField("Art"));
		final Texture icon = gameUI.loadTexture(iconPath);
		final Texture iconDisabled = gameUI.loadTexture(disable(iconPath, disabledPrefix));
		final int buttonPositionX = builtInAbility.getFieldValue("Buttonpos", 0);
		final int buttonPositionY = builtInAbility.getFieldValue("Buttonpos", 1);
		return new IconUI(icon, iconDisabled, buttonPositionX, buttonPositionY);
	}

	public AbilityIconUI getUI(final War3ID rawcode) {
		return this.rawcodeToUI.get(rawcode);
	}

	private static String disable(final String path, final String disabledPrefix) {
		final int slashIndex = path.lastIndexOf('\\');
		String name = path;
		if (slashIndex != -1) {
			name = path.substring(slashIndex + 1);
		}
		return disabledPrefix + name;
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

}
