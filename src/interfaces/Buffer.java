package interfaces;

public interface Buffer {
	public boolean isFulled();
	
	public byte get(int index);
	
	public boolean push();
	
	public boolean pop();
	
	public int getMaxSize();
	
	public int getTail();
	
	public int getHead();
	
	public int size();
}
