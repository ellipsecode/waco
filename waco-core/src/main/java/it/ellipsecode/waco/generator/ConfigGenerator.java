package it.ellipsecode.waco.generator;

import javax.json.JsonObject;

public interface ConfigGenerator {
	
	public void generate(JsonObject jsonConfig, WlstWriter writer);

}
