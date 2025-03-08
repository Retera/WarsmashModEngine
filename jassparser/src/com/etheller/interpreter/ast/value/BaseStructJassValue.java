package com.etheller.interpreter.ast.value;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.struct.JassStructMemberType;

public class BaseStructJassValue {
	private final List<JassValue> members;

	public BaseStructJassValue(final List<JassStructMemberType> memberTypes) {
		final int memberCount = memberTypes.size();
		this.members = new ArrayList<>(memberCount);
		for (int i = 0; i < memberCount; i++) {
			this.members.add(memberTypes.get(i).getType().getNullValue());
		}
	}

	public void allocateAsNewType(final List<JassStructMemberType> memberTypes) {
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

}
