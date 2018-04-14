package utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitsInputStreamReader {
	
	private InputStream ism ;
	
	private int index = 0;
	
	private byte cache;
	
	private byte[] read_cache = new byte[1];
	
	private boolean isNoContent = false;
	
	
	public BitsInputStreamReader(InputStream ism) throws IOException {
		this.ism = ism;
		
		ism = new BufferedInputStream(ism);
		
		loadCache();
	}
	
	public int readBit() throws IOException{
		return readByte(1);
	}
	
	public int readInt(int len) throws IOException{
		if(len > 32)throw new RuntimeException("len > 32 , len : " + len);
		int[] src = new int[4];
		
		int arrLen = len / 8;
		int byteLen = len % 8;
		
		if(byteLen > 0){
			byte b = readByte(byteLen);
			src[3 - arrLen] = b;
		}
		
		for(int i = arrLen;i > 0;i--){
			
			src[4 - arrLen] = readByte();
			
		}
		
		return byteArray2Int(src);
	}
	
	private int byteArray2Int(int[] src) {
		
		int res = 0;
		
		for(int i = 0 ;i < 4;i++){
			src[i] = src[i] & 255;
			res = res | (src[i] << (24 - i * 8));
			
		}
		
		return res;
	}

	public int readInt() throws IOException{
		return readInt(32);
	}
	
	public byte readByte() throws IOException{
		
		return readByte(8);
	}
	
	public byte readByte(int len) throws IOException{
		if(isNoContent) throw new IOException("no more content to read ...");
		if(len > 8)throw new RuntimeException("len > 8 , len : " + len);
		
		
		
		if(len + index > 8){
			
			int befLen = 8 - index;
			int aftLen = len - 8 + index;
			
			byte bef = readByte(befLen);
			byte aft = readByte(aftLen);
			
			return (byte) ((bef << aftLen) | aft);
			
		}
			
		int offset = 8 - index - len;
		
		int mask = (1 << len) - 1;
		
		index += len;
		
		byte res = (byte) ((cache >> offset) & mask);
		
		if(index == 8){
			isNoContent = !loadCache();
		}
		
		return res;
		
	}
	
	private boolean loadCache() throws IOException{
		
		boolean result = ism.read(read_cache, 0, 1) != -1;
		
		cache = read_cache[0];
		
		index = 0;
		
		return result;
	}
	public boolean isNoContent(){
		return isNoContent;
	}
	
}
