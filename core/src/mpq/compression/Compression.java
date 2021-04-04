package mpq.compression;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import mpq.compression.adpcm.ADPCM;
import mpq.compression.huffman.Huffman;
import mpq.compression.pkware.PKException;
import mpq.compression.pkware.PKExploder;

public class Compression {	
	/*static TDecompressTable dcmp_table[] =
	{
	    {MPQ_COMPRESSION_BZIP2,        Decompress_BZIP2},        // Decompression with Bzip2 library
	    {MPQ_COMPRESSION_PKWARE,       Decompress_PKLIB},        // Decompression with Pkware Data Compression Library
	    {MPQ_COMPRESSION_ZLIB,         Decompress_ZLIB},         // Decompression with the "zlib" library
	    {MPQ_COMPRESSION_HUFFMANN,     Decompress_huff},         // Huffmann decompression
	    {MPQ_COMPRESSION_ADPCM_STEREO, Decompress_ADPCM_stereo}, // IMA ADPCM stereo decompression
	    {MPQ_COMPRESSION_ADPCM_MONO,   Decompress_ADPCM_mono},   // IMA ADPCM mono decompression
	    {MPQ_COMPRESSION_SPARSE,       Decompress_SPARSE}        // Sparse decompression
	};*/
	
	//private WritableByteBuffer adapter = new WritableByteBuffer();
	/*private OutputStream adapterFront = Channels.newOutputStream(adapter);
	
	private InflaterOutputStream zlibInflater = new InflaterOutputStream(adapterFront);
	private WritableByteChannel zlibDecompressWriter = Channels.newChannel(zlibInflater);*/
	private PKExploder pkexploderDecompress = new PKExploder();
	private Huffman huffmanDecompress = new Huffman();
	private ADPCM adpcmDecompress = new ADPCM(2);
	
	// an array used to cache buffers of various regular sizes to reduce allocation overhead
	private final ByteBuffer[] bufferCache = new ByteBuffer[22];
	
	/*
	 * Compression Masks
	 */
	/* Masks for Compression Type 2 */
	private static final byte FLAG_HUFFMAN = 0x01;
	private static final byte FLAG_DEFLATE = 0x02;
	// 0x04 is unknown
	private static final byte FLAG_IMPLODE = 0x08;
	private static final byte FLAG_BZIP2   = 0x10; // introduced in version 1
	private static final byte FLAG_SPARSE  = 0x20; // introduced in version 2
	private static final byte FLAG_ADPCM1C = 0x40;
	private static final byte FLAG_ADPCM2C =-0x80;
	/* Masks for Compresion Type 3 */
	private static final byte FLAG_LZMA    = 0x12;
	private static final byte FLAG_SPARSE_DEFLATE = FLAG_SPARSE  | FLAG_DEFLATE;
	private static final byte FLAG_SPARSE_BZIP2   = FLAG_SPARSE  | FLAG_BZIP2  ;
	
	private ByteBuffer fetchBuffer(int size){
		ByteBuffer out;
		if( size >= 0 ){
			out = bufferCache[size];
			if( out == null ){
				out = ByteBuffer.allocate(512 << size);
				bufferCache[size] = out;
			}
		}else out = ByteBuffer.allocate(-size);
		return out;
	}
	
	/**
	 * Decompresses a sector following compression specification 3 used in version 2 and later MPQs.
	 * 
	 * A lookup table is used to resolve compression.
	 * 
	 * @param in buffer with compressed sector
	 * @param size sector size as bit shift or negative for sectors of irregular size
	 * @return buffer with decompressed sector
	 * @throws DecompressionException when decompression fails
	 */
	public ByteBuffer blockDecompress3(ByteBuffer in, int size) throws DecompressionException{
		byte mask = in.get();
		
		ByteBuffer out = fetchBuffer(size);
		boolean flip = true;
		
		// lookup table for valid compression types
		switch( mask ) {
		case FLAG_DEFLATE:
			sectorInflate(in, out);
			break;
		case FLAG_IMPLODE:
			sectorExplode(in, out);
			break;
		case FLAG_BZIP2:
			// TODO add support
			throw new DecompressionException(in, "unsupported compression type: BZIP2");
		case FLAG_SPARSE:
			// TODO add support
			throw new DecompressionException(in, "unsupported compression type: SPARSE");
		case FLAG_LZMA:
			// TODO add support
			throw new DecompressionException(in, "unsupported compression type: LZMA");
		case FLAG_SPARSE_DEFLATE:
			// TODO add support
			throw new DecompressionException(in, "unsupported compression type: SPARSE");
		case FLAG_SPARSE_BZIP2:
			// TODO add support
			throw new DecompressionException(in, "unsupported compression type: SPARSE");
		default:
			throw new DecompressionException(in, "sector has unknown compression");
		}
		
		if( size >= 0 )
			if( flip ){
				in.clear();
				bufferCache[size] = in;
			}else{
				out.clear();
				out = in;
			}
		else
			if( !flip ) out = in;
		
		return out;
	}
	
