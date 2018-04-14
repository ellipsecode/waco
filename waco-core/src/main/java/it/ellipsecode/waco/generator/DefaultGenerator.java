package it.ellipsecode.waco.generator;

import java.io.IOException;

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
					generateArrayReferences(key, value, wlst);
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

	private void generateArrayReferences(String key, JsonValue value, WlstWriter wlst) {
		JsonArray array = (JsonArray) value;
		boolean isFirstRow = true;
		try {
			wlst.write("set('" + key + "',jarray.array([");
			for (JsonValue element : array) {
				if (!isFirstRow)
					wlst.write(",");
				isFirstRow = false;
				if (element.getValueType() == ValueType.STRING) {
					String ref = element.toString().startsWith(GET_MBEAN_REFERENCE)
							? element.toString().replace(GET_MBEAN_REFERENCE, "") : element.toString();

					wlst.write("ObjectName('com.bea:Name=" + ref + ",Type=" + key + ")"); // TODO
																							// mappare
																							// correttamente
																							// la
																							// key
				}
			}
			wlst.write(" ], ObjectName))");
			wlst.endLine();
		} catch (

		IOException e) {
			e.printStackTrace();
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
