package it.ellipsecode.waco.generator;

import java.io.IOException;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class DefaultGenerator implements ConfigGenerator {
	private ConfigGenerators generators = new ConfigGenerators();
	private final static String GET_MBEAN_REFERENCE = "\"REF#";
	private final static String GET_ARRAY_REFERENCE = "\"REFS#";

	@Override
	public void generate(JsonObject jsonConfig, WlstWriter wlst) {
		jsonConfig.forEach((key, value) -> {
			try {
				if (value.getValueType() == ValueType.STRING) {
					if (value.toString().startsWith(GET_ARRAY_REFERENCE)) {
						generateArrayReferences(key, value, wlst);
					} else if (value.toString().startsWith(GET_MBEAN_REFERENCE)) {
						generateMBeanReference(key, value, wlst);
					} else {
						wlst.writeln("set('" + key + "'," + value + ")");
					}
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

	private void generateMBeanReference(String key, JsonValue value, WlstWriter wlst) {
		try {
			String realValue = value.toString().replace(GET_MBEAN_REFERENCE, "");
			String mBeanReference = "getMBean('/"+key+"/"+realValue+"')";
			wlst.writeln("set('" + key + "'," + mBeanReference + ")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String mapRootDirectories(String attribute){
		switch (attribute){
		case "Machine":
			return "Machines";
		case "Cluster":
			return "Clusters";
		default:
			return attribute;
		}
	}

	private void generateArrayReferences(String key, JsonValue value, WlstWriter wlst) {
		String[] refs = value.toString().split("GET_MBEAN_REFERENCE");
		boolean isFirstRow = true;
		try {
			wlst.write("set('"+key+"',jarray.array([");
			for (String ref : refs) {
				if(!isFirstRow)
					wlst.write(",");
				isFirstRow = false;
				wlst.write("ObjectName('com.bea:Name="+ref+",Type="+key+")"); //mappa corretta key
			}
			wlst.write(" ], ObjectName))");
			wlst.endLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
