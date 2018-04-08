package it.ellipsecode.waco.generator;

import java.io.Writer;
import java.util.Map;

import javax.json.JsonValue;

import org.apache.commons.lang3.tuple.Pair;

public class ConfigGenerators {
	public static ConfigGenerator DEFAULT = new DefaultGenerator();
	
	private Map<String, ConfigGenerator> generators = MapUtils.hashMap(
		Pair.of("JDBCSystemResources", new DatasourceGenerator()),
		Pair.of("JMSServers",          new JMSServerGenerator())
	);
	
	public void generate(String key, JsonValue jsonValue, Writer writer) {
		if (generators.containsKey(key)) {
			generators.get(key).generate(jsonValue.asJsonObject(), writer);
		} else {
			DEFAULT.generate(jsonValue.asJsonObject(), writer);
		}
	}

}
