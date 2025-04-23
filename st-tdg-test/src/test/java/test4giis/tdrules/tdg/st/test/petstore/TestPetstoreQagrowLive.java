package test4giis.tdrules.tdg.st.test.petstore;

/** 
 * Test Data generation for APIs (TDG) for the Swagger Petstore as SUT:
 * System test, including data generation with QAGrow and loading using a live SUT.
 */
public class TestPetstoreQagrowLive extends TestPetstoreQagrowLocal {

	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
}
