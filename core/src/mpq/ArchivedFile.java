package mpq;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

import mpq.data.RawArrays;
import mpq.util.Cryption;

public class ArchivedFile implements Serializable {
	private static final long serialVersionUID = 5033693351138253083L;
	
	// CRC is Adler?
	// CRC requires version safety check.
	// Specification is unclear when [CRC block size > archive block size]. Assuming this never happens. May need to handle as special case.
	// Specification is unclear when [(file is single unit) equals TRUE AND (file uses CRC) equals TRUE]. Assuming flag is ignored.
	// Single Unit requires version safety check.
	
	public boolean ready;
	public final int blockShift;
	public final int compressedSize;
	public final int fileSize;
	public final int flags;
	public final long fileOffset;
	public final int[] blockOffsets;
	public int[] blockChecksums = null;
	public final int key;
	public final byte compression;
	
	public ArchivedFile( MPQArchive archive, HashLookup search, BlockTable.Entry file ) throws MPQException{
		// *** load simple values
		compressedSize = file.compressedSize;
		fileSize = file.fileSize;
		flags = file.flags;
		fileOffset = file.filePosition + archive.getArchiveOffset();
		
		// *** load complex values
		if( hasFlag(BlockTable.FLAG_SINGLE_UNIT) ) blockShift = -fileSize;
		else blockShift = archive.getBlockShift();
		
		if( hasFlag(BlockTable.FLAG_ENCRYPTED) ){
			int key = Cryption.HashString(search.lookup, Cryption.MPQ_HASH_FILE_KEY);
			if( hasFlag(BlockTable.FLAG_FIX_KEY) ){
				key = Cryption.adjustFileDecryptKey(key, file.getFilePosition(), file.getFileSize());
			}
			this.key = key;
		}else
			key = 0;
		
		if( hasFlag(BlockTable.FLAG_COMPRESS | BlockTable.FLAG_IMPLODE) ){
			// blocks cannot be both compressed and imploded
			if( hasFlag(BlockTable.FLAG_COMPRESS) && hasFlag(BlockTable.FLAG_IMPLODE) ) throw new MPQException("invalid block: a block is both compressed and imploded");
			
			// determine the type of sector compression to use
			if( hasFlag(BlockTable.FLAG_COMPRESS) ){
				if( archive.getVersion() > 1 ) compression = 3;
				else compression = 2;
			}else{
				compression = 1;
			}
			
			// all compressed files use sector tables for standardization
			// single unit files have a known table
			if( hasFlag(BlockTable.FLAG_SINGLE_UNIT) ){
				
				blockOffsets = new int[2];
				blockOffsets[1] = compressedSize;
				blockChecksums = null;
				ready = true;
			// table will need to be looked up
			}else{
				int blockn = (fileSize + (512 << blockShift) - 1) / (512 << blockShift);
				
				// if CRC is used, there is an additional checksum sector with checksums for all other sectors.
				if( hasFlag(BlockTable.FLAG_SECTOR_CRC) ) blockn+= 1;
				
				blockOffsets = new int[blockn+1];
				ready = false;
			}
		}else{
			compression = 0;
			blockOffsets = null;
			ready = true;
		}
	}
	
	public void loadOffsets( SeekableByteChannel in ) throws IOException, MPQException{
		// read sector table from file
		ByteBuffer temp = ByteBuffer.allocate(blockOffsets.length * 4);
		in.position( fileOffset );
		while( temp.hasRemaining() )
			if( in.read(temp) == -1 )
				break;
		temp.rewind();
		
		// decrypt if required
		if( hasFlag(BlockTable.FLAG_ENCRYPTED) ){
			Cryption.decryptData(temp, temp, key - 1);
		}
		
		// interpret sector table
		RawArrays.getArray(temp, blockOffsets);
		
		// validate offsets in case of corruption
		if( blockOffsets[0] >= 0 && blockOffsets[0] < blockOffsets.length * 4 ||
				blockOffsets[0] < 0 && blockOffsets[blockOffsets.length - 1] > 0 ) throw new MPQException("block sector intersects sector offset table");
		else if( blockOffsets[0] < 0 || blockOffsets[0] > blockOffsets.length * 4 ) 
			System.err.printf("block at %X has detached sectors starting at %X (%d bytes from end of sector table)%n",
					fileOffset, fileOffset + blockOffsets[0], blockOffsets[0] - blockOffsets.length * 4);
		if( fileOffset + blockOffsets[0] < 0 || fileOffset + blockOffsets[blockOffsets.length - 1] > in.size() )
			throw new MPQException("block sector located outside channel");
		for( int i = 1, prevoff = blockOffsets[0] ; i < blockOffsets.length ; i+= 1){
			int curroff = blockOffsets[i];
			if( curroff < prevoff ) throw new MPQException("block sector with negative size");
			prevoff = curroff;
		}
		
		// load CRC sector if present
		if( hasFlag(BlockTable.FLAG_SECTOR_CRC) && blockOffsets[blockOffsets.length - 1] != blockOffsets[blockOffsets.length - 2] ){
			System.err.println("block sector CRC reading currently not supported");
		}
		
		ready = true;
	}
	
	public boolean hasFlag(int flag){
		return (flags & flag) != 0;
	}
}


