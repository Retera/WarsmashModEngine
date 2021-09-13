package com.etheller.warsmash.parsers.jass;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.interpreter.JassLexer;
import com.etheller.interpreter.JassParser;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.scope.variableevent.CLimitOp;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.JassFunctionJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.RealJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StringJassValueVisitor;
import com.etheller.interpreter.ast.visitors.JassProgramVisitor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameSkin;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprAnd;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprCondition;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprFilter;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprNot;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprOr;
import com.etheller.warsmash.parsers.jass.triggers.TriggerAction;
import com.etheller.warsmash.parsers.jass.triggers.TriggerCondition;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.ItemUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.ai.AIDifficulty;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CPlayerAPI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.item.CItemTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapFlag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapPlacement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerColor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerGameResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerScore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CStartLocPrio;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegion;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegionTriggerEnter;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.sound.CMIDISound;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.sound.CSound;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.sound.CSoundFilename;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.sound.CSoundFromLabel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.state.CGameState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.state.CUnitState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimerNativeEvent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CAttackTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CBlendMode;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CCameraField;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CGameSpeed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CGameType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CMapDensity;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CMapDifficulty;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPathingTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CRarityControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CSoundType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CSoundVolumeGroup;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CTexMapFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CVersion;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.MeleeUI;

