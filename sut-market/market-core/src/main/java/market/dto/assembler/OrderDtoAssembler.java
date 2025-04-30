package market.dto.assembler;

import market.domain.Order;
import market.dto.OrderDTO;
import market.domain.OrderedProduct;
import market.dto.OrderedProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderDtoAssembler implements RepresentationModelAssembler<Order, OrderDTO> {

	@Override
	public OrderDTO toModel(Order order) {
		OrderDTO dto = new OrderDTO();
		dto.setId(order.getId());
		dto.setUserAccount(order.getUserAccount().getEmail());
		dto.setBillNumber(order.getBill().getNumber());
		dto.setProductsCost(order.getProductsCost());
		dto.setDateCreated(order.getDateCreated());
		dto.setDeliveryCost(order.getDeliveryCost());
		dto.setTotalCost(order.isDeliveryIncluded() ? (order.getProductsCost() + order.getDeliveryCost()) : order.getProductsCost());
		dto.setDeliveryIncluded(order.isDeliveryIncluded());
		dto.setPayed(order.getBill().isPayed());
		dto.setExecuted(order.isExecuted());
		
		// Tests: incluce ccNumber
		dto.setCcNumber(order.getBill().getCcNumber());
		
		// Tests: include to obtain orderedProductDtos from order
		Set<OrderedProductDTO> orderedProductDto = order.getOrderedProducts().stream()
			.map(this::toOrderedProductDto)
			.collect(Collectors.toSet());
		dto.setOrderedProducts(orderedProductDto);
		return dto;
	}

	// Tests: include to obtain orderedProductDtos from each orderedProduct in the order
	private OrderedProductDTO toOrderedProductDto(OrderedProduct orderedProduct) {
		OrderedProductDTO dto = new OrderedProductDTO();
		dto.setOrderId(orderedProduct.getOrder().getId());
		dto.setProductId(orderedProduct.getProduct().getId());
		dto.setQuantity(orderedProduct.getQuantity());
		return dto;
	}

	public PageImpl<OrderDTO> toModel(Page<Order> page) {
		List<OrderDTO> dtoList = page.map(this::toModel).toList();
		return new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
	}

	public OrderDTO[] toDtoArray(List<Order> items) {
		return toCollectionModel(items).getContent().toArray(new OrderDTO[items.size()]);
	}
	
	// new method for tests
	public Order toDomain(OrderDTO dto) {
		return new Order.Builder()
			.setDateCreated(dto.getDateCreated())
			.setDeliveryCost(dto.getDeliveryCost())
			.setDeliveryIncluded(dto.isDeliveryIncluded())
			.setExecuted(dto.isExecuted())
			.setProductsCost(dto.getProductsCost())
			.build();
	}
}
