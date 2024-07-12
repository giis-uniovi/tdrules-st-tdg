package test4giis.tdrules.tdg.st.eval.petstore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import giis.tdrules.client.TdRulesApi;
import giis.tdrules.client.oa.OaSchemaApi;
import giis.tdrules.client.oa.OaSchemaIdResolver;
import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.IAttrGen;
import giis.tdrules.store.loader.gen.DictionaryAttrGen;
import giis.tdrules.store.loader.oa.ApiWriter;
import giis.tdrules.store.loader.oa.IPathResolver;
import giis.tdrules.store.loader.oa.OaLiveAdapter;
import giis.tdrules.store.loader.oa.OaPathResolver;
import giis.visualassert.Framework;
import giis.visualassert.VisualAssert;
import giis.visualassert.portable.FileUtil;
import in2test.application.qagrow.QAGrowApiProcess;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilities and creation of the main objects to generate, load and evaluate the petstore
 * Test Data Generation (TDG)
 */
@Slf4j
public class Base {
	protected static final String PETSTORE_SCHEMA_LOCAL = "../swagger-petstore-main-fork/src/main/resources/openapi.yaml";

	protected static TdSchema model; // readonly, created before all tests

	@Rule
	public TestName testName = new TestName();

	@Before
	public void setUp() {
		log.info("****** Running test: {} ******", testName.getMethodName());
	}

	protected void load(ApiWriter writer, String query) {
		// Configure the schema id resolver to use id attribute as uid.
		// The exception is the Tag entity, that has an id attribute,
		// but looking at the source code, a post inserts unconditionally,
		// allowing repeated id values. This is considered as a non uid.
		OaSchemaApi schemaApi=new OaSchemaApi(PETSTORE_SCHEMA_LOCAL)
				.setIdResolver(new OaSchemaIdResolver().setIdName("id").excludeEntity("Tag").excludeEntity("Order0"));
		model = schemaApi.getSchema();
		
		// Check the schema, because their changes may invalidate all results
		String modelStr = new giis.tdrules.model.io.ModelJsonSerializer().serialize(model, true);
		FileUtil.fileWrite("target/schema-petstore.json", modelStr);
		VisualAssert va = new VisualAssert().setFramework(Framework.JUNIT4);
		va.assertEquals(FileUtil.fileRead("src/test/resources/schema-petstore.json"), modelStr);
		// Until here, this should be run once in BeforeClass, but some side effects on QAGrow#40 prevent doing that

		// The path resolver is created using a controller mock (writer)
		// and the data loader using a dictionary
		IPathResolver pathResolver = new OaPathResolver().setSchemaModel(model).setServerUrl("").setApiWriter(writer);
		DataLoader loader = new DataLoader(model, new OaLiveAdapter(pathResolver)).setAttrGen(getPetstoreDictionary().setMinYear(2024));

		// Generation and loading: Each test first delete all data previous to the generation and load
		writer.delete("/test/deleteAll");
		QAGrowApiProcess qagrow = new QAGrowApiProcess(model, new TdRulesApi(), loader);
		qagrow.genData4ApiQuery(query);
	}

	/**
	 * Instancia un generador de datos configurado con un diccionario para que los
	 * datos generados no sean solo numeros, sino valores procedentes de un
	 * diccionario o mascaras
	 */
	protected IAttrGen getPetstoreDictionary() {
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
				.with("Order", "quantity").setInterval(1, 13)
				;
	}

}
