package it.ellipsecode.waco.generator;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class WlstGenerator {

	public void generate(Reader reader, Writer writer) {
		WlstWriter wlst = new WlstWriter(writer);
		generateHeader(wlst);
		try (JsonReader jsonReader = Json.createReader(reader)) {
			JsonObject jsonConfig = jsonReader.readObject();
			ConfigGenerators.DEFAULT.generate(jsonConfig, wlst);
		}
		generateFooter(wlst);
	}
	
	private void generateHeader(WlstWriter writer) {
		try {
			writer.writeln("edit()");
			writer.writeln("startEdit()");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void generateFooter(WlstWriter writer) {
		try {
			writer.writeln("save()");
			writer.writeln("activate()");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
