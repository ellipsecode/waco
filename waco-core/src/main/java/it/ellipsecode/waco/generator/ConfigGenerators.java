package it.ellipsecode.waco.generator;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonValue;

public class ConfigGenerators {
	public static ConfigGenerator DEFAULT = new DefaultGenerator();
	
	private Map<String, ConfigGenerator> generators = new HashMap<String, ConfigGenerator>() {{
		put("JDBCSystemResources", new DatasourceGenerator());
	}};
	
	public void generate(String key, JsonValue jsonValue, Writer writer) {
		if (generators.containsKey(key)) {
			generators.get(key).generate(jsonValue.asJsonObject(), writer);
		} else {
			DEFAULT.generate(jsonValue.asJsonObject(), writer);
		}
	}

}
