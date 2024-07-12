package test4giis.tdrules.tdg.st.test.petstore;

import org.junit.Test;

import giis.tdrules.model.io.TdRulesXmlSerializer;
import giis.tdrules.openapi.model.TdRules;
import giis.tdrules.store.loader.DataLoader;

/** 
 * Test Data generation for APIs (TDG) for the Swagger Petstore as SUT:
 * Only data loading in local.
 */
public class TestPetstoreDatagenLocal extends BasePetstore {

	/**	
	 * Specifies pets with a given category that are available to be sold:
	 * 
	 * 	 tds Pet where Pet.category::name='Dogs' and Pet.status='available'
	 * 
	 * The difference with the equivalent testPet1ByCategoryAndStatus in Petstore0 
	 * test is that entities have arrays. 
	 * The model transformation extracts arrays to another entity.
	 * Internally, the transformed query to generate the rules is:
	 * 
	 * SELECT * FROM Pet
     *   LEFT JOIN Pet_tags_xa ON Pet.id = Pet_tags_xa.fk_xa
     *   LEFT JOIN Pet_photoUrls_xa ON Pet.id = Pet_photoUrls_xa.fk_xa
     *   LEFT JOIN Category Pet_category_xref ON Pet.category::id = Pet_category_xref.id
     *   WHERE Pet.category::name = 'Dogs' AND Pet.status = 'available'
     *   
     * There are two versions of this test: without and with a dictionary
	 */
	public static String queryPetByCategoryAndStatus =
			"tds Pet where Pet.category::name='Dogs' and Pet.status='available'";
	@Test
	public void testPetByCategoryAndStatus() {
		TdRules rules = getRules(queryPetByCategoryAndStatus);
		assertModel("rules-pet-by-category-status.xml", new TdRulesXmlSerializer().serialize(rules));
		DataLoader dg = getDataLoader();
		doTestPetByCategoryAndStatus(dg, "datagen-local-pet-by-category-status.txt");
	}
	@Test
	public void testPetByCategoryAndStatusWithDictionary() {
		DataLoader dg = getDataLoader().setAttrGen(getDictionaryAttrGen());
		doTestPetByCategoryAndStatus(dg, "datagen-local-pet-by-category-status-dict.txt");
	}
	private void doTestPetByCategoryAndStatus(DataLoader dg, String outputFileName) {
		// Rules are like in testPet1ByCategoryAndStatus, but now, with arrays,
		// so that before generating each Pet, the objects of each array must be generated.
		dg.load("Pet_Tags_xa", "fk_xa=@pid1");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid1");
		dg.load("Category", "id=@cid1, name=Dogs");
		dg.load("Pet", "id=@pid1, category::id=@cid1, status=available");

		dg.load("Pet_Tags_xa", "fk_xa=@pid2");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid2");
		dg.load("Pet", "id=@pid2, category::id=@cid1, status=sold");
		
		dg.load("Pet_Tags_xa", "fk_xa=@pid3");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid3");
		dg.load("Category", "id=@cid2, name=1");
		dg.load("Pet", "id=@pid3, category::id=@cid2, status=available");

		dg.load("Category", "id=@cid3");
		
		// These are for two new rules that require Pets without Tags and Urls
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid4");
		dg.load("Pet", "id=@pid4, category::id=@cid1, status=available");
		
		dg.load("Pet_Tags_xa", "fk_xa=@pid5");
		dg.load("Pet", "id=@pid5, category::id=@cid1, status=available");
		
		assertData(outputFileName, dg);
	}

	/**
	 * Specifies pets with a given url and tag
	 * 
	 * 	 tds Pet where Pet.photoUrls[]='URL' and Pet.tags[]::name='kitty'"
	 * 
	 * This has the same structure than the previous, but here, conditions
	 * are based on the items in arrays.
	 * Internally, the transformed query to generate the rules must join with the arrays:
	 * 
	 * SELECT * FROM Pet
     *   LEFT JOIN Pet_tags_xa ON Pet.id = Pet_tags_xa.fk_xa
     *   LEFT JOIN Pet_photoUrls_xa ON Pet.id = Pet_photoUrls_xa.fk_xa
     *   LEFT JOIN Category Pet_category_xref ON Pet.category::id = Pet_category_xref.id
     *   WHERE Pet_photoUrls_xa.photoUrls = 'URL' AND Pet_tags_xa.name = 'kitty'
	 */
	public static String queryPetByUrlAndTag =
			"tds Pet where Pet.photoUrls[]='URL' and Pet.tags[]::name='kitty'";
	@Test
	public void testPetByUrlAndTag() {
		TdRules rules = getRules(queryPetByUrlAndTag);
		assertModel("rules-pet-by-url-tag.xml", new TdRulesXmlSerializer().serialize(rules));
		// data loading skipped from now
	}
	
