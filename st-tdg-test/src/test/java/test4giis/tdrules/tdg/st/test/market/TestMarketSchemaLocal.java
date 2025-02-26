package test4giis.tdrules.tdg.st.test.market;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import giis.tdrules.client.oa.MermaidWriter;
import giis.tdrules.model.io.TdSchemaXmlSerializer;
import giis.tdrules.openapi.model.TdSchema;

/**
 * Checks the transformation of the OpenApi specification into the TdSchema model
 * (json, xml and Mermaid graph)
 */
public class TestMarketSchemaLocal extends BaseMarket {

	@Test
	public void testSchemaLocalJson() throws JsonProcessingException {
		TdSchema schema = getSchema();
		assertModel("schema-testMarket.json", serialize(schema));
	}
	
	@Test
	public void testSchemaLocalXml () {
		TdSchema schema = getSchema();
		assertModel("schema-testMarket.xml", new TdSchemaXmlSerializer().serialize(schema));
	}
	
	@Test
	public void testSchemaLocalMermaid () {
		TdSchema schema = getSchema();
		String mermaid = new MermaidWriter(schema).setGroupEntitiesInPath().getMermaid();
		assertModel("schema-testMarket.md", mermaid);
	}
}
