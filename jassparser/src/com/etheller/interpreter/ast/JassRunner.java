package com.etheller.interpreter.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.etheller.interpreter.JassLexer;
import com.etheller.interpreter.JassParser;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.CodeJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.RealJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StringJassValueVisitor;
import com.etheller.interpreter.ast.visitors.JassProgramVisitor;

public class JassRunner {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	static class SleepingData {
		JassThread thread;
		long wakeTime;

		public SleepingData(final JassThread thread, final long wakeTime) {
			this.thread = thread;
			this.wakeTime = wakeTime;
		}
	}

	public static void main(final String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: <JassFiles> [<AdditionaFile>...]");
			return;
		}
		final JassProgramVisitor jassProgramVisitor = new JassProgramVisitor();
		jassProgramVisitor.getJassNativeManager().createNative("BJDebugMsg", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				for (final JassValue argument : arguments) {
					System.out.println(argument.visit(StringJassValueVisitor.getInstance()));
				}
				return null;
			}
		});
		jassProgramVisitor.getJassNativeManager().createNative("StartThread", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final CodeJassValue threadFunction = arguments.get(0).visit(CodeJassValueVisitor.getInstance());
				globalScope.queueThread(globalScope.createThread(threadFunction));
				return null;
			}
		});
		final List<SleepingData> sleepingThreadData = new ArrayList<>();
		jassProgramVisitor.getJassNativeManager().createNative("Sleep", new JassFunction() {
			@Override
			public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
					final TriggerExecutionScope triggerScope) {
				final Double sleepTime = arguments.get(0).visit(RealJassValueVisitor.getInstance());
				final JassThread currentThread = globalScope.getCurrentThread();
				currentThread.setSleeping(true);
				sleepingThreadData
						.add(new SleepingData(currentThread, System.currentTimeMillis() + (long) (sleepTime * 1000.0)));
				return null;
			}
		});
		for (final String arg : args) {
			try {
				jassProgramVisitor.setCurrentFileName(arg);
				final JassLexer lexer = new JassLexer(CharStreams.fromFileName(arg));
				final JassParser parser = new JassParser(new CommonTokenStream(lexer));
				parser.addErrorListener(new BaseErrorListener() {
					@Override
					public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
							final int line, final int charPositionInLine, final String msg,
							final RecognitionException e) {
						if (!REPORT_SYNTAX_ERRORS) {
							return;
						}

						String sourceName = recognizer.getInputStream().getSourceName();
						if (!sourceName.isEmpty()) {
							sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
						}

						System.err.println(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
					}
				});
				jassProgramVisitor.visit(parser.program());
			}
			catch (final Exception e) {
				e.printStackTrace();
			}
		}
		final JassThread myJassThread = jassProgramVisitor.getGlobals().createThread("main", Collections.emptyList(),
				TriggerExecutionScope.EMPTY);
		jassProgramVisitor.getGlobals().queueThread(myJassThread);
		boolean done = false;
		do {
			final long currentTimeMillis = System.currentTimeMillis();
			final Iterator<SleepingData> iterator = sleepingThreadData.iterator();
			while (iterator.hasNext()) {
				final SleepingData next = iterator.next();
				if (currentTimeMillis >= next.wakeTime) {
					next.thread.setSleeping(false);
					iterator.remove();
				}
			}
			done = jassProgramVisitor.getGlobals().runThreads();
		}
		while (!done);
	}

}
