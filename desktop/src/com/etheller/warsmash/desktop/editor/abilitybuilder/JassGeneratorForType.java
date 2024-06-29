package com.etheller.warsmash.desktop.editor.abilitybuilder;

import java.lang.reflect.Field;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class JassGeneratorForType {
	public static void main(final String[] args) {
		for (final CTargetType type : CTargetType.values()) {
			System.out.println(String.format("    constant targettype TARGET_TYPE_%-34s= ConvertTargetType(%d)",
					type.name(), type.ordinal()));
		}
		for (final TextTagConfigType type : TextTagConfigType.values()) {
			System.out.println(String.format(
					"    constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_%-34s= ConvertTextTagConfigType(%d)",
					type.name(), type.ordinal()));
		}

		{
			final Field[] fields = ABLocalStoreKeys.class.getDeclaredFields();
			for (final Field field : fields) {
				try {
					System.out.println(String.format("    constant string AB_LOCAL_STORE_KEY_%-34s= \"%s\"",
							field.getName(), field.get(null)));
				}
				catch (final IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (final IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		{
			final Field[] fields = AbilityFields.class.getDeclaredFields();
			for (final Field field : fields) {
				try {
					System.out.println(String.format("    constant string ABILITY_FIELD_%-34s= \"%s\"", field.getName(),
							field.get(null)));
				}
				catch (final IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (final IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