	/**
	 * Decompresses a sector following compression specification 2 used in version 0 and 1 MPQs.
	 * 
	 * Masks are evaluated in order to undo compression.
	 * 
	 * @param in buffer with compressed sector
	 * @param size sector size as bit shift or negative for sectors of irregular size
	 * @return buffer with decompressed sector
	 * @throws DecompressionException when decompression fails
	 */
	public ByteBuffer blockDecompress2(ByteBuffer in, int size) throws DecompressionException{
		byte mask = in.get();
		
		ByteBuffer out = fetchBuffer(size);
		boolean flip = false;
		
		// apply decompression flag at a time
		if( (mask & FLAG_BZIP2) != 0 ){
			// TODO add support
			throw new DecompressionException(in, "unsupported compression type: BZIP2");
		}
		if( (mask & FLAG_IMPLODE) != 0 ){
			sectorExplode(flip ? out : in, flip ? in : out);
			(flip ? out : in).clear();
			flip = !flip;
		}
		if( (mask & FLAG_DEFLATE) != 0 ){
			sectorInflate(flip ? out : in, flip ? in : out);
			(flip ? out : in).clear();
			flip = !flip;
		}
		if( (mask & FLAG_HUFFMAN) != 0 ){
			sectorHuffmanExpand(flip ? out : in, flip ? in : out);
			(flip ? out : in).clear();
			flip = !flip;
		}
		if( (mask & FLAG_ADPCM2C) != 0 ){
			sectorADPCMReconstruct(flip ? out : in, flip ? in : out, 2);
			(flip ? out : in).clear();
			flip = !flip;
		}
		if( (mask & FLAG_ADPCM1C) != 0 ){
			sectorADPCMReconstruct(flip ? out : in, flip ? in : out, 1);
			(flip ? out : in).clear();
			flip = !flip;
		}
		if( (mask & FLAG_SPARSE) != 0 )	System.err.println("sparse compression flag present in mpq version that lacked support");
		
		if( flip ){
			bufferCache[size] = in;
			return out;
		}else return in;
	}
	
	/**
	 * Decompresses a sector following compression specification 1 used by imploded blocks.
	 * 
	 * pkware explode is always used on the input. A buffer flip always occurs.
	 * 
	 * @param in buffer with compressed sector
	 * @param size sector size as bit shift or negative for sectors of irregular size
	 * @return buffer with decompressed sector
	 * @throws DecompressionException when decompression fails
	 */
	public ByteBuffer blockDecompress1(ByteBuffer in, int size) throws DecompressionException{
		ByteBuffer out = fetchBuffer(size);
		sectorExplode(in, out);
		in.clear();
		bufferCache[size] = in;
		return out;
	}
	
	private void sectorExplode(ByteBuffer in, ByteBuffer out) throws DecompressionException{
		try {
			pkexploderDecompress.explode(in, out);
		} catch (PKException e) {
			throw new DecompressionException(in, "sector explode exception", e);
		}
		
		out.flip();
	}
	
	private void sectorInflate(ByteBuffer in, ByteBuffer out) throws DecompressionException{
		try {
			// a new inflater is needed for each sector as they cannot be recycled
			Inflater zlibInflater = new Inflater();
			zlibInflater.setInput(in.array(), in.position(), in.remaining());
			out.position(zlibInflater.inflate(out.array()));
		} catch ( DataFormatException e ) {
			throw new DecompressionException(in, "sector deflae exception", e);
		}
		
		out.flip();
	}
	
	private void sectorHuffmanExpand(ByteBuffer in, ByteBuffer out) throws DecompressionException{
		try {
			huffmanDecompress.Decompress(in, out);
		} catch ( Exception e ) {
			throw new DecompressionException(in, "sector huffman expand exception", e);
		}
		
		out.flip();
	}
	
