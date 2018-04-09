package it.ellipsecode.waco.generator;

import java.io.IOException;

import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;

public class DefaultGenerator implements ConfigGenerator {
	private ConfigGenerators generators = new ConfigGenerators();

	@Override
	public void generate(JsonObject jsonConfig, WlstWriter wlst) {
		jsonConfig.forEach((key, value) -> {
			try {
				if (value.getValueType() == ValueType.STRING) {
					wlst.writeln("set('"+key+"',"+value+")");
				} else if (value.getValueType() == ValueType.OBJECT) {
					wlst.cd(key);
					generators.generate(key, value, wlst);
					wlst.cdUp();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