public class Jass2 {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	public static CommonEnvironment loadCommon(final DataSource dataSource, final Viewport uiViewport,
			final Scene uiScene, final War3MapViewer war3MapViewer, final MeleeUI meleeUI, final String... files) {

		final JassProgramVisitor jassProgramVisitor = new JassProgramVisitor();
		final CommonEnvironment environment = new CommonEnvironment(jassProgramVisitor, dataSource, uiViewport, uiScene,
				war3MapViewer, meleeUI);
		for (final String jassFile : files) {
			try {
				JassLexer lexer;
				try {
					lexer = new JassLexer(CharStreams.fromStream(dataSource.getResourceAsStream(jassFile)));
				}
				catch (final IOException e) {
					throw new RuntimeException(e);
				}
				final JassParser parser = new JassParser(new CommonTokenStream(lexer));
//				parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
				parser.addErrorListener(new BaseErrorListener() {
					@Override
					public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
							final int line, final int charPositionInLine, final String msg,
							final RecognitionException e) {
						if (!REPORT_SYNTAX_ERRORS) {
							return;
						}

						final String sourceName = String.format("%s:%d:%d: ", jassFile, line, charPositionInLine);

						System.err.println(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
						throw new IllegalStateException(
								sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
					}
				});
				jassProgramVisitor.visit(parser.program());
			}
			catch (final Exception e) {
				e.printStackTrace();
			}
		}
		jassProgramVisitor.getJassNativeManager().checkUnregisteredNatives();
		return environment;
	}

	public static JUIEnvironment loadJUI(final DataSource dataSource, final Viewport uiViewport, final Scene uiScene,
			final War3MapViewer war3MapViewer, final RootFrameListener rootFrameListener, final String... files) {

		final JassProgramVisitor jassProgramVisitor = new JassProgramVisitor();
		final JUIEnvironment environment = new JUIEnvironment(jassProgramVisitor, dataSource, uiViewport, uiScene,
				war3MapViewer, rootFrameListener);
		for (final String jassFile : files) {
			try {
				JassLexer lexer;
				try {
					lexer = new JassLexer(CharStreams.fromStream(dataSource.getResourceAsStream(jassFile)));
				}
				catch (final IOException e) {
					throw new RuntimeException(e);
				}
				final JassParser parser = new JassParser(new CommonTokenStream(lexer));
//				parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
				parser.addErrorListener(new BaseErrorListener() {
					@Override
					public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
							final int line, final int charPositionInLine, final String msg,
							final RecognitionException e) {
						if (!REPORT_SYNTAX_ERRORS) {
							return;
						}

						final String sourceName = String.format("%s:%d:%d: ", jassFile, line, charPositionInLine);

						System.err.println(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
					}
				});
				jassProgramVisitor.visit(parser.program());
			}
			catch (final Exception e) {
				e.printStackTrace();
			}
		}
		jassProgramVisitor.getJassNativeManager().checkUnregisteredNatives();
		return environment;
	}

	public static interface RootFrameListener {
		void onCreate(GameUI rootFrame);
	}

	private static final class JUIEnvironment {
		private GameUI gameUI;
		private Element skin;

		public JUIEnvironment(final JassProgramVisitor jassProgramVisitor, final DataSource dataSource,
				final Viewport uiViewport, final Scene uiScene, final War3MapViewer war3MapViewer,
				final RootFrameListener rootFrameListener) {
			final GlobalScope globals = jassProgramVisitor.getGlobals();
			final HandleJassType frameHandleType = globals.registerHandleType("framehandle");
			final HandleJassType framePointType = globals.registerHandleType("framepointtype");
			final HandleJassType triggerType = globals.registerHandleType("trigger");
			final HandleJassType triggerActionType = globals.registerHandleType("triggeraction");
			final HandleJassType triggerConditionType = globals.registerHandleType("triggercondition");
			final HandleJassType boolExprType = globals.registerHandleType("boolexpr");
			final HandleJassType conditionFuncType = globals.registerHandleType("conditionfunc");
			final HandleJassType filterType = globals.registerHandleType("filterfunc");
			jassProgramVisitor.getJassNativeManager().createNative("LogError", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String stringValue = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					System.err.println(stringValue);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertFramePointType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int value = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(framePointType, FramePoint.values()[value]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateRootFrame", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String skinArg = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final GameSkin skin = GameUI.loadSkin(dataSource, skinArg);
					final GameUI gameUI = new GameUI(dataSource, skin, uiViewport, uiScene, war3MapViewer, 0,
							war3MapViewer.getAllObjectData().getWts());
					JUIEnvironment.this.gameUI = gameUI;
					JUIEnvironment.this.skin = skin.getSkin();
					rootFrameListener.onCreate(gameUI);
					return new HandleJassValue(frameHandleType, gameUI);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("LoadTOCFile", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String tocFileName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					try {
						JUIEnvironment.this.gameUI.loadTOCFile(tocFileName);
					}
					catch (final IOException e) {
						throw new RuntimeException(e);
					}
					return BooleanJassValue.TRUE;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateSimpleFrame", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String templateName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final UIFrame ownerFrame = arguments.get(1).visit(ObjectJassValueVisitor.<UIFrame>getInstance());
					final int createContext = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());

					final UIFrame simpleFrame = JUIEnvironment.this.gameUI.createSimpleFrame(templateName, ownerFrame,
							createContext);

					return new HandleJassValue(frameHandleType, simpleFrame);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateFrame", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String templateName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final UIFrame ownerFrame = arguments.get(1).visit(ObjectJassValueVisitor.<UIFrame>getInstance());
					final int priority = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());
					final int createContext = arguments.get(3).visit(IntegerJassValueVisitor.getInstance());

					final UIFrame simpleFrame = JUIEnvironment.this.gameUI.createFrame(templateName, ownerFrame,
							priority, createContext);

					return new HandleJassValue(frameHandleType, simpleFrame);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetFrameByName", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String templateName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final int createContext = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());

					final UIFrame simpleFrame = JUIEnvironment.this.gameUI.getFrameByName(templateName, createContext);
					return new HandleJassValue(frameHandleType, simpleFrame);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("FrameSetAnchor", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final UIFrame frame = arguments.get(0).visit(ObjectJassValueVisitor.<UIFrame>getInstance());
					final FramePoint framePoint = arguments.get(1)
							.visit(ObjectJassValueVisitor.<FramePoint>getInstance());
					final double x = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					final double y = arguments.get(3).visit(RealJassValueVisitor.getInstance());

					frame.addAnchor(new AnchorDefinition(framePoint, GameUI.convertX(uiViewport, (float) x),
							GameUI.convertY(uiViewport, (float) y)));
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("FrameSetAbsPoint", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final UIFrame frame = arguments.get(0).visit(ObjectJassValueVisitor.<UIFrame>getInstance());
					final FramePoint framePoint = arguments.get(1)
							.visit(ObjectJassValueVisitor.<FramePoint>getInstance());
					final double x = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					final double y = arguments.get(3).visit(RealJassValueVisitor.getInstance());

					frame.setFramePointX(framePoint, GameUI.convertX(uiViewport, (float) x));
					frame.setFramePointY(framePoint, GameUI.convertY(uiViewport, (float) y));
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("FrameSetPoint", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final UIFrame frame = arguments.get(0).visit(ObjectJassValueVisitor.<UIFrame>getInstance());
					final FramePoint framePoint = arguments.get(1)
							.visit(ObjectJassValueVisitor.<FramePoint>getInstance());
					final UIFrame otherFrame = arguments.get(2).visit(ObjectJassValueVisitor.<UIFrame>getInstance());
					final FramePoint otherPoint = arguments.get(3)
							.visit(ObjectJassValueVisitor.<FramePoint>getInstance());
					final double x = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					final double y = arguments.get(3).visit(RealJassValueVisitor.getInstance());

					frame.addSetPoint(new SetPoint(framePoint, otherFrame, otherPoint,
							GameUI.convertX(uiViewport, (float) x), GameUI.convertY(uiViewport, (float) y)));
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("FrameSetText", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final StringFrame frame = arguments.get(0).visit(ObjectJassValueVisitor.<StringFrame>getInstance());
					final String text = arguments.get(1).visit(StringJassValueVisitor.getInstance());

					JUIEnvironment.this.gameUI.setText(frame, text);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("FrameSetTextColor", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final StringFrame frame = arguments.get(0).visit(ObjectJassValueVisitor.<StringFrame>getInstance());
					final int colorInt = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					frame.setColor(new Color(colorInt));
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertColor", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int a = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final int r = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final int g = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());
					final int b = arguments.get(3).visit(IntegerJassValueVisitor.getInstance());
					return new IntegerJassValue(a | (b << 8) | (g << 16) | (r << 24));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("FramePositionBounds", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final UIFrame frame = arguments.get(0).visit(ObjectJassValueVisitor.<UIFrame>getInstance());
					frame.positionBounds(JUIEnvironment.this.gameUI, uiViewport);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SkinGetField", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String fieldName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					return new StringJassValue(JUIEnvironment.this.skin.getField(fieldName));
				}
			});
			setupTriggerAPI(jassProgramVisitor, triggerType, triggerActionType, triggerConditionType, boolExprType,
					conditionFuncType, filterType);
		}
	}

	public static final class CommonEnvironment {
		private GameUI gameUI;
		private Element skin;
		private final JassProgramVisitor jassProgramVisitor;

		private CommonEnvironment(final JassProgramVisitor jassProgramVisitor, final DataSource dataSource,
				final Viewport uiViewport, final Scene uiScene, final War3MapViewer war3MapViewer,
				final MeleeUI meleeUI) {
			this.jassProgramVisitor = jassProgramVisitor;
			this.gameUI = war3MapViewer.getGameUI();
			final Rectangle tempRect = new Rectangle();
			final CSimulation simulation = war3MapViewer.simulation;
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

			jassProgramVisitor.getJassNativeManager().createNative("ConvertRace", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(raceType, CRace.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertAllianceType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(alliancetypeType, CAllianceType.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertRacePref", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(racepreferenceType, CRacePreference.getById(i));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertIGameState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(igamestateType, CGameState.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertFGameState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(fgamestateType, CGameState.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertPlayerState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(playerstateType, CPlayerState.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertPlayerScore", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(playerscoreType, CPlayerScore.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertPlayerGameResult", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(playergameresultType, CPlayerGameResult.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertUnitState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(unitstateType, CUnitState.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertAIDifficulty", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(aidifficultyType, AIDifficulty.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertGameEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(gameeventType, JassGameEventsWar3.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertPlayerEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(playereventType, JassGameEventsWar3.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertPlayerUnitEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(playeruniteventType, JassGameEventsWar3.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertWidgetEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(widgeteventType, JassGameEventsWar3.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertDialogEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(dialogeventType, JassGameEventsWar3.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertUnitEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(uniteventType, JassGameEventsWar3.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertLimitOp", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(limitopType, CLimitOp.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertUnitType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(unittypeType, CUnitTypeJass.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertGameSpeed", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(gamespeedType, CGameSpeed.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertPlacement", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(placementType, CMapPlacement.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertStartLocPrio", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(startlocprioType, CStartLocPrio.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertGameDifficulty", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(gamedifficultyType, CMapDifficulty.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertGameType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(gametypeType, CGameType.getById(i));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertMapFlag", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(mapflagType, CMapFlag.getById(i));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertMapVisibility", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(mapvisibilityType, null);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertMapSetting", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(mapsettingType, null);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertMapDensity", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(mapdensityType, CMapDensity.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertMapControl", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(mapcontrolType, CMapControl.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertPlayerColor", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(playercolorType, CPlayerColor.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertPlayerSlotState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(playerslotstateType, CPlayerSlotState.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertVolumeGroup", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(volumegroupType, CSoundVolumeGroup.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertCameraField", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(camerafieldType, CCameraField.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertBlendMode", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(blendmodeType, CBlendMode.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertRarityControl", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(raritycontrolType, CRarityControl.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertTexMapFlags", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(texmapflagsType, CTexMapFlags.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertFogState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(fogstateType, CFogState.getById(i));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertEffectType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(effecttypeType, CEffectType.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertVersion", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(versionType, CVersion.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertItemType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(itemtypeType, CItemTypeJass.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertAttackType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(attacktypeType, CAttackTypeJass.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertDamageType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(damagetypeType, CDamageType.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertWeaponType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(weapontypeType, CWeaponSoundTypeJass.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertSoundType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(soundtypeType, CSoundType.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertPathingType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(pathingtypeType, CPathingTypeJass.VALUES[i]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("OrderId", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String idString = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final int orderId = OrderIdUtils.getOrderId(idString);
					return new IntegerJassValue(orderId);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("OrderId2String", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer id = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new StringJassValue(OrderIdUtils.getStringFromOrderId(id));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("UnitId", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String idString = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final CUnitType unitType = simulation.getUnitData().getUnitTypeByJassLegacyName(idString);
					if (unitType == null) {
						return new IntegerJassValue(0);
					}
					return new IntegerJassValue(unitType.getTypeId().getValue());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("UnitId2String", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer id = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final War3ID war3id = new War3ID(id);
					return new StringJassValue(simulation.getUnitData().getUnitType(war3id).getLegacyName());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("AbilityId", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new IntegerJassValue(0);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("AbilityId2String", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new StringJassValue("");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetObjectName", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer id = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final War3ID war3id = new War3ID(id);
					final CUnitType unitType = simulation.getUnitData().getUnitType(war3id);
					if (unitType != null) {
						return new StringJassValue(unitType.getName());
					}
					// TODO for now this looks in the ability editor data, not the fast symbol table
					// layer on top, because the layer on top forgot to have a name value...
					final MutableGameObject abilityEditorData = war3MapViewer.getAllObjectData().getAbilities()
							.get(war3id);
					if (abilityEditorData != null) {
						return new StringJassValue(abilityEditorData.getName());
					}
					final ItemUI itemUI = war3MapViewer.getAbilityDataUI().getItemUI(war3id);
					if (itemUI != null) {
						return new StringJassValue(itemUI.getName());
					}
					final CDestructableType destructableType = simulation.getDestructableData().getUnitType(war3id);
					if (destructableType != null) {
						return new StringJassValue(destructableType.getName());
					}
					return new StringJassValue("");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Deg2Rad", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double value = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					return new RealJassValue(StrictMath.toRadians(value));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Rad2Deg", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double value = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					return new RealJassValue(StrictMath.toDegrees(value));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Sin", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double value = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					return new RealJassValue(StrictMath.sin(value));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Cos", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double value = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					return new RealJassValue(StrictMath.cos(value));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Tan", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double value = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					return new RealJassValue(StrictMath.tan(value));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Asin", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double value = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					final double result = StrictMath.asin(value);
					if (Double.isNaN(result)) {
						return new RealJassValue(0);
					}
					return new RealJassValue(result);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Acos", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double value = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					final double result = StrictMath.acos(value);
					if (Double.isNaN(result)) {
						return new RealJassValue(0);
					}
					return new RealJassValue(result);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Atan", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double value = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					final double result = StrictMath.atan(value);
					if (Double.isNaN(result)) {
						return new RealJassValue(0);
					}
					return new RealJassValue(result);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Atan2", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double y = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					final Double x = arguments.get(1).visit(RealJassValueVisitor.getInstance());
					final double result = StrictMath.atan2(y, x);
					if (Double.isNaN(result)) {
						return new RealJassValue(0);
					}
					return new RealJassValue(result);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SquareRoot", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double value = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					final double result = StrictMath.sqrt(value);
					return new RealJassValue(result);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Pow", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double y = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					final Double x = arguments.get(1).visit(RealJassValueVisitor.getInstance());
					final double result = StrictMath.pow(y, x);
					if (Double.isNaN(result)) {
						return new RealJassValue(0);
					}
					return new RealJassValue(result);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("I2R", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new RealJassValue(i.doubleValue());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("R2I", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double r = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					return new IntegerJassValue(r.intValue());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("I2S", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer i = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new StringJassValue(i.toString());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("R2S", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double r = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					return new StringJassValue(r.toString());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("R2SW", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Double r = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					final int width = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final int precision = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());
					return new StringJassValue(String.format("%" + precision + "." + width + "f", r));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("S2I", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String s = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					try {
						final int intValue = Integer.parseInt(s);
						return new IntegerJassValue(intValue);
					}
					catch (final Exception exc) {
						return new IntegerJassValue(0);
					}
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("S2R", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String s = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					try {
						final double parsedValue = Double.parseDouble(s);
						return new RealJassValue(parsedValue);
					}
					catch (final Exception exc) {
						return new RealJassValue(0);
					}
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SubString", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String s = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final int start = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final int end = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());
					return new StringJassValue(s.substring(start, end));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("StringLength", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String s = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					return new IntegerJassValue(s.length());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("StringCase", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String s = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final boolean upper = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					return new StringJassValue(upper ? s.toUpperCase(Locale.US) : s.toLowerCase(Locale.US));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("StringHash", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String s = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					return new IntegerJassValue(s.hashCode());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetLocalizedString", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String key = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					// TODO this might be wrong, or a subset of the needed return values
					final String decoratedString = war3MapViewer.getGameUI().getTemplates().getDecoratedString(key);
					if (key.equals(decoratedString)) {
						System.err.println("GetLocalizedString: NOT FOUND: " + key);
					}
					return new StringJassValue(decoratedString);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetLocalizedHotkey", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String key = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					// TODO this might be wrong, or a subset of the needed return values
					final String decoratedString = war3MapViewer.getGameUI().getTemplates().getDecoratedString(key);
					if (key.equals(decoratedString)) {
						System.err.println("GetLocalizedHotkey: NOT FOUND: " + key);
					}
					return new IntegerJassValue(decoratedString.charAt(0));
				}
			});
			final War3MapConfig mapConfig = war3MapViewer.getMapConfig();
			registerConfigNatives(jassProgramVisitor, mapConfig, startlocprioType, gametypeType, placementType,
					gamespeedType, gamedifficultyType, mapdensityType, locationType, playerType, playercolorType,
					mapcontrolType, playerslotstateType, simulation);

			// ============================================================================
			// Timer API
			//
			jassProgramVisitor.getJassNativeManager().createNative("CreateTimer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(timerType, new CTimerJass(globalScope));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DestroyTimer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CTimerJass timer = arguments.get(0).visit(ObjectJassValueVisitor.<CTimerJass>getInstance());
					simulation.unregisterTimer(timer);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TimerStart", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CTimerJass timer = arguments.get(0).visit(ObjectJassValueVisitor.<CTimerJass>getInstance());
					final Double timeout = arguments.get(1).visit(RealJassValueVisitor.getInstance());
					final boolean periodic = arguments.get(2).visit(BooleanJassValueVisitor.getInstance());
					final JassFunction handlerFunc = arguments.get(3).visit(JassFunctionJassValueVisitor.getInstance());
					if (!timer.isRunning()) {
						timer.setTimeoutTime(timeout.floatValue());
						timer.setRepeats(periodic);
						timer.setHandlerFunc(handlerFunc);
						timer.start(simulation);
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TimerGetElapsed", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CTimerJass timer = arguments.get(0).visit(ObjectJassValueVisitor.<CTimerJass>getInstance());
					return new RealJassValue(timer.getElapsed(simulation));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TimerGetRemaining", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CTimerJass timer = arguments.get(0).visit(ObjectJassValueVisitor.<CTimerJass>getInstance());
					return new RealJassValue(timer.getRemaining(simulation));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TimerGetTimeout", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CTimerJass timer = arguments.get(0).visit(ObjectJassValueVisitor.<CTimerJass>getInstance());
					return new RealJassValue(timer.getTimeoutTime());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("PauseTimer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CTimerJass timer = arguments.get(0).visit(ObjectJassValueVisitor.<CTimerJass>getInstance());
					timer.pause(simulation);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ResumeTimer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CTimerJass timer = arguments.get(0).visit(ObjectJassValueVisitor.<CTimerJass>getInstance());
					timer.resume(simulation);
					return null;
				}
			});

			// ============================================================================
			// Group API
			//
			jassProgramVisitor.getJassNativeManager().createNative("CreateGroup", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(groupType, new ArrayList<CUnit>());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DestroyGroup", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					System.err.println(
							"DestroyGroup called but in Java we don't have a destructor, so we need to unregister later when that is implemented");
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupAddUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final CUnit whichUnit = arguments.get(1).visit(ObjectJassValueVisitor.<CUnit>getInstance());
					group.add(whichUnit);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupRemoveUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final CUnit whichUnit = arguments.get(1).visit(ObjectJassValueVisitor.<CUnit>getInstance());
					group.remove(whichUnit);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupClear", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					group.clear();
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsOfType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final String unitname = arguments.get(1).visit(StringJassValueVisitor.getInstance());
					final TriggerBooleanExpression filter = arguments.get(2)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					for (final CUnit unit : simulation.getUnits()) {
						if (unitname.equals(unit.getUnitType().getLegacyName())) {
							if (filter.evaluate(globalScope,
									CommonTriggerExecutionScope.filterScope(triggerScope, unit))) {
								// TODO the trigger scope for evaluation here might need to be a clean one?
								group.add(unit);
							}
						}
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsOfPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final CPlayerJass player = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final TriggerBooleanExpression filter = arguments.get(2)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					for (final CUnit unit : simulation.getUnits()) {
						if (unit.getPlayerIndex() == player.getId()) {
							if (filter.evaluate(globalScope,
									CommonTriggerExecutionScope.filterScope(triggerScope, unit))) {
								// TODO the trigger scope for evaluation here might need to be a clean one?
								group.add(unit);
							}
						}
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsOfTypeCounted", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final String unitname = arguments.get(1).visit(StringJassValueVisitor.getInstance());
					final TriggerBooleanExpression filter = arguments.get(2)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					final Integer countLimit = arguments.get(3).visit(IntegerJassValueVisitor.getInstance());
					int count = 0;
					for (final CUnit unit : simulation.getUnits()) {
						if (unitname.equals(unit.getUnitType().getLegacyName())) {
							if (filter.evaluate(globalScope,
									CommonTriggerExecutionScope.filterScope(triggerScope, unit))) {
								// TODO the trigger scope for evaluation here might need to be a clean one?
								group.add(unit);
								count++;
								if (count >= countLimit) {
									break;
								}
							}
						}
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsInRect", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final Rectangle rect = arguments.get(1).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					final TriggerBooleanExpression filter = arguments.get(2)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					simulation.getWorldCollision().enumUnitsInRect(rect, new CUnitEnumFunction() {
						@Override
						public boolean call(final CUnit unit) {
							if ((filter == null) || filter.evaluate(globalScope,
									CommonTriggerExecutionScope.filterScope(triggerScope, unit))) {
								// TODO the trigger scope for evaluation here might need to be a clean one?
								group.add(unit);
							}
							return false;
						}
					});
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsInRectCounted", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final Rectangle rect = arguments.get(1).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					final TriggerBooleanExpression filter = arguments.get(2)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					final Integer countLimit = arguments.get(3).visit(IntegerJassValueVisitor.getInstance());
					simulation.getWorldCollision().enumUnitsInRect(rect, new CUnitEnumFunction() {
						int count = 0;

						@Override
						public boolean call(final CUnit unit) {
							if (filter.evaluate(globalScope,
									CommonTriggerExecutionScope.filterScope(triggerScope, unit))) {
								// TODO the trigger scope for evaluation here might need to be a clean one?
								group.add(unit);
								this.count++;
								if (this.count >= countLimit) {
									return true;
								}
							}
							return false;
						}
					});
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsInRange", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float radius = arguments.get(3).visit(RealJassValueVisitor.getInstance()).floatValue();
					final TriggerBooleanExpression filter = nullable(arguments, 4,
							ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					simulation.getWorldCollision().enumUnitsInRect(tempRect.set(x - radius, y - radius, radius, radius),
							new CUnitEnumFunction() {

								@Override
								public boolean call(final CUnit unit) {
									if (unit.distance(x, y) <= radius) {
										if ((filter == null) || filter.evaluate(globalScope,
												CommonTriggerExecutionScope.filterScope(triggerScope, unit))) {
											// TODO the trigger scope for evaluation here might need to be a clean one?
											group.add(unit);
										}
									}
									return false;
								}
							});
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsInRangeOfLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final Point2D.Double whichLocation = arguments.get(1)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					final float x = (float) whichLocation.x;
					final float y = (float) whichLocation.y;
					final float radius = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final TriggerBooleanExpression filter = nullable(arguments, 3,
							ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					simulation.getWorldCollision().enumUnitsInRect(tempRect.set(x - radius, y - radius, radius, radius),
							new CUnitEnumFunction() {

								@Override
								public boolean call(final CUnit unit) {
									if (unit.distance(x, y) <= radius) {
										if ((filter == null) || filter.evaluate(globalScope,
												CommonTriggerExecutionScope.filterScope(triggerScope, unit))) {
											// TODO the trigger scope for evaluation here might need to be a clean one?
											group.add(unit);
										}
									}
									return false;
								}
							});
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsInRangeCounted", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float radius = arguments.get(3).visit(RealJassValueVisitor.getInstance()).floatValue();
					final TriggerBooleanExpression filter = arguments.get(4)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					final Integer countLimit = arguments.get(5).visit(IntegerJassValueVisitor.getInstance());
					simulation.getWorldCollision().enumUnitsInRect(tempRect.set(x - radius, y - radius, radius, radius),
							new CUnitEnumFunction() {
								int count = 0;

								@Override
								public boolean call(final CUnit unit) {
									if (unit.distance(x, y) <= radius) {
										if (filter.evaluate(globalScope,
												CommonTriggerExecutionScope.filterScope(triggerScope, unit))) {
											// TODO the trigger scope for evaluation here might need to be a clean one?
											group.add(unit);
											this.count++;
											if (this.count >= countLimit) {
												return true;
											}
										}
									}
									return false;
								}
							});
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsInRangeOfLocCounted",
					new JassFunction() {
						@Override
						public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
								final TriggerExecutionScope triggerScope) {
							final List<CUnit> group = arguments.get(0)
									.visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
							final Point2D.Double whichLocation = arguments.get(1)
									.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
							final float x = (float) whichLocation.x;
							final float y = (float) whichLocation.y;
							final float radius = arguments.get(2).visit(RealJassValueVisitor.getInstance())
									.floatValue();
							final TriggerBooleanExpression filter = arguments.get(3)
									.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
							final Integer countLimit = arguments.get(4).visit(IntegerJassValueVisitor.getInstance());
							simulation.getWorldCollision().enumUnitsInRect(
									tempRect.set(x - radius, y - radius, radius, radius), new CUnitEnumFunction() {
										int count = 0;

										@Override
										public boolean call(final CUnit unit) {
											if (unit.distance(x, y) <= radius) {
												if (filter.evaluate(globalScope,
														CommonTriggerExecutionScope.filterScope(triggerScope, unit))) {
													// TODO the trigger scope for evaluation here might need to be a
													// clean one?
													group.add(unit);
													this.count++;
													if (this.count >= countLimit) {
														return true;
													}
												}
											}
											return false;
										}
									});
							return null;
						}
					});
			jassProgramVisitor.getJassNativeManager().createNative("GroupEnumUnitsSelected", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final CPlayerJass whyichPlayer = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final TriggerBooleanExpression filter = arguments.get(2)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					throw new UnsupportedOperationException("GroupEnumUnitsSelected not supported yet.");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupImmediateOrder", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final String order = arguments.get(1).visit(StringJassValueVisitor.getInstance());
					final int orderId = OrderIdUtils.getOrderId(order);
					boolean success = true;
					for (final CUnit unit : group) {
						success &= unit.order(simulation, orderId, null);
					}
					return BooleanJassValue.of(success);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupImmediateOrderById", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final int order = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					boolean success = true;
					for (final CUnit unit : group) {
						success &= unit.order(simulation, order, null);
					}
					return BooleanJassValue.of(success);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupPointOrder", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final String order = arguments.get(1).visit(StringJassValueVisitor.getInstance());
					final Double x = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					final Double y = arguments.get(3).visit(RealJassValueVisitor.getInstance());
					final AbilityPointTarget target = new AbilityPointTarget(x.floatValue(), y.floatValue());
					final int orderId = OrderIdUtils.getOrderId(order);
					boolean success = true;
					for (final CUnit unit : group) {
						success &= unit.order(simulation, orderId, target);
					}
					return BooleanJassValue.of(success);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupPointOrderLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final String order = arguments.get(1).visit(StringJassValueVisitor.getInstance());
					final Point2D.Double whichLocation = arguments.get(2)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					final AbilityPointTarget target = new AbilityPointTarget((float) whichLocation.x,
							(float) whichLocation.y);
					final int orderId = OrderIdUtils.getOrderId(order);
					boolean success = true;
					for (final CUnit unit : group) {
						success &= unit.order(simulation, orderId, target);
					}
					return BooleanJassValue.of(success);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupPointOrderById", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final int orderId = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final Double x = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					final Double y = arguments.get(3).visit(RealJassValueVisitor.getInstance());
					final AbilityPointTarget target = new AbilityPointTarget(x.floatValue(), y.floatValue());
					boolean success = true;
					for (final CUnit unit : group) {
						success &= unit.order(simulation, orderId, target);
					}
					return BooleanJassValue.of(success);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupPointOrderByIdLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final int orderId = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final Point2D.Double whichLocation = arguments.get(2)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					final AbilityPointTarget target = new AbilityPointTarget((float) whichLocation.x,
							(float) whichLocation.y);
					boolean success = true;
					for (final CUnit unit : group) {
						success &= unit.order(simulation, orderId, target);
					}
					return BooleanJassValue.of(success);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupTargetOrder", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final String order = arguments.get(1).visit(StringJassValueVisitor.getInstance());
					final CWidget target = arguments.get(2).visit(ObjectJassValueVisitor.<CWidget>getInstance());
					final int orderId = OrderIdUtils.getOrderId(order);
					boolean success = true;
					for (final CUnit unit : group) {
						success &= unit.order(simulation, orderId, target);
					}
					return BooleanJassValue.of(success);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GroupTargetOrderById", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final int orderId = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final CWidget target = arguments.get(2).visit(ObjectJassValueVisitor.<CWidget>getInstance());
					boolean success = true;
					for (final CUnit unit : group) {
						success &= unit.order(simulation, orderId, target);
					}
					return BooleanJassValue.of(success);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForGroup", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					final JassFunction callback = arguments.get(1).visit(JassFunctionJassValueVisitor.getInstance());
					for (final CUnit unit : group) {
						callback.call(Collections.<JassValue>emptyList(), globalScope,
								CommonTriggerExecutionScope.enumScope(triggerScope, unit));
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("FirstOfGroup", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CUnit> group = arguments.get(0).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					return new HandleJassValue(unitType, group.get(0));
				}
			});
			// ============================================================================
			// Force API
			//
			jassProgramVisitor.getJassNativeManager().createNative("CreateForce", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(forceType, new ArrayList<CPlayerJass>());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DestroyForce", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CPlayerJass> force = arguments.get(0)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					System.err.println(
							"DestroyForce called but in Java we don't have a destructor, so we need to unregister later when that is implemented");
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForceAddPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CPlayerJass> force = arguments.get(0)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					final CPlayerJass player = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					force.add(player);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForceRemovePlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CPlayerJass> force = arguments.get(0)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					final CPlayerJass player = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					force.remove(player);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForceClear", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CPlayerJass> force = arguments.get(0)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					force.clear();
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForceEnumPlayers", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CPlayerJass> force = arguments.get(0)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					final TriggerBooleanExpression filter = nullable(arguments, 1,
							ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
						final CPlayerJass jassPlayer = simulation.getPlayer(i);
						if ((filter == null) || filter.evaluate(globalScope,
								CommonTriggerExecutionScope.filterScope(triggerScope, jassPlayer))) {
							force.add(jassPlayer);
						}
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForceEnumPlayersCounted", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CPlayerJass> force = arguments.get(0)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					final TriggerBooleanExpression filter = arguments.get(1)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					final Integer countLimit = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());
					int count = 0;
					for (int i = 0; (i < WarsmashConstants.MAX_PLAYERS) && (count < countLimit); i++) {
						final CPlayerJass jassPlayer = simulation.getPlayer(i);
						if (filter.evaluate(globalScope,
								CommonTriggerExecutionScope.filterScope(triggerScope, jassPlayer))) {
							force.add(jassPlayer);
							count++;
						}
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForceEnumAllies", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CPlayerJass> force = arguments.get(0)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					final CPlayerJass player = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final TriggerBooleanExpression filter = arguments.get(2)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
						final CPlayerJass jassPlayer = simulation.getPlayer(i);
						if (player.hasAlliance(i, CAllianceType.PASSIVE)) {
							if (filter.evaluate(globalScope,
									CommonTriggerExecutionScope.filterScope(triggerScope, jassPlayer))) {
								force.add(jassPlayer);
							}
						}
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForceEnumEnemies", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CPlayerJass> force = arguments.get(0)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					final CPlayerJass player = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final TriggerBooleanExpression filter = arguments.get(2)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
						final CPlayerJass jassPlayer = simulation.getPlayer(i);
						if (!player.hasAlliance(i, CAllianceType.PASSIVE)) {
							if (filter.evaluate(globalScope,
									CommonTriggerExecutionScope.filterScope(triggerScope, jassPlayer))) {
								force.add(jassPlayer);
							}
						}
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForForce", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final List<CPlayerJass> force = arguments.get(0)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					final JassFunction callback = arguments.get(1).visit(JassFunctionJassValueVisitor.getInstance());
					for (final CPlayerJass player : force) {
						callback.call(Collections.<JassValue>emptyList(), globalScope,
								CommonTriggerExecutionScope.enumScope(triggerScope, player));
					}
					return null;
				}
			});
			// ============================================================================
			// Region and Location API
			//
			jassProgramVisitor.getJassNativeManager().createNative("Rect", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final float minx = arguments.get(0).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float miny = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float maxx = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float maxy = arguments.get(3).visit(RealJassValueVisitor.getInstance()).floatValue();
					return new HandleJassValue(rectType, new Rectangle(minx, miny, maxx - minx, maxy - miny));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RectFromLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Point2D.Double min = arguments.get(0)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					final Point2D.Double max = arguments.get(1)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					final float minx = (float) min.x;
					final float miny = (float) min.y;
					final float maxx = (float) max.x;
					final float maxy = (float) max.y;
					return new HandleJassValue(rectType, new Rectangle(minx, miny, maxx - minx, maxy - miny));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RemoveRect", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					System.err.println(
							"RemoveRect called but in Java we don't have a destructor, so we need to unregister later when that is implemented");
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetRect", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					final float minx = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float miny = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float maxx = arguments.get(3).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float maxy = arguments.get(4).visit(RealJassValueVisitor.getInstance()).floatValue();
					rect.set(minx, miny, maxx - minx, maxy - miny);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetRectFromLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					final Point2D.Double min = arguments.get(1)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					final Point2D.Double max = arguments.get(2)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					final float minx = (float) min.x;
					final float miny = (float) min.y;
					final float maxx = (float) max.x;
					final float maxy = (float) max.y;
					rect.set(minx, miny, maxx - minx, maxy - miny);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("MoveRectTo", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					final float newCenterX = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float newCenterY = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					rect.setCenter(newCenterX, newCenterY);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("MoveRectToLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					final Point2D.Double newCenterLoc = arguments.get(1)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					rect.setCenter((float) newCenterLoc.x, (float) newCenterLoc.y);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetRectCenterX", new JassFunction() {
				Vector2 centerHeap = new Vector2();

				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					return new RealJassValue(rect.getCenter(this.centerHeap).x);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetRectCenterY", new JassFunction() {
				Vector2 centerHeap = new Vector2();

				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					return new RealJassValue(rect.getCenter(this.centerHeap).y);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetRectMinX", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					return new RealJassValue(rect.getX());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetRectMinY", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					return new RealJassValue(rect.getY());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetRectMaxX", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					return new RealJassValue(rect.getX() + rect.getWidth());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetRectMaxY", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle rect = arguments.get(0).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					return new RealJassValue(rect.getY() + rect.getHeight());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateRegion", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(regionType, new CRegion());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RemoveRegion", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion region = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					region.remove(simulation.getRegionManager());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RegionAddRect", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion region = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final Rectangle rect = arguments.get(1).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					region.addRect(rect, simulation.getRegionManager());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RegionClearRect", new JassFunction() {

				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion region = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final Rectangle rect = arguments.get(1).visit(ObjectJassValueVisitor.<Rectangle>getInstance());
					region.clearRect(rect, simulation.getRegionManager());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RegionAddCell", new JassFunction() {

				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion region = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					region.addCell(x, y, simulation.getRegionManager());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RegionAddCellAtLoc", new JassFunction() {

				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion region = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final Point2D.Double whichLocation = arguments.get(1)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					region.addCell((float) whichLocation.x, (float) whichLocation.y, simulation.getRegionManager());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RegionClearCell", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion region = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					region.clearCell(x, y, simulation.getRegionManager());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RegionClearCellAtLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion region = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final Point2D.Double whichLocation = arguments.get(1)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					region.clearCell((float) whichLocation.x, (float) whichLocation.y, simulation.getRegionManager());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Location", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final float x = arguments.get(0).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					return new HandleJassValue(locationType, new Point2D.Double(x, y));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RemoveLocation", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Point2D.Double whichLocation = arguments.get(0)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					System.err.println(
							"RemoveRect called but in Java we don't have a destructor, so we need to unregister later when that is implemented");
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("MoveLocation", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Point2D.Double whichLocation = arguments.get(0)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					whichLocation.x = x;
					whichLocation.y = y;
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetLocationX", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Point2D.Double whichLocation = arguments.get(0)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					return new RealJassValue(whichLocation.x);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetLocationY", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Point2D.Double whichLocation = arguments.get(0)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					return new RealJassValue(whichLocation.y);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetLocationZ", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Point2D.Double whichLocation = arguments.get(0)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					return new RealJassValue(
							war3MapViewer.terrain.getGroundHeight((float) whichLocation.x, (float) whichLocation.y));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsUnitInRegion", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion whichRegion = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final CUnit whichUnit = arguments.get(1).visit(ObjectJassValueVisitor.<CUnit>getInstance());
					return BooleanJassValue.of(whichUnit.isInRegion(whichRegion));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsPointInRegion", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion whichRegion = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					return BooleanJassValue.of(whichRegion.contains(x, y, simulation.getRegionManager()));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsLocationInRegion", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CRegion whichRegion = arguments.get(0).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final Point2D.Double whichLocation = arguments.get(1)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					return BooleanJassValue.of(whichRegion.contains((float) whichLocation.x, (float) whichLocation.y,
							simulation.getRegionManager()));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetWorldBounds", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final float worldMinX = simulation.getPathingGrid().getWorldX(0) - 16f;
					final float worldMinY = simulation.getPathingGrid().getWorldY(0) - 16f;
					final float worldMaxX = simulation.getPathingGrid()
							.getWorldX(simulation.getPathingGrid().getWidth() - 1) + 16f;
					final float worldMaxY = simulation.getPathingGrid()
							.getWorldY(simulation.getPathingGrid().getHeight() - 1) + 16f;
					return new HandleJassValue(rectType,
							new Rectangle(worldMinX, worldMinY, worldMaxX - worldMinX, worldMaxY - worldMinY));
				}
			});
			// ============================================================================
			// Native trigger interface
			//
			setupTriggerAPI(jassProgramVisitor, triggerType, triggeractionType, triggerconditionType, boolexprType,
					conditionfuncType, filterfuncType);
			jassProgramVisitor.getJassNativeManager().createNative("GetFilterUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(unitType, ((CommonTriggerExecutionScope) triggerScope).getFilterUnit());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetEnumUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(unitType, ((CommonTriggerExecutionScope) triggerScope).getEnumUnit());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetFilterDestructable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(destructableType,
							((CommonTriggerExecutionScope) triggerScope).getFilterDestructable());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetEnumDestructable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(destructableType,
							((CommonTriggerExecutionScope) triggerScope).getEnumDestructable());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetFilterItem", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(itemType, ((CommonTriggerExecutionScope) triggerScope).getFilterItem());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetEnumItem", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(itemType, ((CommonTriggerExecutionScope) triggerScope).getEnumItem());
				}
			});

			// ============================================================================
			// Trigger Game Event API
			// ============================================================================
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterVariableEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
					final String varName = arguments.get(1).visit(ObjectJassValueVisitor.<String>getInstance());
					final CLimitOp limitOp = arguments.get(2).visit(ObjectJassValueVisitor.<CLimitOp>getInstance());
					final Double limitval = arguments.get(3).visit(RealJassValueVisitor.getInstance());
					final RemovableTriggerEvent event = globalScope.registerVariableEvent(trigger, varName, limitOp,
							limitval.doubleValue());
					return new HandleJassValue(eventType, event);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterTimerEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
					final Double timeout = arguments.get(1).visit(RealJassValueVisitor.getInstance());
					final Boolean periodic = arguments.get(2).visit(BooleanJassValueVisitor.getInstance());
					final CTimerNativeEvent timer = new CTimerNativeEvent(globalScope, trigger);
					timer.setRepeats(periodic.booleanValue());
					timer.setTimeoutTime(timeout.floatValue());
					simulation.registerTimer(timer);
					return new HandleJassValue(eventType, new RemovableTriggerEvent() {
						@Override
						public void remove() {
							simulation.unregisterTimer(timer);
						}
					});
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterTimerExpireEvent",
					new JassFunction() {
						@Override
						public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
								final TriggerExecutionScope triggerScope) {
							final Trigger trigger = arguments.get(0)
									.visit(ObjectJassValueVisitor.<Trigger>getInstance());
							final CTimerJass timer = arguments.get(1)
									.visit(ObjectJassValueVisitor.<CTimerJass>getInstance());
							timer.addEvent(trigger);
							return new HandleJassValue(eventType, new RemovableTriggerEvent() {
								@Override
								public void remove() {
									timer.removeEvent(trigger);
								}
							});
						}
					});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterGameStateEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
					final CGameState whichState = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CGameState>getInstance());
					final CLimitOp opcode = arguments.get(2).visit(ObjectJassValueVisitor.<CLimitOp>getInstance());
					final Double limitval = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					if (whichState != CGameState.TIME_OF_DAY) {
						// TODO not yet impl
						throw new UnsupportedOperationException("Not yet implemented: TriggerRegisterGameStateEvent");
					}
					return new HandleJassValue(eventType,
							simulation.registerTimeOfDayEvent(globalScope, trigger, opcode, limitval.doubleValue()));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterDialogEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("Not yet implemented: TriggerRegisterDialogEvent");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterDialogButtonEvent",
					new JassFunction() {
						@Override
						public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
								final TriggerExecutionScope triggerScope) {
							throw new UnsupportedOperationException(
									"Not yet implemented: TriggerRegisterDialogButtonEvent");
						}
					});
			jassProgramVisitor.getJassNativeManager().createNative("GetEventGameState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("Not yet implemented: GetEventGameState");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterGameEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
					final JassGameEventsWar3 gameEvent = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					return new HandleJassValue(eventType,
							simulation.registerGameEvent(globalScope, trigger, gameEvent));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetWinningPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("Not yet implemented: GetWinningPlayer");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterEnterRegion", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
					final CRegion region = arguments.get(1).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final TriggerBooleanExpression boolexpr = nullable(arguments, 2,
							ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					return new HandleJassValue(eventType,
							region.add(new CRegionTriggerEnter(globalScope, trigger, boolexpr)));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetTriggeringRegion", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(unitType,
							((CommonTriggerExecutionScope) triggerScope).getEnteringUnit());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetEnteringUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(unitType,
							((CommonTriggerExecutionScope) triggerScope).getEnteringUnit());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterLeaveRegion", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
					final CRegion region = arguments.get(1).visit(ObjectJassValueVisitor.<CRegion>getInstance());
					final TriggerBooleanExpression boolexpr = arguments.get(2)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					return new HandleJassValue(eventType,
							region.add(new CRegionTriggerEnter(globalScope, trigger, boolexpr)));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetLeavingUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(unitType, ((CommonTriggerExecutionScope) triggerScope).getLeavingUnit());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterTrackableHitEvent",
					new JassFunction() {
						@Override
						public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
								final TriggerExecutionScope triggerScope) {
							throw new UnsupportedOperationException(
									"TriggerRegisterTrackableHitEvent not yet implemented ???");
							// dont feel like implementing this atm, although it probably wouldnt be that
							// hard to do
						}
					});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterTrackableTrackEvent",
					new JassFunction() {
						@Override
						public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
								final TriggerExecutionScope triggerScope) {
							throw new UnsupportedOperationException(
									"TriggerRegisterTrackableTrackEvent not yet implemented ???");
						}
					});
			jassProgramVisitor.getJassNativeManager().createNative("GetTriggeringTrackable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("GetTriggeringTrackable not yet implemented ???");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetClickedButton", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("GetClickedButton not yet implemented ???");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetClickedDialog", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("GetClickedDialog not yet implemented ???");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetTournamentFinishSoonTimeRemaining",
					new JassFunction() {
						@Override
						public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
								final TriggerExecutionScope triggerScope) {
							throw new UnsupportedOperationException(
									"GetTournamentFinishSoonTimeRemaining not yet implemented ???");
						}
					});
			jassProgramVisitor.getJassNativeManager().createNative("GetTournamentFinishNowRule", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("GetTournamentFinishNowRule not yet implemented ???");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetTournamentFinishNowPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("GetTournamentFinishNowPlayer not yet implemented ???");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetTournamentScore", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("GetTournamentScore not yet implemented ???");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetSaveBasicFilename", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					throw new UnsupportedOperationException("GetSaveBasicFilename not yet implemented ???");
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterPlayerEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger whichTrigger = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final CPlayerJass whichPlayer = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					final JassGameEventsWar3 whichPlayerEvent = arguments.get(2)
							.visit(ObjectJassValueVisitor.getInstance());
					return new HandleJassValue(eventType,
							whichPlayer.addEvent(globalScope, whichTrigger, whichPlayerEvent));
				}
			});
			// TODO past this point things are inconsistent about ordering
			jassProgramVisitor.getJassNativeManager().createNative("GetCameraMargin", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int whichMargin = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final Rectangle playableMapArea = war3MapViewer.terrain.getPlayableMapArea();
					switch (whichMargin) {
					case 0:// CAMERA_MARGIN_LEFT
						return new RealJassValue(war3MapViewer.terrain.getDefaultCameraBounds()[0] - playableMapArea.x);
					case 1:// CAMERA_MARGIN_RIGHT
						return new RealJassValue((playableMapArea.x + playableMapArea.width)
								- war3MapViewer.terrain.getDefaultCameraBounds()[2]);
					case 2:// CAMERA_MARGIN_TOP
						return new RealJassValue((playableMapArea.y + playableMapArea.height)
								- war3MapViewer.terrain.getDefaultCameraBounds()[3]);
					case 3:// CAMERA_MARGIN_BOTTOM
						return new RealJassValue(war3MapViewer.terrain.getDefaultCameraBounds()[1] - playableMapArea.y);
					default:
						throw new IllegalArgumentException(
								"Must input one of these constants: [CAMERA_MARGIN_LEFT, CAMERA_MARGIN_RIGHT, CAMERA_MARGIN_TOP, CAMERA_MARGIN_BOTTOM]");
					}
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetCameraBoundMinX", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new RealJassValue(meleeUI.getCameraManager().getCameraBounds().getX());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetCameraBoundMinY", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new RealJassValue(meleeUI.getCameraManager().getCameraBounds().getY());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetCameraBoundMaxX", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle cameraBounds = meleeUI.getCameraManager().getCameraBounds();
					return new RealJassValue(cameraBounds.getX() + cameraBounds.getWidth());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetCameraBoundMaxY", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle cameraBounds = meleeUI.getCameraManager().getCameraBounds();
					return new RealJassValue(cameraBounds.getY() + cameraBounds.getHeight());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetCameraBounds", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final double left = arguments.get(0).visit(RealJassValueVisitor.getInstance());
					final double bottom = arguments.get(1).visit(RealJassValueVisitor.getInstance());
					final double right = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					final double top = arguments.get(3).visit(RealJassValueVisitor.getInstance());
					meleeUI.getCameraManager().setCameraBounds(new Rectangle((float) left, (float) bottom,
							(float) (right - left), (float) (top - bottom)));
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetDayNightModels", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String terrainDNCFile = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final String unitDNCFile = arguments.get(1).visit(StringJassValueVisitor.getInstance());
					war3MapViewer.setDayNightModels(terrainDNCFile, unitDNCFile);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("NewSoundEnvironment", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String environmentName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					System.err.println("#########");
					System.err.println("# Engine requested sound environment: " + environmentName);
					System.err.println("# I don't know how to do that on LibGDX, so for now, I do nothing!");
					System.err.println("#########");
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateMIDISound", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String soundLabel = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final int fadeInRate = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final int fadeOutRate = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(soundType, new CMIDISound(soundLabel, fadeInRate, fadeOutRate));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetFloatGameState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CGameState gameState = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					switch (gameState) {
					case TIME_OF_DAY:
						return new RealJassValue(simulation.getGameTimeOfDay());
					}
					throw new IllegalArgumentException("Not a float game state: " + gameState);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetIntegerGameState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CGameState gameState = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					switch (gameState) {
					case DISCONNECTED:
					case DIVINE_INTERVENTION:
						return new IntegerJassValue(0); // TODO
					}
					throw new IllegalArgumentException("Not an integer game state: " + gameState);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetFloatGameState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CGameState gameState = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final float value = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					switch (gameState) {
					case TIME_OF_DAY:
						simulation.setGameTimeOfDay(value);
						return null;
					}
					throw new IllegalArgumentException("Not a float game state: " + gameState);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("StartSound", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CSound soundHandle = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					soundHandle.start();
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetMapMusic", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String musicName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final boolean random = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					final int index = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());

					String musicField;
					if (!CommonEnvironment.this.gameUI.hasSkinField(musicName)) {
						// TODO this versioning system should probably be more general case than this,
						// maybe at the level
						// of skin field lookup???
						final String versionedMusic = "Music_V" + WarsmashConstants.GAME_VERSION;
						if (!CommonEnvironment.this.gameUI.hasSkinField(versionedMusic)) {
							musicField = musicName;
						}
						else {
							musicField = CommonEnvironment.this.gameUI.getSkinField(versionedMusic);
						}
					}
					else {
						musicField = CommonEnvironment.this.gameUI.getSkinField(musicName);
					}
					final String[] musics = musicField.split(";");
					meleeUI.playMusic(musics, random, index);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateItem", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int rawcode = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final double x = arguments.get(1).visit(RealJassValueVisitor.getInstance());
					final double y = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					return new HandleJassValue(itemType,
							simulation.createItem(new War3ID(rawcode), (float) x, (float) y));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ChooseRandomItem", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int level = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final War3ID randomItemId = simulation.getItemData().chooseRandomItem(level,
							simulation.getSeededRandom());
					return new IntegerJassValue(randomItemId == null ? 0 : randomItemId.getValue());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ChooseRandomItemEx", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CItemTypeJass whichType = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int level = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final War3ID randomItemId = simulation.getItemData().chooseRandomItem(whichType, level,
							simulation.getSeededRandom());
					return new IntegerJassValue(randomItemId == null ? 0 : randomItemId.getValue());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateDestructable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int rawcode = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float facing = arguments.get(3).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float scale = arguments.get(4).visit(RealJassValueVisitor.getInstance()).floatValue();
					final int variation = arguments.get(5).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(destructableType,
							simulation.createDestructable(new War3ID(rawcode), x, y, facing, scale, variation));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateDestructableZ", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int rawcode = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float z = arguments.get(3).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float facing = arguments.get(4).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float scale = arguments.get(5).visit(RealJassValueVisitor.getInstance()).floatValue();
					final int variation = arguments.get(6).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(destructableType,
							simulation.createDestructableZ(new War3ID(rawcode), x, y, z, facing, scale, variation));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("KillDestructable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CDestructable dest = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					dest.setLife(simulation, 0f);
					return null;
				}
			});

			jassProgramVisitor.getJassNativeManager().createNative("Player", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int playerIndex = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(playerType, simulation.getPlayer(playerIndex));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int rawcode = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final double x = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					final double y = arguments.get(3).visit(RealJassValueVisitor.getInstance());
					final double facing = arguments.get(4).visit(RealJassValueVisitor.getInstance());
					final CUnit newUnit = simulation.createUnit(new War3ID(rawcode), player.getId(), (float) x,
							(float) y, (float) facing);
					final CUnitType newUnitType = newUnit.getUnitType();
					final int foodUsed = newUnitType.getFoodUsed();
					newUnit.setFoodUsed(foodUsed);
					player.setFoodUsed(player.getFoodUsed() + foodUsed);
					if (newUnitType.getFoodMade() != 0) {
						player.setFoodCap(player.getFoodCap() + newUnitType.getFoodMade());
					}
					// nudge unit
					newUnit.setPointAndCheckUnstuck((float) x, (float) y, simulation);
					return new HandleJassValue(unitType, newUnit);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateUnitAtLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int rawcode = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final Point2D.Double whichLocation = arguments.get(2).visit(ObjectJassValueVisitor.getInstance());
					final float facing = arguments.get(3).visit(RealJassValueVisitor.getInstance()).floatValue();
					final CUnit newUnit = simulation.createUnit(new War3ID(rawcode), player.getId(),
							(float) whichLocation.x, (float) whichLocation.y, facing);
					final CUnitType newUnitType = newUnit.getUnitType();
					final int foodUsed = newUnitType.getFoodUsed();
					newUnit.setFoodUsed(foodUsed);
					player.setFoodUsed(player.getFoodUsed() + foodUsed);
					if (newUnitType.getFoodMade() != 0) {
						player.setFoodCap(player.getFoodCap() + newUnitType.getFoodMade());
					}
					// nudge unit
					newUnit.setPointAndCheckUnstuck((float) whichLocation.x, (float) whichLocation.y, simulation);
					return new HandleJassValue(unitType, newUnit);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateBlightedGoldmine", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					// TODO this needs to setup a non-blighted mine underneath!!!
					final CPlayerJass player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final double x = arguments.get(1).visit(RealJassValueVisitor.getInstance());
					final double y = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					final double facing = arguments.get(3).visit(RealJassValueVisitor.getInstance());
					return new HandleJassValue(unitType, simulation.createUnit(War3ID.fromString("ugol"),
							player.getId(), (float) x, (float) y, (float) facing));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetUnitColor", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final CPlayerColor whichPlayerColor = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					final RenderUnit renderPeer = war3MapViewer.getRenderPeer(whichUnit);
					renderPeer.setPlayerColor(whichPlayerColor.ordinal());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetResourceAmount", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int resourceAmount = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					whichUnit.setGold(resourceAmount);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetUnitState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final CUnitState whichUnitState = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					final float value = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					whichUnit.setUnitState(simulation, whichUnitState, value);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetUnitState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final CUnitState whichUnitState = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					return new RealJassValue(whichUnit.getUnitState(simulation, whichUnitState));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsUnitType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final CUnitTypeJass whichUnitType = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					return BooleanJassValue.of(whichUnit.isUnitType(whichUnitType));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final CPlayerState whichPlayerState = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					final int value = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());
					player.setPlayerState(simulation, whichPlayerState, value);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerTechResearched", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int techIdRawcode = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final boolean specificOnly = arguments.get(2).visit(BooleanJassValueVisitor.getInstance());
					return BooleanJassValue.of(player.getTechtreeUnlocked(new War3ID(techIdRawcode)) > 0);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerTechMaxAllowed", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int techIdRawcode = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final int maximum = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());
					player.setTechtreeMaxAllowed(new War3ID(techIdRawcode), maximum);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerTechMaxAllowed", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int techIdRawcode = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					return new IntegerJassValue(player.getTechtreeMaxAllowed(new War3ID(techIdRawcode)));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsFogEnabled", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					// TODO fog of war!!
					return BooleanJassValue.FALSE;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsFogMaskEnabled", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					// TODO fog of war!!
					return BooleanJassValue.FALSE;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateSoundFromLabel", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String soundLabel = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final boolean looping = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					final boolean is3D = arguments.get(2).visit(BooleanJassValueVisitor.getInstance());
					final boolean stopWhenOutOfRange = arguments.get(3).visit(BooleanJassValueVisitor.getInstance());
					final int fadeInRate = arguments.get(4).visit(IntegerJassValueVisitor.getInstance());
					final int fadeOutRate = arguments.get(5).visit(IntegerJassValueVisitor.getInstance());
					final UnitSound sound = war3MapViewer.getUiSounds().getSound(soundLabel);
					return new HandleJassValue(soundType,
							new CSoundFromLabel(sound,
									is3D ? war3MapViewer.worldScene.audioContext : meleeUI.getUiScene().audioContext,
									looping, is3D, stopWhenOutOfRange, fadeInRate, fadeOutRate));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateSound", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String fileName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final boolean looping = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					final boolean is3D = arguments.get(2).visit(BooleanJassValueVisitor.getInstance());
					final boolean stopWhenOutOfRange = arguments.get(3).visit(BooleanJassValueVisitor.getInstance());
					final int fadeInRate = arguments.get(4).visit(IntegerJassValueVisitor.getInstance());
					final int fadeOutRate = arguments.get(5).visit(IntegerJassValueVisitor.getInstance());
					final String eaxSetting = arguments.get(6).visit(StringJassValueVisitor.getInstance());
					final Sound newSound = UnitSound.createSound(war3MapViewer.mapMpq, fileName);

					return new HandleJassValue(soundType,
							new CSoundFilename(newSound,
									is3D ? war3MapViewer.worldScene.audioContext : meleeUI.getUiScene().audioContext,
									looping, stopWhenOutOfRange, fadeInRate, fadeOutRate, eaxSetting));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetSoundParamsFromLabel", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CSoundFilename sound = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final String soundLabel = arguments.get(1).visit(StringJassValueVisitor.getInstance());

					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("VersionGet", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(versionType, CVersion.VALUES[WarsmashConstants.GAME_VERSION]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetAllItemTypeSlots", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int slots = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					simulation.setAllItemTypeSlots(slots);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetAllUnitTypeSlots", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int slots = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					simulation.setAllItemTypeSlots(slots);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterPlayerUnitEvent",
					new JassFunction() {
						@Override
						public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
								final TriggerExecutionScope triggerScope) {
							final Trigger whichTrigger = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
							final CPlayer whichPlayer = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
							final JassGameEventsWar3 whichPlayerEvent = arguments.get(2)
									.visit(ObjectJassValueVisitor.getInstance());
							final TriggerBooleanExpression filter = nullable(arguments, 3,
									ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
							return new HandleJassValue(eventType,
									whichPlayer.addUnitEvent(globalScope, whichTrigger, whichPlayerEvent, filter));
						}
					});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerEvaluate", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger whichTrigger = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return BooleanJassValue.of(whichTrigger.evaluate(globalScope, triggerScope));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerExecute", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger whichTrigger = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					whichTrigger.execute(globalScope, triggerScope);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Preloader", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String filename = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					doPreloadScript(dataSource, war3MapViewer, filename);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateTimerDialog", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CTimerJass timer = nullable(arguments, 0, ObjectJassValueVisitor.getInstance());
					return new HandleJassValue(timerdialogType, meleeUI.createTimerDialog(timer));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsPlayerObserver", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return BooleanJassValue.of(player.isObserver());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetSoundParamsFromLabel", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CSoundFilename sound = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final String soundLabel = arguments.get(1).visit(StringJassValueVisitor.getInstance());
					// TODO NYI
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetSoundDuration", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CSoundFilename sound = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int duration = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					// TODO NYI
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetSoundPitch", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CSoundFilename sound = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final float pitch = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					// TODO NYI
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetSoundVolume", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CSoundFilename sound = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int volume = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					// TODO NYI
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("AddWeatherEffect", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle where = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int effectId = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					// TODO NYI
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("EnableWeatherEffect", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Rectangle where = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final boolean enable = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					// TODO NYI
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterDeathEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger whichTrigger = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final CWidget whichWidget = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					return new HandleJassValue(eventType, whichWidget.addDeathEvent(globalScope, whichTrigger));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("TriggerRegisterUnitEvent", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Trigger whichTrigger = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final CUnit whichWidget = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					final JassGameEventsWar3 whichPlayerEvent = arguments.get(2)
							.visit(ObjectJassValueVisitor.getInstance());
					return new HandleJassValue(eventType,
							whichWidget.addEvent(globalScope, whichTrigger, whichPlayerEvent));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsUnitHidden", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit unit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return BooleanJassValue.of(unit.isHidden());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerHandicapXP", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer player = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final float handicap = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					player.setHandicapXP(handicap);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetChangingUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					// TODO this is supposed to have some magic nonsense going on where apparently
					// EVENT_WIDGET_DEATH
					// assigns to the return value of this and fires itself upon changing owner!!??
					return unitType.getNullValue();
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetChangingUnitPrevOwner", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					// TODO this is supposed to have some magic nonsense going on where apparently
					// EVENT_WIDGET_DEATH
					// assigns to the return value of this and fires itself upon changing owner!!??
					return playerType.getNullValue();
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetRandomReal", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final float lowBound = arguments.get(0).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float highBound = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					return new RealJassValue(
							(simulation.getSeededRandom().nextFloat() * (highBound - lowBound)) + lowBound);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetRandomInt", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final int lowBound = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final int highBound = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					return new IntegerJassValue(
							(simulation.getSeededRandom().nextInt((highBound - lowBound) + 1)) + lowBound);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetWidgetX", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CWidget whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new RealJassValue(whichWidget.getX());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetWidgetY", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CWidget whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new RealJassValue(whichWidget.getY());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetUnitX", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new RealJassValue(whichWidget.getX());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetUnitY", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new RealJassValue(whichWidget.getY());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetItemX", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CItem whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new RealJassValue(whichWidget.getX());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetItemY", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CItem whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new RealJassValue(whichWidget.getY());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetItemLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CItem whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new HandleJassValue(locationType,
							new Point2D.Double(whichWidget.getX(), whichWidget.getY()));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetUnitLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new HandleJassValue(locationType,
							new Point2D.Double(whichWidget.getX(), whichWidget.getY()));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("AddSpecialEffectTarget", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String modelName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final CWidget targetWidget = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					final String attachPointName = arguments.get(2).visit(StringJassValueVisitor.getInstance());
					return new HandleJassValue(effectType,
							war3MapViewer.addSpecialEffectTarget(modelName, targetWidget, attachPointName));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetItemInvulnerable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CItem whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final boolean flag = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					whichWidget.setInvulernable(flag);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetDestructableInvulnerable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CDestructable whichWidget = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final boolean flag = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					whichWidget.setInvulnerable(flag);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SuspendTimeOfDay", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final boolean flag = arguments.get(0).visit(BooleanJassValueVisitor.getInstance());
					simulation.setTimeOfDaySuspended(flag);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("UnitAddItem", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final CItem whichItem = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					final CAbilityInventory inventoryData = whichUnit.getInventoryData();
					if (inventoryData != null) {
						inventoryData.giveItem(simulation, whichUnit, whichItem, false);
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("UnitItemInSlot", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final int whichSlot = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final CAbilityInventory inventoryData = whichUnit.getInventoryData();
					if (inventoryData != null) {
						return new HandleJassValue(itemType, inventoryData.getItemInSlot(whichSlot));
					}
					return new HandleJassValue(itemType, null);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetCameraTargetController", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final float xoffset = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float yoffset = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final boolean inheritOrientation = arguments.get(3).visit(BooleanJassValueVisitor.getInstance());
					meleeUI.getCameraManager().setTargetController(war3MapViewer.getRenderPeer(whichUnit), xoffset,
							yoffset, inheritOrientation);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CameraSetupGetField", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					// TODO NYI
					return new RealJassValue(0.0f);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("LeaderboardGetItemCount", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					// TODO NYI
					return new IntegerJassValue(0);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("LeaderboardGetPlayerIndex", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					// TODO NYI
					return new IntegerJassValue(0);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetUnitInvulnerable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final boolean flag = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					whichUnit.setInvulnerable(flag);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ShowUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final boolean show = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					whichUnit.setHidden(!show);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("KillUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					if (!whichUnit.isDead()) {
						whichUnit.setLife(simulation, 0f);
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("RemoveUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					simulation.removeUnit(whichUnit);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetTriggerWidget", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(widgetType,
							((CommonTriggerExecutionScope) triggerScope).getTriggerWidget());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerRace", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer whichPlayer = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new HandleJassValue(raceType, whichPlayer.getRace());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerId", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer whichPlayer = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new IntegerJassValue(whichPlayer.getId());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetDestructableLife", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CDestructable whichDestructable = arguments.get(0)
							.visit(ObjectJassValueVisitor.getInstance());
					return new RealJassValue(whichDestructable.getLife());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetDestructableMaxLife", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CDestructable whichDestructable = arguments.get(0)
							.visit(ObjectJassValueVisitor.getInstance());
					return new RealJassValue(whichDestructable.getMaxLife());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetDestructableAnimation", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CDestructable whichDestructable = arguments.get(0)
							.visit(ObjectJassValueVisitor.getInstance());
					final String animation = CommonEnvironment.this.gameUI
							.getTrigStr(arguments.get(1).visit(StringJassValueVisitor.getInstance()));
					final RenderDestructable renderPeer = war3MapViewer.getRenderPeer(whichDestructable);
					renderPeer.setAnimation(animation);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DestructableRestoreLife", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CDestructable whichDestructable = arguments.get(0)
							.visit(ObjectJassValueVisitor.getInstance());
					final float life = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final boolean birth = arguments.get(2).visit(BooleanJassValueVisitor.getInstance());
					// TODO this "restore" function, is it a summation or assignment below???
					// Guessing assign is OK
					whichDestructable.setLife(simulation, life);
					if (!birth) {
						final RenderDestructable renderPeer = war3MapViewer.getRenderPeer(whichDestructable);
						renderPeer.notifyLifeRestored();
					} // else birth plays automatically
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DisplayTimedTextToPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer whichPlayer = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float duration = arguments.get(3).visit(RealJassValueVisitor.getInstance()).floatValue();
					final String message = CommonEnvironment.this.gameUI
							.getTrigStr(arguments.get(4).visit(StringJassValueVisitor.getInstance()));
					if (whichPlayer == simulation.getPlayer(war3MapViewer.getLocalPlayerIndex())) {
						meleeUI.displayTimedText(x, y, duration, message);
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DisplayTextToPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer whichPlayer = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final float x = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
					final float y = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final String message = CommonEnvironment.this.gameUI
							.getTrigStr(arguments.get(3).visit(StringJassValueVisitor.getInstance()));
					if (whichPlayer == simulation.getPlayer(war3MapViewer.getLocalPlayerIndex())) {
						meleeUI.displayTimedText(x, y, (message.length() / 6) + 5, message);
					}
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsPlayerInForce", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer whichPlayer = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final List<CPlayerJass> force = arguments.get(1)
							.visit(ObjectJassValueVisitor.<List<CPlayerJass>>getInstance());
					return BooleanJassValue.of(force.contains(whichPlayer));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetUnitTypeId", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					if (whichUnit == null) {
						return new IntegerJassValue(0);
					}
					return new IntegerJassValue(whichUnit.getTypeId().getValue());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetItemTypeId", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CItem whichItem = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					if (whichItem == null) {
						return new IntegerJassValue(0);
					}
					return new IntegerJassValue(whichItem.getTypeId().getValue());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetItemType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CItem whichItem = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					if (whichItem == null) {
						return itemtypeType.getNullValue();
					}
					return new HandleJassValue(itemtypeType, whichItem.getItemType().getItemClass());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetItemLevel", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CItem whichItem = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					if (whichItem == null) {
						return new IntegerJassValue(0);
					}
					return new IntegerJassValue(whichItem.getItemType().getLevel());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetOwningPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					return new HandleJassValue(playerType, simulation.getPlayer(whichUnit.getPlayerIndex()));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetFilterPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(playerType,
							((CommonTriggerExecutionScope) triggerScope).getFilterPlayer());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetDyingUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(unitType, ((CommonTriggerExecutionScope) triggerScope).getDyingUnit());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetKillingUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(unitType, ((CommonTriggerExecutionScope) triggerScope).getKillingUnit());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetTriggerUnit", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(unitType,
							((CommonTriggerExecutionScope) triggerScope).getTriggeringUnit());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetEnumPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(playerType,
							((CommonTriggerExecutionScope) triggerScope).getEnumPlayer());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetBlightLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayer whichPlayer = arguments.get(0).visit(ObjectJassValueVisitor.getInstance());
					final Point2D.Double whichLocation = arguments.get(1).visit(ObjectJassValueVisitor.getInstance());
					final float radius = arguments.get(2).visit(RealJassValueVisitor.getInstance()).floatValue();
					final boolean addBlight = arguments.get(3).visit(BooleanJassValueVisitor.getInstance());
					final Rectangle blightRectangle = new Rectangle((float) (whichLocation.x - radius),
							(float) (whichLocation.y - radius), radius * 2, radius * 2);
					final float blightRectangleMaxX = blightRectangle.x + blightRectangle.width;
					final float blightRectangleMaxY = blightRectangle.y + blightRectangle.height;
					final float rSquared = radius * radius;
					for (float x = blightRectangle.x; x < blightRectangleMaxX; x += 128.0f) {
						for (float y = blightRectangle.y; y < blightRectangleMaxY; y += 128.0f) {
							final float dx = x - (float) whichLocation.x;
							final float dy = y - (float) whichLocation.y;
							final float distSquared = (dx * dx) + (dy * dy);
							if (distSquared <= rSquared) {
								for (float pathX = -64; pathX < 64; pathX += 32f) {
									for (float pathY = -64; pathY < 64; pathY += 32f) {
										simulation.getPathingGrid().setBlighted(x + pathX + 16, y + pathY + 16,
												addBlight);
									}
								}
								war3MapViewer.terrain.getCorner(x, y).setBlight(true);
							}
						}
					}
					war3MapViewer.terrain.updateGroundTextures(blightRectangle);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetLocalPlayer", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(playerType, simulation.getPlayer(war3MapViewer.getLocalPlayerIndex()));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsUnitInGroup", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CUnit whichUnit = arguments.get(0).visit(ObjectJassValueVisitor.<CUnit>getInstance());
					final List<CUnit> group = arguments.get(1).visit(ObjectJassValueVisitor.<List<CUnit>>getInstance());
					return BooleanJassValue.of(group.contains(whichUnit));
				}
			});
		}

		private void registerConfigNatives(final JassProgramVisitor jassProgramVisitor, final War3MapConfig mapConfig,
				final HandleJassType startlocprioType, final HandleJassType gametypeType,
				final HandleJassType placementType, final HandleJassType gamespeedType,
				final HandleJassType gamedifficultyType, final HandleJassType mapdensityType,
				final HandleJassType locationType, final HandleJassType playerType,
				final HandleJassType playercolorType, final HandleJassType mapcontrolType,
				final HandleJassType playerslotstateType, final CPlayerAPI playerAPI) {
			jassProgramVisitor.getJassNativeManager().createNative("SetMapName", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String name = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					mapConfig.setMapName(name);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetMapDescription", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final String name = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					mapConfig.setMapDescription(name);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetTeams", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer teamCount = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					mapConfig.setTeamCount(teamCount);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayers", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer playerCount = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					mapConfig.setPlayerCount(playerCount);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DefineStartLocation", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer whichStartLoc = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final Double x = arguments.get(1).visit(RealJassValueVisitor.getInstance());
					final Double y = arguments.get(2).visit(RealJassValueVisitor.getInstance());
					mapConfig.getStartLoc(whichStartLoc).setX(x.floatValue());
					mapConfig.getStartLoc(whichStartLoc).setY(y.floatValue());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DefineStartLocationLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer whichStartLoc = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final Point2D.Double whichLocation = arguments.get(1)
							.visit(ObjectJassValueVisitor.<Point2D.Double>getInstance());
					mapConfig.getStartLoc(whichStartLoc).setX((float) whichLocation.x);
					mapConfig.getStartLoc(whichStartLoc).setY((float) whichLocation.y);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetStartLocPrioCount", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer whichStartLoc = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final Integer prioSlotCount = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					mapConfig.getStartLoc(whichStartLoc).setStartLocPrioCount(prioSlotCount);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetStartLocPrio", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer whichStartLoc = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final Integer prioSlotIndex = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					final Integer otherStartLocIndex = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());
					final CStartLocPrio priority = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CStartLocPrio>getInstance());
					mapConfig.getStartLoc(whichStartLoc).setStartLocPrio(prioSlotIndex, otherStartLocIndex, priority);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetStartLocPrioSlot", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer whichStartLoc = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final Integer prioSlotIndex = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					return new IntegerJassValue(
							mapConfig.getStartLoc(whichStartLoc).getOtherStartIndices()[prioSlotIndex]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetStartLocPrio", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer whichStartLoc = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					final Integer prioSlotIndex = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(startlocprioType,
							mapConfig.getStartLoc(whichStartLoc).getOtherStartLocPriorities()[prioSlotIndex]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetGameTypeSupported", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CGameType gameType = arguments.get(0).visit(ObjectJassValueVisitor.<CGameType>getInstance());
					final Boolean value = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					mapConfig.setGameTypeSupported(gameType, value);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetMapFlag", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CMapFlag mapFlag = arguments.get(0).visit(ObjectJassValueVisitor.<CMapFlag>getInstance());
					final Boolean value = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					mapConfig.setMapFlag(mapFlag, value);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetGamePlacement", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CMapPlacement placement = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CMapPlacement>getInstance());
					mapConfig.setPlacement(placement);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetGameSpeed", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CGameSpeed gameSpeed = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CGameSpeed>getInstance());
					mapConfig.setGameSpeed(gameSpeed);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetGameDifficulty", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CMapDifficulty gameDifficulty = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CMapDifficulty>getInstance());
					mapConfig.setGameDifficulty(gameDifficulty);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetResourceDensity", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CMapDensity resourceDensity = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CMapDensity>getInstance());
					mapConfig.setResourceDensity(resourceDensity);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetCreatureDensity", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CMapDensity creatureDensity = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CMapDensity>getInstance());
					mapConfig.setCreatureDensity(creatureDensity);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetTeams", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new IntegerJassValue(mapConfig.getTeamCount());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayers", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new IntegerJassValue(mapConfig.getPlayerCount());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsGameTypeSupported", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CGameType gameType = arguments.get(0).visit(ObjectJassValueVisitor.<CGameType>getInstance());
					return BooleanJassValue.of(mapConfig.isGameTypeSupported(gameType));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetGameTypeSelected", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(gametypeType, mapConfig.getGameTypeSelected());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsMapFlagSet", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CMapFlag mapFlag = arguments.get(0).visit(ObjectJassValueVisitor.<CMapFlag>getInstance());
					return BooleanJassValue.of(mapConfig.isMapFlagSet(mapFlag));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetGamePlacement", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(placementType, mapConfig.getPlacement());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetGameSpeed", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(gamespeedType, mapConfig.getGameSpeed());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetGameDifficulty", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(gamedifficultyType, mapConfig.getGameDifficulty());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetResourceDensity", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(mapdensityType, mapConfig.getResourceDensity());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetCreatureDensity", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					return new HandleJassValue(mapdensityType, mapConfig.getCreatureDensity());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetStartLocationX", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer whichStartLoc = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new RealJassValue(mapConfig.getStartLoc(whichStartLoc).getX());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetStartLocationY", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer whichStartLoc = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new RealJassValue(mapConfig.getStartLoc(whichStartLoc).getY());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetStartLocationLoc", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final Integer whichStartLoc = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(locationType, new Point2D.Double(
							mapConfig.getStartLoc(whichStartLoc).getX(), mapConfig.getStartLoc(whichStartLoc).getY()));
				}
			});
			// PlayerAPI

			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerTeam", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final Integer whichTeam = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					player.setTeam(whichTeam);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerStartLocation", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final Integer startLocIndex = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					player.setStartLocationIndex(startLocIndex);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ForcePlayerStartLocation", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final Integer startLocIndex = arguments.get(1).visit(IntegerJassValueVisitor.getInstance());
					player.forceStartLocation(startLocIndex);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerColor", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CPlayerColor playerColor = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerColor>getInstance());
					player.setColor(playerColor.ordinal());
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerAlliance", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CPlayerJass otherPlayer = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CAllianceType whichAllianceSetting = arguments.get(2)
							.visit(ObjectJassValueVisitor.<CAllianceType>getInstance());
					final Boolean value = arguments.get(3).visit(BooleanJassValueVisitor.getInstance());
					player.setAlliance(otherPlayer.getId(), whichAllianceSetting, value);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerTaxRate", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CPlayerJass otherPlayer = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CPlayerState whichResource = arguments.get(2)
							.visit(ObjectJassValueVisitor.<CPlayerState>getInstance());
					final int taxRate = arguments.get(3).visit(IntegerJassValueVisitor.getInstance());
					player.setTaxRate(otherPlayer.getId(), whichResource, taxRate);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerRacePreference", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CRacePreference whichRacePreference = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CRacePreference>getInstance());
					player.setRacePref(whichRacePreference);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerRaceSelectable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final Boolean value = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					player.setRaceSelectable(value);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerController", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CMapControl controlType = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CMapControl>getInstance());
					player.setController(controlType);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerName", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final String name = arguments.get(1).visit(StringJassValueVisitor.getInstance());
					player.setName(name);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SetPlayerOnScoreScreen", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final Boolean value = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
					player.setOnScoreScreen(value);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerTeam", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					return new IntegerJassValue(player.getTeam());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerStartLocation", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					return new IntegerJassValue(player.getStartLocationIndex());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerColor", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					return new HandleJassValue(playercolorType, CPlayerColor.getColorByIndex(player.getColor()));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerSelectable", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					return BooleanJassValue.of(player.isSelectable());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerController", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					return new HandleJassValue(mapcontrolType, player.getController());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerSlotState", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					return new HandleJassValue(playerslotstateType, player.getSlotState());
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerTaxRate", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CPlayerJass otherPlayer = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CPlayerState whichResource = arguments.get(2)
							.visit(ObjectJassValueVisitor.<CPlayerState>getInstance());
					return new IntegerJassValue(player.getTaxRate(otherPlayer.getId(), whichResource));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("IsPlayerRacePrefSet", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					final CRacePreference racePref = arguments.get(1)
							.visit(ObjectJassValueVisitor.<CRacePreference>getInstance());
					return BooleanJassValue.of(player.isRacePrefSet(racePref));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("GetPlayerName", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final CPlayerJass player = arguments.get(0)
							.visit(ObjectJassValueVisitor.<CPlayerJass>getInstance());
					return new StringJassValue(player.getName());
				}
			});
		}

		public void main() {
			this.jassProgramVisitor.getGlobals().getFunctionByName("main").call(Collections.emptyList(),
					new GlobalScope(), null);
		}
	}

	private static void setupTriggerAPI(final JassProgramVisitor jassProgramVisitor, final HandleJassType triggerType,
			final HandleJassType triggeractionType, final HandleJassType triggerconditionType,
			final HandleJassType boolexprType, final HandleJassType conditionfuncType,
			final HandleJassType filterfuncType) {
		// ============================================================================
		// Native trigger interface
		//
		jassProgramVisitor.getJassNativeManager().createNative("CreateTrigger", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				return new HandleJassValue(triggerType, new Trigger());
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("DestroyTrigger", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				trigger.destroy();
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("ResetTrigger", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				trigger.reset();
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("EnableTrigger", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				trigger.setEnabled(true);
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("DisableTrigger", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				trigger.setEnabled(false);
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("IsTriggerEnabled", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				return BooleanJassValue.of(trigger.isEnabled());
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("TriggerWaitOnSleeps", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				final Boolean value = arguments.get(1).visit(BooleanJassValueVisitor.getInstance());
				trigger.setWaitOnSleeps(value.booleanValue());
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("IsTriggerWaitOnSleeps", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				return BooleanJassValue.of(trigger.isWaitOnSleeps());
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("GetTriggeringTrigger", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				return new HandleJassValue(triggerType, triggerScope.getTriggeringTrigger());
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("GetTriggerEventId", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				throw new UnsupportedOperationException("GetTriggerEventId not yet implemented ???");
				// TODO I need to review what eventid is, I'm forgetting, sorry
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("GetTriggerEvalCount", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				return new IntegerJassValue(trigger.getEvalCount());
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("GetTriggerExecCount", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger trigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				return new IntegerJassValue(trigger.getExecCount());
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("ExecuteFunc", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final String funcName = arguments.get(0).visit(ObjectJassValueVisitor.<String>getInstance());
				final JassFunction functionByName = globalScope.getFunctionByName(funcName);
				if (functionByName != null) {
					// TODO below TriggerExecutionScope.EMPTY is probably not correct
					functionByName.call(Collections.emptyList(), globalScope, TriggerExecutionScope.EMPTY);
				}
				return null;
			}
		});

		// ============================================================================
		// Boolean Expr API ( for compositing trigger conditions and unit filter
		// funcs...)
		// ============================================================================
		jassProgramVisitor.getJassNativeManager().createNative("And", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final TriggerBooleanExpression operandA = arguments.get(0)
						.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
				final TriggerBooleanExpression operandB = arguments.get(1)
						.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
				return new HandleJassValue(boolexprType, new BoolExprAnd(operandA, operandB));
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("Or", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final TriggerBooleanExpression operandA = arguments.get(0)
						.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
				final TriggerBooleanExpression operandB = arguments.get(1)
						.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
				return new HandleJassValue(boolexprType, new BoolExprOr(operandA, operandB));
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("Not", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final TriggerBooleanExpression operand = arguments.get(0)
						.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
				return new HandleJassValue(boolexprType, new BoolExprNot(operand));
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("Condition", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final JassFunction func = arguments.get(0).visit(JassFunctionJassValueVisitor.getInstance());
				return new HandleJassValue(conditionfuncType, new BoolExprCondition(func));
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("DestroyCondition", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final BoolExprCondition condition = arguments.get(0)
						.visit(ObjectJassValueVisitor.<BoolExprCondition>getInstance());
				System.err.println(
						"DestroyCondition called but in Java we don't have a destructor, so we need to unregister later when that is implemented");
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("Filter", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final JassFunction func = arguments.get(0).visit(JassFunctionJassValueVisitor.getInstance());
				return new HandleJassValue(filterfuncType, new BoolExprFilter(func));
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("DestroyFilter", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final BoolExprFilter filter = arguments.get(0)
						.visit(ObjectJassValueVisitor.<BoolExprFilter>getInstance());
				System.err.println(
						"DestroyFilter called but in Java we don't have a destructor, so we need to unregister later when that is implemented");
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("DestroyBoolExpr", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final TriggerBooleanExpression boolexpr = arguments.get(0)
						.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
				System.err.println(
						"DestroyBoolExpr called but in Java we don't have a destructor, so we need to unregister later when that is implemented");
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("TriggerAddCondition", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger whichTrigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				final TriggerBooleanExpression condition = arguments.get(1)
						.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
				final int index = whichTrigger.addCondition(condition);
				return new HandleJassValue(triggerconditionType, new TriggerCondition(condition, whichTrigger, index));
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("TriggerRemoveCondition", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger whichTrigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				final TriggerCondition condition = arguments.get(1)
						.visit(ObjectJassValueVisitor.<TriggerCondition>getInstance());
				if (condition.getTrigger() != whichTrigger) {
					throw new IllegalArgumentException("Unable to remove condition, wrong trigger");
				}
				whichTrigger.removeConditionAtIndex(condition.getConditionIndex());
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("TriggerAddAction", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Trigger whichTrigger = arguments.get(0).visit(ObjectJassValueVisitor.<Trigger>getInstance());
				final JassFunction actionFunc = arguments.get(1).visit(JassFunctionJassValueVisitor.getInstance());
				final int actionIndex = whichTrigger.addAction(actionFunc);
				return new HandleJassValue(triggeractionType, new TriggerAction(whichTrigger, actionFunc, actionIndex));
			}
		});
	}

	private static <T> T nullable(final List<JassValue> arguments, final int index,
			final ObjectJassValueVisitor<T> visitor) {
		final JassValue arg = arguments.get(index);
		if (arg == null) {
			return null;
		}
		return arg.visit(visitor);
	}

	private static void doPreloadScript(final DataSource dataSource, final War3MapViewer war3MapViewer,
			final String filename) {
		final JassProgramVisitor jassProgramVisitor = new JassProgramVisitor();
		jassProgramVisitor.getJassNativeManager().createNative("Preload", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final String filename = arguments.get(0).visit(StringJassValueVisitor.getInstance());
				war3MapViewer.load(filename, war3MapViewer.mapPathSolver, war3MapViewer.solverParams);
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("PreloadEnd", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final float timeout = arguments.get(1).visit(RealJassValueVisitor.getInstance()).floatValue();
				return null;
			}
		});
		try {
			JassLexer lexer;
			try {
				lexer = new JassLexer(CharStreams.fromStream(dataSource.getResourceAsStream(filename)));
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
			final JassParser parser = new JassParser(new CommonTokenStream(lexer));
//							parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
			parser.addErrorListener(new BaseErrorListener() {
				@Override
				public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
						final int charPositionInLine, final String msg, final RecognitionException e) {
					if (!REPORT_SYNTAX_ERRORS) {
						return;
					}

					final String sourceName = String.format("%s:%d:%d: ", filename, line, charPositionInLine);

					System.err.println(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
					throw new IllegalStateException(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
				}
			});
			jassProgramVisitor.visit(parser.program());
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