	private void sectorADPCMReconstruct(ByteBuffer in, ByteBuffer out, int channeln) throws DecompressionException{
		try {
			adpcmDecompress.decompress(in, out, channeln);
		} catch ( Exception e ) {
			throw new DecompressionException(in, "sector adpcm reconstruction exception", e);
		}
		
		out.flip();
	}
	
	/*private static class WritableByteBuffer implements WritableByteChannel{
		public ByteBuffer dst;

		@Override
		public void close() throws IOException {
			// nothing to close
		}

		@Override
		public boolean isOpen() {
			// always open
			return true;
		}

		@Override
		public int write(ByteBuffer src) throws IOException {
			int size = src.remaining();
			dst.put(src);
			return size;
		}
		
	}*/
	
	public ByteBuffer blockDecompressAny(ByteBuffer block, ByteBuffer extra) throws DecompressionException{
		if( blockDecompress(block, extra) ) return extra;
		else return block;
	}
	
	public void blockExplode(ByteBuffer block, ByteBuffer extra){
		try {
			pkexploderDecompress.explode(block, extra);
		} catch (PKException e) {
			System.err.println("pkware decompression exception: "+e.getLocalizedMessage());
		}
		
		if( extra.position() != extra.limit() ){
			System.err.println("a block failed exploding");
		}
		
		block.clear();
		extra.rewind();;
	}
	
	public boolean blockDecompress(ByteBuffer block, ByteBuffer extra) throws DecompressionException{
		return blockDecompress(block, extra, false);
	}
	
	public boolean blockDecompress(ByteBuffer block, ByteBuffer extra, boolean strict) throws DecompressionException{
		byte mask = block.get();
		
		if( strict ){
			System.err.println("strict compression flag mode not supported");
		}
		
		boolean swap = false;
		
		// BZIP2
		if( (mask & 0x10) > 0 ){
			// TODO add support
			throw new DecompressionException(block, "unsupported compression type: BZIP2");
		}
		
		// PKWARE
		if( (mask & 0x08) > 0 ){			
			try {
				pkexploderDecompress.explode(block, extra);
			} catch (PKException e) {
				throw new DecompressionException(block, "failed PKWARE decompression", e);
			}
			block.rewind();
			block.limit(extra.limit());
			extra.flip();
			
			ByteBuffer temp = extra;
			extra = block;
			block = temp;
			
			swap = !swap;
		}
		
		// ZLIB
		if( (mask & 0x02) > 0 ){			
			try {
				Inflater zlibInflater = new Inflater();
				zlibInflater.setInput(block.array(), block.position(), block.remaining());
				extra.position(zlibInflater.inflate(extra.array()));
			} catch ( DataFormatException e ) {
				throw new DecompressionException(block, "failed ZLIB decompression", e);
			}
			block.rewind();
			block.limit(extra.limit());
			extra.flip();
			
			ByteBuffer temp = extra;
			extra = block;
			block = temp;
			
			swap = !swap;
		}
		
		// HUFFMANN
		if( (mask & 0x01) > 0 ){
			huffmanDecompress.Decompress(block, extra);
			
			block.rewind();
			block.limit(extra.limit());
			extra.flip();
			
			ByteBuffer temp = extra;
			extra = block;
			block = temp;
			
			swap = !swap;
		}
		
		// ADPCM_STEREO
		if( (mask & 0x80) > 0 ){
			adpcmDecompress.decompress(block, extra, 2);
			
			block.rewind();
			block.limit(extra.limit());
			extra.flip();
			
			ByteBuffer temp = extra;
			extra = block;
			block = temp;
			
			swap = !swap;
		}
		
		// ADPCM_MONO
		if( (mask & 0x40) > 0 ){
			adpcmDecompress.decompress(block, extra, 1);
			
			block.rewind();
			block.limit(extra.limit());
			extra.flip();
			
			ByteBuffer temp = extra;
			extra = block;
			block = temp;
			
			swap = !swap;
		}
		
		// SPARSE
		if( (mask & 0x20) > 0 ){
			// TODO add support
			throw new DecompressionException(block, "unsupported compression type: SPARSE");
		}
		
		if( block.limit() != extra.limit() ){
			throw new DecompressionException(block, "decompression result was smaller than expected");
			//System.err.println("a sector passed decompression but failed to meet the expected size");
			//block.limit(extra.limit());
		}
		
		extra.clear();
		return swap;
	}
}