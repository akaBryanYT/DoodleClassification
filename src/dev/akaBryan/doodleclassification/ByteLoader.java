package dev.akaBryan.doodleclassification;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ByteLoader {

	public static byte[] loadBytes(String pathName) {
		System.out.println(pathName);
		Path path = null;
		try {
			path = Paths.get(ByteLoader.class.getResource(pathName).toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		byte[] data = {};
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("You successfully loaded your file");
		return data;
	}
	
}
