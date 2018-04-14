package interfaces;

import java.io.OutputStream;

public interface Compressor {
	
	byte[] compress();
	
	void compress(OutputStream osm);
}
