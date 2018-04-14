package utils;

import java.util.NoSuchElementException;

/**
 * 
 * All methods of QueueList cost O(1) , </br>
 * 
 * but QueueList has it's max size
 * 
 * @author ssochi
 *
 */
public class QueueList<E> {
	
	private int maxSize;
	
	private int mainSize;
	
	private int size;
	
	private Object[] arr;
	
	int head;
	
	int tail;
	
	public QueueList(int maxSize){
		
		this.maxSize = maxSize;
		
		mainSize = maxSize + 1;
		
		arr = new Object[mainSize];
		
	}
	
	public boolean push(E e){
		
		if(size >= maxSize) throw new RuntimeException("QueueList is fulled");
		
		arr[head] = e;
		
		head = next(head);
		size ++;
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public E pop(){
		if(size == 0) throw new NoSuchElementException();
		
		E e = (E) arr[tail];
		
		tail = next(tail);
		
		size --;
		
		return e;
	}
	
	@SuppressWarnings("unchecked")
	public E get(int index){
		if(index+1 > size) throw new NoSuchElementException("index : " + index +" , size : " + size);
		
		index = next(tail,index);
		
		return (E) arr[index];
	}
	
	
	
	private int next(int p){
		return next(p,1);
	}
	
	private int next(int p,int n){
		return (p + n) % mainSize;
	}
	
	public boolean isFulled(){
		return maxSize == size;
	}
	
	public int size(){
		return size;
	}
	
}
