package lz77;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import interfaces.DeCompressor;
import utils.BitsInputStreamReader;
import utils.BitsOutputStreamWriter;
import utils.QueueList;

public class LZ77DeCompressor implements DeCompressor{

	private int offsetSize;
	private int repectSize;
	
	private Cache<Byte> cache;
	
	BitsInputStreamReader reader;
	
	public LZ77DeCompressor(byte[] bytes){
		this(new ByteArrayInputStream(bytes),15,8);
	}
	public LZ77DeCompressor(byte[] bytes,int offsetSize){
		this(new ByteArrayInputStream(bytes),offsetSize,8);
	}
	public LZ77DeCompressor(byte[] bytes,int offsetSize,int repectSize) {
		this(new ByteArrayInputStream(bytes),offsetSize,repectSize);
	}
	
	public LZ77DeCompressor(InputStream ism){
		this(ism,15,8);
	}
	public LZ77DeCompressor(InputStream ism,int offsetSize){
		this(ism,offsetSize,8);
	}
	public LZ77DeCompressor(InputStream ism,int offsetSize,int repectSize){
		
		this.offsetSize = offsetSize;
		this.repectSize = repectSize;
		
		try {
			reader = new BitsInputStreamReader(ism);
		} catch (IOException e) {
			throw new RuntimeException("load LZ77DeCompressor error",e);
		}
		
		cache = new Cache<Byte>(2 << offsetSize);
	}
	
	
	@Override
	public byte[] deCompress() {
		
		ByteArrayOutputStream osm = new ByteArrayOutputStream();
		
		deCompress(osm);
		
		return osm.toByteArray();
	}

	@Override
	public void deCompress(OutputStream osm) {
		
		BitsOutputStreamWriter writer = new BitsOutputStreamWriter(osm);
		
		try{
			
			int flag ;
			
			while(!reader.isNoContent() && (flag = reader.readBit()) != -1){
				
				switch (flag) {
				case 0:
					
					byte b = reader.readByte();
					writer.write(b);
					
					cache.push(b);
					
					break;
				case 1:
					
					int offset = reader.readInt(offsetSize);
					int len = reader.readInt(repectSize);
					
					int index = cache.size() - offset ;

					for(int i = 0;i<len;i++){
						
						byte bt = cache.get(index);
						writer.write(bt);
						
						if(cache.push(bt)){
							index ++;
						}
						
					}
					
					
					break;
					
				default:
					break;
				}
				
			}
			
		}catch(IOException e){
			return ;
		}
		
		
		
		
		
		
	}
	
	class Cache<E> extends QueueList<E>{

		public Cache(int maxSize) {
			super(maxSize);
		}
		
		/**
		 * if queue is fulled,then exec pop() and return false
		 */
		@Override
		public boolean push(E e) {
			
			if(isFulled()) {
				pop();
				super.push(e);
				return false;
			}
			
			return super.push(e);
			
			
		}
		
	}

}
