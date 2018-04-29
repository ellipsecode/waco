package it.ellipsecode.waco.generator;

import java.io.IOException;

import javax.json.JsonObject;

public class PropertiesGenerator implements ConfigGenerator {

	@Override
	public void generate(JsonObject jsonConfig, WlstWriter writer) {
		jsonConfig.forEach(
				(k1,v1) -> {
					try {
						writer.cd(k1);
						v1.asJsonObject().forEach(
							(k2,v2) -> {
								try {
									writer.cd(k2);
									v2.asJsonObject().forEach(
										(key, value) -> { 
											generateProperty(key, value.toString(), writer);
										});
									writer.cdUp();
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							});
						writer.cdUp();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
		);
	}

	private void generateProperty(String key, String value, WlstWriter writer) {
		try {
			writer.writeln("cmo.createProperty('"+key+"')");
			writer.cd(key);
			writer.writeln("cmo.setValue("+value+")");
			writer.cdUp();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
