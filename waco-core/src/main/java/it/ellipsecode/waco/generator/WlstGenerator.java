package it.ellipsecode.waco.generator;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class WlstGenerator {

	public void generate(Reader reader, Writer writer) {
		generateHeader(writer);
		try (JsonReader jsonReader = Json.createReader(reader)) {
			JsonObject jsonConfig = jsonReader.readObject();
			ConfigGenerators.DEFAULT.generate(jsonConfig, writer);
		}
		generateFooter(writer);
	}
	
	private void generateHeader(Writer writer) {
		try {
			writer.write("edit()\n");
			writer.write("startEdit()\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void generateFooter(Writer writer) {
		try {
			writer.write("save()\n");
			writer.write("activate()\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
