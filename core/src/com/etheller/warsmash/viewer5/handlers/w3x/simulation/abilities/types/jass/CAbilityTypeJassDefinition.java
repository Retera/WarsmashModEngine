package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass.CAbilityJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class CAbilityTypeJassDefinition implements CAbilityTypeDefinition {
	private final GlobalScope jassGlobalScope;
	private final StaticStructTypeJassValue abilityStructType;

	public CAbilityTypeJassDefinition(final GlobalScope jassGlobalScope,
			final StaticStructTypeJassValue abilityStructType) {
		this.jassGlobalScope = jassGlobalScope;
		this.abilityStructType = abilityStructType;
	}

	@Override
	public CAbilityType<?> createAbilityType(final War3ID alias, final GameObject abilityEditorData) {
		final List<CAbilityTypeLevelData> emptyLevelDatas = new ArrayList<>();
		final int levels = abilityEditorData.getFieldAsInteger(AbilityFields.LEVELS, 0);
		for (int i = 0; i < levels; i++) {
			// NOTE: for now, size of this list is getting used for ability's max level for
			// heroes
			emptyLevelDatas.add(null);
		}
		return new CAbilityType<CAbilityTypeLevelData>(alias,
				abilityEditorData.getFieldAsWar3ID(AbilityFields.CODE, -1), emptyLevelDatas) {
			private final IntegerJassValue jassAlias = IntegerJassValue.of(alias.getValue());
			final List<JassValue> constructorArguments = new ArrayList<>();
			{
				this.constructorArguments.add(this.jassAlias);
			}

			@Override
			public CAbility createAbility(final int handleId) {
				final StructJassType staticType = CAbilityTypeJassDefinition.this.abilityStructType.getStaticType();
				final Integer createInstructionPtr = staticType.getMethodTable()
						.get(staticType.getMethodTableIndex("create"));
				final JassThread thread = CAbilityTypeJassDefinition.this.jassGlobalScope
						.createThreadCapturingReturnValue(createInstructionPtr, this.constructorArguments,
								TriggerExecutionScope.EMPTY);
				final JassValue jassReturnValue = CAbilityTypeJassDefinition.this.jassGlobalScope
						.runThreadUntilCompletionAndReadReturnValue(thread, "createAbility", null);
				if (jassReturnValue == null) {
					throw new IllegalStateException(
							"A jass based ability did not return the newly created ability in its constructor!");
				}
				final CAbilityJass jassAbility = jassReturnValue.visit(ObjectJassValueVisitor.getInstance());
				jassAbility.setCode(getCode());
				jassAbility.populate(abilityEditorData, 1);
				return jassAbility;
			}

			@Override
			public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility,
					final int level) {
				existingAbility.setLevel(game, unit, level);
				((CAbilityJass) existingAbility).populate(abilityEditorData, level);
			}
		};
	}

	public GlobalScope getJassGlobalScope() {
		return this.jassGlobalScope;
	}
}
