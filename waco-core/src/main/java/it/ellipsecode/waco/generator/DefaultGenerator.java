package it.ellipsecode.waco.generator;

import java.io.IOException;
import java.io.Writer;

import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;

public class DefaultGenerator implements ConfigGenerator {
	private ConfigGenerators generators = new ConfigGenerators();

	@Override
	public void generate(JsonObject jsonConfig, Writer writer) {
		jsonConfig.forEach((key, value) -> {
			try {
				if (value.getValueType() == ValueType.STRING) {
					writer.write("set('"+key+"',"+value+")\n");
				} else if (value.getValueType() == ValueType.OBJECT) {
					writer.write("cd('"+key+"')\n");
					generators.generate(key, value, writer);
					writer.write("cd('..')\n");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
