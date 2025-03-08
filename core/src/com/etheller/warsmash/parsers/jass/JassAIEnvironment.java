package com.etheller.warsmash.parsers.jass;

import java.util.Collections;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.visitor.CodeJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.RealJassValueVisitor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.HandleIdAllocator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;

public class JassAIEnvironment {
	private final GameUI gameUI;
	private Element skin;
	private final JassProgram jassProgramVisitor;

	private JassAIEnvironment(final JassProgram jassProgramVisitor, final DataSource dataSource,
			final Viewport uiViewport, final Scene uiScene, final GameUI gameUI, final War3MapConfig mapConfig,
			final CSimulation simulation) {
		this.jassProgramVisitor = jassProgramVisitor;
		this.gameUI = gameUI;
		final Rectangle tempRect = new Rectangle();
		final GlobalScope globals = jassProgramVisitor.getGlobals();
		final HandleJassType agentType = globals.registerHandleType("agent");
		final HandleJassType eventType = globals.registerHandleType("event");
		final HandleJassType playerType = globals.registerHandleType("player");
		final HandleJassType widgetType = globals.registerHandleType("widget");
		final HandleJassType unitType = globals.registerHandleType("unit");
		final HandleJassType destructableType = globals.registerHandleType("destructable");
		final HandleJassType itemType = globals.registerHandleType("item");
		final HandleJassType abilityType = globals.registerHandleType("ability");
		final HandleJassType buffType = globals.registerHandleType("buff");
		final HandleJassType forceType = globals.registerHandleType("force");
		final HandleJassType groupType = globals.registerHandleType("group");
		final HandleJassType triggerType = globals.registerHandleType("trigger");
		final HandleJassType triggerconditionType = globals.registerHandleType("triggercondition");
		final HandleJassType triggeractionType = globals.registerHandleType("triggeraction");
		final HandleJassType timerType = globals.registerHandleType("timer");
		final HandleJassType locationType = globals.registerHandleType("location");
		final HandleJassType regionType = globals.registerHandleType("region");
		final HandleJassType rectType = globals.registerHandleType("rect");
		final HandleJassType boolexprType = globals.registerHandleType("boolexpr");
		final HandleJassType soundType = globals.registerHandleType("sound");
		final HandleJassType conditionfuncType = globals.registerHandleType("conditionfunc");
		final HandleJassType filterfuncType = globals.registerHandleType("filterfunc");
		final HandleJassType unitpoolType = globals.registerHandleType("unitpool");
		final HandleJassType itempoolType = globals.registerHandleType("itempool");
		final HandleJassType raceType = globals.registerHandleType("race");
		final HandleJassType alliancetypeType = globals.registerHandleType("alliancetype");
		final HandleJassType racepreferenceType = globals.registerHandleType("racepreference");
		final HandleJassType gamestateType = globals.registerHandleType("gamestate");
		final HandleJassType igamestateType = globals.registerHandleType("igamestate");
		final HandleJassType fgamestateType = globals.registerHandleType("fgamestate");
		final HandleJassType playerstateType = globals.registerHandleType("playerstate");
		final HandleJassType playerscoreType = globals.registerHandleType("playerscore");
		final HandleJassType playergameresultType = globals.registerHandleType("playergameresult");
		final HandleJassType unitstateType = globals.registerHandleType("unitstate");
		final HandleJassType aidifficultyType = globals.registerHandleType("aidifficulty");
		final HandleJassType eventidType = globals.registerHandleType("eventid");
		final HandleJassType gameeventType = globals.registerHandleType("gameevent");
		final HandleJassType playereventType = globals.registerHandleType("playerevent");
		final HandleJassType playeruniteventType = globals.registerHandleType("playerunitevent");
		final HandleJassType uniteventType = globals.registerHandleType("unitevent");
		final HandleJassType limitopType = globals.registerHandleType("limitop");
		final HandleJassType widgeteventType = globals.registerHandleType("widgetevent");
		final HandleJassType dialogeventType = globals.registerHandleType("dialogevent");
		final HandleJassType unittypeType = globals.registerHandleType("unittype");
		final HandleJassType gamespeedType = globals.registerHandleType("gamespeed");
		final HandleJassType gamedifficultyType = globals.registerHandleType("gamedifficulty");
		final HandleJassType gametypeType = globals.registerHandleType("gametype");
		final HandleJassType mapflagType = globals.registerHandleType("mapflag");
		final HandleJassType mapvisibilityType = globals.registerHandleType("mapvisibility");
		final HandleJassType mapsettingType = globals.registerHandleType("mapsetting");
		final HandleJassType mapdensityType = globals.registerHandleType("mapdensity");
		final HandleJassType mapcontrolType = globals.registerHandleType("mapcontrol");
		final HandleJassType playerslotstateType = globals.registerHandleType("playerslotstate");
		final HandleJassType volumegroupType = globals.registerHandleType("volumegroup");
		final HandleJassType camerafieldType = globals.registerHandleType("camerafield");
		final HandleJassType camerasetupType = globals.registerHandleType("camerasetup");
		final HandleJassType playercolorType = globals.registerHandleType("playercolor");
		final HandleJassType placementType = globals.registerHandleType("placement");
		final HandleJassType startlocprioType = globals.registerHandleType("startlocprio");
		final HandleJassType raritycontrolType = globals.registerHandleType("raritycontrol");
		final HandleJassType blendmodeType = globals.registerHandleType("blendmode");
		final HandleJassType texmapflagsType = globals.registerHandleType("texmapflags");
		final HandleJassType effectType = globals.registerHandleType("effect");
		final HandleJassType effecttypeType = globals.registerHandleType("effecttype");
		final HandleJassType weathereffectType = globals.registerHandleType("weathereffect");
		final HandleJassType terraindeformationType = globals.registerHandleType("terraindeformation");
		final HandleJassType fogstateType = globals.registerHandleType("fogstate");
		final HandleJassType fogmodifierType = globals.registerHandleType("fogmodifier");
		final HandleJassType dialogType = globals.registerHandleType("dialog");
		final HandleJassType buttonType = globals.registerHandleType("button");
		final HandleJassType questType = globals.registerHandleType("quest");
		final HandleJassType questitemType = globals.registerHandleType("questitem");
		final HandleJassType defeatconditionType = globals.registerHandleType("defeatcondition");
		final HandleJassType timerdialogType = globals.registerHandleType("timerdialog");
		final HandleJassType leaderboardType = globals.registerHandleType("leaderboard");
		final HandleJassType multiboardType = globals.registerHandleType("multiboard");
		final HandleJassType multiboarditemType = globals.registerHandleType("multiboarditem");
		final HandleJassType trackableType = globals.registerHandleType("trackable");
		final HandleJassType gamecacheType = globals.registerHandleType("gamecache");
		final HandleJassType versionType = globals.registerHandleType("version");
		final HandleJassType itemtypeType = globals.registerHandleType("itemtype");
		final HandleJassType texttagType = globals.registerHandleType("texttag");
		final HandleJassType attacktypeType = globals.registerHandleType("attacktype");
		final HandleJassType damagetypeType = globals.registerHandleType("damagetype");
		final HandleJassType weapontypeType = globals.registerHandleType("weapontype");
		final HandleJassType soundtypeType = globals.registerHandleType("soundtype");
		final HandleJassType lightningType = globals.registerHandleType("lightning");
		final HandleJassType pathingtypeType = globals.registerHandleType("pathingtype");
		final HandleJassType imageType = globals.registerHandleType("image");
		final HandleJassType ubersplatType = globals.registerHandleType("ubersplat");
		final HandleJassType hashtableType = globals.registerHandleType("hashtable");
		final HandleJassType frameHandleType = globals.registerHandleType("framehandle");

		// Warsmash Ability API
		final HandleJassType abilitytypeType = globals.registerHandleType("abilitytype");
		final HandleJassType ordercommandcardType = globals.registerHandleType("ordercommandcard");
		final HandleJassType ordercommandcardtypeType = globals.registerHandleType("ordercommandcardtype");
		final HandleJassType abilitybehaviorType = globals.registerHandleType("abilitybehavior");
		final HandleJassType behaviorexprType = globals.registerHandleType("behaviorexpr");
		final HandleJassType iconuiType = globals.registerHandleType("iconui");

		Jass2.registerTypingNatives(jassProgramVisitor, raceType, alliancetypeType, racepreferenceType, igamestateType,
				fgamestateType, playerstateType, playerscoreType, playergameresultType, unitstateType, aidifficultyType,
				gameeventType, playereventType, playeruniteventType, uniteventType, limitopType, widgeteventType,
				dialogeventType, unittypeType, gamespeedType, gamedifficultyType, gametypeType, mapflagType,
				mapvisibilityType, mapsettingType, mapdensityType, mapcontrolType, playerslotstateType, volumegroupType,
				camerafieldType, playercolorType, placementType, startlocprioType, raritycontrolType, blendmodeType,
				texmapflagsType, effecttypeType, fogstateType, versionType, itemtypeType, attacktypeType,
				damagetypeType, weapontypeType, soundtypeType, pathingtypeType);
		Jass2.registerConversionAndStringNatives(jassProgramVisitor, gameUI);
		Jass2.registerConfigNatives(jassProgramVisitor, mapConfig, startlocprioType, gametypeType, placementType,
				gamespeedType, gamedifficultyType, mapdensityType, locationType, playerType, playercolorType,
				mapcontrolType, playerslotstateType, mapConfig, new HandleIdAllocator());
		Jass2.registerRandomNatives(jassProgramVisitor, simulation);

		jassProgramVisitor.getJassNativeManager().createNative("StartThread",
				(arguments, globalScope, triggerScope) -> {
					final CodeJassValue threadFunc = arguments.get(0).visit(CodeJassValueVisitor.getInstance());
					return null;
				});
		jassProgramVisitor.getJassNativeManager().createNative("Sleep", (arguments, globalScope, triggerScope) -> {
			final float lowBound = arguments.get(0).visit(RealJassValueVisitor.getInstance()).floatValue();
			final float highBound = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
			return null;
		});
	}

	public void main() {
		try {
			final JassThread mainThread = this.jassProgramVisitor.getGlobals().createThread("main",
					Collections.emptyList(), TriggerExecutionScope.EMPTY);
			this.jassProgramVisitor.getGlobals().queueThread(mainThread);
		}
		catch (final Exception exc) {
			throw new JassException(this.jassProgramVisitor.getGlobals(),
					"Exception on Line " + this.jassProgramVisitor.getGlobals().getLineNumber(), exc);
		}
	}

}
