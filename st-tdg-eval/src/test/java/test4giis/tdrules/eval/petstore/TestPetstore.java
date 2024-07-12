package test4giis.tdrules.eval.petstore;

import org.junit.Test;

import giis.tdrules.store.loader.oa.ApiResponse;
import giis.tdrules.store.loader.oa.Reserializer;
import giis.visualassert.Framework;
import giis.visualassert.SoftVisualAssert;
import giis.visualassert.portable.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Tests that generate, load and evaluate the petstore Test Data Generation
 * (TDG), containing a subset of the main project's tests. To evaluate the
 * mutation score of the business processes run this command fron maven:
 * 
 * mvn test-compile org.pitest:pitest-maven:mutationCoverage
 * 
 * As Pitest runs in the same process of the tested methods, the controller is
 * mocked. Note that petstore does not use Spring, so that MockMvc can be used.
 * A mock controller has been created for this purpose.
 */
@Slf4j
public class TestPetstore extends Base {

	private MockController mvc = new MockController();
	private SoftVisualAssert sva = new SoftVisualAssert().setFramework(Framework.JUNIT4);

	// All tests that are readonly follow the same pattern, by defining a static
	// variable to prevent multiple data loads when mutants are evaluated.
	// The result assertions check both the output produced by the tested method and
	// the data loaded. All these outputs are saved in files at target folder and
	// compared against the expected files at src/test/resources folder.

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
		// Por que si anyado and \"Order\".complete=true, excepcion en qagrow, pero si pongo 1 en vez de true no.
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
		// Por que si  uso select Customer.address[]::zip, sum(\"Order\".quantity) from
		// quantity da varios valores cero, incluso al poner un minimo de uno en el diccionario?
		// y si solo pongo tds no?
		// por que zip no tiene mascara aplicada?
		ApiResponse pets = mvc.get("/store/totalPetsToDeliverByAddress");
		assertReadResults(pets);
	}
	
	// This is a put request that updates the db, do not save generated data
	@Test
	public void testUpdateDeliveryToCustomer() {
		load(mvc, "tds Customer, \"Order\", Pet where Customer.id=1 and \"Order\".status='approved'");
		sva.assertClear();
		assertResults(true, false, false, null);
		ApiResponse pets = mvc.post("/store/updateDeliveryToCustomer?customerId=1", "", true);
		assertResults(false, true, true, pets);
		sva.assertAll();
	}

	// to check get operations that do not modify the database
	private void assertReadResults(ApiResponse result) {
		sva.assertClear();
		assertResults(true, false, true, result);
		sva.assertAll();
	}
	
	private void assertResults(boolean checkBefore, boolean checkAfter, boolean checkResult, ApiResponse result) {
		if (checkBefore) {
			ApiResponse data = mvc.get("/test/getAll");
			saveOutput(getResultString(data, "data"), "data");
			assertFiles(sva, "data");
		}
		if (checkAfter) {
			ApiResponse data = mvc.get("/test/getAll");
			saveOutput(getResultString(data, "dataout"), "dataout");
			assertFiles(sva, "dataout");
		}
		if (checkResult) {
			saveOutput(getResultString(result, "list"), "output");
			assertFiles(sva, "output");
		}
	}
	
	private String getResultString(ApiResponse result, String format) {
		String ret = "";
		if (result.getStatus() == 200) {
			if ("data".equals(format) || "dataout".equals(format))
				ret = new Reserializer().reserializeData(result.getBody());
			else if ("list".equals(format))
				ret = new Reserializer().reserializeList(result.getBody());
			else
				ret = result.getBody();
		} else {
			ret = result.getStatus() + " " + result.getBody();
		}
		return ret;
	}

	private void saveOutput(String content, String type) {
		log.info("*** Test {}:\n{}", type, content.trim());
		FileUtil.fileWrite("target/" + testName.getMethodName() + "-" + type + ".txt", content);
	}

	private void assertFiles(SoftVisualAssert sva, String type) {
		String expected = FileUtil.fileRead("src/test/resources/" + testName.getMethodName() + "-" + type + ".txt",
				false);
		String actual = FileUtil.fileRead("target/" + testName.getMethodName() + "-" + type + ".txt", false);
		sva.assertEquals(expected == null ? "" : expected.replace("\r", ""),
				actual == null ? "" : actual.replace("\r", ""));
	}

}
