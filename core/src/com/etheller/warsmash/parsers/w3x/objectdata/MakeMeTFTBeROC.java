package com.etheller.warsmash.parsers.w3x.objectdata;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;
import com.etheller.warsmash.datasources.MpqDataSourceDescriptor;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataOutputStream;

public class MakeMeTFTBeROC {

	public static void main(final String[] args) {

		try {
			final MpqDataSourceDescriptor reignOfChaosData = new MpqDataSourceDescriptor(
					"E:\\Games\\Warcraft III Patch 1.14\\war3.mpq");
			final Warcraft3MapObjectData reignOfChaosUnitData = Warcraft3MapObjectData
					.load(reignOfChaosData.createDataSource(), true);

			final FolderDataSourceDescriptor tftDesc1 = new FolderDataSourceDescriptor(
					"D:\\NEEDS_ORGANIZING\\Reforged Beta 13991\\war3.w3mod");
			final FolderDataSourceDescriptor tftDesc2 = new FolderDataSourceDescriptor(
					"D:\\NEEDS_ORGANIZING\\Reforged Beta 13991\\war3.w3mod\\_balance\\custom_v1.w3mod");
			final FolderDataSourceDescriptor tftDesc3 = new FolderDataSourceDescriptor(
					"D:\\NEEDS_ORGANIZING\\Reforged Beta 13991\\war3.w3mod\\_locales\\enus.w3mod");
			final CompoundDataSourceDescriptor frozenThroneData = new CompoundDataSourceDescriptor(
					Arrays.asList(tftDesc1, tftDesc2, tftDesc3));

			final Warcraft3MapObjectData frozenThroneUnitData = Warcraft3MapObjectData
					.load(frozenThroneData.createDataSource(), true);
			for (final War3ID unitId : reignOfChaosUnitData.getUnits().keySet()) {
				final MutableGameObject reignOfChaosUnit = reignOfChaosUnitData.getUnits().get(unitId);
				final MutableGameObject frozenThroneEquivalentUnit = frozenThroneUnitData.getUnits().get(unitId);
				if (frozenThroneEquivalentUnit == null) {
					System.err.println("No TFT equivalent for: " + reignOfChaosUnit.getName());
					continue;
				}
				final ObjectData metaDataSlk = reignOfChaosUnitData.getUnits().getSourceSLKMetaData();
				for (final String fieldTypeId : metaDataSlk.keySet()) {
					final War3ID fieldTypeIdCode = War3ID.fromString(fieldTypeId);
					final GameObject unitFieldInformation = metaDataSlk.get(fieldTypeId);
					if (unitFieldInformation.getFieldValue("useItem") == 1) {
						continue;
					}
					final String fieldType = unitFieldInformation.getField("type");
					switch (fieldType) {
					case "int":
						frozenThroneEquivalentUnit.setField(fieldTypeIdCode, 0,
								reignOfChaosUnit.getFieldAsInteger(fieldTypeIdCode, 0));
						break;
					case "real":
					case "unreal":
						frozenThroneEquivalentUnit.setField(fieldTypeIdCode, 0,
								reignOfChaosUnit.getFieldAsFloat(fieldTypeIdCode, 0));
						break;
					case "bool":
						frozenThroneEquivalentUnit.setField(fieldTypeIdCode, 0,
								reignOfChaosUnit.getFieldAsBoolean(fieldTypeIdCode, 0));
						break;
					case "string":
					case "abilityList":
					case "stringList":
					case "soundLabel":
					case "unitList":
					case "itemList":
					case "techList":
					case "intList":
					case "model":
					case "char":
					case "icon":
						frozenThroneEquivalentUnit.setField(fieldTypeIdCode, 0,
								reignOfChaosUnit.getFieldAsString(fieldTypeIdCode, 0));
						break;
					default: // treat as string
						break;

					}
				}
			}

			try (LittleEndianDataOutputStream outputStream = new LittleEndianDataOutputStream(
					new FileOutputStream("C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Data\\roc.w3u"))) {
				frozenThroneUnitData.getUnits().getEditorData().save(outputStream, false);
			}

		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
