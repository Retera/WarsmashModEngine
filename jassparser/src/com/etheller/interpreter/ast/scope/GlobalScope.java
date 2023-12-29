package com.etheller.interpreter.ast.scope;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.debug.DebuggingJassFunction;
import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.debug.JassStackElement;
import com.etheller.interpreter.ast.execution.JassStackFrame;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.execution.instruction.BeginFunctionInstruction;
import com.etheller.interpreter.ast.execution.instruction.InstructionAppendingJassStatementVisitor;
import com.etheller.interpreter.ast.execution.instruction.JassInstruction;
import com.etheller.interpreter.ast.execution.instruction.PushLiteralInstruction;
import com.etheller.interpreter.ast.execution.instruction.ReturnInstruction;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.function.NativeJassFunction;
import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.scope.variableevent.CLimitOp;
import com.etheller.interpreter.ast.scope.variableevent.VariableEvent;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.util.JassSettings;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.visitor.ArrayPrimitiveTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.HandleJassTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.HandleTypeSuperTypeLoadingVisitor;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

public final class GlobalScope {
	private final List<GlobalScopeAssignable> indexedGlobals = new ArrayList<GlobalScopeAssignable>();
	private final List<JassInstruction> instructions = new ArrayList<JassInstruction>();
	private final Map<String, Integer> globals = new HashMap<>();
	private final Map<String, GlobalScopeAssignable> fastGlobals = new HashMap<>();
	private final Map<String, JassFunction> functions = new HashMap<>();
	private final Map<String, Integer> functionNameToInstructionPtr = new HashMap<>();
	private final Map<String, Integer> functionNameToNativeId = new HashMap<>();
	private final List<JassFunction> indexedNativeFunctions = new ArrayList<>();
	private final Map<String, JassType> types = new HashMap<>();
	private final HandleTypeSuperTypeLoadingVisitor handleTypeSuperTypeLoadingVisitor = new HandleTypeSuperTypeLoadingVisitor();
	private final ArrayDeque<QueuedCallback> triggerQueue = new ArrayDeque<>();
	private final ArrayDeque<QueuedCallback> runningTriggerQueue = new ArrayDeque<>();
	private final List<JassThread> threads = new ArrayList<>();
	private final List<JassThread> newThreads = new ArrayList<>();
	private JassThread currentThread;
	private boolean yieldedCurrentThread = false;

	public final HandleJassType handleType;

	private final ArrayDeque<JassStackElement> jassStack = new ArrayDeque<>();
	private boolean debug;

	public GlobalScope() {
		this.handleType = registerHandleType("handle");// the handle type
		registerPrimitiveType(JassType.BOOLEAN);
		registerPrimitiveType(JassType.INTEGER);
		registerPrimitiveType(JassType.CODE);
		registerPrimitiveType(JassType.NOTHING);
		registerPrimitiveType(JassType.REAL);
		registerPrimitiveType(JassType.STRING);
	}

	public Deque<JassStackElement> getJassStack() {
		return this.jassStack;
	}

	public List<JassStackElement> copyJassStack() {
		final List<JassStackElement> copiedStack = new ArrayList<>();
		for (final JassStackElement stackElement : this.jassStack) {
			copiedStack.add(new JassStackElement(stackElement));
		}
		JassThread unwindThread = this.currentThread;
		while (unwindThread != null) {
			JassStackFrame frame = unwindThread.stackFrame;
			while (frame != null) {
				if (frame.functionNameMetaData != null) {
					copiedStack.add(new JassStackElement(frame.functionNameMetaData, frame.debugLineNo));
				}
				else {
					copiedStack.add(new JassStackElement("<unknown source>", "<unknown function>", -1));
				}
				frame = frame.stackBase;
			}
			unwindThread = unwindThread.parent;
		}
		return copiedStack;
	}

	public void pushJassStack(final JassStackElement element) {
		this.jassStack.push(element);
	}

	public void popJassStack() {
		this.jassStack.pop();
	}

	public void setLineNumber(final int lineNo) {
		final JassStackElement top = this.jassStack.peekFirst();
		if (top != null) {
			top.setLineNumber(lineNo);
		}
	}

	public int getLineNumber() {
		final JassStackElement top = this.jassStack.peekFirst();
		if (top != null) {
			return top.getLineNumber();
		}
		return -1;
	}

	public HandleJassType registerHandleType(final String name) {
		final HandleJassType handleJassType = new HandleJassType(null, name);
		this.types.put(name, handleJassType);
		return handleJassType;
	}

