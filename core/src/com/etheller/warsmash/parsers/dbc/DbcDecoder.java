package com.etheller.warsmash.parsers.dbc;

import com.hiveworkshop.rms.util.BinaryReader;

public interface DbcDecoder<T> {
	T readRecord(BinaryReader reader);
}
