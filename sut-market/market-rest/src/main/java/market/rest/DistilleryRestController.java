package market.rest;

import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import market.domain.Distillery;
import market.dto.DistilleryDTO;
import market.dto.assembler.DistilleryDtoAssembler;
import market.service.DistilleryService;

/*
 *  New class for test: endpoint (POST) to insert a distillery
 */
@RestController
@ExposesResourceFor(DistilleryDTO.class)

public class DistilleryRestController {

	private final DistilleryService distilleryService;
	private final DistilleryDtoAssembler distilleryDtoAssembler = new DistilleryDtoAssembler();

	public DistilleryRestController(DistilleryService distilleryService) {
		this.distilleryService = distilleryService;
	}

	/*
	 * New endpoint for test: insert a new distillery in a region
	*/
	@PostMapping (value = "distillerydto")
	public DistilleryDTO createDistillery(@RequestBody DistilleryDTO distillery) {
		Distillery dis = distilleryDtoAssembler.toDomain(distillery);
		
		distilleryService.create(dis, distillery.getRegion());
		return distilleryDtoAssembler.toModel(distilleryService.findById(dis.getId()));
	}
}
