package it.ellipsecode.waco.generator;

import java.io.IOException;
import java.io.Writer;

import javax.json.JsonObject;

public class JMSServerGenerator implements ConfigGenerator {

	@Override
	public void generate(JsonObject jsonConfig, Writer writer) {
		jsonConfig.forEach((key, value) -> {
			generateJMSServer(key, value.asJsonObject(), writer);
		});
	}
	
	private void generateJMSServer(String name, JsonObject jsonConfig, Writer writer) {
		try {
			writer.write("if (ls().find('"+name+"') == -1):\n");
			writer.write("  create('"+name+"', 'JMSServer')\n");
			writer.write("cd('"+name+"')\n");
			ConfigGenerators.DEFAULT.generate(jsonConfig, writer);
			writer.write("cd('..')\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
