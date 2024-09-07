package com.etheller.interpreter.ast.value;

import java.util.List;

import com.etheller.interpreter.ast.struct.JassStructMemberType;

public class StructJassValue extends BaseStructJassValue implements JassValue {
	private StructJassType type;
	private final JassValue superValue;

	public StructJassValue(final StructJassType type, final JassValue superValue) {
		super(type.getMemberTypes());
		this.type = type;
		this.superValue = superValue;
	}

	public void allocateAsNewType(final StructJassType childType) {
		this.type = childType;
		final List<JassStructMemberType> memberTypes = childType.getMemberTypes();
		allocateAsNewType(memberTypes);
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	public StructJassType getType() {
		return this.type;
	}

	public JassValue getSuperValue() {
		return this.superValue;
	}

}
