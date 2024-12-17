package test4giis.tdrules.tdg.st.test.petstore;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;

/**
 * Proof of concept of Test Data Generation for APIs (TDG)
 * using simplified entities from the Swagger Petstore.
 * 
 * Tests in this class automatically generate the test data, 
 * but working locally.
 */
public class TestPetstore0QagrowLocal extends BasePetstore {

	@Test
	public void testSmoke() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstore0DatagenLocal.querySmoke);
		assertData("qagrow-local-smoke.txt", dg);
	}

	@Test
	public void testPet0ByCategoryAndStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstore0DatagenLocal.queryPet0ByCategoryAndStatus);
		assertData("qagrow-local-pet0-by-category-status.txt", dg);
	}
	
	@Test
	public void testPet1ByCategoryAndStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstore0DatagenLocal.queryPet1ByCategoryAndStatus);
		assertData("qagrow-local-pet1-by-category-status.txt", dg);
	}
	
	@Test
	public void testPlacedPet0OrdersByCategoryAndOrderStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstore0DatagenLocal.queryPlacedPet0OrdersByCategoryAndOrderStatus);
		assertData("qagrow-local-placed-pet0-orders-by-category-order-status.txt", dg);
	}
	
	@Test
	public void testPlacedPet0OrdersWithAlias() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstore0DatagenLocal.queryPlacedPet0OrdersWithAlias);
		assertData("qagrow-local-placed-pet0-orders-by-category-order-status.txt", dg);
	}
	
}
