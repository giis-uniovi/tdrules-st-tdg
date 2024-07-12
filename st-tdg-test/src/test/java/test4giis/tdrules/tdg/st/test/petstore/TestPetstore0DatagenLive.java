package test4giis.tdrules.tdg.st.test.petstore;

/**
 * Proof of concept of Test Data generation for APIs (TDG)
 * using simplified entities from the Swagger Petstore.
 * 
 * Tests in this class simulate the data generation by specifying
 * the commands sent to the Data Loader and load the data 
 * in a running (live) SUT backend.
 */
public class TestPetstore0DatagenLive extends TestPetstore0DatagenLocal {

	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	// all test methods are inherited

}
