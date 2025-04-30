package market.service;

import java.util.List;

import market.domain.Order;
import market.domain.OrderedProduct;
import market.domain.OrderedProductId;
import market.domain.Product;

// For test: new interface for ordered products

public interface OrderedProductService {

	/**
	 * @return all the ordered products of an order
	 */
	List<OrderedProduct> findByOrder(Order order);

	/**
	 * Creates new ordered product.
	 */
	void create(OrderedProduct newOrderedProduct);
	
	/**
	 * @return the ordered product identified by its PK
	 */
	OrderedProduct fingById(OrderedProductId id);

	// test
	OrderedProduct addToOrder(Order order, Product product, int quantity);
}
