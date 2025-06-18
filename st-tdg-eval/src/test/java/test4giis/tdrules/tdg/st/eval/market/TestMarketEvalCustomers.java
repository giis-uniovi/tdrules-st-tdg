package test4giis.tdrules.tdg.st.eval.market;

import java.io.IOException;

import org.junit.Test;
import giis.tdrules.store.loader.oa.ApiResponse;

/**
 * Tests to generate, load and evaluate Test Data Generation (TDG) for
 * the market SUT using API requests that exercise the business logic.
 * 
 * Tests related to Customers (register and access)
 * 
 * Results are shown for mutation rules:
 * - Actual and expected outputs are under a market folder at target 
 *   and src/test/resources, respectively
 * - Generated rules and reports are under target/market-qacover.
 *   The rules folder is mapped to the SUT to allow visibility from 
 *   outside of the container.
 * 
 */
public class TestMarketEvalCustomers extends BaseMarketEval {

	// parameter to indicate if initializing or accumulating results
	boolean init = true;
	
	/**
	 * get an user registered
	 */
	@Test
	public void testCustomerAccess() throws IOException {
		load("tds UserDTORes where email = 'lucia@email.com' and password = '123456'");
		
		ApiResponse data = callSutGet("/customer","lucia@email.com","123456", init);
		report();
		System.out.println("**** " + data.getBody());
		assertReadResults(data);
	}
	
	/**
	 * register an user 
	 */
	@Test
	public void testCustomerRegister() throws IOException {
		ApiResponse data = callSutPost("/register","{ \"address\": \"address\","
				                             	   +" \"email\": \"lucia@email.com\","
		                                   		   +" \"name\": \"Lucia\","
		                                   		   +" \"password\": \"123456\","
		                                   		   + "\"phone\": \"+34666666666\" }", false,"","", init);
		report();
		System.out.println("**** " + data.getBody());
		assertReadResults(data);
	}
	
	/**
	 * a user registered is not able to register again
	 */
	@Test
	public void testCustomerRegisterAgain() throws IOException {
		load("tds UserDTORes where email = 'lucia@email.com' and password = '123456'");
		
		ApiResponse data = callSutPost("/register","{ \"address\": \"address\","
				                             	   +" \"email\": \"lucia@email.com\","
		                                   		   +" \"name\": \"Lucy\","
		                                   		   +" \"password\": \"1234567\","
		                                   		   + "\"phone\": \"+34666666667\" }", false,"","", init);
		report();
		System.out.println("**** " + data.getBody());
		assertReadResults(data);
	}	
}
