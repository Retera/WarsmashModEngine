package mpq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

import mpq.compression.Compression;
import mpq.util.Cryption;

public class ArchivedFileExtractor {
	private Compression decompress = new Compression();
	
	public ByteBuffer readBlock(ByteBuffer bufferold, SeekableByteChannel in, ArchivedFile file, int block) throws IOException, MPQException{
		// *** calculate the current block size
		int currentSize;
		if( file.fileSize < (block + 1) * bufferold.capacity() )
			currentSize = file.fileSize % bufferold.capacity();
		else
			currentSize = bufferold.capacity();
		
		// *** read block
		if( file.blockOffsets != null ){
			// use block offset table
			if( !file.ready ){
				file.loadOffsets(in);
			}
			bufferold.limit(file.blockOffsets[block+1] - file.blockOffsets[block]);
			in.position(file.fileOffset + file.blockOffsets[block]);
		}else{
			// compute offset
			bufferold.limit(currentSize);
			in.position(file.fileOffset + bufferold.capacity() * block);
		}
		while( bufferold.hasRemaining() )
			if( in.read(bufferold) == -1 )
				break;
		bufferold.rewind();
		
		// *** decrypt if required
		if( file.key != 0 ){
			Cryption.decryptData(bufferold, bufferold, file.key + block);
		}
		
		// *** CRC check goes here
		if( file.blockChecksums != null ){
			// TODO add support for CRC
			System.err.println("block sector CRC validation currently not supported");
		}
				
		// *** decompress if required
		if( file.compression > 0 ){
			// only decompress if block is compressed
			if( bufferold.limit() < currentSize ){				
				// decompress block
				if( file.compression >= 3 ){
					bufferold = decompress.blockDecompress3(bufferold, file.blockShift);
				}else if( file.compression == 2 ){
					bufferold = decompress.blockDecompress2(bufferold, file.blockShift);
				}else{
					bufferold = decompress.blockDecompress1(bufferold, file.blockShift);
				}
			}
		}
		return bufferold;
	}
}
