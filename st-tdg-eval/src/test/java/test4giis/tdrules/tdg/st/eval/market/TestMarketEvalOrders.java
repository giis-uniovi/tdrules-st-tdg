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
	// after each test, init is false to accumulate result with the rest of them
	boolean init = true;
		
	/*
	 * From an user with at least an order, get orders
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
		
		report();
		assertReadResults(data);
		init = false;
	}
	
	/*
	 * From an user with at least an order, get orders
	 * */
	@Test
	public void testGetAnOrder() throws IOException {
		// include product 1 available
		// register user and generate a empty cart
		// add an order
		String [] queries = {"tds ProductDTORes where name='pr1' and available = 1",
							 "tds ProductDTORes where name='pr2' and available = 0",
	                         "tds UserDTORes where email = 'lucia@email.com' and password='123456'",
	                         "tds OrderDTORes,OrderedProductDTORes,ProductDTORes where userAccount='lucia@email.com' and "
	                         + " (OrderedProductDTORes.productId=1 or OrderedProductDTORes.productId=2) and ProductDTORes.available=1"};
		load(queries);
		
		// get order 1
		ApiResponse data = callSutGet("/customer/orders/1","lucia@email.com","123456", init);
		assertModel(testName.getMethodName() + "-1.txt",getResultString(data, "object"));
		
		report();
		assertReadResults(data);
		init = false;
	}
	
	/*
	 * Get the total costs of an user's orders not executed with delivery included
	 * 	SELECT u.email as email, SUM(o.productsCost) as totalAmount
        FROM Order o INNER JOIN UserAccount u on o.userAccount=u.id 
        WHERE o.deliveryIncluded = '1' AND o.executed = '0'
        GROUP BY o.userAccount
        
        User with several orders in different states executed/not executed and delivery included/not included
	 * 
	 * */
	@Test
	public void testTotalOrdersNoExecuted() throws IOException {
		/*String [] queries = {"tds UserDTORes where email = 'lucia@email.com' and password='123456'",
							 "tds UserDTORes where email = 'sofia@email.com' and password='1234567'",
							 "tds OrderDTORes,OrderedProductDTORes where userAccount='lucia@email.com' and "
						   + " OrderDTORes.deliveryIncluded='1' and OrderDTORes.executed='0' ",
						   	 "tds OrderDTORes,OrderedProductDTORes where userAccount='sofia@email.com' and "
			               + " OrderDTORes.deliveryIncluded='1' and OrderDTORes.executed='0' ",
			                 "select sum(OrderDTORes.productsCost) from OrderDTORes,OrderedProductDTORes "
			               + " where OrderDTORes.deliveryIncluded='1' and OrderDTORes.executed='0' "
	                       + " group by OrderDTORes.UserAccount "};
		 */	                       
		
		String [] queries = {"tds UserDTORes where email = 'lucia@email.com' and password='123456'",
				 "select sum(OrderDTORes.productsCost) from OrderDTORes,OrderedProductDTORes "
              + " where OrderDTORes.deliveryIncluded='1' and OrderDTORes.executed='0' "
              + " group by OrderDTORes.UserAccount "};
		load(queries);
		
		// get the orders
		ApiResponse data = callSutGet("/customer/orders/total-by-user","lucia@email.com","123456", init);
		assertModel(testName.getMethodName() + "-1.txt",getResultString(data, "object"));
		
		report();
		assertReadResults(data);
		init = false;
	}
	
	/*
	 * Get the user's orders not executed with delivery not included, grouped by address
	 * 	SELECT u.address as address
        FROM Order o INNER JOIN UserAccount u on o.userAccount=u.id 
        WHERE o.deliveryIncluded = '0' AND o.executed = '0'
        GROUP BY u.address
        
        User with several orders in different states executed/not executed and delivery included/not included
        with distinct/the same address than other 
	 * 
	 * */
	@Test
	public void testOrdersGroupAddressDeliveryNotIncludedNotExecuted() throws IOException {
		String [] queries = {"tds UserDTORes where email = 'lucia@email.com' and password='123456'",
				 "select OrderDTORes.address "
			  + " from OrderDTORes inner join OrderedProductDTORes on OrderDTORes.id=OrderedProductDTORes.orderId "
			  + " inner join UserDTORes on OrderDTORes.userAccount=UserDTORes.email"
              + " where OrderDTORes.deliveryIncluded='0' and OrderDTORes.executed='0' "
              + " group by UserDTORes.address "};
		load(queries);
		
		// get the orders
		ApiResponse data = callSutGet("/customer/orders/sameAddress?deliveryincluded=false&executed=false","lucia@email.com","123456", init);
		assertModel(testName.getMethodName() + "-1.txt",getResultString(data, "object"));
		
		report();
		assertReadResults(data);
		init = false;
	}

	
	/* órdenes de con envío incluido con importe superior a ...*
	 * 
	 * órdenes sin importe incluido del mismo zip
	 * información de diferentes customers
	 * informacion de tarjetas y pedidos de un customer
	 * 
	 *
	 * */
}
