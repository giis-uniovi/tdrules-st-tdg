package test4giis.tdrules.petstore;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import giis.tdrules.client.oa.MermaidWriter;
import giis.tdrules.model.io.TdSchemaXmlSerializer;
import giis.tdrules.model.transform.SchemaSorter;
import giis.tdrules.openapi.model.TdSchema;

/**
 * Genera el esquema a partir de la especificacion (leida de archivo) y lo comprueba en formato json y xml
 */
public class TestPetstoreSchemaLocal extends BasePetstore {

	@Test
	public void testSchemaLocalJson() throws JsonProcessingException {
		TdSchema schema = getSchema();
		assertModel("schema-petstore.json", serialize(schema));
	}
	
	@Test
	public void testSchemaLocalXml () {
		TdSchema schema = getSchema();
		assertModel("schema-petstore.xml", new TdSchemaXmlSerializer().serialize(schema));
	}

	@Test
	public void testSchemaLocalMermaid () {
		String mermaid = new MermaidWriter(getSchema()).getMermaid();
		assertModel("schema-petstore.md", mermaid);
	}

	//Regression test for QAGrow#28
	@Test
	public void testSchemaGetTableList () {
		TdSchema schema = getSchema();
		assertEquals("[Order, Customer_address_xa, Customer, Address, Category, User,"
				+ " Tag, Pet_category_xt, Pet_photoUrls_xa, Pet_tags_xa, Pet,"
				+ " Customer0, Order0, Pet0_category_xt, Pet0, Pet1_category_xt, Pet1, ApiResponse]", 
				schema.getEntityNames().toString());
	}

	//Regression test for QAGrow#29
	@Test
	public void testSchemaOrderTables () {
		TdSchema schema = getSchema();
		List<String> tables = schema.getEntityNames();
		assertEquals("[Category, Pet_category_xt, Pet, Customer, Order, Customer_address_xa, Address, User,"
				+ " Tag, Pet_photoUrls_xa, Pet_tags_xa, Customer0, Pet0_category_xt, Pet0, Order0, Pet1_category_xt, Pet1, ApiResponse]", 
				new SchemaSorter(schema).sort(tables).toString());
	}

}