	private void registerPrimitiveType(final PrimitiveJassType type) {
		this.types.put(type.getName(), type);
	}

	private void putGlobal(final String name, final GlobalScopeAssignable globalScopeAssignable) {
		final int index = this.indexedGlobals.size();
		this.indexedGlobals.add(globalScopeAssignable);
		this.globals.put(name, index);
		this.fastGlobals.put(name, globalScopeAssignable);
	}

	public void createGlobalArray(final String name, final JassType type) {
		final GlobalScopeAssignable assignable = new GlobalScopeAssignable(type, this);
		assignable.setValue(new ArrayJassValue((ArrayJassType) type)); // TODO less bad code
		putGlobal(name, assignable);
	}

	public void createGlobal(final String name, final JassType type) {
		putGlobal(name, new GlobalScopeAssignable(type, this));
	}

	public void createGlobal(final String name, final JassType type, final JassValue value) {
		final GlobalScopeAssignable assignable = new GlobalScopeAssignable(type, this);
		try {
			assignable.setValue(value);
		}
		catch (final Exception exc) {
			throw new RuntimeException("Global initialization failed for name: " + name, exc);
		}
		putGlobal(name, assignable);
	}

	public void setGlobal(final String name, final JassValue value) {
		final GlobalScopeAssignable assignable = this.fastGlobals.get(name);
		if (assignable == null) {
			throw new RuntimeException("Undefined global: " + name);
		}
		if (assignable.getType().visit(ArrayPrimitiveTypeVisitor.getInstance()) != null) {
			throw new RuntimeException("Unable to assign array variable: " + name);
		}
		assignable.setValue(value);
	}

	public JassValue getGlobal(final String name) {
		final Assignable global = this.fastGlobals.get(name);
		if (global == null) {
			throw new RuntimeException("Undefined global: " + name);
		}
		return global.getValue();
	}

	public JassValue getGlobalById(final int globalId) {
		return getAssignableGlobalById(globalId).getValue();
	}

	public GlobalScopeAssignable getAssignableGlobalById(final int globalId) {
		return this.indexedGlobals.get(globalId);
	}

	/**
	 * @param name
	 * @return the global id, or else -1 if no such global exists
	 */
	public int getGlobalId(final String name) {
		final Integer globalId = this.globals.get(name);
		if (globalId == null) {
			return -1;
		}
		return globalId;
	}

	public GlobalScopeAssignable getAssignableGlobal(final String name) {
		return this.fastGlobals.get(name);
	}

