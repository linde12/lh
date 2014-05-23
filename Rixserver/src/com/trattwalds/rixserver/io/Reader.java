package com.trattwalds.rixserver.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Reader extends BufferedReader {

	public Reader(java.io.Reader in) throws IOException {
		super(in);
	}

	public Reader(InputStream in) throws IOException {
		super(new InputStreamReader(in));
	}

	public Reader(File file) throws IOException {
		super(new FileReader(file));
	}

	public Reader(String path) throws IOException {
		super(new FileReader(new File(path)));
	}
}
