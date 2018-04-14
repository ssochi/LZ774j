package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import utils.BitsInputStreamReader;
import utils.BitsOutputStreamWriter;

public class TestBitsReaderAndWriter {

	public static void main(String[] args) throws IOException {
		
		ByteArrayOutputStream osm = new ByteArrayOutputStream();
		
		BitsOutputStreamWriter writer = new BitsOutputStreamWriter(osm);
		
//		writer.write((byte)49);
		
		
		for(int i = 0;i < 5;i++){
			writer.write0();
			writer.write((byte)51);
		}
		
		writer.write(6, 32-15, 15);
		writer.write(6, 32-8,	 8);
		
		writer.flush();
		
		byte[] arr = osm.toByteArray();
		
		ByteArrayInputStream ism = new ByteArrayInputStream(arr);
		
		BitsInputStreamReader reader = new BitsInputStreamReader(ism);
		
		
		for(int i = 0;i < 5;i++){
			System.out.println(reader.readBit());
			System.out.println(reader.readByte());
		}

		
		System.out.println(reader.readInt(15));
		System.out.println(reader.readInt(8));
		
		
		
		
		
	}
	
	
}