	private JassFunction internalDefineFunction(final int lineNo, final String sourceFile, final String name,
			final JassFunction function) {
		JassFunction result;
		if (JassSettings.DEBUG) {
			result = new DebuggingJassFunction(lineNo, sourceFile, name, function);
		}
		else {
			result = function;
		}
		this.functions.put(name, result);
		return result;
	}

	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final NativeJassFunction function) {
		final JassFunction definedFunction = internalDefineFunction(lineNo, sourceFile, name, function);
		final int nativeId = this.indexedNativeFunctions.size();
		this.functionNameToNativeId.put(name, nativeId);
		this.indexedNativeFunctions.add(definedFunction);
	}

	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction function) {
		internalDefineFunction(lineNo, sourceFile, name, function);
		this.functionNameToInstructionPtr.put(name, this.instructions.size());
		final List<JassStatement> statements = function.getStatements();
		this.instructions.add(new BeginFunctionInstruction(lineNo, sourceFile, name));
		final InstructionAppendingJassStatementVisitor visitor = new InstructionAppendingJassStatementVisitor(
				this.instructions, this, function.getParameters());
		for (final JassStatement statement : statements) {
			statement.accept(visitor);
		}
		this.instructions.add(new PushLiteralInstruction(JassType.NOTHING.getNullValue()));
		this.instructions.add(new ReturnInstruction());
	}

	public JassFunction getFunctionByName(final String name) {
		return this.functions.get(name);
	}

	public Integer getUserFunctionInstructionPtr(final String name) {
		return this.functionNameToInstructionPtr.get(name);
	}

	public Integer getNativeId(final String name) {
		return this.functionNameToNativeId.get(name);
	}

	public JassFunction getNativeById(final int id) {
		return this.indexedNativeFunctions.get(id);
	}

	public JassType parseType(final String text) {
		final JassType type = this.types.get(text);
		if (type != null) {
			return type;
		}
		else {
			throw new RuntimeException("Unknown type: " + text);
		}
	}

	public JassType parseArrayType(final String primitiveTypeName) {
		final String arrayTypeName = primitiveTypeName + " array";
		JassType arrayType = this.types.get(arrayTypeName);
		if (arrayType == null) {
			arrayType = new ArrayJassType(parseType(primitiveTypeName));
			this.types.put(arrayTypeName, arrayType);
		}
		return arrayType;
	}

	public void loadTypeDefinition(final String type, final String supertype) {
		final JassType superType = this.types.get(supertype);
		if (superType != null) {
			final HandleJassType handleSuperType = superType.visit(HandleJassTypeVisitor.getInstance());
			if (handleSuperType != null) {
				JassType jassType = this.types.get(type);
				if (jassType == null) {
					if (JassSettings.CONTINUE_EXECUTING_ON_ERROR) {
						System.err.println(
								"Generating stub declaration for type " + type + " because it does not exist natively");
						jassType = registerHandleType(type);
					}
					else {
						throw new RuntimeException(
								"unable to declare type " + type + " because it does not exist natively");
					}
				}
				jassType.visit(this.handleTypeSuperTypeLoadingVisitor.reset(handleSuperType));
			}
			else {
				throw new RuntimeException("type " + type + " cannot extend primitive type " + supertype);
			}
		}
		else {
			throw new RuntimeException("type " + type + " cannot extend unknown type " + supertype);
		}
	}

	public JassThread createThread(final CodeJassValue codeValue) {
		return createThread(codeValue, TriggerExecutionScope.EMPTY);
	}

	public void queueThread(final JassThread thread) {
		this.newThreads.add(thread);
	}

	public JassThread createThread(final CodeJassValue codeValue, final TriggerExecutionScope triggerScope) {
		final JassStackFrame jassStackFrame = new JassStackFrame();
		jassStackFrame.returnAddressInstructionPtr = -1;
		final JassThread jassThread = new JassThread(jassStackFrame, this, triggerScope,
				codeValue.getUserFunctionInstructionPtr());
		return jassThread;
	}

	public JassThread createThread(final String functionName, final List<JassValue> arguments,
			final TriggerExecutionScope triggerExecutionScope) {
		JassFunction functionByName = getFunctionByName(functionName);
		if (functionByName instanceof DebuggingJassFunction) {
			functionByName = ((DebuggingJassFunction) functionByName).getDelegate();
		}
		if (functionByName instanceof UserJassFunction) {
			final UserJassFunction userJassFunction = (UserJassFunction) functionByName;
			final Integer userFunctionInstructionPtr = getUserFunctionInstructionPtr(functionName);
			final int instructionPtr = userFunctionInstructionPtr == null ? -1 : userFunctionInstructionPtr;
			final List<JassParameter> parameters = userJassFunction.getParameters();
			if (arguments.size() != parameters.size()) {
				throw new RuntimeException("Invalid number of arguments passed to function");
			}
			final JassStackFrame jassStackFrame = new JassStackFrame(arguments.size());
			jassStackFrame.returnAddressInstructionPtr = -1;
			for (int i = 0; i < parameters.size(); i++) {
				final JassParameter parameter = parameters.get(i);
				final JassValue argument = arguments.get(i);
				if (!parameter.matchesType(argument)) {
					if ((parameter == null) || (argument == null)) {
						System.err.println(
								"We called some Jass function with incorrect argument types, and the types were null!!!");
					}
					System.err.println(
							parameter.getType() + " != " + argument.visit(JassTypeGettingValueVisitor.getInstance()));
					throw new RuntimeException(
							"Invalid type " + argument.visit(JassTypeGettingValueVisitor.getInstance()).getName()
									+ " for specified argument " + parameter.getType().getName());
				}
				jassStackFrame.push(argument);
			}
			final JassThread jassThread = new JassThread(jassStackFrame, this, triggerExecutionScope, instructionPtr);
			return jassThread;
		}
		else {
			throw new IllegalStateException("Can only create thread from user function");
		}
	}

	/**
	 * @return true if all threads have terminated
	 */
	public boolean runThreads() {
		boolean anyThreadsAdded = false;
		do {
			runOneThreadLooop();
			anyThreadsAdded = !this.newThreads.isEmpty();
			this.threads.addAll(this.newThreads);
			this.newThreads.clear();
		}
		while (anyThreadsAdded);
		return this.threads.isEmpty();
	}

	public void runThreadUntilCompletion(final JassThread thread) {
		final JassThread parentThread = this.currentThread;
		thread.parent = parentThread;
		this.currentThread = thread;
		try {
			while (!thread.isSleeping()) {
				if (thread.instructionPtr == -1) {
					break;
				}
				else {
					this.instructions.get(thread.instructionPtr++).run(thread);
				}
			}
		}
		catch (final Exception exc) {
			throw new JassException(this, "runThreads() encountered exception", exc);
		}
		this.currentThread = parentThread;
	}

	private void runOneThreadLooop() {
		for (int threadIndex = this.threads.size() - 1; threadIndex >= 0; threadIndex--) {
			final JassThread thread = this.threads.get(threadIndex);
			this.currentThread = thread;
			try {
				while (!thread.isSleeping()) {
					if (thread.instructionPtr == -1) {
						this.threads.remove(threadIndex);
						this.yieldedCurrentThread = false;
						break;
					}
					else {
						this.instructions.get(thread.instructionPtr++).run(thread);
					}
				}
			}
			catch (final Exception exc) {
				throw new JassException(this, "runThreads() encountered exception", exc);
			}
			if (this.yieldedCurrentThread) {
				this.currentThread.setSleeping(false);
				this.newThreads.add(this.threads.remove(threadIndex));
			}
		}
		this.currentThread = null;
	}

	public JassThread getCurrentThread() {
		return this.currentThread;
	}

	public void yieldCurrentThread() {
		if (this.currentThread != null) {
			this.currentThread.setSleeping(true);
			this.yieldedCurrentThread = true;
		}
	}

	public RemovableTriggerEvent registerVariableEvent(final Trigger trigger, final String varName,
			final CLimitOp limitOp, final double doubleValue) {
		final VariableEvent variableEvent = new VariableEvent(trigger, limitOp, doubleValue);
		final GlobalScopeAssignable assignableGlobal = getAssignableGlobal(varName);
		if (assignableGlobal == null) {
			throw new IllegalArgumentException(
					"registerVariableEvent failed to find var with name: \"" + varName + "\"");
		}
		assignableGlobal.add(variableEvent);
		return new RemovableTriggerEvent() {
			@Override
			public void remove() {
				assignableGlobal.remove(variableEvent);
			}
		};
	}

	public void queueTrigger(final TriggerBooleanExpression filter, final TriggerExecutionScope filterScope,
			final Trigger trigger, final TriggerExecutionScope evaluateScope,
			final TriggerExecutionScope executeScope) {
		if (trigger.isEnabled()) {
			this.triggerQueue.add(new QueuedTrigger(filter, filterScope, trigger, evaluateScope, executeScope));
		}
	}

	public void queueFunction(final JassFunction function, final TriggerExecutionScope scope) {
		this.triggerQueue.add(new QueuedFunction(function, scope));
	}

	public void replayQueuedTriggers() {
		this.runningTriggerQueue.clear();
		this.runningTriggerQueue.addAll(this.triggerQueue);
		this.triggerQueue.clear();
		for (final QueuedCallback trigger : this.runningTriggerQueue) {
			trigger.fire(this);
		}
	}

	private static interface QueuedCallback {
		void fire(GlobalScope globalScope);
	}

	private static final class QueuedFunction implements QueuedCallback {
		private final JassFunction function;
		private final TriggerExecutionScope scope;

		public QueuedFunction(final JassFunction function, final TriggerExecutionScope scope) {
			this.function = function;
			this.scope = scope;
		}

		@Override
		public void fire(final GlobalScope globalScope) {
			this.function.call(Collections.<JassValue>emptyList(), globalScope, this.scope);
		}
	}

	private static final class QueuedTrigger implements QueuedCallback {
		private final TriggerBooleanExpression filter;
		private final TriggerExecutionScope filterScope;
		private final Trigger trigger;
		private final TriggerExecutionScope evaluateScope;
		private final TriggerExecutionScope executeScope;

		public QueuedTrigger(final TriggerBooleanExpression filter, final TriggerExecutionScope filterScope,
				final Trigger trigger, final TriggerExecutionScope evaluateScope,
				final TriggerExecutionScope executeScope) {
			this.filter = filter;
			this.filterScope = filterScope;
			this.trigger = trigger;
			this.evaluateScope = evaluateScope;
			this.executeScope = executeScope;
		}

		@Override
		public void fire(final GlobalScope globalScope) {
			if (this.filter != null) {
				if (!this.filter.evaluate(globalScope, this.filterScope)) {
					return;
				}
			}
			if (this.trigger.evaluate(globalScope, this.evaluateScope)) {
				this.trigger.execute(globalScope, this.executeScope);
			}
		}
	}
}
