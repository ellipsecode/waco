package it.ellipsecode.waco.generator;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import static org.junit.Assert.*;

import it.ellipsecode.waco.generator.WlstGenerator;

public class WlstGeneratorTest {
	
	private Reader readFile(String fileName) throws Exception {
		return new InputStreamReader(this.getClass().getResourceAsStream(fileName), Charset.forName("UTF-8"));
	}
	
	private void assertSameContent(String fileName, Writer scriptWriter) throws Exception {
		assertTrue(IOUtils.contentEqualsIgnoreEOL(readFile(fileName), new StringReader(scriptWriter.toString())));
	}

	@Test
	public void testDataSource() throws Exception {
		InputStreamReader jsonReader = new InputStreamReader(this.getClass().getResourceAsStream("setup_datasource.wlst.json"));
		StringWriter scriptWriter = new StringWriter(); 
		WlstGenerator generator = new WlstGenerator();
		generator.generate(jsonReader, scriptWriter);
		assertSameContent("expected_datasource.wlst", scriptWriter);
	}

	@Test
	public void testJMSServer() throws Exception {
		Reader jsonReader = readFile("setup_jms_server.wlst.json");
		StringWriter scriptWriter = new StringWriter(); 
		WlstGenerator generator = new WlstGenerator();
		generator.generate(jsonReader, scriptWriter);
		System.out.println(scriptWriter.toString());
//		assertSameContent("expected_jms_server.wlst", scriptWriter);
	}
	
	@Test
	public void testServer() throws Exception {
		Reader jsonReader = readFile("setup_managed_server.wlst.json");
		StringWriter scriptWriter = new StringWriter(); 
		WlstGenerator generator = new WlstGenerator();
		generator.generate(jsonReader, scriptWriter);
		System.out.println(scriptWriter.toString());
		//TODO ASSERT
	}
}
