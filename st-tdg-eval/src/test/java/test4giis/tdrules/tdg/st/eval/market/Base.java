package test4giis.tdrules.tdg.st.eval.market;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import giis.tdrules.client.TdRulesApi;
import giis.tdrules.client.oa.OaSchemaApi;
import giis.tdrules.client.oa.OaSchemaFilter;
import giis.tdrules.client.oa.OaSchemaIdResolver;
import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.gen.DictionaryAttrGen;
import giis.tdrules.store.loader.gen.IAttrGen;
import giis.tdrules.store.loader.oa.ApiWriter;
import giis.tdrules.store.loader.oa.OaLiveAdapter;
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
	protected static final String MARKET_SCHEMA_LOCAL = "../sut-market/src/main/resources/testMarket.json";
		
	protected static TdSchema model; // readonly, created before all tests

	@Rule
	public TestName testName = new TestName();

	@Before
	public void setUp() {
		log.info("****** Running test: {} ******", testName.getMethodName());
	}

	protected void load(ApiWriter writer, String query) {
		// Configure:
		// - filter entities Link* and attributes _link*
		// - the schema id resolver to use id attributes as uid 
		//   except in entities CartItemDTO (pk are user + productId) and ProductDTO (pk is productId)
		//   except in entities DistilleryDTO (pk can be id and title, but title is rid in ProductDTO) and 
		//   RegionDTO (pk can be id and name, but name is rid in DistilleryDTO)   
		OaSchemaApi schemaApi = new OaSchemaApi(MARKET_SCHEMA_LOCAL)
				.setFilter(new OaSchemaFilter()
						.add("*", "_link*")
						.add("Link*", "*"))
				.setIdResolver(new OaSchemaIdResolver().setIdName("id")
							.excludeEntity("CartItemDTOReq")
							.excludeEntity("CartItemDTORes")
							.excludeEntity("DistilleryDTOReq")
							.excludeEntity("DistilleryDTORes")
							.excludeEntity("OrderedProductDTO")
							.excludeEntity("OrderedProductDTORes")
							.excludeEntity("OrderedProductDTOReq")
							.excludeEntity("ProductDTORes")
							.excludeEntity("ProductDTOReq")
							.excludeEntity("RegionDTOReq")
							.excludeEntity("RegionDTORes")
							)
				
				;
		model = schemaApi.getSchema();
		
		// Check the schema, because their changes may invalidate all results
		String modelStr = new giis.tdrules.model.io.ModelJsonSerializer().serialize(model, true);
		FileUtil.fileWrite("target/schema-market.json", modelStr);
		VisualAssert va = new VisualAssert().setFramework(Framework.JUNIT4);
		va.assertEquals(FileUtil.fileRead("src/test/resources/schema-market.json"), modelStr);
		// Until here, this should be run once in BeforeClass, but some side effects on QAGrow#40 prevent doing that

		// The path resolver is created using a controller mock (writer)
		// and the data loader using a dictionary
		DataLoader loader = new DataLoader(model, new OaLiveAdapter("").setApiWriter(writer))
				.setAttrGen(getDictionaryAttrGen());

		// Generation and loading: Each test first delete all data previous to the generation and load
		writer.delete("/test/deleteAll");
		QAGrowApiProcess qagrow = new QAGrowApiProcess(model, new TdRulesApi().setCache("../.tdrules-cache"), loader);
		qagrow.genData4ApiQuery(query);
	}
	
	protected IAttrGen getDictionaryAttrGen() {
		// Dictionary configured for:
		// - UserDTO (attributes email, name, password, phone)
		// - DistilleryDTO (attribute title)
		// - RegionDTO (attribute name)
		// - CreditCardDTO (attribute ccNumber, source https://dev.na.bambora.com/docs/references/payment_APIs/test_cards/)
		return new DictionaryAttrGen()
				.with("UserDTORes", "email").padLeft('0', 2).mask("us{}@email.com")
				.with("UserDTOReq", "email").padLeft('0', 2).mask("us{}@email.com")
				.with("UserDTORes", "name").dictionary("Lucia","Sofia","Martina","Maria", "Jose","Juan","Luis","Antonio","Mateo")
				.with("UserDTOReq", "name").dictionary("Lucia","Sofia","Martina","Maria", "Jose","Juan","Luis","Antonio","Mateo")
				.with("UserDTORes", "password").dictionary("123456","1234567","12345678","123456789", "abcdef","abcdefg","abcdefgh","abcdefghi","abcdefghij")
				.with("UserDTOReq", "password").dictionary("123456","1234567","12345678","123456789", "abcdef","abcdefg","abcdefgh","abcdefghi","abcdefghij")
				.with("UserDTORes", "phone").dictionary("+12123456789","+12112345678","+13122334568","+14123345566", "+15123344557","+16123345668","+17123456777","+18123346778","+19129346779")
				.with("UserDTOReq", "phone").dictionary("+12123456789","+12112345678","+13122334568","+14123345566", "+15123344557","+16123345668","+17123456777","+18123346778","+19129346779")
				.with("DistilleryDTORes", "title").dictionary("Ardbeg", "Balvenie", "Caol Ila", "Dalwhinnie", "Glenkinchie", "Lagavulin", "Laphroaig", "Springbank", "Talisker")
				.with("DistilleryDTOReq", "title").dictionary("Ardbeg", "Balvenie", "Caol Ila", "Dalwhinnie", "Glenkinchie", "Lagavulin", "Laphroaig", "Springbank", "Talisker")
				.with("RegionDTORes", "name").dictionary("Campbeltown", "Highland", "Island", "Islay", "Lowland", "Speyside")
				.with("RegionDTOReq", "name").dictionary("Campbeltown", "Highland", "Island", "Islay", "Lowland", "Speyside")
				.with("CreditCardDTO","ccNumber").dictionary("4030000010001234","5100000010001004","2223000048400011","371100001000131","6011500080009080")
				;
	}
}
