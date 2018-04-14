package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class BitsOutputStreamWriter {
	
	OutputStream osm;
	
	byte cache;
	
	byte[] write_cache = new byte[1];
	
	int index = 0;
	
	public BitsOutputStreamWriter(OutputStream osm) {
		this.osm = osm;
	}
	
	
	public void write(boolean isZero) throws IOException{
		byte writed = (byte) (isZero ? 0 : 1);
		
		write(writed,7,1);
	}
	
	
	
	public void write(byte b,int off,int len) throws IOException{
		if(len + off > 8)throw new RuntimeException("len + off > 8 , len : " + len + " off : " + off );
		
		int left = 8 - index;
		
		if(len > left){
			
			write(b,off,left);
			write(b,off+left,len-left);
			
		}else{
			
			int writed = b >> (8 - off - len);
			
			int mask = (1 << len) -1;
			
			writed = writed & mask;
			
			writed = writed << (8 - index - len);
			
			cache = (byte) (cache | writed);
			
			if(nextIndex(len) == 0) putCache();
			
		}
		
	}
	
	public void write1() throws IOException{
		
		write(false);
		
	}
	
	public void write0() throws IOException{
		
		write(true);
		
	}
	
	public void write(byte b) throws IOException{
		write(b,0,8);
	}
	
	public void write(int i,int off,int len) throws IOException{
		if(len + off > 32)throw new RuntimeException("len + off > 32 , len : " + len + " off : " + off );
		
		byte[] arr = byteArrs(i);
		
		int byteOffset = off % 8;
		int preLen = off / 8;
		
		while( preLen * 8 + byteOffset + len <= 32 && len != 0){
			
			int writeLen = Math.min(8 - byteOffset, len);
			
			write(arr[preLen++],byteOffset,writeLen);
			
			byteOffset = (byteOffset + writeLen) % 8;
			
			len -= writeLen;
			
		}
		
	}
	
	private byte[] byteArrs(int i) {
		byte[] arr = new byte[4];
		
		arr[3] = (byte) i;
		arr[2] = (byte) (i >> 8);
		arr[1] = (byte) (i >> 16);
		arr[0] = (byte) (i >> 24);
		
		return arr;
	}


	private void putCache() throws IOException{
		
		write_cache[0] = cache;
		
		osm.write(write_cache, 0, 1);
		
		cache = 0;
		index = 0;
	}
	
	public void flush() throws IOException  {
		
		putCache();
	}
	
	
	private int nextIndex(int n){
		
		index = (index + n) % 8;
		
		return index;
	}
	
	
	
}
