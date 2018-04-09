package it.ellipsecode.waco.generator;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class MapUtils {

	private MapUtils() {
	}
	
	@SafeVarargs
	public static <T extends Enum<T>,U> Map<T, U> enumMap(Class<T> enumClass, Pair<T, U>... pairs) {
		EnumMap<T, U> result = new EnumMap<>(enumClass);
		for (Pair<T,U> pair: pairs) {
			result.put(pair.getKey(), pair.getValue());
		}
		return result;
	}
	
	@SafeVarargs
	public static <T,U> Map<T, U> hashMap(Pair<T, U>... pairs) {
		HashMap<T, U> result = new HashMap<>();
		for (Pair<T,U> pair: pairs) {
			result.put(pair.getKey(), pair.getValue());
		}
		return result;
	}
	
}
