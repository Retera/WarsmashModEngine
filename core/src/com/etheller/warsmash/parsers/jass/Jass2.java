package com.etheller.warsmash.parsers.jass;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.interpreter.JassLexer;
import com.etheller.interpreter.JassParser;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.RealJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StringJassValueVisitor;
import com.etheller.interpreter.ast.visitors.JassProgramVisitor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.units.Element;

public class Jass2 {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	public static JUIEnvironment loadJUI(final DataSource dataSource, final Viewport uiViewport,
			final RootFrameListener rootFrameListener, final String... files) {

		final JassProgramVisitor jassProgramVisitor = new JassProgramVisitor();
		final JUIEnvironment environment = new JUIEnvironment(jassProgramVisitor, dataSource, uiViewport,
				rootFrameListener);
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
				final Viewport uiViewport, final RootFrameListener rootFrameListener) {
			jassProgramVisitor.getJassNativeManager().createNative("LogError", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope) {
					final String stringValue = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					System.err.println(stringValue);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("ConvertFramePointType", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope) {
					final int value = arguments.get(0).visit(IntegerJassValueVisitor.getInstance());
					return new HandleJassValue(jassProgramVisitor.getGlobals().framePointType,
							FramePoint.values()[value]);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("CreateRootFrame", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope) {
					final String skinArg = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final Element skin = GameUI.loadSkin(dataSource, skinArg);
					final GameUI gameUI = new GameUI(dataSource, skin, uiViewport);
					JUIEnvironment.this.gameUI = gameUI;
					JUIEnvironment.this.skin = skin;
					rootFrameListener.onCreate(gameUI);
					return new HandleJassValue(jassProgramVisitor.getGlobals().frameHandleType, gameUI);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("LoadTOCFile", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope) {
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
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope) {
					final String templateName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					final UIFrame ownerFrame = arguments.get(1).visit(ObjectJassValueVisitor.<UIFrame>getInstance());
					final int createContext = arguments.get(2).visit(IntegerJassValueVisitor.getInstance());

					final UIFrame simpleFrame = JUIEnvironment.this.gameUI.createSimpleFrame(templateName, ownerFrame,
							createContext);

					return new HandleJassValue(jassProgramVisitor.getGlobals().frameHandleType, simpleFrame);
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("FrameSetAbsPoint", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope) {
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
			jassProgramVisitor.getJassNativeManager().createNative("FramePositionBounds", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope) {
					final UIFrame frame = arguments.get(0).visit(ObjectJassValueVisitor.<UIFrame>getInstance());
					frame.positionBounds(uiViewport);
					return null;
				}
			});
			jassProgramVisitor.getJassNativeManager().createNative("SkinGetField", new JassFunction() {
				@Override
				public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope) {
					final String fieldName = arguments.get(0).visit(StringJassValueVisitor.getInstance());
					return new StringJassValue(JUIEnvironment.this.skin.getField(fieldName));
				}
			});
		}
	}
}
