package test4giis.tdrules.tdg.st.test.petstore;

/**
 * Proof of concept of Test Data generation for APIs (TDG)
 * using simplified entities from the Swagger Petstore.
 * 
 * Tests in this class Automatically generates the test data 
 * and load the data in a running (live) SUT backend.
 * 
 * This is the true system test that integrates all main components:
 * data loader, qagrow data generator, fpc rule generator and the SUT backend.
 */
public class TestPetstore0QagrowLive extends TestPetstore0QagrowLocal {

	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	// all test methods are inherited
	
}
