package test4giis.tdrules.tdg.st.test.petstore;

import org.junit.Test;

import giis.tdrules.model.io.TdRulesXmlSerializer;
import giis.tdrules.openapi.model.TdRules;
import giis.tdrules.store.loader.DataLoader;

/**
 * Proof of concept of Test Data generation for APIs (TDG)
 * using simplified entities from the Swagger Petstore.
 * 
 * Tests in this class simulate the data generation by specifying
 * the commands sent to the Data Loader and gets the data that would be loaded.
 * 
 * This also serves to illustrate using examples the main transformations performed on the
 * model and the coverage rules.
 * 
 * Test data specification is written as a query in a SQL like language 
 * called "Test Data Specification" (TDS).
 * The OpenApi model is first transformed into the Test Data Model (TDM)
 * that is represented as a DbSchema model (see the tdrules project)
 * and the TDS is also transformed into a query to generate the FPC
 * coverage rules
 */
public class TestPetstore0DatagenLocal extends BasePetstore {

	/**
	 * The simplest TDS with a single entity and a single condition
	 */
	public static String querySmoke = "tds Category where name='Dogs'";
	@Test
	public void testSmoke() {
		// The rules look for a category that matches Dogs and other that doesn't
		TdRules rules=getRules(querySmoke);
		assertModel("rules-smoke.xml", new TdRulesXmlSerializer().serialize(rules));
		// The data generator should issue the appropriate commands to the data loader
		// to cover each of the rules, this is simulated here.
		DataLoader dg = getDataLoader();
		dg.load("Category","name=Dogs");
		dg.load("Category","");
		assertData("datagen-local-smoke.txt", dg);
	}

	/**
	 * Using a simplified schema: Pet0 without arrays and external references.
	 * Specifies pets with a given category that are available to be sold:
	 * 
	 *   TDS Pet0 where Pet0.category::name='Dogs' and Pet0.status='available'
	 * 
	 * Note the :: notation to access to an attribute that is inside of an object.
	 */
	public static String queryPet0ByCategoryAndStatus = 
			"tds Pet0 where Pet0.category::name='Dogs' and Pet0.status='available'";
	@Test
	public void testPet0ByCategoryAndStatus() {
		TdRules rules = getRules(queryPet0ByCategoryAndStatus);
		assertModel("rules-pet0-by-category-status.xml", new TdRulesXmlSerializer().serialize(rules));
		
		DataLoader dg = getDataLoader();
		// Rules to specify category=Dogs and status=available
		dg.load("Pet0","category::name=Dogs, status=available");
		// Other rows, one for each condition that must not match
		dg.load("Pet0","category::name=1, status=available");
		dg.load("Pet0","category::name=Dogs, status=sold");
		assertData("datagen-local-pet0-by-category-status.txt", dg);
	}
	
	/**	
	 * Using a simplified schema: Pet1 without arrays bat with external references.
	 * As in the previous, specifies pets with a given category that are available to be sold:
	 * 
	 * 	 tds Pet1 where Pet1.category::name='Dogs' and Pet1.status='available'
	 * 
	 * Here the external reference is transformed by creating a type Pet1_category_xt
	 * that references a Category. This is the data type assigned to Pet1.category
	 * 
	 * As the schema references an independent object (Category), data of Pet1::category
	 * must be consistent with those that are in Category (there is duplicated data).
	 * To support this, the rule generation makes a transformation by adding a join to Category.
	 * The data generator produces data that is consistent with this relation and the data loader
	 * ensures that the appropriated data is stored both in Category and in Pet1::category.
	 * Internally, the transformed query that generates the rules will be:
	 * 
	 * SELECT * FROM Pet1
     *   LEFT JOIN Category Pet1_category_xref ON Pet1.category::id = Pet1_category_xref.id
     *   WHERE Pet1.category::name = 'Dogs' AND Pet1.status = 'available'
	 */
	public static String queryPet1ByCategoryAndStatus =
			"tds Pet1 where Pet1.category::name='Dogs' and Pet1.status='available'";
	@Test
	public void testPet1ByCategoryAndStatus() {
		TdRules rules = getRules(queryPet1ByCategoryAndStatus);
		assertModel("rules-pet1-by-category-status.xml", new TdRulesXmlSerializer().serialize(rules));

		DataLoader dg = getDataLoader();
		// First three rules like the previous test, but here, the data is in another entity (category)
		// that must be loaded before
		dg.load("Category", "id=@cid1, name=Dogs");
		dg.load("Pet1", "id=@pid1, category::id=@cid1, status=available");
		dg.load("Pet1", "id=@pid2, category::id=@cid1, status=sold");//sold es un valor !=available indicado por qagrow entre los permitidos 
		// Now the rules look for Pet1.category!=Dogs, a new category must be created
		dg.load("Category", "id=@cid2, name=1"); //1 is any value !=Dogs created by the generator
		dg.load("Pet1", "id=@pid3, category::id=@cid2, status=available");
		// Finally, a category without any related pet
		dg.load("Category", "id=@cid3");
		
		assertData("datagen-local-pet1-by-category-status.txt", dg);
	}
	
