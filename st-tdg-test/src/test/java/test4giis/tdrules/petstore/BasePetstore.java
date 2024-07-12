package test4giis.tdrules.petstore;

import giis.tdrules.client.oa.OaSchemaApi;
import giis.tdrules.client.oa.OaSchemaIdResolver;
import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.IAttrGen;
import giis.tdrules.store.loader.gen.DictionaryAttrGen;
import test4giis.tdrules.BaseAll;

public class BasePetstore  extends BaseAll{
	//Para la generacion "live" arrancar antes el container de petstore con docker-run (asegurar tener el puerto 8081 libre)
	protected static final String PETSTORE_SCHEMA_LOCAL = "swagger-petstore-main-fork/src/main/resources/openapi.yaml";
	protected static final String PETSTORE_SCHEMA_LIVE = "http://localhost:8081/api/v3/openapi.json";
	private static final String PETSTORE_URL_LIVE = "http://localhost:8081/api/v3";
	
	@Override
	protected String getSutName() {
		return "petstore";
	}
	
	@Override
	protected String getServerUrl() {
		return PETSTORE_URL_LIVE;
	}

	@Override
	protected String getAllDataLiveEndpoint() {
		return PETSTORE_URL_LIVE + "/test/getAll";
	}

	@Override
	protected String getDeleteAllDataLiveEndpoint() {
		return PETSTORE_URL_LIVE + "/test/deleteAll";
	}

	@Override
	protected TdSchema getSchema() {
		// Configure the schema id resolver to use id attribute as uid, but there are exceptions:
		// - Tag has an id, but looking at the source code, a post inserts unconditionally,
		//   allowing repeated id values. Considers this id as no uid
		// - Order0 has been artificially created for some tests, it does not follow strictely
		//   the conventions (attribute petId references Pet0.id)
		OaSchemaApi api = new OaSchemaApi(PETSTORE_SCHEMA_LOCAL)
				.setIdResolver(new OaSchemaIdResolver().setIdName("id")
						.excludeEntity("Tag").excludeEntity("Order0"));
		return api.getSchema();
	}

	/**
	 * Instancia un generador de datos configurado con un diccionario para que los datos
	 * generados no sean solo numeros, sino valores procedentes de un diccionario o mascaras
	 */
	protected IAttrGen getDictionaryAttrGen() {
		return new DictionaryAttrGen()
				//https://www.southernliving.com/most-popular-pet-names-rover-6829769
				.with("Pet", "name").dictionary("Max", "Luna", "Charlie", "Bella", "Cooper", "Daisy", "Milo", "Lucy")
				.with("Pet_photoUrls_xa", "photoUrls").padLeft('0', 6).mask("http://localhost/photos/{}.jpg")
				.with("Pet_Tags_xa", "name").dictionary("Puppy", "Young", "Old")
				.with("Category", "name").dictionary("Tiger", "Lion", "Monkey", "Snake")
				.with("Customer_address_xa", "street").dictionary("Main St", "Broadway", "Park Ave", "Fulton St", "Madison Ave", "Pine St", "Amsterdam Ave", "Wall St")
				//https://www.ssa.gov/oact/babynames/decades/century.html
				//https://www.al.com/news/2019/10/50-most-common-last-names-in-america.html
				.with("Customer", "username").dictionary("James Smith", "Mary Johnson", "Robert Williams", "Patricia Brown", "David Garcia", "Elizabeth Miller", "William Davis", "Barbara Wilson")
				//https://www.worldatlas.com/articles/most-common-town-and-city-names-in-the-u-s-a.html#:~:text=Washington,this%20way%20is%20no%20surprise.
				.with("Customer_address_xa", "city").dictionary("Springfield", "Franklin", "Greenville", "Bristol", "Clinton", "Fairview", "Salem", "Madison")
				.with("Customer_address_xa", "state").dictionary("California", "Texas", "Florida", "New York", "Pennsylvania", "Illinois", "Ohio", "Georgia")
				.with("Customer_address_xa", "zip").padLeft('0', 6)
				;
	}

}
