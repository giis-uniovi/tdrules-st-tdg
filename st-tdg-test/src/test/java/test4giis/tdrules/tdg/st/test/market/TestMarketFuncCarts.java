package test4giis.tdrules.tdg.st.test.market;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.gen.IAttrGen;

/**
 * Carts Functionality Tests.
 * - Use the live SUT where the uids of each generated object are generated by the backend.
 * - Use the dictionary
 * - Include Users, Carts and Orders
 */
public class TestMarketFuncCarts extends BaseMarket {
	
	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	@Test
	public void testDictUserDTOResByName() {
		String query = queryUserByName;
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-UsersByName.txt", dg);
	}

	@Test
	public void testDictCartDTOByUser() {
		String query = queryCartByUser;
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-CartsByUser.txt", dg);
	}
	
	@Test
	public void testDictCartDTOByUserProductQuantity() {
		String query = queryCartByUserProductQuantity;
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-CartsByUserProductQuantity.txt", dg);
	}
	
	@Test
	public void testOrderDTOByUser() {
		// order (cart must not be empty)
		// query 1 generates a non-empty cart, 
		// query 2 generates the order
		String query1= queryCartByUserProductQuantity;
		String query2 = queryOrderByUser;
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, new String[] {query1, query2}, dict);
		assertData("func-OrderByUser.txt", dg);
	}
}
