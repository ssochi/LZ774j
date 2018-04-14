package lz77;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import interfaces.Buffer;
import interfaces.Compressor;
import utils.BitsOutputStreamWriter;
import utils.SlidingWindow;



public class LZ77Compressor implements Compressor{
	
	private int offsetSize;
	private int repectSize;
	
	private SlidingWindow window ;
	private BitsOutputStreamWriter writer;
	
	private Buffer lookBuf;
	private Buffer searchBuf;
	
	private int offset;
	private int lengthOfRepect;
	
	
	public LZ77Compressor(byte[] bytes){
		this(new ByteArrayInputStream(bytes),15,8);
	}
	public LZ77Compressor(byte[] bytes,int offsetSize){
		this(new ByteArrayInputStream(bytes),offsetSize,8);
	}
	public LZ77Compressor(byte[] bytes,int offsetSize,int repectSize) {
		this(new ByteArrayInputStream(bytes),offsetSize,repectSize);
	}
	
	public LZ77Compressor(InputStream ism){
		this(ism,15,8);
	}
	public LZ77Compressor(InputStream ism,int offsetSize){
		this(ism,offsetSize,8);
	}
	public LZ77Compressor(InputStream ism,int offsetSize,int repectSize){
		
		this.offsetSize = offsetSize;
		this.repectSize = repectSize;
		init(ism);
		
	}
	
	public byte[] compress(){
		
		ByteArrayOutputStream osm = new ByteArrayOutputStream();
		
		compress(osm);
		
		return osm.toByteArray();
		
	}
	
	
	public void compress(OutputStream osm){
		
		writer = new BitsOutputStreamWriter(osm);
		
		//装满lookaheadBuffer	
		while(!lookBuf.isFulled() && lookBuf.push());
		
		do{
			execOffsetAndLength();
			encode();	
			
		}while(updateWindow());
		
		try {
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("LZ77Compress catch IOException when encode", e);
		}
		
	}
	
	private boolean updateWindow() {
		
		
		for(int i=0;i<lengthOfRepect;i++){
			
			lookBuf.pop(); 
			searchBuf.push();
			
			if( window.isNoCache()) {
//				System.out.println("updateWindow error lookBuf size = " + lookBuf.size()
//				+ " searchBuf size = " + searchBuf.size() );
				return false;
			}
			
		}
		
		while(!lookBuf.isFulled() && lookBuf.push());
		
		
		for(int i=0;i<lengthOfRepect;i++){
			
			if(searchBuf.isFulled()){
				searchBuf.pop();
			}else break;
			
		}
		
		
		return true;	
		
		
	}

	private void encode() {
		
		try{
			if(shouldEncode()){
				
				writer.write1();
				
				writer.write(offset, 32 - offsetSize, offsetSize);
				writer.write(lengthOfRepect, 32 - repectSize, repectSize);
				
			}else{
				
				writer.write0();
				writer.write(lookBuf.get(0));
				
				lengthOfRepect = 1;
				
			}
		}catch(IOException ex){
			
			throw new RuntimeException("LZ77Compress catch IOException when encode", ex);
			
		}
		
	}

	private boolean shouldEncode() {
		
		
		int noEncodeSize = lengthOfRepect * 9;
		
		int encodeSize = offsetSize + repectSize + 1;
		
		return encodeSize < noEncodeSize;
		
	}

	private void execOffsetAndLength() {
		
		lengthOfRepect = 0;
		offset = 0;
		
		for(int i = 0;i < searchBuf.size();i++){
			int deep = 0;
			
			for(int j = 0;j < lookBuf.size() && i + deep < searchBuf.size();j++){
				
				if(lookBuf.get(j) == searchBuf.get(i + deep)){
					deep++;
				}else break;
				
			}
			
			if(deep > lengthOfRepect){
				
				offset = searchBuf.size() - i;
				lengthOfRepect = deep;
			}
			
		}
	}

	private void init(InputStream ism) {
		
		window = new SlidingWindow(offsetSize,repectSize, ism);
		this.lookBuf = window.lookaheadBuffer;
		this.searchBuf = window.searchBuffer;
		
	}

	
	
	
	
	
	
}
