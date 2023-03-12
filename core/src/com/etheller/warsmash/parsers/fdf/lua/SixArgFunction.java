package com.etheller.warsmash.parsers.fdf.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * Abstract base class for Java function implementations that take two arguments
 * and return one value.
 * <p>
 * Subclasses need only implement
 * {@link LuaValue#call(LuaValue,LuaValue,LuaValue)} to complete this class,
 * simplifying development. All other uses of {@link #call()},
 * {@link #invoke(Varargs)},etc, are routed through this method by this class,
 * dropping or extending arguments with {@code nil} values as required.
 * <p>
 * If more or less than three arguments are required, or variable argument or
 * variable return values, then use one of the related function
 * {@link ZeroArgFunction}, {@link OneArgFunction}, {@link TwoArgFunction}, or
 * {@link VarArgFunction}.
 * <p>
 * See {@link LibFunction} for more information on implementation libraries and
 * library functions.
 *
 * @see #call(LuaValue,LuaValue,LuaValue)
 * @see LibFunction
 * @see ZeroArgFunction
 * @see OneArgFunction
 * @see TwoArgFunction
 * @see VarArgFunction
 */
abstract public class SixArgFunction extends LibFunction {

	abstract public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4, LuaValue arg5,
			LuaValue arg6);

	/** Default constructor */
	public SixArgFunction() {
	}

	@Override
	public final LuaValue call() {
		return call(NIL, NIL, NIL, NIL, NIL, NIL);
	}

	@Override
	public final LuaValue call(final LuaValue arg) {
		return call(arg, NIL, NIL, NIL, NIL, NIL);
	}

	@Override
	public LuaValue call(final LuaValue arg1, final LuaValue arg2) {
		return call(arg1, arg2, NIL, NIL, NIL, NIL);
	}

	@Override
	public LuaValue call(final LuaValue arg1, final LuaValue arg2, final LuaValue arg3) {
		return call(arg1, arg2, arg3, NIL, NIL, NIL);
	}

	@Override
	public LuaValue call(final LuaValue arg1, final LuaValue arg2, final LuaValue arg3, final LuaValue arg4) {
		return call(arg1, arg2, arg3, arg4, NIL, NIL);
	}

	@Override
	public Varargs invoke(final Varargs varargs) {
		return call(varargs.arg1(), varargs.arg(2), varargs.arg(3), varargs.arg(4), varargs.arg(5), varargs.arg(6));
	}

}