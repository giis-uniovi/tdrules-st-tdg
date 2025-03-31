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
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, queryProductByAge);
		assertData("qagrow-local-product.txt", dg);
	}
	
	@Test
	public void testUserByName() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, queryUserByName);
		assertData("qagrow-local-userbyname.txt", dg);
	}
	
	@Test
	public void testUserByEmail() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, queryUserByEmail);
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
