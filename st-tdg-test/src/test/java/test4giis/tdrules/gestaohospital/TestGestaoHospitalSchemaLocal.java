package test4giis.tdrules.gestaohospital;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import giis.tdrules.client.oa.MermaidWriter;
import giis.tdrules.model.io.TdSchemaXmlSerializer;
import giis.tdrules.openapi.model.TdSchema;

/**
 * Genera el esquema a partir de la especificacion (leida de archivo) y lo comprueba en formato json y xml
 */
public class TestGestaoHospitalSchemaLocal extends BaseGestaoHospital {

	@Test
	public void testSchemaLocalJson() throws JsonProcessingException {
		TdSchema schema = getSchema();
		assertModel("schema-hospital.json", serialize(schema));
	}
	
	@Test
	public void testSchemaLocalXml () {
		TdSchema schema = getSchema();
		assertModel("schema-hospital.xml", new TdSchemaXmlSerializer().serialize(schema));
	}

	@Test
	public void testSchemaLocalMermaid () {
		String mermaid = new MermaidWriter(getSchema()).getMermaid();
		assertModel("schema-hospital.md", mermaid);
	}
}
