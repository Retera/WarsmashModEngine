package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.util.CExtensibleHandle;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StructJassTypeVisitor;

public class ExtendHandleInstruction implements JassInstruction {
	private final StructJassType childType;

	public ExtendHandleInstruction(final StructJassType childType) {
		this.childType = childType;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue handleAsJassValue = thread.stackFrame.pop();
		final Object rawHandle = handleAsJassValue.visit(ObjectJassValueVisitor.getInstance());
		if (rawHandle instanceof CExtensibleHandle) {
			final StructJassType structJassType = this.childType.visit(StructJassTypeVisitor.getInstance());
			final StructJassValue structValue = new StructJassValue(structJassType, handleAsJassValue);
			((CExtensibleHandle) rawHandle).setStructValue(structValue);
			thread.stackFrame.push(structValue);
		}
		else {
			final StructJassType structJassType = this.childType.visit(StructJassTypeVisitor.getInstance());
			thread.stackFrame.push(new StructJassValue(structJassType, null));
		}
	}

}
