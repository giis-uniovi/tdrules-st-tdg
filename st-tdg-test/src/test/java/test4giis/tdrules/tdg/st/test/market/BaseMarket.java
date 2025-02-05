package test4giis.tdrules.tdg.st.test.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import giis.tdrules.client.oa.OaSchemaApi;
import giis.tdrules.client.oa.OaSchemaIdResolver;
import giis.tdrules.client.oa.OaSchemaFilter;
import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.gen.DictionaryAttrGen;
import giis.tdrules.store.loader.gen.IAttrGen;
import giis.tdrules.store.loader.oa.OaBasicAuthStore;
import giis.tdrules.store.loader.oa.OaLiveAdapter;
import giis.tdrules.store.loader.oa.OaLiveUidGen;
import giis.tdrules.store.loader.oa.OaPathResolver;
import test4giis.tdrules.tdg.st.test.BaseAll;

/**
 * Common configuration and customization for all Market tests that use entities in its OpenApi model.
 */
public class BaseMarket extends BaseAll {
	protected static final String MARKET_SCHEMA_LOCAL = "../sut-market/src/main/resources/marketWithoutArrays.json";
	private static final String MARKET_URL_LIVE = "http://localhost:8083";

	// attributes that can be filtered during comparisons of assertions 
	private static final String[] FILTERED_ATTRS = {"password", "dateCreated","number"};
	// empty cart of an user
	public static String queryCartByUser = "tds CartDTO where user='lucia@email.com'";
	// carts with items (products added) of an user, products must be available
	public static String queryCartByUserProductQuantity = "tds CartDTO,CartItemDTORes,ProductDTORes where CartDTO.user='pepe@email.com' and CartItemDTORes.productId=1 and CartItemDTORes.quantity=5 and ProductDTORes.available=1";
	// order of an user
	public static String queryOrderByUser = "tds OrderDTO where userAccount='pepe@email.com' ";
	// products by age
	public static String queryProductByAge = "tds ProductDTORes where age=10";
	// users by name
	public static String queryUserByName = "tds UserDTORes where name='Pepe'";
	// user by email
	public static String queryUserByEmail = "tds UserDTORes where email ='pepe@email.com'";
	
	@Override
	protected String getSutName() {
		return "market";
	}
	
	@Override
	protected String getServerUrl() {
		return MARKET_URL_LIVE;
	}

	@Override
	protected String getAllDataLiveEndpoint() {
		return MARKET_URL_LIVE + "/test/getAll";
	}

	@Override
	protected String getDeleteAllDataLiveEndpoint() {
		return MARKET_URL_LIVE + "/test/deleteAll";
	}
	
	@Override
	protected TdSchema getSchema() {
		// Configure:
		// - filter entities Link* and attributes _link*
		// - the schema id resolver to use id attributes as uid 
		//   except in entities CartItemDTO (pk are user + productId) and ProductDTO (pk is productId)	
		OaSchemaApi api = new OaSchemaApi(MARKET_SCHEMA_LOCAL)
				.setFilter(new OaSchemaFilter()
						.add("*", "_link*")
						.add("Link*", "*"))
				.setIdResolver(new OaSchemaIdResolver().setIdName("id")
							.excludeEntity("CartItemDTOReq")
							.excludeEntity("CartItemDTORes")
							.excludeEntity("ProductDTORes")
							.excludeEntity("ProductDTOReq"))
				;
		return api.getSchema();
	}
	
	@Override
	protected DataLoader getLiveDataLoader() {
		// Generator with a custom path resolver injected:
		// * Get uids from the backend
		// * Include an authenticator:
		//   - The provider is the entity userDTOReq storing credential in email and password attributes
		//   - Consumers are entities CartItemDTO and ContactsDTO (username is user attribute) and 
		//     OrderDTO (username is userAccount attribute)
		// * Use a dictionary
		TdSchema model = getSchema();
		OaBasicAuthStore authenticator = new OaBasicAuthStore()
				.setProvider("UserDTORes", "email", "password")
				.addConsumer(new String[] { "CartItemDTORes", "CartItemDTOReq", 
						                    "ContactsDTORes", "ContactsDTOReq" }, "user")
				.addConsumer(new String[] { "OrderDTO"} , "userAccount");
		
		return new DataLoader(model, new OaLiveAdapter(MARKET_URL_LIVE).setPathResolver(new CustomPathResolver()).setAuthStore(authenticator))
				                            .setUidGen(new OaLiveUidGen())
				                            .setAttrGen(getDictionaryAttrGen());
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
	
	// Overrides the default implementation to filter unwanted attributes that shouldn't participate in comparisons
	@Override
	protected void assertLiveData(String fileName, DataLoader dg) {
		String dataLive = getAllLiveData();
		log.info("Actual data stored in the backend\n{}", reserializeStoredData(dataLive));
		// before comparing, filter attributes
		super.assertModel(fileName, filterAttributes(dataLive,FILTERED_ATTRS));  
	}
	
	private String filterAttributes(String strJson, String... ignoreAttributes) {
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		StringBuilder sb = new StringBuilder();
		
		try {
			JsonNode tables = mapper.readTree(strJson);
			for (String attr : ignoreAttributes) {
				removeAttribute(tables, attr);
			}
			sb.append(tables.toString());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		
		return reserializeStoredData(sb.toString());
    }
	
	// update json deleting attributeName and its value  
	private void removeAttribute(JsonNode node, String attributeName) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.remove(attributeName);
            objectNode.fields().forEachRemaining(entry -> removeAttribute(entry.getValue(), attributeName));
        } else if (node.isArray()) {
            node.forEach(childNode -> removeAttribute(childNode, attributeName));
        }
    }
	
	// Use the appropiate endpoint depending on the entity
	public class CustomPathResolver extends OaPathResolver {
		@Override
		public String getEndpointPath(String tableName) {
			if ("CartDTO".equals(tableName))
				return null;
			else if ("CreditCardDTO".equals(tableName))
				return null;
			else if ("CartItemDTORes".equals(tableName))
				return super.getEndpointPath("customer/cart");
			else
				return super.getEndpointPath(tableName);
		}
		
		// Endpoint using PUT instead of POST for entity CartItemDTO
		@Override public boolean usePut(String tableName) {
			String table = tableName.split("Re(s|q)")[0];
			return "CartItemDTO".equals(table);
		}
	}
	
}
