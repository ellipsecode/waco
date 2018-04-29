package it.ellipsecode.waco.generator;

import java.util.Map;

import javax.json.JsonValue;

import org.apache.commons.lang3.tuple.Pair;

public class ConfigGenerators {
	public static ConfigGenerator DEFAULT = new DefaultGenerator();
	
	private Map<String, ConfigGenerator> generators = MapUtils.hashMap(
		Pair.of("JDBCSystemResources", new DatasourceGenerator()),
		Pair.of("JMSServers",          new JMSServerGenerator()),
		Pair.of("Servers",			   new ServerGenerator()),
		Pair.of("Clusters",            new ClusterGenerator()),
		Pair.of("Properties",          new PropertiesGenerator())
	);
	
	
	
	public void generate(String key, JsonValue jsonValue, WlstWriter writer) {
		if (generators.containsKey(key)) {
			generators.get(key).generate(jsonValue.asJsonObject(), writer);
		} else {
			DEFAULT.generate(jsonValue.asJsonObject(), writer);
		}
	}

}
