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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.interpreter.JassLexer;
import com.etheller.interpreter.JassParser;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
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
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.ItemUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerColor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerGameResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerScore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CStartLocPrio;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegion;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.state.CGameState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.state.CUnitState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CAttackTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CBlendMode;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CCameraField;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CGameSpeed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CGameType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CLimitOp;
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

public class Jass2 {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	public static CommonEnvironment loadCommon(final DataSource dataSource, final Viewport uiViewport,
			final Scene uiScene, final War3MapViewer war3MapViewer, final RootFrameListener rootFrameListener,
			final String... files) {

		final JassProgramVisitor jassProgramVisitor = new JassProgramVisitor();
		final CommonEnvironment environment = new CommonEnvironment(jassProgramVisitor, dataSource, uiViewport, uiScene,
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
			jassProgramVisitor.getJassNativeManager().createNative("Condition", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final JassFunction func = arguments.get(0).visit(JassFunctionJassValueVisitor.getInstance());
					return new HandleJassValue(conditionFuncType, new BoolExprCondition(func));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Filter", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final JassFunction func = arguments.get(0).visit(JassFunctionJassValueVisitor.getInstance());
					return new HandleJassValue(filterType, new BoolExprFilter(func));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DestroyCondition", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final BoolExprCondition trigger = arguments.get(0)
							.visit(ObjectJassValueVisitor.<BoolExprCondition>getInstance());
					System.err.println(
							"DestroyCondition called but in Java we don't have a destructor, so we need to unregister later when that is implemented");
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("DestroyFilter", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final BoolExprFilter trigger = arguments.get(0)
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
					final TriggerBooleanExpression trigger = arguments.get(0)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					System.err.println(
							"DestroyBoolExpr called but in Java we don't have a destructor, so we need to unregister later when that is implemented");
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("And", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final TriggerBooleanExpression operandA = arguments.get(0)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					final TriggerBooleanExpression operandB = arguments.get(1)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					return new HandleJassValue(boolExprType, new BoolExprAnd(operandA, operandB));
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
					return new HandleJassValue(boolExprType, new BoolExprOr(operandA, operandB));
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("Not", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
						final TriggerExecutionScope triggerScope) {
					final TriggerBooleanExpression operand = arguments.get(0)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					return new HandleJassValue(boolExprType, new BoolExprNot(operand));
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
					return new HandleJassValue(triggerConditionType,
							new TriggerCondition(condition, whichTrigger, index));
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
					return new HandleJassValue(triggerActionType,
							new TriggerAction(whichTrigger, actionFunc, actionIndex));
				}
			});
		}
	}

	private static final class CommonEnvironment {
		private GameUI gameUI;
		private Element skin;

		public CommonEnvironment(final JassProgramVisitor jassProgramVisitor, final DataSource dataSource,
				final Viewport uiViewport, final Scene uiScene, final War3MapViewer war3MapViewer,
				final RootFrameListener rootFrameListener) {
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
			jassProgramVisitor.getJassNativeManager().createNative("ConvertGameResult", new JassFunction() {
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
					return new HandleJassValue(gametypeType, CMapFlag.getById(i));
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
					return new HandleJassValue(playercolorType, CMapControl.VALUES[i]);
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
					return new HandleJassValue(fogstateType, CFogState.VALUES[i]);
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
					return new HandleJassValue(attacktypeType, CDamageType.VALUES[i]);
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
							if (filter.evaluate(globalScope,
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
					final TriggerBooleanExpression filter = arguments.get(4)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					simulation.getWorldCollision().enumUnitsInRect(tempRect.set(x - radius, y - radius, radius, radius),
							new CUnitEnumFunction() {

								@Override
								public boolean call(final CUnit unit) {
									if (unit.distance(x, y) <= radius) {
										if (filter.evaluate(globalScope,
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
					final TriggerBooleanExpression filter = arguments.get(3)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					simulation.getWorldCollision().enumUnitsInRect(tempRect.set(x - radius, y - radius, radius, radius),
							new CUnitEnumFunction() {

								@Override
								public boolean call(final CUnit unit) {
									if (unit.distance(x, y) <= radius) {
										if (filter.evaluate(globalScope,
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
					return new HandleJassValue(unitType, group.get(0));
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
					final TriggerBooleanExpression filter = arguments.get(1)
							.visit(ObjectJassValueVisitor.<TriggerBooleanExpression>getInstance());
					for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
						final CPlayerJass jassPlayer = simulation.getPlayer(i);
						if (filter.evaluate(globalScope,
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
					return new BooleanJassValue(mapConfig.isGameTypeSupported(gameType));
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
					return new BooleanJassValue(mapConfig.isMapFlagSet(mapFlag));
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
					return new BooleanJassValue(player.isSelectable());
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
					return new HandleJassValue(playerslotstateType, player.getController());
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
					return new BooleanJassValue(player.isRacePrefSet(racePref));
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
	}
}
