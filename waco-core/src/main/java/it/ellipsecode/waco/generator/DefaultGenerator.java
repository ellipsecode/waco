package it.ellipsecode.waco.generator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
					if (isReference(value)) {
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
					wlst.writeln("set('" + key + "'," + generateArray(key, value) + ")");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private boolean isReference(JsonValue value) {
		return value.toString().startsWith(GET_MBEAN_REFERENCE);
	}

	private String generateMBeanReference(String key, JsonValue value) {
		String realValue = value.toString().replace(GET_MBEAN_REFERENCE, "");
		String mBeanReference = "getMBean('/" + mapRootDirectories(key) + "/" + realValue + "')";
		return mBeanReference;
	}

	private String generateArray(String key, JsonValue value) {
		JsonArray array = value.asJsonArray();
		Set<String> arrayTypes = array.stream().map(this::arrayElementType).collect(Collectors.toSet());
		if (arrayTypes.size() != 1) {
			throw new RuntimeException("Multiple array type");
		}
		String arrayType = arrayTypes.iterator().next();
		return array.stream()
		.map(element -> arrayElement(key, element))
		.collect(Collectors.joining(",", "jarray.array([", " ], "+arrayType+")"));
	}
	
	private String arrayElementType(JsonValue value) {
		if (isReference(value)) {
			return "ObjectName";
		} else {
			return "String";
		}
	}
		
	private String arrayElement(String key, JsonValue element) {
		if (element.getValueType() == ValueType.STRING) {
			if (isReference(element)) {
				return "ObjectName('com.bea:Name=" + element.toString().replace(GET_MBEAN_REFERENCE, "") + ",Type=" + key + "')"; // TODO
																						// mappare
																						// correttamente
																						// la
																						// key
			} else {
				return "String(" + element.toString() + ")";
			}
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