	/**
	 * Relational join, 3 entities:
	 * Specifies a Client with a given name such that his Order0 reference a Pet1 with category Dogs and status placed
	 * This can be specified as:
	 * 
	 *   tds Customer0 xjoin Order0 xjoin Pet where Pet0.category::name='Dogs' and Order0.status='placed'
	 *   
	 * or more more simple:
	 * 
	 *   tds Customer0, Order0, Pet where Pet0.category::name='Dogs' and Order0.status='placed'
	 *   
	 * Internally, the transformed query that generates the rules will be:
	 * 
	 * SELECT * FROM Customer0
     *   INNER JOIN "Order0" ON Customer0.id = Order0.CustomerId
     *   INNER JOIN Pet0 ON Order0.petId = Pet0.id
     *   WHERE Pet0.category::name = 'Dogs' AND "Order0".status = 'placed'
	 */
	public static String queryPlacedPet0OrdersByCategoryAndOrderStatus=
			"tds Customer0, \"Order0\", Pet0"
				+ " where Pet0.category::name='Dogs' and \"Order0\".status='placed'";
	@Test
	public void testPlacedPet0OrdersByCategoryAndOrderStatus() {
		TdRules rules = getRules(queryPlacedPet0OrdersByCategoryAndOrderStatus);
		assertModel("rules-placed-pet0-orders-by-category-order-status.xml", new TdRulesXmlSerializer().serialize(rules));
		
		DataLoader dg = getDataLoader();
		// To cover the first rule we need to generate the master entities before
		dg.load("Customer0", "id=@cid1");
		dg.load("Pet0", "id=@pid1, category::name=Dogs");
		dg.load("Order0", "id=@oid1, customerId=@cid1, petId=@pid1, status=placed");
		
		// To cover the second rule that requires Pet0.category::name!=Dogs, requires a new master
		dg.load("Pet0","id=@pid2, category::name=1");
		dg.load("Order0","id=@oid2, customerId=@cid1, petId=@pid2, status=placed");
		// Third rule requires status!=placed, reusing the previous master entities
		dg.load("Order0","id=@oid3, customerId=@cid1, petId=@pid1, status=delivered"); //status indicado por qagrow !=placed
		
		// Four and five rules require a Customer0 without Order0, and a Pet0 without Order0
		dg.load("Customer0", "id=@cid2");
		dg.load("Pet0", "id=@pid2, category::name=Dogs");

		assertData("datagen-local-placed-pet0-orders-by-category-order-status.txt", dg);
	}
	
	/**
	 * Same query than before, but using aliases for some entity names
	 */
	public static String queryPlacedPet0OrdersWithAlias=
			"tds Customer0 c, \"Order0\" o, Pet0"
				+ " where category::name='Dogs' and o.status='placed'";
	@Test
	public void testPlacedPet0OrdersWithAlias() {
		TdRules rules = getRules(queryPlacedPet0OrdersWithAlias);
		assertModel("rules-placed-pet0-orders-with-alias.xml", new TdRulesXmlSerializer().serialize(rules));
	}

}
