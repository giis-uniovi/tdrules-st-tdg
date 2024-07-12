package market.rest;

import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import market.domain.Region;
import market.dto.RegionDTO;
import market.dto.assembler.RegionDtoAssembler;
import market.service.RegionService;

/*
 *  New class for test: endpoint (POST) to insert a region
 */
@RestController
@ExposesResourceFor(RegionDTO.class)
public class RegionRestController {

	private final RegionService regionService;
	private final RegionDtoAssembler regionDtoAssembler = new RegionDtoAssembler();
	
	public RegionRestController(RegionService regionService) {
		this.regionService = regionService;
	}

	/*
	 * New endpoint for test: insert a new region
	 */
	@PostMapping (value = "regiondto")
	public RegionDTO createRegion(@RequestBody RegionDTO region) {
		Region reg = regionDtoAssembler.toDomain(region);
		
		regionService.create(reg);
		
		return regionDtoAssembler.toModel(regionService.findByName(region.getName()));
	}
}
