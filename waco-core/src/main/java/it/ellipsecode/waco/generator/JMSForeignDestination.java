package it.ellipsecode.waco.generator;

import java.io.IOException;

import javax.json.JsonObject;

public class JMSForeignDestination implements ConfigGenerator {

	@Override
	public void generate(JsonObject jsonConfig, WlstWriter writer) {
		jsonConfig.forEach((key, value) -> {
			generateJMSForeignDestination(key, value.asJsonObject(), writer);
		});
	}

	private void generateJMSForeignDestination(String name, JsonObject jsonConfig, WlstWriter wlst) {
		try{
			wlst.writeln("if (ls().find('"+name+"') == -1):");
			wlst.indent();
				wlst.writeln("create('"+name+"', 'ForeignDestination')");
			wlst.endIndent();
			wlst.cd(name);
			ConfigGenerators.DEFAULT.generate(jsonConfig, wlst);
			wlst.cdUp();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
