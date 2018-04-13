package it.ellipsecode.waco.generator;

import java.io.IOException;

import javax.json.JsonObject;

public class ClusterGenerator implements ConfigGenerator {

	@Override
	public void generate(JsonObject jsonConfig, WlstWriter writer) {
		jsonConfig.forEach((key, value) -> {
			generateCluster(key, value.asJsonObject(), writer);
		});
		
	}

	private void generateCluster(String name, JsonObject jsonConfig, WlstWriter wlst) {
		try {
			wlst.writeln("if (ls().find('"+name+"') == -1):");
			wlst.indent();
				wlst.writeln("create('"+name+"', 'Cluster')");
			wlst.endIndent();
			wlst.cd(name);
			ConfigGenerators.DEFAULT.generate(jsonConfig, wlst);
			wlst.cdUp();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
