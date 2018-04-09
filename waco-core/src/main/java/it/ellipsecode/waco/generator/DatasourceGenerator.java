package it.ellipsecode.waco.generator;

import java.io.IOException;

import javax.json.JsonObject;

public class DatasourceGenerator implements ConfigGenerator {

	@Override
	public void generate(JsonObject jsonConfig, WlstWriter writer) {
		jsonConfig.forEach((key, value) -> {
			generateDatasource(key, value.asJsonObject(), writer);
		});
	}
	
	private void generateDatasource(String name, JsonObject jsonConfig, WlstWriter wlst) {
		try {
			wlst.writeln("if (ls().find('"+name+"') == -1):");
			wlst.indent();
				wlst.writeln("cmo.createJDBCSystemResource('"+name+"')");
			wlst.endIndent();
			wlst.cd(name);
			ConfigGenerators.DEFAULT.generate(jsonConfig, wlst);
			wlst.cdUp();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
