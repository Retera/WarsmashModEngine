package com.etheller.interpreter.ast.value;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.struct.JassStructMemberType;

public class StructJassValue implements JassValue {
	private StructJassType type;
	private final JassValue superValue;
	private final List<JassValue> members;

	public StructJassValue(final StructJassType type, final JassValue superValue) {
		this.type = type;
		this.superValue = superValue;
		final List<JassStructMemberType> memberTypes = type.getMemberTypes();
		final int memberCount = memberTypes.size();
		this.members = new ArrayList<>(memberCount);
		for (int i = 0; i < memberCount; i++) {
			this.members.add(memberTypes.get(i).getType().getNullValue());
		}
	}

	public void allocateAsNewType(final StructJassType childType) {
		this.type = childType;
		final List<JassStructMemberType> memberTypes = childType.getMemberTypes();
		final int memberCount = memberTypes.size();
		for (int i = this.members.size(); i < memberCount; i++) {
			this.members.add(memberTypes.get(i).getType().getNullValue());
		}
	}

	public void setMember(final int memberIndex, final JassValue value) {
		this.members.set(memberIndex, value);
	}

	public JassValue getMember(final int memberIndex) {
		return this.members.get(memberIndex);
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
