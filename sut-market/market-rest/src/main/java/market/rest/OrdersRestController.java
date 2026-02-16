package market.rest;

import static java.util.stream.Collectors.toList;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import market.domain.Order;
import market.dto.AddressOrderDTO;
import market.dto.OrderDTO;
import market.dto.UserOrderTotalDTO;
import market.dto.assembler.OrderDtoAssembler;
import market.exception.UnknownEntityException;
import market.properties.MarketProperties;
import market.service.OrderService;

/**
 * Customer orders history.
 */
@RestController
@RequestMapping(value = "customer/orders")
@ExposesResourceFor(OrderDTO.class)
@Secured({"ROLE_USER"})
public class OrdersRestController {
	private final OrderService orderService;
	private final OrderDtoAssembler orderDtoAssembler;
	
	public OrdersRestController(OrderService orderService, MarketProperties marketProperties) {
		this.orderService = orderService;
		
		// add for test
		orderDtoAssembler = new OrderDtoAssembler();
	}

	/**
	 * View orders.
	 *
	 * @return orders list of the specified customer
	 */
	@GetMapping
	public List<OrderDTO> getOrders(Principal principal) {
		return orderService.getUserOrders(principal.getName()).stream()
			.map(orderDtoAssembler::toModel)
			.collect(toList());
	}

	/**
	 * View a single order.
	 *
	 * @return order of the specified customer
	 * @throws UnknownEntityException if the order with the specified id doesn't exist
	 */
	@GetMapping(value = "/{orderId}")
	public OrderDTO getOrder(Principal principal, @PathVariable long orderId) {
		String login = principal.getName();
		return orderService.getUserOrder(login, orderId)
			.map(orderDtoAssembler::toModel)
			.orElseThrow(() -> new UnknownEntityException(OrderDTO.class, orderId));
	}
	
	/**
	 * New endpoint (POST) for test: insert a new order
	 */
	@PostMapping(value = "orderdto")
	public OrderDTO createOrder(Principal principal, @RequestBody @Valid OrderDTO orderdto) {
		Order order = orderDtoAssembler.toDomain(orderdto);
		order = orderService.createOrder(principal.getName(), order, orderdto.getCcNumber());
		
		return orderDtoAssembler.toModel(order);
	}
	
	/**
	 * New endpoint (GET) for testing: add to get the total costs  and number of orders 
	 * of an user
	 */
	@GetMapping("/total-by-user")
    public ResponseEntity<List<UserOrderTotalDTO>> getTotalOrdersByUser() {
        List<UserOrderTotalDTO> totals = orderService.getTotalAmountByUser();
        return ResponseEntity.ok(totals);
    }
	
	/**
	 * New endpoint (GET) for testing: add to get the total costs and number of orders non executed
	 * of an user
	 */
	@GetMapping("/non-executed-by-user")
    public ResponseEntity<List<UserOrderTotalDTO>> getTotalNonExecutedOrdersByUser() {
        List<UserOrderTotalDTO> totals = orderService.getTotalNonExecutedOrdersByUser();
        return ResponseEntity.ok(totals);
    }
	
	/**
	 * New endpoint (GET) for testing: add to get orders with the same address
	 */
	@GetMapping(value = "sameAddress")
	public ResponseEntity<List<AddressOrderDTO>> getOrdersSameAddress(@RequestParam boolean deliveryincluded, @RequestParam boolean executed) {
		List<AddressOrderDTO> totals = orderService.getOrdersSameAddress(deliveryincluded, executed);
        return ResponseEntity.ok(totals);
    }
}
