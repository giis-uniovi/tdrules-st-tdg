package test4giis.tdrules.tdg.st.eval.market;

import java.io.IOException;

import org.junit.Test;
import giis.tdrules.store.loader.oa.ApiResponse;

/**
 * Tests to generate, load and evaluate Test Data Generation (TDG) for
 * the market SUT using API requests that exercise the business logic.
 * 
 * Tests related to Carts (add products, update information of carts and delete carts)
 * 
 * Results are shown for mutation rules:
 * - Actual and expected outputs are under a market folder at target 
 *   and src/test/resources, respectively
 * - Generated rules and reports are under target/market-qacover.
 *   The rules folder is mapped to the SUT to allow visibility from 
 *   outside of the container.
 * 
 */
public class TestMarketEvalCarts extends BaseMarketEval {
	/*
	 * From an user with an empty cart:
	 * add units of a product unavailable
	 * add units of two product available
	 * modify the number of units (increasing and decreasing)
	 * */
	@Test
	public void testAddProducts() throws IOException {
		// include product 1 available
		// register user and generate a empty cart
		String [] queries = {"tds ProductDTORes where name='pr1' and available = 1",
							 "tds ProductDTORes where name='pr2' and available = 0",
	                         "tds UserDTORes where email = 'lucia@email.com' and password='123456'"};
		load(queries);
		
		// add product 3 (unavailable), quantity 2 units
		// since it is not possible to insert, output is an empty cart 
		ApiResponse data = callSutPost("/customer/cart",
						               "{\"productId\":\"3\", \"quantity\":\"2\"}",true,
						               "lucia@email.com","123456", true);
		assertModel(testName.getMethodName() + "-0.txt",getResultString(data, "object"));
		
		// add product 1, quantity 6
		data = callSutPost("/customer/cart",
				                       "{\"productId\":\"1\", \"quantity\":\"6\"}",true,
				                       "lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-1.txt",getResultString(data, "object"));
		
		// update units of product 1 decreasing 1 unit
		// product 1, quantity 4
		data = callSutPost("/customer/cart",
				                       "{\"productId\":\"1\", \"quantity\":\"4\"}",true,
				                       "lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-2.txt",getResultString(data, "object"));
		
		// update units of product 1 increasing 1 unit
		// product 1, quantity 5
		data = callSutPost("/customer/cart",
						               "{\"productId\":\"1\", \"quantity\":\"5\"}",true,
						               "lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-3.txt",getResultString(data, "object"));
		
		// add product 3 (unavailable), quantity 2 units
		// product 3, quantity 2
		// As it is not possible to insert, output of endpoint is the same as above, 
		data = callSutPost("/customer/cart",
						               "{\"productId\":\"3\", \"quantity\":\"2\"}",true,
						               "lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-3.txt",getResultString(data, "object"));
		
		// add product 2, quantity 1 unit
		// product 3, quantity 1
		data = callSutPost("/customer/cart",
						               "{\"productId\":\"2\", \"quantity\":\"1\"}",true,
						               "lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-4.txt",getResultString(data, "object"));
		
		saveResults(data);		
	}
	
	/*
	 * From an user with a non-empty cart:
	 * get the cart
	 * from delivery included to not included
	 * from not included to included
	 * pay
	 * 
	 * after paying, get the cart (it is empty)
	 * 
	 * */
	@Test
	public void testUpdateCart() throws IOException {
		// include product 1 available
		// register user and generate a empty cart
		String [] queries = {"tds UserDTORes where email = 'lucia@email.com' and password='123456'",
							 "tds CartDTO,CartItemDTORes,ProductDTORes "
						   + " where CartDTO.user='lucia@email.com' and "
						   + "       CartItemDTORes.productId=1 and CartItemDTORes.quantity=5 and "
						   + "       ProductDTORes.available=1 "};
		load(queries);

		ApiResponse data = callSutGet("/customer/cart","lucia@email.com","123456", true);
		
		assertModel(testName.getMethodName() + "-1.txt",getResultString(data, "object"));
		
		// change to not delivery included
		data = callSutPost("/customer/cart/delivery?included=false","",true,
						               "lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-2.txt",getResultString(data, "object"));
				
		// change to delivery included
		data = callSutPost("/customer/cart/delivery?included=true","",true,
						               "lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-3.txt",getResultString(data, "object"));
		
		
		// pay
		data = callSutPost("/customer/cart/pay", 
										"{\"ccNumber\":\"4030000010001234\"}", false,
										"lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-4.txt",getResultString(data, "object"));

		

		// after paying, the cart is empty
		data = callSutGet("/customer/cart", "lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-0.txt",getResultString(data, "object"));
		
		saveResults(data);
	}
	
	/*
	 * From an user with a non-empty cart:
	 * get the cart
	 * delete the cart
	 * get the cart (empty)
	 * try to pay
	 * */
	@Test
	public void testDeleteCart() throws IOException {
		// include product 1 available
		// register user and generate a empty cart
		String [] queries = {"tds UserDTORes where email = 'lucia@email.com' and password='123456'",
							 "tds CartDTO,CartItemDTORes,ProductDTORes "
						   + " where CartDTO.user='lucia@email.com' and "
						   + "       CartItemDTORes.productId=1 and CartItemDTORes.quantity=5 and "
						   + "       ProductDTORes.available=1 "};
		load(queries);

		ApiResponse data = callSutGet("/customer/cart","lucia@email.com","123456", true);
		
		assertModel(testName.getMethodName() + "-1.txt",getResultString(data, "object"));
		
		// delete the cart
		data = callSutDelete("/customer/cart",
						               "lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-0.txt",getResultString(data, "object"));
				
		// pay
		data = callSutPost("/customer/cart/pay", 
										"{\"ccNumber\":\"4030000010001234\"}", false,
										"lucia@email.com","123456", false);
		assertModel(testName.getMethodName() + "-2.txt",getResultString(data, "object"));
		
		saveResults(data);
	}
}
