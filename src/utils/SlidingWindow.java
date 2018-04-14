package utils;

import java.io.InputStream;

import interfaces.Buffer;

/**
 * 
 *
 * </br>
 * 术语介绍:</br>
 * lookahead buffer -> 待编码区</br>
 * search buffer -> 搜索缓冲区</br>
 * sliding window -> 滑动窗口：指定大小的窗，包含“搜索缓冲区”（左） + “待编码区”（右）</br>
 * 
 * ┌────────────┬──────────────┐</br>
 * │    搜索缓冲区      │      待编码区          │                        输出</br>
 * └────────────┴──────────────┘</br>
 *                  A   A  B      C B B A B C    -->   (0,0)A</br>
 *           A      A   B  C      B B A B C      -->   (1,1)</br>
 * 		  A  A      B   C  B      B A B C        -->   (0,0)B</br>
 *     A  A  B      C   B  B      A B C          -->   (0,0)C</br>
 *   A A  B  C      B   B  A      B C            -->   (2,1)</br>
 * A A B  C  B      B   A  B      C              -->   (1,1)</br>
 * A B C  B  B      A   B  C                     -->   (5,3)</br>
 * </br>
 * </br>
 * 
 * @author ssochi
 *
 */
public class SlidingWindow {
	
	public static final int LOOKAHEADEDBUFFER_SIZE = 256;
	public static final int REPECTED_BUFFER_SIZE = 8;
	
	private int searchBufferSize;
	private int lookaheadBufferSize;
	
	private int cacheSize;
	
	public Buffer searchBuffer;
	public Buffer lookaheadBuffer;
	
	CircleCache cache;
	
	
	public SlidingWindow(int offsetBufferSize,int repectedBufferSize,InputStream ism){
		
		
		
		searchBufferSize = (int) Math.pow(2, offsetBufferSize) - 1;
		lookaheadBufferSize = (int) Math.pow(2, repectedBufferSize) - 1;
		cacheSize = (searchBufferSize + LOOKAHEADEDBUFFER_SIZE) * 2;
		
		cache = new CircleCache(cacheSize, lookaheadBufferSize, ism);
		
		searchBuffer = cache.registerBuffer(searchBufferSize);
		lookaheadBuffer = cache.aheadBuffer;
	}
	
	public boolean isNoCache(){
		return cache.isNoCache();
	}
	
}




