package it.ellipsecode.waco.generator;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class DefaultGenerator implements ConfigGenerator {
	private ConfigGenerators generators = new ConfigGenerators();
	private final static String GET_MBEAN_REFERENCE = "\"REF#";

	@Override
	public void generate(JsonObject jsonConfig, WlstWriter wlst) {
		jsonConfig.forEach((key, value) -> {
			try {
				if (value.getValueType() == ValueType.STRING) {
					String stringValue;
					if (value.toString().startsWith(GET_MBEAN_REFERENCE)) {
						stringValue = generateMBeanReference(key, value);
					} else {
						stringValue = value.toString();
					}
					wlst.writeln("set('" + key + "'," + stringValue + ")");
				} else if (value.getValueType() == ValueType.OBJECT) {
					wlst.cd(key);
					generators.generate(key, value, wlst);
					wlst.cdUp();
				} else if (value.getValueType() == ValueType.ARRAY) {
					wlst.writeln("set('" + key + "'," + generateArrayReferences(key, value) + ")");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private String generateMBeanReference(String key, JsonValue value) {
		String realValue = value.toString().replace(GET_MBEAN_REFERENCE, "");
		String mBeanReference = "getMBean('/" + mapRootDirectories(key) + "/" + realValue + "')";
		return mBeanReference;
	}

	private String generateArrayReferences(String key, JsonValue value) {
		JsonArray array = value.asJsonArray();
		return array.stream()
		.map(element -> arrayReference(key, element))
		.collect(Collectors.joining(",", "jarray.array([", " ], ObjectName))"));
	}
		
	private String arrayReference(String key, JsonValue element) {
		if (element.getValueType() == ValueType.STRING) {
			String ref = element.toString().startsWith(GET_MBEAN_REFERENCE)
					? element.toString().replace(GET_MBEAN_REFERENCE, "") : element.toString();

			return ("ObjectName('com.bea:Name=" + ref + ",Type=" + key + "')"); // TODO
																					// mappare
																					// correttamente
																					// la
																					// key
		} else {
			return "";
		}
	}
	
	private String mapRootDirectories(String attribute) {
		switch (attribute) {
		case "Machine":
			return "Machines";
		case "Cluster":
			return "Clusters";
		default:
			return attribute;
		}
	}

}
