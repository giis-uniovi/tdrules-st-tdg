package test4giis.tdrules.tdg.st.test.market;

import org.junit.Test;

import giis.tdrules.model.io.TdRulesXmlSerializer;
import giis.tdrules.openapi.model.TdRules;
import giis.tdrules.store.loader.DataLoader;

/**
 * Generates test data for a given query using QAGrow and loads
 * the data as indicated by the local specified data loader
 */

public class TestMarketQagrowLocal extends BaseMarket {

	@Test
	public void testProduct() {
		// products by age
		String query = "tds ProductDTORes where age=10";
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, query);
		assertData("qagrow-local-product.txt", dg);
	}
	
			
	@Test
	public void testUserByName() {
		// user by name
		String query = "tds UserDTORes where name ='Pepe'";
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, query);
		assertData("qagrow-local-userbyname.txt", dg);
	}
	
	@Test
	public void testUserByEmail() {
		// user by email
		String query = "tds UserDTORes where email ='pepe@email.com'";
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, query);
		assertData("qagrow-local-userbyemail.txt", dg);
	}
	
	@Test
	public void testRulesProductsByRegion() {
		// The rules look for products that matches a region and another that doesn't
		String query = "tds ProductDTOReq, DistilleryDTOReq where ProductDTOReq.distillery=DistilleryDTOReq.title and DistilleryDTOReq.region='Islay'";
		TdRules rules=getRules(query);
		assertModel("rules-ProductsByRegion.xml", new TdRulesXmlSerializer().serialize(rules));
	}
}
