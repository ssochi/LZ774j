package interfaces;

import java.io.OutputStream;

public interface DeCompressor {
	
	byte[] deCompress();
	
	void deCompress(OutputStream osm);
}
