package test4giis.tdrules.tdg.st.test.petstore;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.gen.IAttrGen;

/** 
 * Test Data generation for APIs (TDG) for the Swagger Petstore as SUT:
 * Data generation with QAGrow and loading in local.
 */
public class TestPetstoreQagrowLocal extends BasePetstore {

	@Test
	public void testPetByCategoryAndStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPetByCategoryAndStatus);
		assertData("qagrow-local-pet-by-category-status.txt", dg);
	}

	@Test
	public void testPetByCategoryAndStatusWithDictionary() {
		//Repetir la misma anterior pero especificando el AttrGen con diccionario:
		//usar getDictionaryAttrGen() e inyectarlo en el DataGenerator
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getDataLoader().setAttrGen(dict);
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPetByCategoryAndStatus, dict);
		assertData("qagrow-local-pet-by-category-status-dict.txt", dg);
		
	}

	@Test
	public void testPetByUrlAndTag() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPetByUrlAndTag);
		assertData("qagrow-local-pet-by-url-tag.txt", dg);
	}
	
	@Test
	public void testPlacedPetOrdersByCategoryAndOrderStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPlacedPetOrdersByCategoryAndOrderStatus);
		assertData("qagrow-local-placed-pet-orders-by-category-order-status.txt", dg);
	}
	
	@Test
	public void testPlacedPetOrdersByAddressAndOrderStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPlacedPetOrdersByAddressAndOrderStatus);
		assertData("qagrow-local-placed-pet-orders-by-address-order-status.txt", dg);
	}
	
	@Test
	public void testPlacedPetOrdersByAddressAndOrderStatusWithDictionary() {
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getDataLoader().setAttrGen(dict);
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPlacedPetOrdersByAddressAndOrderStatus, dict);
		assertData("qagrow-local-placed-pet-orders-by-address-order-status-dict.txt", dg);
	}
	
	////////////////////////////// Generacion de datos con group by //////////////////////////////

	@Test
	public void testTotalOrdersToDeliverByAddress() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryTotalOrdersToDeliverByAddress);
		assertData("qagrow-local-total-pet-orders-by-address.txt", dg);
	}
	
	@Test
	public void testTotalPetsToDeliverByAddress() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryTotalPetsToDeliverByAddress);
		assertData("qagrow-local-total-pets-by-address.txt", dg);
	}

}
