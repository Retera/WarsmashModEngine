package mpq;

import mpq.data.HashTableEntry;

public class HashTable {
	public static final int BLOCK_EMPTY_ALWAYS = 0xFFFFFFFF;
	public static final int BLOCK_EMPTY_NOW = 0xFFFFFFFE;
	private Entry[] bucketArray;
	
	// raw constructor, assumes every entry is not null and the array is a power of 2
	public HashTable(Entry[] entries){
		bucketArray = entries;
	}
	
	public int lookupBlock(HashLookup what) throws MPQException{
		int mask = bucketArray.length-1;
		int index = what.index & mask;
		for(int pos = index ; ; ){
			Entry temp = bucketArray[pos];
			if(temp.blockIndex == BLOCK_EMPTY_ALWAYS) break;
			if(temp.getHash() == what.hash) return temp.blockIndex;
			pos = ( pos + 1 ) & mask;
			if(pos == index) break;
		}
		throw new MPQException("lookup not found");
	}
	
	/*public static int lookupBlock(Entry[] hashtable, byte[] file) throws FileNotFoundException{
		int mask = hashtable.length-1;
		int index = Cryption.HashString(file, Cryption.MPQ_HASH_TABLE_OFFSET) & mask;
		long hash = Cryption.HashString(file, Cryption.MPQ_HASH_NAME_A) & 0xFFFFFFFFL | (long)Cryption.HashString(file, Cryption.MPQ_HASH_NAME_B)<<32;
		for(int pos = index ; ; ){
			Entry temp = hashtable[pos];
			if(temp.getBlockIndex() == BLOCK_EMPTY_ALWAYS) break;
			if(temp.getHash() == hash) return temp.getBlockIndex();
			pos = ( pos + 1 ) & mask;
			if(pos == index) break;
		}
		throw new FileNotFoundException("hash not in hashtable");
	}*/
	
	/*public static int lookupBlock(Entry[] hashtable, String file) throws FileNotFoundException{
		return  lookupBlock(hashtable, Cryption.stringToHashable(file));
	}*/
		
	// entry is an internal data type and as such performs no safety checks
	public static class Entry{
		public long hash;
		public short locale;
		public short platform;
		public int blockIndex;
		
		// raw constructor
		public Entry(){
		}
		
		public Entry(HashTableEntry source){
			hash = source.getHash();
			locale = source.getLocale();
			platform = source.getPlatform();
			blockIndex = source.getBlockIndex();
		}

		public long getHash() {
			return hash;
		}

		public short getLocale() {
			return locale;
		}

		public short getPlatform() {
			return platform;
		}

		public int getBlockIndex() {
			return blockIndex;
		}

		
	}
}
