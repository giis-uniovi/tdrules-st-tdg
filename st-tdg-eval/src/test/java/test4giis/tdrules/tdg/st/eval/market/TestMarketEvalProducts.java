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
public class TestMarketEvalProducts extends BaseMarketEval {
	
	// parameter to indicate if initializing or accumulating results
	// after each test, init is false to accumulate result with the rest of them
	boolean init = true;
		
	/**
	 * Get total of sold products 
	 * */
	@Test
	public void testTotalSoldProducts() throws IOException {
		String [] queries = {"tds ProductDTORes where name='pr1' and available = 1",
				 "tds UserDTORes where email = 'lucia@email.com' and password='123456'",
				 "tds OrderDTORes,OrderedProductDTORes,ProductDTORes "
               + " group by ProductDTORes.productId "};
		load(queries);
		
		// get the sold products
		ApiResponse data = callSutGet("/products/soldProducts","lucia@email.com","123456", init);
		assertModel(testName.getMethodName() + "-1.txt",getResultString(data, "object"));
		
		report();
		assertReadResults(data);
	}
	
}
