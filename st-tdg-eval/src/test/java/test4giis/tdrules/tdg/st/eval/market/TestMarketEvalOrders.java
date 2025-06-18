package test4giis.tdrules.tdg.st.eval.market;

import java.io.IOException;

import org.junit.Test;
import giis.tdrules.store.loader.oa.ApiResponse;

/**
 * Tests to generate, load and evaluate Test Data Generation (TDG) for
 * the market SUT using API requests that exercise the business logic.
 * 
 * Tests related to Orders (get orders )
 * 
 * Results are shown for sql fpc rules:
 * - Actual and expected outputs are under a market folder at target 
 *   and src/test/resources, respectively
 * - Generated rules and reports are under target/market-qacover.
 *   The rules folder is mapped to the SUT to allow visibility from 
 *   outside of the container.
 * 
 */
public class TestMarketEvalOrders extends BaseMarketEval {
	
	// parameter to indicate if initializing or accumulating results
	boolean init = true;
		
	/*
	 * From an user with almost an order, get orders
	 * */
	@Test
	public void testGetOrders() throws IOException {
		// include product 1 available
		// register user and generate a empty cart
		// add an order
		String [] queries = {"tds ProductDTORes where name='pr1' and available = 1",
							 "tds ProductDTORes where name='pr2' and available = 0",
	                         "tds UserDTORes where email = 'lucia@email.com' and password='123456'",
	                         "tds OrderDTORes,OrderedProductDTORes,ProductDTORes where userAccount='lucia@email.com' and "
	                         + " (OrderedProductDTORes.productId=1 or OrderedProductDTORes.productId=2) and ProductDTORes.available=1"};
		load(queries);
		
		// get the orders
		ApiResponse data = callSutGet("/customer/orders","lucia@email.com","123456", init);
		assertModel(testName.getMethodName() + "-1.txt",getResultString(data, "object"));
		
		
		// get order 1
		data = callSutGet("/customer/orders/1","lucia@email.com","123456", !init);
		assertModel(testName.getMethodName() + "-2.txt",getResultString(data, "object"));
		
		report();
		assertReadResults(data);
		
	}
	
	/*
	 * From an user with almost an order, get orders
	 * */
	@Test
	public void testGetOrders2() throws IOException {
		// include product 1 available
		// register user and generate a empty cart
		// add an order
		String [] queries = {"tds ProductDTORes where name='pr1' and available = 1",
							 "tds ProductDTORes where name='pr2' and available = 0",
	                         "tds UserDTORes where email = 'lucia@email.com' and password='123456'",
	                         "tds OrderDTORes,OrderedProductDTORes,ProductDTORes where userAccount='lucia@email.com' and "
	                         + " (OrderedProductDTORes.productId=1 or OrderedProductDTORes.productId=2) and ProductDTORes.available=1"};
		load(queries);
		
		// get the orders
		ApiResponse data = callSutGet("/customer/orders","lucia@email.com","123456", init);
		assertModel(testName.getMethodName() + "-1.txt",getResultString(data, "object"));
		
		report();
		assertReadResults(data);
		
	}	
}
