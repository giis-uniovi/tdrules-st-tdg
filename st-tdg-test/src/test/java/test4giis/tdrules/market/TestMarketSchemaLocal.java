package test4giis.tdrules.market;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import giis.tdrules.client.oa.MermaidWriter;
import giis.tdrules.model.io.TdSchemaXmlSerializer;
import giis.tdrules.openapi.model.TdSchema;

/**
 * Genera el esquema a partir de la especificacion (leida de archivo) y lo comprueba en formato json y xml
 */
public class TestMarketSchemaLocal extends BaseMarket {

	@Test
	public void testSchemaLocalJson() throws JsonProcessingException {
		TdSchema schema = getSchema();
		assertModel("schema-marketWithoutArrays.json", serialize(schema));
	}
	
	@Test
	public void testSchemaLocalXml () {
		TdSchema schema = getSchema();
		assertModel("schema-marketWithoutArrays.xml", new TdSchemaXmlSerializer().serialize(schema));
	}
	
	@Test
	public void testSchemaLocalMermaid () {
		TdSchema schema = getSchema();
		String mermaid = new MermaidWriter(schema).getMermaid();
		assertModel("schema-marketWithoutArrays.md", mermaid);
	}
}
