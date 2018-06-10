package it.ellipsecode.waco.generator;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class DefaultGenerator implements ConfigGenerator {
	private ConfigGenerators generators = new ConfigGenerators();
	private final static String GET_MBEAN_REFERENCE = "REF#";
	private final static String OBJECT_NAME_REFERENCE = "com.bea:Name";

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
				} else if (value.getValueType() == ValueType.NUMBER) {
					BigDecimal numberValue = ((JsonNumber)value).bigDecimalValue();
					NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
					nf.setGroupingUsed(false);
					String stringValue = nf.format(numberValue);
					wlst.writeln("set('" + key + "'," + stringValue + ")");
				} else if (value.getValueType() == ValueType.OBJECT) {
					wlst.cd(key);
					generators.generate(key, value, wlst);
					wlst.cdUp();
				} else if (value.getValueType() == ValueType.ARRAY) {
					wlst.writeln("set('" + key + "'," + generateArray(key, value) + ")");
				} else if (value == JsonValue.TRUE) {
					wlst.writeln("set('" + key + "', true)");
				} else if (value == JsonValue.FALSE) {
					wlst.writeln("set('" + key + "', false)");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private boolean isReference(JsonValue value) {
		String stringValue = removeQuotes(value);
		return stringValue.startsWith(GET_MBEAN_REFERENCE);
	}
	
	private boolean isObjectName(JsonValue value) {
		String stringValue = removeQuotes(value);
		return stringValue.startsWith(OBJECT_NAME_REFERENCE);
	}

	private String generateMBeanReference(String key, JsonValue value) {
		String stringValue = removeQuotes(value);
		String realValue = stringValue.replace(GET_MBEAN_REFERENCE, "");
		String mBeanReference = "getMBean('" + realValue + "')";
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
			return "Object";
		} else if(isObjectName(value)) {
			return "ObjectName";
		} else {
			return "String";
		}
	}
		
	private String arrayElement(String key, JsonValue element) {
		if (element.getValueType() == ValueType.STRING) {
			if (isObjectName(element)) {
				return "ObjectName(str(" + element.toString() + "))";
			} else if(isReference(element)) {
				return generateMBeanReference(key, element);
			} else {
				return "String(" + element.toString() + ")";
			}
		} else {
			return "";
		}
	}

	private String removeQuotes(JsonValue element) {
		String stringElement = element.toString();
		stringElement = stringElement.substring(1, stringElement.length()-1);
		return stringElement;
	}

}
