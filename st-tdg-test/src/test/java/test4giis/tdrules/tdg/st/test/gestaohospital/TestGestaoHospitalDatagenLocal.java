package test4giis.tdrules.tdg.st.test.gestaohospital;

import org.junit.Test;

import giis.tdrules.model.io.TdRulesXmlSerializer;
import giis.tdrules.openapi.model.TdRules;
import giis.tdrules.store.loader.DataLoader;

/**
 * Test data generation for GestaoHospital:
 * using a local Data Loader (which does not require an active connection to the server) 
 */
 public class TestGestaoHospitalDatagenLocal extends BaseGestaoHospital {

	/**
	 *A simple query with a single table for the initial test that checks that it is generated correctly.
	 */
	public static String querySmoke = "select * from HospitalDTO where availableBeds=10";
	@Test
	public void testSmoke() {
		TdRules rules=getRules(querySmoke);
		assertModel("rules-smoke.xml", new TdRulesXmlSerializer().serialize(rules));
		
		//Las reglas buscan una Hospital con availableBeds=10 y otro que no 
		DataLoader dg = getDataLoader();
		dg.load("HospitalDTO","availableBeds=10");
		dg.load("HospitalDTO","availableBeds=1");
		assertData("datagen-local-smoke.txt", dg);
	}

	/**
	 * A query that searches for Products of type “COMMON” that have availability:
	 *    TDS ProductDTO where ProductDTO.productType='COMMON' and ProductDTO.quantity>0   
	 */
	public static String queryProductByProductTypeAndQuantity = 
			"tds ProductDTO where ProductDTO.productType='COMMON' and ProductDTO.quantity>0";
	@Test
	public void testProductbyProductTypeAndQuantity() {
		TdRules rules = getRules(queryProductByProductTypeAndQuantity);
		assertModel("rules-product-by-producttype-and-quantity.xml", new TdRulesXmlSerializer().serialize(rules));
		
		DataLoader dg = getDataLoader();
		//One row ProductType=COMMON and quantity >0
		dg.load("ProductDTO","productType=COMMON, quantity=1");
		//two rows in which equality fails in each of these properties
		dg.load("ProductDTO","productType=BLOOD, quantity=1"); 
		dg.load("ProductDTO","productType=COMMON, quantity=0");
		
		assertData("datagen-local-product-by-productype-quantity.txt", dg);
	}
	
	/**
	 * A similar query to the previous one searching for Products of type “COMMON” that have availability 
	 * but that are in some Hospital (relational join of two tables).
	 *    TDS ProductDTO, HospitalDTO where ProductDTO.productType='COMMON' and ProductDTO.quantity>0   
	 */
	public static String queryHospitalProductByProductTypeAndQuantity = 
			"tds ProductDTO,HospitalDTO where ProductDTO.productType='COMMON' and ProductDTO.quantity>0";
	@Test
	public void testHospitalProductbyProductTypeAndQuantity() {
		TdRules rules = getRules(queryHospitalProductByProductTypeAndQuantity);
		assertModel("rules-hospital-product-by-producttype-and-quantity.xml", new TdRulesXmlSerializer().serialize(rules));
		
		DataLoader dg = getDataLoader();
		//The first rule must generate a HospitalDTO with Products 
		//that meet the decision of where
		dg.load("HospitalDTO","id=@hid1");
		dg.load("ProductDTO","id=@pid1,hospitalDTOId=@hid1 ,productType=COMMON, quantity=1");
		//Reuses the same hospital but with non-conforming products
		dg.load("ProductDTO","id=@pid2,hospitalDTOId=@hid1,productType=BLOOD,quantity=1");
		dg.load("ProductDTO","id=@pid3,hospitalDTOId=@hid1,productType=COMMON,quantity=0");
		//A hospital without products
		dg.load("HospitalDTO","id=@hid2");
				
		assertData("datagen-local-hospital-product-by-productype-quantity.txt", dg);
	}
}