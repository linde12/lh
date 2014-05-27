package com.trattwalds.rixserver.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Writer extends BufferedWriter {

	public Writer(java.io.Writer out) throws IOException {
		super(out);
	}

	public Writer(OutputStream out) throws IOException {
		super(new OutputStreamWriter(out));
	}

	public Writer(File file) throws IOException {
		super(new FileWriter(file));
	}

	public Writer(String path) throws IOException {
		super(new FileWriter(new File(path)));
	}

}
