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
import com.etheller.interpreter.ast.debug.JassStackElement;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.scope.variableevent.CLimitOp;
import com.etheller.interpreter.ast.scope.variableevent.VariableEvent;
import com.etheller.interpreter.ast.util.JassSettings;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.visitor.ArrayPrimitiveTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.HandleJassTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.HandleTypeSuperTypeLoadingVisitor;

public final class GlobalScope {
	private final Map<String, GlobalScopeAssignable> globals = new HashMap<>();
	private final Map<String, JassFunction> functions = new HashMap<>();
	private final Map<String, JassType> types = new HashMap<>();
	private final HandleTypeSuperTypeLoadingVisitor handleTypeSuperTypeLoadingVisitor = new HandleTypeSuperTypeLoadingVisitor();
	private final ArrayDeque<QueuedCallback> triggerQueue = new ArrayDeque<>();
	private final ArrayDeque<QueuedCallback> runningTriggerQueue = new ArrayDeque<>();

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

	public void createGlobalArray(final String name, final JassType type) {
		final GlobalScopeAssignable assignable = new GlobalScopeAssignable(type, this);
		assignable.setValue(new ArrayJassValue((ArrayJassType) type)); // TODO less bad code
		this.globals.put(name, assignable);
	}

	public void createGlobal(final String name, final JassType type) {
		this.globals.put(name, new GlobalScopeAssignable(type, this));
	}

	public void createGlobal(final String name, final JassType type, final JassValue value) {
		final GlobalScopeAssignable assignable = new GlobalScopeAssignable(type, this);
		try {
			assignable.setValue(value);
		}
		catch (final Exception exc) {
			throw new RuntimeException("Global initialization failed for name: " + name, exc);
		}
		this.globals.put(name, assignable);
	}

	public void setGlobal(final String name, final JassValue value) {
		final GlobalScopeAssignable assignable = this.globals.get(name);
		if (assignable == null) {
			throw new RuntimeException("Undefined global: " + name);
		}
		if (assignable.getType().visit(ArrayPrimitiveTypeVisitor.getInstance()) != null) {
			throw new RuntimeException("Unable to assign array variable: " + name);
		}
		assignable.setValue(value);
	}

	public JassValue getGlobal(final String name) {
		final Assignable global = this.globals.get(name);
		if (global == null) {
			throw new RuntimeException("Undefined global: " + name);
		}
		return global.getValue();
	}

	public GlobalScopeAssignable getAssignableGlobal(final String name) {
		return this.globals.get(name);
	}

	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final JassFunction function) {
		if (JassSettings.DEBUG) {
			this.functions.put(name, new DebuggingJassFunction(lineNo, sourceFile, name, function));
		}
		else {
			this.functions.put(name, function);
		}
	}

	public JassFunction getFunctionByName(final String name) {
		return this.functions.get(name);
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
		this.triggerQueue.add(new QueuedTrigger(filter, filterScope, trigger, evaluateScope, executeScope));
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
