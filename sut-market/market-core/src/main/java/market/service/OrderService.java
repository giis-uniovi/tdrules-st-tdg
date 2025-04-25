package market.service;

import market.domain.Order;
import market.domain.OrderedProduct;
import market.exception.EmptyCartException;
import market.exception.UnknownEntityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface OrderService {

	/**
	 * @return all the orders of the specified user
	 */
	List<Order> getUserOrders(String userLogin);

	/**
	 * @return order of the specified user and id
	 * @throws UnknownEntityException if the requested order does not exist
	 */
	Optional<Order> getUserOrder(String userLogin, long orderId);

	/**
	 * @return orders filtered according to the passed parameters
	 */
	Page<Order> fetchFiltered(String executed, String created, PageRequest request);

	/**
	 * Creates new order for the specified user.
	 *
	 * @return newly created order
	 * @throws EmptyCartException if the specified user cart is empty
	 */
	Order createUserOrder(String userLogin, int deliveryCost, String cardNumber);

	/**
	 * Updates a state of the order with the specified id
	 */
	void updateStatus(long orderId, boolean executed);
	
	/**
	 * New method for test
	 * Creates new order
	 */
	Order createOrder(String userLogin, Order order, String ccNumber);
	
	/**
	 * New method for tests
	 * Gets an order by id
	 */
	Order getOrder(long orderId);

	/**
	 * New method for tests
	 * Updates an order inserting an ordered product
	 */
	Order updateOrder(Order order, OrderedProduct op);
}
