package test4giis.tdrules.tdg.st.eval.petstore;

import org.junit.Test;

import giis.tdrules.store.loader.oa.ApiResponse;
import giis.visualassert.Framework;
import giis.visualassert.SoftVisualAssert;
import lombok.extern.slf4j.Slf4j;

/**
 * Tests that generate, load and evaluate the petstore Test Data Generation
 * (TDG), containing a subset of the main project's tests. To evaluate the
 * mutation score of the business processes run this command fron maven:
 * 
 * mvn -pl st-tdg-eval test-compile org.pitest:pitest-maven:mutationCoverage
 * 
 * As Pitest runs in the same process of the tested methods, the controller is
 * mocked. Note that petstore does not use Spring, so that MockMvc can be used.
 * A mock controller has been created for this purpose.
 */
@Slf4j
public class TestPetstoreEval extends BasePetstoreEval {

	// All tests that are readonly follow the same pattern, by defining a static
	// variable to prevent multiple data loads when mutants are evaluated.
	// The result assertions check both the output produced by the tested method and
	// the data loaded. All these outputs are saved in files at target folder and
	// compared against the expected files at src/test/resources folder.
	// All tests use a mock controller (mvc) declared in the parent class

	private static boolean testFindPetByStatusLoaded = false;

	@Test
	public void testFindPetByStatus() {
		if (!testFindPetByStatusLoaded) {
			load(mvc, "tds Pet where Pet.status='available'");
			testFindPetByStatusLoaded = true;
		}
		ApiResponse pets = mvc.get("/pet/findByStatus?status=available");
		assertReadResults(pets);
	}

	private static boolean testFindPetByCategoryAndStatusLoaded = false;

	@Test
	public void testFindPetByCategoryAndStatus() {
		if (!testFindPetByCategoryAndStatusLoaded) {
			load(mvc, "tds Pet where Pet.category::name='Cats' and Pet.status='available'");
			testFindPetByCategoryAndStatusLoaded = true;
		}
		ApiResponse pets = mvc.get("/pet/findByCategoryAndStatus?category=Cats&status=available");
		assertReadResults(pets);
	}

	private static boolean testFindOrdersByCategoryAndOrderStatusLoaded = false;

	@Test
	public void testFindOrdersByCategoryAndOrderStatus() {
		if (!testFindOrdersByCategoryAndOrderStatusLoaded) {
			load(mvc, "tds Customer, \"Order\", Pet where Pet.category::name='Dogs' and \"Order\".status='placed'");
			testFindOrdersByCategoryAndOrderStatusLoaded = true;
		}
		ApiResponse pets = mvc.get("/store/findOrdersByCategoryAndOrderStatus?category=Dogs&status=placed");
		assertReadResults(pets);
	}
	
	private static boolean testTotalPetsToDeliverByAddressLoaded = false;

	@Test
	public void testTotalPetsToDeliverByAddress() {
		if (!testTotalPetsToDeliverByAddressLoaded) {
			load(mvc, "tds Customer, \"Order\", Pet"
					+ " where \"Order\".status='approved'"
					+ " group by Customer.address[]::zip");
			testTotalPetsToDeliverByAddressLoaded = true;
		}
		ApiResponse pets = mvc.get("/store/totalPetsToDeliverByAddress");
		assertReadResults(pets);
	}
	
	// This is a put request that updates the db, do not save generated data
	@Test
	public void testUpdateDeliveryToCustomer() {
		load(mvc, "tds Customer, \"Order\", Pet where Customer.id=1 and \"Order\".status='approved'");
		SoftVisualAssert sva = new SoftVisualAssert().setFramework(Framework.JUNIT4);
		sva.assertClear();
		assertResults(true, false, false, null);
		ApiResponse pets = mvc.post("/store/updateDeliveryToCustomer?customerId=1", "", true);
		assertResults(false, true, true, pets);
		sva.assertAll();
	}

}
