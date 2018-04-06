package it.ellipsecode.waco.generator;

import java.io.InputStreamReader;
import java.io.StringWriter;

import org.junit.Test;

import it.ellipsecode.waco.generator.WlstGenerator;

public class WlstGeneratorTest {

	@Test
	public void testDataSource() {
		InputStreamReader jsonReader = new InputStreamReader(this.getClass().getResourceAsStream("setup_datasource.wlst.json"));
		StringWriter scriptWriter = new StringWriter(); 
		WlstGenerator generator = new WlstGenerator();
		generator.generate(jsonReader, scriptWriter);
		System.out.println(scriptWriter.toString());
	}
	
}
