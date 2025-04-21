package test4giis.tdrules.tdg.st.test.gestaohospital;

import giis.tdrules.client.oa.OaSchemaApi;
import giis.tdrules.client.oa.OaSchemaIdResolver;
import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.gen.DictionaryAttrGen;
import giis.tdrules.store.loader.gen.IAttrGen;
import test4giis.tdrules.tdg.st.test.BaseAll;

/**
 * Common configuration and customization for the all GestaoHospital tests that
 * use entities in the OpenAPI model.
 */

public class BaseGestaoHospital extends BaseAll{

	protected static final String GESTAOHOSPITAL_SCHEMA_LOCAL = "../sut-gestaoHospital/src/main/resources/gestaohospital-rest.json";
	protected static final String GESTAOHOSPITAL_SCHEMA_LIVE = "http://localhost:8085/v2/api-docs";
	private static final String GESTAOHOSPITAL_URL_LIVE = "http://localhost:8085";
	
	@Override
	protected String getSutName() {
		return "gestaoHospital";
	}

	@Override
	protected String getServerUrl() {
		return GESTAOHOSPITAL_URL_LIVE;
	}

	@Override
	protected String getAllDataLiveEndpoint() {
		return GESTAOHOSPITAL_URL_LIVE + "/v1/hospitais/test/getAll";
	}

	@Override
	protected String getDeleteAllDataLiveEndpoint() {
		return GESTAOHOSPITAL_URL_LIVE + "/v1/hospitais/test/deleteAll";
	}

	@Override
	protected TdSchema getSchema() {
		//Configure the schema id resolver to use id attributes as uid.
		OaSchemaApi api = new OaSchemaApi(GESTAOHOSPITAL_SCHEMA_LOCAL)
				.setIdResolver(new OaSchemaIdResolver().setIdName("id"));
		return api.getSchema();
	}

	/**
	 * Instantiate a data generator with realistic values from a dictionary
	 */
	protected IAttrGen getDictionaryAttrGen() {
		return new DictionaryAttrGen()
				 //https://forbes.es/forbes-panel/121316/los-20-mejores-hospitales-del-mundo/
				.with("HospitalDTO", "name").dictionary("Mayo Clinic", "John Hopkins Hospital", "Singapore General Hospital", "Karolinska University Hospital", "Cleveland Clinic") 
				.with("HospitalDTO", "address").dictionary("2nd Avenue Southwest, Rochester, Olmsted County, Minnesota, 55902, USA", // 44.0181799, -92.4659151
						                                   "5th Avenue South, Downtown, Saint Petersburg, Pinellas County, Florida, 33701, USA", //27.7657,-82.6385709
						                                   "Everton Road, Bukit Merah, Singapur, Central Region, 088860, Singapore", //1.276579,103.837679
						                                   "Maria Aspmans gata, Solna Kyrkby, Solna kommun, Provincia de Estocolmo, 113 66, Sweden", //59.3506023,18.0363573
						                                   "Cleveland Clinic, 9500, Euclid Avenue, Fairfax, Cleveland, Cuyahoga County, Ohio, 44106, USA") //41.50217195,-81.61981946090904	
				.with("ProductDTO", "name").dictionary("Rice","Blood A+","Blood A-","Milk","Bread","Blood B+","Meat","Blood B-")
				.with("ProductDTO", "productName").dictionary("Rice","Blood A+","Blood A-","Milk","Bread","Blood B+","Meat","Blood B-")
				.with("ProductDTO", "description").dictionary ("Food","Blood Bank","Blood Bank","Food","Food","Blood Bank","Food","Blood Bank")
				;
	}
	
}