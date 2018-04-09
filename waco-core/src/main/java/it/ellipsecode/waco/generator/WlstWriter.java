package it.ellipsecode.waco.generator;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;

public class WlstWriter {
	private Writer writer;
	private int indent = 0;
	
	public WlstWriter(Writer writer) {
		this.writer = writer;
	}
	
	public void indent() {
		indent += 2;
	}
	
	public void endIndent() {
		indent -= 2;
	}
	
	public void writeln(String line) throws IOException {
		writer.write(StringUtils.repeat(' ', indent));
		writer.write(line);
		writer.write('\n');
	}
	
	public void cd(String name) throws IOException {
		writeln("cd('"+name+"')");
	}
	
	public void cdUp() throws IOException {
		cd("..");
	}
	
}
