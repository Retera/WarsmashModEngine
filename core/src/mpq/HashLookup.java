package mpq;

import java.io.Serializable;

import mpq.util.Cryption;

public class HashLookup implements Serializable {
	private static final long serialVersionUID = -731458056988218435L;
	
	public final byte[] lookup;
	public final long hash;
	public final int index;
	
	public HashLookup(String path){
		// *** convert string to 8 bit ascii
		byte[] raw = Cryption.stringToHashable(path);
		
		// *** generate hashtable lookup arguments
		hash = Cryption.HashString(raw, Cryption.MPQ_HASH_NAME_A) & 0xFFFFFFFFL | (long)Cryption.HashString(raw, Cryption.MPQ_HASH_NAME_B)<<32;
		index = Cryption.HashString(raw, Cryption.MPQ_HASH_TABLE_OFFSET);
		
		// *** find file name		
		int index = 0;
		for( int i = raw.length ; --i >= 0 ; ){
			if( raw[i] == (byte) '\\' || raw[i] == (byte) '/' ){
				index = i + 1;
				break;
			}
		}
		
		// *** save raw ascii file name in-case file is encrypted
		lookup = new byte[raw.length - index];
		System.arraycopy(raw, index, lookup, 0, lookup.length);		
	}
}
