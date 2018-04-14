package utils;

import java.io.IOException;
import java.io.InputStream;

public class CircleCache {
	
	private InputStream ism;
	
	private int cacheSize;
	private int endPoint = -1;
	
	public Buffer aheadBuffer;
	
	private byte[] cache ;
	
	
	CircleCache(int cacheSize,int aheadBufferSize ,InputStream ism){
		
		this.ism = ism;
		
		this.cacheSize = cacheSize;
		
		aheadBuffer = new AheadBuffer(aheadBufferSize);
		
		cache = new byte[cacheSize];
		
		
		initCache();
	}
	
	public Buffer registerBuffer(int maxSize){
		
		Buffer buf = new Buffer(maxSize);
		
		return buf;
		
	}
	
	
	public boolean isNoCache(){
		
		AheadBuffer abuf = (AheadBuffer) aheadBuffer;
		
		return abuf.noMorePush();
		
	}
	
	private void initCache() {
		loadCache(1);
	}
	
	class Buffer implements interfaces.Buffer{
		
		private int head;
		private int tail;
		private int size;
		
		private int maxSize;
		
		Buffer(int maxSize){
			
			this.maxSize = maxSize;
			
		}
		
		public int getHead() {
			return head;
		}

		
		public int getTail() {
			return tail;
		}
		

		public int getMaxSize() {
			return maxSize;
		}

		public boolean pop(){
			
			if(size <= 0) return false;
			
			tail = next(tail);
			size -- ;
			
			return true;
			
		}
		
		public boolean push(){
			
			if(head == endPoint) return false;
			
			head = next(head);
			size ++;
			
			return true;
		}
		
		public byte get(int index){
			if(index >= size) throw new ArrayIndexOutOfBoundsException("size = " + size + ",index = " + index);
			
			index = next(tail,index);
			
			return cache[index];
			
		}
		
		
		public int size(){
			
			return size;
			
		}
		
		
		protected int next(int p){
			return next(p,1);
		}
		
		protected int next(int p,int n){
			return (p + n) % cacheSize;
		}
		
		public boolean isFulled(){
			return size >= maxSize;
		}
		
	}
	
	class AheadBuffer extends Buffer{
		
		AheadBuffer(int maxSize) {
			super(maxSize);
		}

		public boolean push(){
			
			if(getHead() == cacheSize/2){
				if(!loadCache(2)){
					return false;
				}
			}else if(getHead() == 0 && size() != 0){
				if(!loadCache(1)){
					return false;
				}
			}
			
			return super.push();
			
		}
		
		public boolean noMorePush(){
			
			return size() == 0 && endPoint == getHead();
			
		}
		
	}

	private boolean loadCache(int i) {
		
		switch (i) {
		case 1:
			
			
			try {
			
				int offset = ism.read(cache, 0, cacheSize/2);
				if(offset != cacheSize/2){
					endPoint = offset;
				}

			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("catch IOException when load cache location at : " + i);
			}
			
			break;
		case 2:
			

			try {
				
				int offset = ism.read(cache, cacheSize/2, cacheSize/2);
				if(offset != cacheSize/2){
					endPoint = cacheSize/2 + offset;
				}

			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("catch IOException when load cache location at : " + i);
			}
			
			break;
		default:
			throw new RuntimeException("noknow cache location at: " + i);
		}
		
		return true;
	}
	
}
