package com.etheller.interpreter.ast.expression.visitor;

import java.util.Map;

import com.etheller.interpreter.ast.definition.JassCodeDefinitionBlock;
import com.etheller.interpreter.ast.expression.AllocateAsNewTypeExpression;
import com.etheller.interpreter.ast.expression.ArithmeticJassExpression;
import com.etheller.interpreter.ast.expression.ArrayRefJassExpression;
import com.etheller.interpreter.ast.expression.ExtendHandleExpression;
import com.etheller.interpreter.ast.expression.FunctionCallJassExpression;
import com.etheller.interpreter.ast.expression.FunctionReferenceJassExpression;
import com.etheller.interpreter.ast.expression.JassExpressionVisitor;
import com.etheller.interpreter.ast.expression.JassNewExpression;
import com.etheller.interpreter.ast.expression.LiteralJassExpression;
import com.etheller.interpreter.ast.expression.MemberJassExpression;
import com.etheller.interpreter.ast.expression.MethodCallJassExpression;
import com.etheller.interpreter.ast.expression.MethodReferenceJassExpression;
import com.etheller.interpreter.ast.expression.NegateJassExpression;
import com.etheller.interpreter.ast.expression.NotJassExpression;
import com.etheller.interpreter.ast.expression.ParentlessMethodCallJassExpression;
import com.etheller.interpreter.ast.expression.ReferenceJassExpression;
import com.etheller.interpreter.ast.expression.TypeCastJassExpression;
import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.GlobalScopeAssignable;
import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.struct.JassStructMemberType;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassTypeInterface;
import com.etheller.interpreter.ast.value.visitor.ArrayPrimitiveTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StaticStructTypeJassTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.StaticStructTypeJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StructJassTypeVisitor;

public class JassTypeExpressionVisitor implements JassExpressionVisitor<JassType> {
	public static final JassTypeExpressionVisitor INSTANCE = new JassTypeExpressionVisitor();

	public static JassTypeExpressionVisitor getInstance() {
		return INSTANCE;
	}

	private Scope globalScope;
	private Map<String, JassType> nameToLocalType;
	private StructJassType enclosingType;

	public JassTypeExpressionVisitor reset(final Scope globalScope, final Map<String, JassType> nameToLocalType,
			final StructJassType enclosingType) {
		this.globalScope = globalScope;
		this.nameToLocalType = nameToLocalType;
		this.enclosingType = enclosingType;
		return this;
	}

	private JassType getTypeForIdentifier(final String identifier) {
		JassType localType = this.nameToLocalType.get(identifier);
		if ((localType == null) && GlobalScope.KEYWORD_THIS.equals(identifier)) {
			localType = this.nameToLocalType.get(GlobalScope.KEYNAME_THIS);
		}
		if (localType != null) {
			return localType;
		}
		else {
			final JassType thisStructType = this.nameToLocalType.get(GlobalScope.KEYNAME_THIS);
			if (thisStructType != null) {
				final StructJassType structJassType = thisStructType.visit(StructJassTypeVisitor.getInstance());
				final JassStructMemberType memberType = structJassType.tryGetMemberByName(identifier);
				if (memberType != null) {
					return memberType.getType();
				}
			}

			if (this.enclosingType != null) {
				final int staticStructValueGlobalId = this.globalScope.getGlobalId(this.enclosingType.getName());
				final GlobalScopeAssignable globalById = this.globalScope
						.getAssignableGlobalById(staticStructValueGlobalId);
				final StaticStructTypeJassValue structJassType = globalById.getValue()
						.visit(StaticStructTypeJassValueVisitor.getInstance());

				final JassStructMemberType memberType = structJassType.tryGetMemberByName(identifier);
				if (memberType != null) {
					return memberType.getType();
				}
			}

			final GlobalScopeAssignable assignableGlobal = this.globalScope.getAssignableGlobal(identifier);
			if (assignableGlobal != null) {
				return assignableGlobal.getType();
			}
			else {
				final JassValue constantValue = this.globalScope.getPreprocessorConstant(identifier);
				if (constantValue != null) {
					return constantValue.visit(JassTypeGettingValueVisitor.getInstance());
				}
			}
		}
		return JassType.NOTHING;
	}

