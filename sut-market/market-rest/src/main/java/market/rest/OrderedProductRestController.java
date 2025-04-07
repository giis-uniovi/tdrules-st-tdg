package market.rest;

import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import market.domain.Distillery;
import market.domain.OrderedProduct;
import market.dto.DistilleryDTO;
import market.dto.OrderedProductDTO;
import market.dto.assembler.DistilleryDtoAssembler;
import market.dto.assembler.OrderedProductDtoAssembler;
import market.service.DistilleryService;
import market.service.OrderedProductService;

/*
 *  New class for test: endpoint (POST) to insert a distillery
 */
@RestController
@ExposesResourceFor(DistilleryDTO.class)

public class OrderedProductRestController {

	private final OrderedProductService orderedProductService;
	private final OrderedProductDtoAssembler orderedProductDtoAssembler = new OrderedProductDtoAssembler();

	public OrderedProductRestController(OrderedProductService orderedProductService) {
		this.orderedProductService = orderedProductService;
	}

	/*
	 * New endpoint for test: insert a new orderedProduct in a order
	*/
	@PostMapping (value = "orderedproductdto")
	public OrderedProductDTO createOrderedProduct(@RequestBody OrderedProductDTO orderedProduct) {
		OrderedProduct op = orderedProductDtoAssembler.toDomain(orderedProduct);
		
		orderedProductService.create(op);
		return orderedProductDtoAssembler.toModel(orderedProductService.fingById(op.getPk()));
	}
}
