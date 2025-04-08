package market.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import market.dao.OrderedProductDAO;
import market.domain.Cart;
import market.domain.Order;
import market.domain.OrderedProduct;
import market.domain.OrderedProductId;
import market.domain.Product;
import market.service.OrderService;
import market.service.OrderedProductService;

@Service
public class OrderedProductServiceImpl implements OrderedProductService {
	private final OrderedProductDAO orderedProductDAO;

	public OrderedProductServiceImpl(OrderService orderService, OrderedProductDAO orderedProductDAO) {
		this.orderedProductDAO = orderedProductDAO;
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrderedProduct> findByOrder(Order order) {
		return new ArrayList<OrderedProduct>(order.getOrderedProducts());
	}

	@Transactional
	@Override
	public void create(OrderedProduct op) {
		orderedProductDAO.save(op);
	}

	@Transactional(readOnly = true)
	@Override
	public OrderedProduct fingById(OrderedProductId id) {
		return orderedProductDAO.findById(id.getCustomerOrder()).orElse(null);
	}
	
	// for tests
	@Transactional
	@Override
	public Order addToOrder(Order order, Product product, int quantity) {
		OrderedProduct op = new OrderedProduct.Builder()
				.setOrder(order)
				.setProduct(product)
				.setQuantity(quantity)
				.build();
		
		orderedProductDAO.save(op);
		
		return order;
	}
}
