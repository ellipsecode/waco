package it.amattioli.setupwl.generator;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue.ValueType;

public class WlstGenerator {

	public void generate(Reader reader, Writer writer) {
		generateHeader(writer);
		try (JsonReader jsonReader = Json.createReader(reader)) {
			JsonObject jsonConfig = jsonReader.readObject();
			generateConfig(jsonConfig, writer);
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
	
	private void generateConfig(JsonObject jsonConfig, Writer writer) {
		jsonConfig.forEach((key, value) -> {
			try {
				if (value.getValueType() == ValueType.STRING) {
					writer.write("set('"+key+"',"+value+")\n");
				} else if (value.getValueType() == ValueType.OBJECT) {
					writer.write("cd('"+key+"')\n");
					if ("JDBCSystemResources".equals(key)) {
						generateDatasources(value.asJsonObject(), writer);
					} else {
						generateConfig(value.asJsonObject(), writer);
					}
					writer.write("cd('..')\n");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	private void generateDatasources(JsonObject jsonConfig, Writer writer) {
			jsonConfig.forEach((key, value) -> {
				generateDatasource(key, value.asJsonObject(), writer);
			});
	}
	
	private void generateDatasource(String name, JsonObject jsonConfig, Writer writer) {
		try {
			writer.write("if (ls().find('"+name+"') == -1):\n");
			writer.write("  cmo.createJDBCSystemResource('"+name+"')\n");
			writer.write("cd('"+name+"')\n");
			generateConfig(jsonConfig, writer);
			writer.write("cd('..')\n");
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
