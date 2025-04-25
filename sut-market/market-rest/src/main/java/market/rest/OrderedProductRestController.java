package market.rest;

import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import market.domain.Product;
import market.domain.Order;
import market.domain.OrderedProduct;
import market.dto.OrderDTO;
import market.dto.OrderedProductDTO;
import market.dto.assembler.OrderDtoAssembler;
import market.service.ProductService;
import market.service.OrderService;
import market.service.OrderedProductService;

/*
 *  New class for test: endpoint (POST) to insert a ordered product into a order
 */
@RestController
@ExposesResourceFor(OrderedProductDTO.class)

public class OrderedProductRestController {

	private final ProductService productService;
	private final OrderService orderService;
	private final OrderedProductService orderedProductService;
	private final OrderDtoAssembler orderDtoAssembler = new OrderDtoAssembler();

	public OrderedProductRestController(ProductService productService, OrderService orderService, OrderedProductService orderedProductService) {
		this.productService = productService;
		this.orderService = orderService;
		this.orderedProductService = orderedProductService;
	}

	/*
	 * New endpoint for test: insert a new orderedProduct in a order
	*/
	@PostMapping (value = "orderedproductdto")
	public OrderDTO createOrderedProduct(@RequestBody OrderedProductDTO orderedProduct) {
		Order order = orderService.getOrder(orderedProduct.getOrderId());

		Product product = productService.getProduct(orderedProduct.getProductId());
		int quantity = orderedProduct.getQuantity();
		
		OrderedProduct op = orderedProductService.addToOrder(order, product, quantity);
		
		order = orderService.updateOrder(order, op);
		return orderDtoAssembler.toModel(order);
	}
}
