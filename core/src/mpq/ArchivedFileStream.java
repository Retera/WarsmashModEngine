package mpq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;

public class ArchivedFileStream implements SeekableByteChannel{
	private boolean open;
	private SeekableByteChannel from;
	private ByteBuffer buffer;
	private ArchivedFile file;
	private ArchivedFileExtractor extractor;
	private long position;
	private int currentBlock;
	
	public ArchivedFileStream(SeekableByteChannel in, ArchivedFileExtractor extractor, ArchivedFile file){
		from = in;
		this.extractor = extractor;
		this.file = file;
		if( file.hasFlag(BlockTable.FLAG_SINGLE_UNIT) ){
			buffer = ByteBuffer.allocate(file.fileSize);
		}else{
			buffer = ByteBuffer.allocate(512 << file.blockShift);
		}
		position = 0;
		currentBlock = -1;
		open = true;
	}

	@Override
	public void close() throws IOException {
		from = null;
		buffer = null;
		open = false;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public long position() throws IOException {
		return position;
	}

	@Override
	public SeekableByteChannel position(long newPosition)
			throws IOException {
		// *** argument validation as described by SeekableByteChannel interface
		if( newPosition < 0 ) throw new IllegalArgumentException("files cannot have a negative positon");
		
		// update stream position
		position = newPosition;
		// try and update the buffer position of loaded sectors
		if( currentBlock != -1 ){		
			if( currentBlock != newPosition / buffer.capacity() )
				currentBlock = -1;
			else
				buffer.position((int) (newPosition % buffer.capacity()));
		}
		
		// *** return value as described by SeekableByteChannel interface
		return this;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		// closed
		if( !open ) throw new ClosedChannelException();
		// end of stream
		if( position >= file.fileSize ) return -1;
		
		// load current block if no block is currently loaded
		if( currentBlock == -1 ){
			currentBlock = (int) (position / buffer.capacity());
			buffer.clear();
			try {
				buffer = extractor.readBlock(buffer, from, file, currentBlock);
			} catch (MPQException e) {
				throw new IOException(e);
			}
			buffer.position((int) (position % buffer.capacity()));
			
		}
		
		long positionstart = position;
		while( dst.hasRemaining() ){				
			if( buffer.remaining() > dst.remaining() ){
				int limit = buffer.limit();
				buffer.limit(buffer.position() + dst.remaining());
				position+= buffer.remaining();
				dst.put(buffer);
				buffer.limit(limit);
			}else{
				position+= buffer.remaining();
				dst.put(buffer);
				if(position < file.fileSize){
					currentBlock = (int) (position / buffer.capacity());
					buffer.clear();
					try {
						buffer = extractor.readBlock(buffer, from, file, currentBlock);
					} catch (MPQException e) {
						throw new IOException(e);
					}
				}else{
					break;
				}
			}
		}
		
		return (int) (position - positionstart);
	}

	@Override
	public long size() throws IOException {
		return file.fileSize;
	}

	@Override
	public SeekableByteChannel truncate(long size) throws IOException {
		throw new NonWritableChannelException();
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		throw new NonWritableChannelException();
	}
}
