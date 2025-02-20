package com.etheller.interpreter.ast.scope;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.debug.JassStackElement;
import com.etheller.interpreter.ast.definition.JassImplementModuleDefinition;
import com.etheller.interpreter.ast.definition.JassMethodDefinitionBlock;
import com.etheller.interpreter.ast.definition.JassModuleDefinitionBlock;
import com.etheller.interpreter.ast.execution.JassStackFrame;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.execution.instruction.BeginFunctionInstruction;
import com.etheller.interpreter.ast.execution.instruction.BranchInstruction;
import com.etheller.interpreter.ast.execution.instruction.InstructionAppendingJassStatementVisitor;
import com.etheller.interpreter.ast.execution.instruction.JassInstruction;
import com.etheller.interpreter.ast.execution.instruction.PushLiteralInstruction;
import com.etheller.interpreter.ast.execution.instruction.ReturnInstruction;
import com.etheller.interpreter.ast.expression.LiteralJassExpression;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.function.NativeJassFunction;
import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.scope.variableevent.CLimitOp;
import com.etheller.interpreter.ast.scope.variableevent.VariableEvent;
import com.etheller.interpreter.ast.statement.JassSetMemberStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.struct.JassStructMemberType;
import com.etheller.interpreter.ast.struct.JassStructMemberTypeDefinition;
import com.etheller.interpreter.ast.type.JassTypeToken;
import com.etheller.interpreter.ast.util.JassLog;
import com.etheller.interpreter.ast.util.JassSettings;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.visitor.ArrayPrimitiveTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.HandleJassTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.HandleTypeSuperTypeLoadingVisitor;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

