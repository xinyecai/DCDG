package com.util.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	public static String getLine(String file){
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			return line;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getAllLines(String file) {
		List<String> Arr = new ArrayList<>();
		String line;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			while ((line = bufferedReader.readLine()) != null) {
				Arr.add(line);
			}
			return Arr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
