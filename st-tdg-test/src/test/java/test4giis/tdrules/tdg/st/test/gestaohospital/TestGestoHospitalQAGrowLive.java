package test4giis.tdrules.tdg.st.test.gestaohospital;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;

/**
 * Test data generation for GestaoHospital:
 * data generation (with QAGrow) and loading using a live SUT 
 */
public class TestGestoHospitalQAGrowLive extends BaseGestaoHospital {
	
	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	//A simple query for initial live generation
	//with representative data taken from a dictionary 
	@Test
	public void testSmokewithDict() { 
		String query =  "tds HospitalDTO where HospitalDTO.availableBeds=10";
		DataLoader dg = getDataLoader().setAttrGen(getDictionaryAttrGen());
		generateAndLoad(dg, query);
		assertData("qagrow-live-smoke-dict.txt", dg);
	}
	
	//A query on the Hospital and Products objects to check that it stores hospitals and associated products.
	//List of products that have stock
	@Test
	public void testAvalProductsbyHospital () {
		String query =  "tds HospitalDTO, ProductDTO where ProductDTO.quantity>0";
		DataLoader dg = getDataLoader().setAttrGen(getDictionaryAttrGen());
		generateAndLoad(dg, query);
		assertData("qagrow-live-aval-products-by-hospital.txt", dg);
	}
	
	//List of products out of stock in hospitals with available beds
	@Test
	public void testUnavProductsbyHospital () {
		String query =  "tds HospitalDTO, ProductDTO where HospitalDTO.availableBeds>0 and ProductDTO.quantity=0";
		DataLoader dg = getDataLoader().setAttrGen(getDictionaryAttrGen());
		generateAndLoad(dg, query);
		assertData("qagrow-live-unav-products-by-hospital.txt", dg);
	}
	
}
