package it.ellipsecode.waco.generator;

import java.io.Writer;

import javax.json.JsonObject;

public interface ConfigGenerator {
	
	public void generate(JsonObject jsonConfig, Writer writer);

}
