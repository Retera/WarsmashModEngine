package com.etheller.warsmash.parsers.dbc;

import com.hiveworkshop.rms.util.BinaryReader;

public interface DbcDecoder {
	long getRecordSize();

	DbcRecord readRecord(BinaryReader reader);
}
