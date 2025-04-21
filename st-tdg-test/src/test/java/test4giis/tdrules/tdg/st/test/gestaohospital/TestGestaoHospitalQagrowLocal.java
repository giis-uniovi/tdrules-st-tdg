package test4giis.tdrules.tdg.st.test.gestaohospital;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;

/**
 * Test data generation for GestaoHospital:
 * using a local Data Loader (which does not require an active connection to the server), 
 * with the test data generated with QAGrow.
 * 
 * Same tests as those included in TestGestaoHospitalDatagenLocal 
 */
public class TestGestaoHospitalQagrowLocal extends BaseGestaoHospital {

	@Test
	public void testSmoke() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestGestaoHospitalDatagenLocal.querySmoke);
		assertData("qagrow-local-smoke.txt", dg);
	}

	@Test
	public void testProductbyProductTypeAndQuantity() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestGestaoHospitalDatagenLocal.queryProductByProductTypeAndQuantity);
		assertData("qagrow-local-product-by-productype-quantity.txt", dg);
	}
	
	@Test
	public void testHospitalProductbyProductTypeAndQuantity() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestGestaoHospitalDatagenLocal.queryHospitalProductByProductTypeAndQuantity);
		assertData("qagrow-local-hospital-product-by-productype-quantity.txt", dg);
	}	
}