	/**
	 * Relational join, 3 entities:
	 * Specifies a Client with a given name such that his Order reference a Pet with category Dogs and status placed
	 * 
	 *   tds Customer, Order, Pet where Pet.category::name='Dogs' and Order.status='placed'
	 * 
	 * The difference with the equivalent testPlacedPet0OrdersByCategoryAndOrderStatus in Petstore0 
	 * is that here the arrays.
	 * Internally they must be included and joined to generate the rules:
	 * 
	 * SELECT * FROM Customer
     *   LEFT JOIN Customer_address_xa ON Customer.id = Customer_address_xa.fk_xa
     *   INNER JOIN "Order" ON Customer.id = Order.customerId
     *   INNER JOIN Pet ON Order.petId = Pet.id
     *   LEFT JOIN Pet_tags_xa ON Pet.id = Pet_tags_xa.fk_xa
     *   LEFT JOIN Pet_photoUrls_xa ON Pet.id = Pet_photoUrls_xa.fk_xa
     *   LEFT JOIN Category Pet_category_xref ON Pet.category::id = Pet_category_xref.id
     *   WHERE Pet.category::name = 'Dogs' AND "Order".status = 'placed'
	 */
	public static String queryPlacedPetOrdersByCategoryAndOrderStatus=
			"tds Customer, \"Order\", Pet"
				+ " where Pet.category::name='Dogs' and \"Order\".status='placed'";
	@Test
	public void testPlacedPetOrdersByCategoryAndOrderStatus() {
		TdRules rules = getRules(queryPlacedPetOrdersByCategoryAndOrderStatus);
		assertModel("rules-placed-pet-orders-by-category-order-status.xml", new TdRulesXmlSerializer().serialize(rules));
	}

	/**
	 * Specifies a the orders that must be sent to an address area (given by the zip).
	 * Includes two versions: without and with dictionary.
	 * 
	 * From now, we does not comment on the internal detals of queries needed to generate the rules
	 */
	public static String queryPlacedPetOrdersByAddressAndOrderStatus=
			"tds Customer, \"Order\", Pet"
				+ " where Customer.address[]::zip='99999' and \"Order\".status='placed'";
	@Test
	public void testPlacedPetOrdersByAddressAndOrderStatus() {
		TdRules rules = getRules(queryPlacedPetOrdersByAddressAndOrderStatus);
		assertModel("rules-placed-pet-orders-by-address-order-status.xml", new TdRulesXmlSerializer().serialize(rules));
		DataLoader dg = getDataLoader();
		doPlacedPetOrdersByAddressAndOrderStatus(dg, "datagen-local-placed-pet-orders-by-address-order-status.txt");
	}
	@Test
	public void testPlacedPetOrdersByAddressAndOrderStatusWithDictionary() {
		DataLoader dg=getDataLoader().setAttrGen(getDictionaryAttrGen());
		doPlacedPetOrdersByAddressAndOrderStatus(dg, "datagen-local-placed-pet-orders-by-address-order-status-dict.txt");
	}
	private void doPlacedPetOrdersByAddressAndOrderStatus(DataLoader dg, String outputFileName) {
		dg.load("Pet_Tags_xa", "fk_xa=@pid1");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid1");
		dg.load("Category", "id=@cid1, name=Dogs");
		dg.load("Pet", "id=@pid1, category::id=@cid1, status=available");
		dg.load("Customer_address_xa", "fk_xa=@cuid1");
		dg.load("Customer", "id=@cuid1,");
		dg.load("Order", "id=@oid1,petId=@pid1,customerId=@cuid1");
		assertData(outputFileName, dg);
	}
	
	////////////////////////////// Generacion de datos con group by //////////////////////////////

	/**
	 * This TDS indicates that the generated test data must form groups, 
	 * used when the SUT performs some kind of aggregation:
	 * 
	 * Specifies that we want to count the total number of orders that must be sent (status is approved)
	 * to an address area (given by zip).
	 */
	public static String queryTotalOrdersToDeliverByAddress=
			"tds Customer, \"Order\", Pet"
				+ " where \"Order\".status='approved'"
				+ " group by Customer.address[]::zip";
	@Test
	public void testTotalOrdersToDeliverByAddress() {
		TdRules rules = getRules(queryTotalOrdersToDeliverByAddress);
		assertModel("rules-total-pet-orders-by-address.xml", new TdRulesXmlSerializer().serialize(rules));
	}
	
	/**
	 * A variant of the previous to show that we also can use a sql syntax by specifying 
	 * the values that the SUT  is going to obtain is also allowed.
	 */
	public static String queryTotalPetsToDeliverByAddress=
			"select Customer.address[]::zip, sum(\"Order\".quantity) from Customer, \"Order\", Pet"
				+ " where \"Order\".status='approved'"
				+ " group by Customer.address[]::zip";
	@Test
	public void testTotalPetsToDeliverByAddress() {
		TdRules rules = getRules(queryTotalPetsToDeliverByAddress);
		assertModel("rules-total-pets-by-address.xml", new TdRulesXmlSerializer().serialize(rules));
	}
	
}
