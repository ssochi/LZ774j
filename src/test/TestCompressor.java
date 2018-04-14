package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import interfaces.Compressor;
import interfaces.DeCompressor;
import lz77.LZ77Compressor;
import lz77.LZ77DeCompressor;

public class TestCompressor {
	public static void main(String[] args) throws IOException {
		
		testCompressString("123456123456123456123456123456123456123456");
		testCompressString("If the encoding has an historical name then that name is returned; otherwise the encoding's canonical name is returned.");
	
		testCompressFile("test.txt");
		testDeCompressFile("test.txt.LZ77");
	}
	
	private static void testDeCompressFile(String path) throws IOException {
		FileInputStream ism = new FileInputStream(path);
		FileOutputStream osm = new FileOutputStream(path+".new");
		
		getDeCompressorByInputStream(ism).deCompress(osm);
		ism.close();
		osm.close();
	}

	private static void testCompressFile(String path) throws IOException {
		
		FileInputStream ism = new FileInputStream(path);
		FileOutputStream osm = new FileOutputStream(path+".LZ77");
		
		getCompressorByInputStream(ism).compress(osm);
		ism.close();
		osm.close();
	}

	private static Compressor getCompressorByInputStream(InputStream ism) {
		return new LZ77Compressor(ism);
	}
	
	private static DeCompressor getDeCompressorByInputStream(InputStream ism) {
		return new LZ77DeCompressor(ism);
	}

	private static void testCompressString(String string) {
		byte[] byteArray = string.getBytes();
		
		long startMs,endMs;
		
		Compressor compressor = getCompressorByByteArray(byteArray);
		
		startMs = System.currentTimeMillis();
		
		byte[] compressed = compressor.compress();

		endMs = System.currentTimeMillis();
		
		System.out.println("compress cost :" + (endMs - startMs));
		
		double det = byteArray.length - compressed.length;
		
		System.out.println("capacity decrease : " + det);
		System.out.println("capacity decrease rate : " + det/byteArray.length );
		
		endMs = startMs = 0;
		
		DeCompressor deCompressor = getDeCompressorByByteArray(compressed);
		
		startMs = System.currentTimeMillis();

		byte[] deCompressed = deCompressor.deCompress();

		endMs = System.currentTimeMillis();
		
		System.out.println("decompress cost :" + (endMs - startMs));
		
		System.out.println("is compress valid : " + (byteArray.length == deCompressed.length));
		System.out.println("deCompressed string : " + new String(deCompressed));
		
	}

	private static DeCompressor getDeCompressorByByteArray(byte[] byteArray) {
		return new LZ77DeCompressor(byteArray);
	}

	private static Compressor getCompressorByByteArray(byte[] byteArray) {
		return new LZ77Compressor(byteArray);
	}


}
