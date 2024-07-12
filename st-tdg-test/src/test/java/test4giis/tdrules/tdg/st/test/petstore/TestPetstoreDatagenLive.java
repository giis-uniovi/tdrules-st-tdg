package test4giis.tdrules.tdg.st.test.petstore;

/** 
 * Test Data generation for APIs (TDG) for the Swagger Petstore as SUT:
 * Only data loading, but using a live SUT.
 */
public class TestPetstoreDatagenLive extends TestPetstoreDatagenLocal {

	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
}
