package test4giis.tdrules.tdg.st.test.petstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giis.tdrules.client.oa.OaSchemaApi;
import giis.tdrules.client.oa.OaSchemaIdResolver;
import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.gen.DictionaryAttrGen;
import giis.tdrules.store.loader.gen.IAttrGen;
import test4giis.tdrules.tdg.st.test.BaseAll;

/**
 * Common configuration and customization for all Swagger Petstore tests.
 * 
 * There are two different sets of test classes:
 * 
 * - Classes containing Petstore0 in the name: It is an initial 
 *   proof of concept of TDG using simplified entities (Pet0, Pet1...)
 *   from the Swagger Petstore. Tests in this classes also serve to 
 *   illustrate by means of examples the main transformations performed
 *   on the model and the coverage rules.
 *   
 * - Classes containing Petstore in the name: They use the real
 *   entities in the Petstore OpenApi model.
 *     
 * Documentation is more exhaustive in TestPetstore0DatagenLocal and TestPetstoreDatagenLocal.
 * Other test classes contain different variants to include data generation and a live SUT.
 */
public class BasePetstore  extends BaseAll{
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected static final String PETSTORE_SCHEMA_LOCAL = "../sut-petstore/src/main/resources/openapi.yaml";
	protected static final String PETSTORE_SCHEMA_LIVE = "http://localhost:8081/api/v3/openapi.json";
	private static final String PETSTORE_URL_LIVE = "http://localhost:8081/api/v3";
	
	// Cache for the schema to avoid multiple calls per test.
	private static TdSchema schemaCache = null;
	
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
		if (schemaCache == null) {
			log.info("*** Begin: Transform the Petstore schema and save to cache");
			schemaCache = getPetstoreSchema();
			log.info("*** End: Transform the Petstore schema and save to cache");
		} else {
			log.info("*** Get the Petstore schema from cache");
		}
		return schemaCache;
	}
	private TdSchema getPetstoreSchema() {
		// Configures the schema idResolver to use id attribute as uid, but there are exceptions:
		// - Tag entity has an id, but looking at the source code, a post endpoint inserts unconditionally,
		//   allowing repeated id values. Considers this id as no uid
		// - Order0 entity has been created for some tests. It does not strictly follow
		//   the conventions (attribute petId references Pet0.id)
		OaSchemaApi api = new OaSchemaApi(PETSTORE_SCHEMA_LOCAL)
				.setIdResolver(new OaSchemaIdResolver().setIdName("id")
						.excludeEntity("Tag").excludeEntity("Order0"));
		return api.getSchema();
	}

	/**
	 * Dictionary to load more user friendly Petstore data. It includes values enumerations for strings and masks
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
