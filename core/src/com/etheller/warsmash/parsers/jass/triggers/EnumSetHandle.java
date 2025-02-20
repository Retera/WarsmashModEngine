package com.etheller.warsmash.parsers.jass.triggers;

import java.util.EnumSet;

import com.etheller.interpreter.ast.util.CExtensibleHandleAbstract;
import com.etheller.interpreter.ast.util.CHandle;

/**
 * This describes an enum set to the jass universe, so that it has a handle ID
 * and the extensible property. It's pretty stupid and bad for performance that
 * this will end up allocating a "JassStructValue" who points to a "parent" that
 * is a "HandleJassValue" that combines a type and a value, whose value is an
 * "EnumSetHandle", who then actually has a member "enumSet" which the Java
 * implementation. It shows we have created too much superfluous garbage in the
 * hopes of added language features. Maybe later someone will clean this up.
 *
 * @param <E>
 */
public class EnumSetHandle<E extends Enum<E>> extends CExtensibleHandleAbstract implements CHandle {
	private final int handleId;
	private final EnumSet<E> enumSet;

	public EnumSetHandle(final int handleId, final EnumSet<E> enumSet) {
		this.handleId = handleId;
		this.enumSet = enumSet;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

	public EnumSet<E> getEnumSet() {
		return this.enumSet;
	}
}