	@Override
	public JassType visit(final ArithmeticJassExpression expression) {
		final JassType leftType = expression.getLeftExpression().accept(this);
		final JassType rightType = expression.getRightExpression().accept(this);
		return expression.getArithmeticSign().predictType(leftType, rightType);
	}

	@Override
	public JassType visit(final ArrayRefJassExpression expression) {
		final JassType arrayType = getTypeForIdentifier(expression.getIdentifier());
		final JassType primitiveType = arrayType.visit(ArrayPrimitiveTypeVisitor.getInstance());
		return primitiveType;
	}

	@Override
	public JassType visit(final FunctionCallJassExpression expression) {
		final UserJassFunction functionByName = this.globalScope
				.getFunctionDefinitionByName(expression.getFunctionName());
		return functionByName.getReturnType();
	}

	@Override
	public JassType visit(final MethodCallJassExpression expression) {
		final JassType jassTypeOfStruct = expression.getStructExpression().accept(this);
		final String functionName = expression.getFunctionName();
		return getReturnTypeOfMethodOfType(jassTypeOfStruct, functionName);
	}

	private JassType getReturnTypeOfMethodOfType(final JassType jassTypeOfStruct, final String functionName) {
		StructJassType structType = jassTypeOfStruct.visit(StructJassTypeVisitor.getInstance());
		if (structType == null) {
			final StaticStructTypeJassValue staticStruct = jassTypeOfStruct
					.visit(StaticStructTypeJassTypeVisitor.getInstance());
			structType = staticStruct.getStaticType();
		}
		final JassCodeDefinitionBlock methodBlock = structType.getMethodByName(functionName);
		return methodBlock.getReturnType().resolve(this.globalScope);
	}

	@Override
	public JassType visit(final ParentlessMethodCallJassExpression expression) {
		final JassType jassTypeOfStruct = this.nameToLocalType.get(GlobalScope.KEYNAME_THIS);
		final String functionName = expression.getFunctionName();
		if (jassTypeOfStruct != null) {
			return getReturnTypeOfMethodOfType(jassTypeOfStruct, functionName);
		}
		else {
			return getReturnTypeOfMethodOfType(this.enclosingType, functionName);
		}
	}

	@Override
	public JassType visit(final FunctionReferenceJassExpression expression) {
		return JassType.CODE;
	}

	@Override
	public JassType visit(final MethodReferenceJassExpression expression) {
		return JassType.CODE;
	}

	@Override
	public JassType visit(final LiteralJassExpression expression) {
		return expression.getValue().visit(JassTypeGettingValueVisitor.getInstance());
	}

	@Override
	public JassType visit(final NegateJassExpression expression) {
		return expression.getExpression().accept(this);
	}

	@Override
	public JassType visit(final NotJassExpression expression) {
		return expression.getExpression().accept(this);
	}

	@Override
	public JassType visit(final ReferenceJassExpression expression) {
		return getTypeForIdentifier(expression.getIdentifier());
	}

	@Override
	public JassType visit(final MemberJassExpression expression) {
		final JassType jassTypeOfStruct = expression.getStructExpression().accept(this);
		StructJassTypeInterface structType = jassTypeOfStruct.visit(StructJassTypeVisitor.getInstance());
		if (structType == null) {
			final StaticStructTypeJassValue staticStruct = jassTypeOfStruct
					.visit(StaticStructTypeJassTypeVisitor.getInstance());
			structType = staticStruct;
		}
		return structType.getMemberByName(expression.getIdentifier()).getType();
	}

	@Override
	public JassType visit(final JassNewExpression expression) {
		return expression.getType();
	}

	@Override
	public JassType visit(final AllocateAsNewTypeExpression expression) {
		return expression.getType();
	}

	@Override
	public JassType visit(final ExtendHandleExpression expression) {
		return expression.getType();
	}

	@Override
	public JassType visit(final TypeCastJassExpression expression) {
		return expression.getCastToType();
	}
}