public final class GlobalScope {
	public static final String INIT_GLOBALS_AUTOGEN_FXN_NAME = "{init-globals}";
	public static final String KEYWORD_THIS = "this";
	public static final String KEYWORD_THISTYPE = "thistype";
	public static final String KEYWORD_SCOPE_PREFIX = "SCOPE_PREFIX";
	public static final String KEYWORD_SCOPE_PRIVATE = "SCOPE_PRIVATE";
	public static final String KEYNAME_THIS = "{this}";
	private final List<GlobalScopeAssignable> indexedGlobals = new ArrayList<GlobalScopeAssignable>();
	private final List<JassInstruction> instructions = new ArrayList<JassInstruction>();
	private final Map<String, Integer> globals = new HashMap<>();
	private final Map<String, GlobalScopeAssignable> fastGlobals = new HashMap<>();
	private final Map<String, UserJassFunction> functionNameToDefinition = new HashMap<>();
	private final Map<String, Integer> functionNameToInstructionPtr = new HashMap<>();
	private final Map<String, Integer> functionNameToNativeId = new HashMap<>();
	private final List<NativeJassFunction> indexedNativeFunctions = new ArrayList<>();
	private final Map<String, JassType> types = new HashMap<>();
	private final Map<String, JassModuleDefinitionBlock> modules = new HashMap<>();
	private final HandleTypeSuperTypeLoadingVisitor handleTypeSuperTypeLoadingVisitor = new HandleTypeSuperTypeLoadingVisitor();
	private final ArrayDeque<QueuedCallback> triggerQueue = new ArrayDeque<>();
	private final ArrayDeque<QueuedCallback> runningTriggerQueue = new ArrayDeque<>();
	private final List<JassThread> threads = new ArrayList<>();
	private final List<JassThread> newThreads = new ArrayList<>();
	private JassThread currentThread;
	private boolean yieldedCurrentThread = false;
	private int lastGlobalsBlockEndInstructionPtr = -1;

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
		registerPrimitiveType(JassType.ANY_STRUCT_TYPE);
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
		final GlobalScopeAssignable assignable = new GlobalScopeAssignable(type, this);
		assignable.setValue(type.getNullValue());
		putGlobal(name, assignable);
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

	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final NativeJassFunction function) {
		final int nativeId = this.indexedNativeFunctions.size();
		this.functionNameToNativeId.put(name, nativeId);
		this.indexedNativeFunctions.add(function);
	}

	public int defineMethod(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction function, final StructJassType type, final Scope scope) {
		final int instructionPtr = this.instructions.size();

		final List<JassStatement> statements = function.getStatements();
		this.instructions.add(new BeginFunctionInstruction(lineNo, sourceFile, name));
		final InstructionAppendingJassStatementVisitor visitor = new InstructionAppendingJassStatementVisitor(
				this.instructions, scope, function.getParameters());
		for (final JassStatement statement : statements) {
			statement.accept(visitor);
		}
		this.instructions.add(new PushLiteralInstruction(JassType.NOTHING.getNullValue()));
		this.instructions.add(new ReturnInstruction());

		return instructionPtr;
	}

	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction function, final Scope scope) {
		this.functionNameToDefinition.put(name, function);
		this.functionNameToInstructionPtr.put(name, this.instructions.size());
		writeFunctionInstructions(lineNo, sourceFile, name, function, scope);
	}

	private void writeFunctionInstructions(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction function, final Scope scope) {
		final List<JassStatement> statements = function.getStatements();
		this.instructions.add(new BeginFunctionInstruction(lineNo, sourceFile, name));
		final InstructionAppendingJassStatementVisitor visitor = new InstructionAppendingJassStatementVisitor(
				this.instructions, scope, function.getParameters());
		for (final JassStatement statement : statements) {
			statement.accept(visitor);
		}
		this.instructions.add(new PushLiteralInstruction(JassType.NOTHING.getNullValue()));
		this.instructions.add(new ReturnInstruction());
	}

	public void defineGlobals(final int lineNo, final String sourceFile, final List<JassStatement> globalStatements,
			final Scope scope) {
		innerBeginDefiningGlobals(lineNo, sourceFile);
		final InstructionAppendingJassStatementVisitor visitor = new InstructionAppendingJassStatementVisitor(
				this.instructions, scope, Collections.emptyList());
		for (final JassStatement statement : globalStatements) {
			statement.accept(visitor);
		}
		endDefiningGlobals();
		this.instructions.add(new PushLiteralInstruction(JassType.NOTHING.getNullValue()));
		this.instructions.add(new ReturnInstruction());
	}

	public void defineStruct(final EnumSet<JassQualifier> qualifiers, final String structName,
			final JassTypeToken structSuperTypeToken, final List<JassStructMemberTypeDefinition> memberTypeDefinitions,
			final List<JassImplementModuleDefinition> implementModuleDefinitions,
			final List<JassMethodDefinitionBlock> methodDefinitions, final Scope scope) {
		try {
			final StructJassType structJassType = new StructJassType(structSuperTypeToken.resolve(scope), structName);
			this.types.put(structName, structJassType);

			final List<JassStructMemberType> memberTypes = new ArrayList<>();
			final List<JassStructMemberType> staticMemberTypes = new ArrayList<>();
			for (final JassStructMemberTypeDefinition memberTypeDefinition : memberTypeDefinitions) {
				final JassType resolvedType = memberTypeDefinition.getType().resolve(scope);
				if (memberTypeDefinition.getQualifiers().contains(JassQualifier.STATIC)) {
					staticMemberTypes.add(new JassStructMemberType(memberTypeDefinition.getQualifiers(), resolvedType,
							memberTypeDefinition.getId(), memberTypeDefinition.getDefaultValueExpression()));
				}
				else {
					memberTypes.add(new JassStructMemberType(memberTypeDefinition.getQualifiers(), resolvedType,
							memberTypeDefinition.getId(), memberTypeDefinition.getDefaultValueExpression()));
				}
			}
			final StaticStructTypeJassValue staticTypeInfo = new StaticStructTypeJassValue(structJassType,
					staticMemberTypes);
			createGlobal(structName, staticTypeInfo);
			final Set<String> modulesImplementedByStruct = new HashSet<>();
			final List<JassMethodDefinitionBlock> implementedMethodDefinitions = new ArrayList<>(methodDefinitions);
			implementModules(modulesImplementedByStruct, implementModuleDefinitions, memberTypes,
					implementedMethodDefinitions, scope, structName);
			structJassType.buildMethodTable(scope, implementedMethodDefinitions, memberTypes);

			if (!staticMemberTypes.isEmpty()) {
				final List<JassStatement> staticMemberInitStatements = new ArrayList<>();
				for (final JassStructMemberType member : staticMemberTypes) {
					staticMemberInitStatements.add(new JassSetMemberStatement(new LiteralJassExpression(staticTypeInfo),
							member.getId(), member.getDefaultValueExpression()));
				}
				defineGlobals(-1, "<static-member-init>", staticMemberInitStatements, scope);
			}
		}
		catch (final RuntimeException exc) {
			throw new JassException(this, "Failed to define struct: " + structName, exc);
		}
	}

	private void implementModules(final Set<String> modulesImplementedByStruct,
			final List<JassImplementModuleDefinition> implementModuleDefinitions,
			final List<JassStructMemberType> memberTypes,
			final List<JassMethodDefinitionBlock> implementedMethodDefinitions, final Scope scope,
			final String structName) {
		for (final JassImplementModuleDefinition implementModuleDefinition : implementModuleDefinitions) {
			final String moduleName = implementModuleDefinition.getModuleName();
			if (modulesImplementedByStruct.add(moduleName)) {
				final JassModuleDefinitionBlock module = scope.forEachPossibleResolvedIdentifier(moduleName,
						(identifier) -> {
							return this.modules.get(identifier);
						});
				if (module != null) {
					implementModules(modulesImplementedByStruct, module.getImplementModuleDefinitions(), memberTypes,
							implementedMethodDefinitions, scope, structName);
					for (final JassStructMemberTypeDefinition memberTypeDefinition : module
							.getMemberTypeDefinitions()) {
						final JassType resolvedType = memberTypeDefinition.getType().resolve(scope);
						memberTypes.add(new JassStructMemberType(memberTypeDefinition.getQualifiers(), resolvedType,
								memberTypeDefinition.getId(), memberTypeDefinition.getDefaultValueExpression()));
					}
					implementedMethodDefinitions.addAll(module.getMethodDefinitions());
				}
				else {
					if (!implementModuleDefinition.isOptional()) {
						throw new IllegalStateException(
								"missing module: \"" + moduleName + "\" for struct \"" + structName + "\"");
					}
				}
			}
		}
	}

	public void defineModule(final String name, final JassModuleDefinitionBlock moduleDefinitionBlock) {
		this.modules.put(name, moduleDefinitionBlock);
	}

	public void endDefiningGlobals() {
		this.lastGlobalsBlockEndInstructionPtr = this.instructions.size();
	}

	public void resetGlobalInitialization() {
		this.lastGlobalsBlockEndInstructionPtr = -1;
	}

	public void innerBeginDefiningGlobals(final int lineNo, final String sourceFile) {
		final int newSectionStart = this.instructions.size();
		if (this.lastGlobalsBlockEndInstructionPtr != -1) {
			this.instructions.set(this.lastGlobalsBlockEndInstructionPtr, new BranchInstruction(newSectionStart));
		}
		else {
			this.functionNameToInstructionPtr.put(INIT_GLOBALS_AUTOGEN_FXN_NAME, this.instructions.size());
		}
		this.instructions.add(new BeginFunctionInstruction(lineNo, sourceFile, INIT_GLOBALS_AUTOGEN_FXN_NAME));
	}

	public UserJassFunction getFunctionDefinitionByName(final String name) {
		return this.functionNameToDefinition.get(name);
	}

	public Integer getUserFunctionInstructionPtr(final String name) {
		return this.functionNameToInstructionPtr.get(name);
	}

	public Integer getNativeId(final String name) {
		return this.functionNameToNativeId.get(name);
	}

	public NativeJassFunction getNativeById(final int id) {
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

	public JassType parseType(final String text, final LibraryScopeTree libraryScopeTree) {
		final JassType typeFound = libraryScopeTree.forEachPossibleResolvedIdentifier(text, (possibleId) -> {
			return this.types.get(possibleId);
		});
		if (typeFound == null) {
			throw new RuntimeException("Unknown type: " + text);
		}
		return typeFound;
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

	public JassType parseArrayType(final String primitiveTypeName, final LibraryScopeTree libraryScopeTree) {
		final String qualifiedPrimitiveType = libraryScopeTree.forEachPossibleResolvedIdentifier(primitiveTypeName,
				(possibleId) -> {
					if (this.types.containsKey(possibleId)) {
						return possibleId;
					}
					return null;
				});
		if (qualifiedPrimitiveType == null) {
			throw new RuntimeException("Unknown type for array: " + primitiveTypeName);
		}
		final String arrayTypeName = qualifiedPrimitiveType + " array";
		JassType arrayType = this.types.get(arrayTypeName);
		if (arrayType == null) {
			arrayType = new ArrayJassType(parseType(qualifiedPrimitiveType));
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
		final JassThread newThread = createThread(codeValue.getUserFunctionInstructionPtr());
		codeValue.initStack(newThread.stackFrame);
		return newThread;
	}

	public JassThread createThread(final int instructionPtr) {
		return createThread(instructionPtr, TriggerExecutionScope.EMPTY);
	}

	public void queueThread(final JassThread thread) {
		this.newThreads.add(thread);
	}

	public JassThread createThread(final CodeJassValue codeValue, final TriggerExecutionScope triggerScope) {
		final JassThread newThread = createThread(codeValue.getUserFunctionInstructionPtr(), triggerScope);
		codeValue.initStack(newThread.stackFrame);
		return newThread;
	}

	public JassThread createThread(final int instructionPtr, final TriggerExecutionScope triggerScope) {
		final JassStackFrame jassStackFrame = new JassStackFrame();
		jassStackFrame.returnAddressInstructionPtr = -1;
		final JassThread jassThread = new JassThread(jassStackFrame, this, triggerScope, instructionPtr);
		return jassThread;
	}

	public JassThread createThreadCapturingReturnValue(final CodeJassValue codeValue,
			final TriggerExecutionScope triggerScope) {
		final JassThread jassThread = createThreadCapturingReturnValue(codeValue.getUserFunctionInstructionPtr(),
				Collections.emptyList(), triggerScope);
		codeValue.initStack(jassThread.stackFrame);
		return jassThread;
	}

	public JassThread createThreadCapturingReturnValue(final int instructionPtr, final List<JassValue> arguments,
			final TriggerExecutionScope triggerScope) {
		final JassThread jassThread = createThread(instructionPtr, arguments, triggerScope);
		jassThread.stackFrame.stackBase = new JassStackFrame();
		return jassThread;
	}

	public JassThread createThread(final int instructionPtr, final List<JassValue> arguments,
			final TriggerExecutionScope triggerExecutionScope) {
		final JassStackFrame jassStackFrame = new JassStackFrame(arguments.size());
		jassStackFrame.returnAddressInstructionPtr = -1;
		for (int i = 0; i < arguments.size(); i++) {
			final JassValue argument = arguments.get(i);
			jassStackFrame.push(argument);
		}
		final JassThread jassThread = new JassThread(jassStackFrame, this, triggerExecutionScope, instructionPtr);
		return jassThread;
	}

	public JassThread createThread(final String functionName, final List<JassValue> arguments,
			final TriggerExecutionScope triggerExecutionScope) {
		final UserJassFunction userJassFunction = this.functionNameToDefinition.get(functionName);
		if (userJassFunction != null) {
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
			throw new IllegalStateException("Unable to spawn jass thread for function name: " + functionName);
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
			Collections.reverse(this.newThreads);
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
			final JassException jassException = new JassException(this, "runThreads() encountered exception", exc);
			JassLog.report(jassException);
			throw jassException;
		}
		this.currentThread = parentThread;
	}

	public JassValue runThreadUntilCompletionAndReadReturnValue(final JassThread thread,
			final String contextNameForError, final JassValue defaultValue) {
		JassValue jassReturnValue;
		try {
			runThreadUntilCompletion(thread);
			if (thread.instructionPtr == -1) {
				jassReturnValue = thread.stackFrame.getLast(0);
			}
			else {
				if (!JassSettings.CONTINUE_EXECUTING_ON_ERROR) {
					throw new IllegalStateException("The " + contextNameForError
							+ " created a thread that did not immediately return; did you call TriggerSleepAction where it is unsupported??");
				}
				else {
					return defaultValue;
				}
			}
		}
		catch (final Exception e) {
			throw new JassException(this, "Exception during " + contextNameForError, e);
		}
		return jassReturnValue;
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
				final JassException jassException = new JassException(this, "runThreads() encountered exception", exc);
				JassLog.report(jassException);
				throw jassException;
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
		return new RemovableTriggerEvent(trigger) {
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
			if (filter != null) {
				if (!filter.evaluate(this, filterScope)) {
					return;
				}
			}
			if (trigger.evaluate(this, evaluateScope)) {
				trigger.execute(this, executeScope);
			}
		}
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

	public HandleJassType getHandleType(final String typeName) {
		// TODO avoid cast
		return (HandleJassType) parseType(typeName);
	}

}
