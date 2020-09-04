package com.etheller.warsmash.parsers.jass;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.JassFunctionJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.RealJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StringJassValueVisitor;
import com.etheller.interpreter.ast.visitors.JassProgramVisitor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprAnd;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprCondition;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprFilter;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprNot;
import com.etheller.warsmash.parsers.jass.triggers.BoolExprOr;
import com.etheller.warsmash.parsers.jass.triggers.TriggerAction;
import com.etheller.warsmash.parsers.jass.triggers.TriggerCondition;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class Jass2 {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	public static JUIEnvironment loadJUI(final DataSource dataSource, final Viewport uiViewport,
			final FreeTypeFontGenerator fontGenerator, final Scene uiScene, final War3MapViewer war3MapViewer,
			final RootFrameListener rootFrameListener, final String... files) {

		final JassProgramVisitor jassProgramVisitor = new JassProgramVisitor();
		final JUIEnvironment environment = new JUIEnvironment(jassProgramVisitor, dataSource, uiViewport, fontGenerator,
				uiScene, war3MapViewer, rootFrameListener);
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
				final Viewport uiViewport, final FreeTypeFontGenerator fontGenerator, final Scene uiScene,
				final War3MapViewer war3MapViewer, final RootFrameListener rootFrameListener) {
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
					final Element skin = GameUI.loadSkin(dataSource, skinArg);
					final GameUI gameUI = new GameUI(dataSource, skin, uiViewport, fontGenerator, uiScene,
							war3MapViewer);
					JUIEnvironment.this.gameUI = gameUI;
					JUIEnvironment.this.skin = skin;
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

					frame.setText(text);
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
					frame.positionBounds(uiViewport);
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
}
