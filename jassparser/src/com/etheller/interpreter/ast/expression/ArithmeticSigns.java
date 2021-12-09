package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;

public enum ArithmeticSigns implements ArithmeticSign {
	ADD() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			throw new UnsupportedOperationException("Cannot perform numeric arithmetic on boolean");
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return new RealJassValue(left.getValue() + right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return new RealJassValue(left.getValue() + right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return new RealJassValue(left.getValue() + right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return new IntegerJassValue(left.getValue() + right.getValue());
		}

		@Override
		public JassValue apply(final String left, final String right) {
			return new StringJassValue(left + right);
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on handle");
		}

		@Override
		public JassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on code");
		}
	},
	SUBTRACT() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			throw new UnsupportedOperationException("Cannot perform numeric arithmetic on boolean");
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return new RealJassValue(left.getValue() - right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return new RealJassValue(left.getValue() - right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return new RealJassValue(left.getValue() - right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return new IntegerJassValue(left.getValue() - right.getValue());
		}

		@Override
		public JassValue apply(final String left, final String right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on string");
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on handle");
		}

		@Override
		public JassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on code");
		}
	},
	MULTIPLY() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			throw new UnsupportedOperationException("Cannot perform numeric arithmetic on boolean");
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return new RealJassValue(left.getValue() * right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return new RealJassValue(left.getValue() * right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return new RealJassValue(left.getValue() * right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return new IntegerJassValue(left.getValue() * right.getValue());
		}

		@Override
		public JassValue apply(final String left, final String right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on string");
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on handle");
		}

		@Override
		public JassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on code");
		}
	},
	DIVIDE() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			throw new UnsupportedOperationException("Cannot perform numeric arithmetic on boolean");
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return new RealJassValue(left.getValue() / right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return new RealJassValue(left.getValue() / right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return new RealJassValue(left.getValue() / right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return new IntegerJassValue(left.getValue() / right.getValue());
		}

		@Override
		public JassValue apply(final String left, final String right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on string");
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on handle");
		}

		@Override
		public JassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Cannot perform arithmetic on code");
		}
	},
	OR() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			return BooleanJassValue.of(left.getValue() || right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on number");
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on number");
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on number");
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on number");
		}

		@Override
		public JassValue apply(final String left, final String right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on string");
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on handle");
		}

		@Override
		public JassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on code");
		}
	},
	AND() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			return BooleanJassValue.of(left.getValue() && right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on number");
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on number");
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on number");
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on number");
		}

		@Override
		public JassValue apply(final String left, final String right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on string");
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on handle");
		}

		@Override
		public JassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Cannot perform boolean arithmetic on code");
		}
	},
	EQUALS() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			return BooleanJassValue.of(left.getValue() == right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(Math.abs(left.getValue() - right.getValue()) <= 0.00001);
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(Math.abs(left.getValue() - right.getValue()) <= 0.00001);
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(Math.abs(left.getValue() - right.getValue()) <= 0.00001);
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(Math.abs(left.getValue() - right.getValue()) <= 0.00001);
		}

		@Override
		public BooleanJassValue apply(final String left, final String right) {
			return BooleanJassValue.of(isEqual(left, right));
		}

		@Override
		public BooleanJassValue apply(final HandleJassValue left, final HandleJassValue right) {
			return BooleanJassValue.of(isEqual(left, right));
		}

		@Override
		public BooleanJassValue apply(final CodeJassValue left, final CodeJassValue right) {
			return BooleanJassValue.of(isEqual(left, right));
		}
	},
	NOT_EQUALS() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			return BooleanJassValue.of(left.getValue() != right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() != right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() != right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() != right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() != right.getValue());
		}

		@Override
		public JassValue apply(final String left, final String right) {
			return BooleanJassValue.of(!isEqual(left, right));
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			return BooleanJassValue.of(!isEqual(left, right));
		}

		@Override
		public BooleanJassValue apply(final CodeJassValue left, final CodeJassValue right) {
			return BooleanJassValue.of(!isEqual(left, right));
		}
	},
	LESS() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() < right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() < right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() < right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() < right.getValue());
		}

		@Override
		public JassValue apply(final String left, final String right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public BooleanJassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}
	},
	LESS_OR_EQUALS() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() <= right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() <= right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() <= right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() <= right.getValue());
		}

		@Override
		public JassValue apply(final String left, final String right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public BooleanJassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}
	},
	GREATER() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() > right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() > right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() > right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() > right.getValue());
		}

		@Override
		public JassValue apply(final String left, final String right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public BooleanJassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}
	},
	GREATER_OR_EQUALS() {
		@Override
		public JassValue apply(final BooleanJassValue left, final BooleanJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public JassValue apply(final RealJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() >= right.getValue());
		}

		@Override
		public JassValue apply(final RealJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() >= right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final RealJassValue right) {
			return BooleanJassValue.of(left.getValue() >= right.getValue());
		}

		@Override
		public JassValue apply(final IntegerJassValue left, final IntegerJassValue right) {
			return BooleanJassValue.of(left.getValue() >= right.getValue());
		}

		@Override
		public JassValue apply(final String left, final String right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public JassValue apply(final HandleJassValue left, final HandleJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}

		@Override
		public BooleanJassValue apply(final CodeJassValue left, final CodeJassValue right) {
			throw new UnsupportedOperationException("Invalid type for specified operator");
		}
	};

	private static boolean isEqual(final String left, final String right) {
		boolean equals;
		if (left == null) {
			if (right == null) {
				equals = true;
			}
			else {
				equals = false;
			}
		}
		else {
			equals = left.equals(right);
		}
		return equals;
	}

	private static boolean isEqual(final HandleJassValue left, final HandleJassValue right) {
		return (left.getJavaValue() == right.getJavaValue()) && (left.getType() == right.getType());
	}

	private static boolean isEqual(final CodeJassValue left, final CodeJassValue right) {
		if (left == null) {
			if (right == null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (right == null) {
				return false;
			}
			else {
				return (left.getValue() == right.getValue());
			}
		}
	}
}
