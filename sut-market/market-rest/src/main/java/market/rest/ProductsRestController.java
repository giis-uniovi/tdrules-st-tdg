package market.rest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import market.domain.Product;
import market.dto.ProductDTO;
import market.dto.ProductTotalDTO;
import market.dto.SalesByRegionDistilleryDTO;
import market.dto.assembler.ProductDtoAssembler;
import market.exception.UnknownEntityException;
import market.service.ProductService;

@RestController
@RequestMapping(value = "products")
@ExposesResourceFor(ProductDTO.class)
public class ProductsRestController {

	private final ProductService productService;
	private final ProductDtoAssembler productAssembler = new ProductDtoAssembler();

	public ProductsRestController(ProductService productService) {
		this.productService = productService;
	}

	/*
	 * New endpoint for test: insert a new product of a distillery
	*/
	@PostMapping(value = "productdto")
	public ProductDTO createProduct(@RequestBody ProductDTO product) {
		Product prod = productAssembler.dtoDomain(product);
		
		productService.create(prod, product.getDistillery());
		return productService.findById(prod.getId())
				.map(productAssembler::toModel)
				.map(this::addListLink)
				.orElseThrow(() -> new UnknownEntityException(ProductDTO.class, prod.getId()));
	}
	
	/**
	 * All the existing products, sorted by id.
	 */
	@GetMapping
	public Collection<ProductDTO> getProducts() {
		return productService.findAll().stream()
			.sorted(Comparator.comparing(Product::getId))
			.map(productAssembler::toModel)
			.peek(this::addSelfLink)
			.collect(Collectors.toList());
	}

	private void addSelfLink(ProductDTO dto) {
		dto.add(linkTo(methodOn(getClass()).getProduct(dto.getProductId())).withRel("self"));
	}

	/**
	 * Viewing a single product.
	 *
	 * @return product with the specified id
	 * @throws UnknownEntityException if the product with the specified id doesn't exist
	 */
	@GetMapping(value = "/{productId}")
	public ProductDTO getProduct(@PathVariable long productId) {
		return productService.findById(productId)
			.map(productAssembler::toModel)
			.map(this::addListLink)
			.orElseThrow(() -> new UnknownEntityException(ProductDTO.class, productId));
	}

	private ProductDTO addListLink(ProductDTO dto) {
		dto.add(linkTo(methodOn(getClass()).getProducts()).withRel("All products"));
		return dto;
	}
	
	/**
	 * New endpoint (GET) for testing: add to get the total number of sold products 
	 */
	@GetMapping("/soldProducts")
	public ResponseEntity<List<ProductTotalDTO>> getTotalSoldProducts() {
	    List<ProductTotalDTO> totals = productService.getTotalSoldProducts();
	    // rows are sorted by id
	    totals.sort(Comparator.comparingInt(ProductTotalDTO::getId));
	    return ResponseEntity.ok(totals);
	}
	
	/**
	 * New endpoint (GET) for testing: add to get the total sales by region and distillery
	 */
	@GetMapping("/salesByRegionDistillery")
	public ResponseEntity<List<market.dto.SalesByRegionDistilleryDTO>> getSalesByRegionDistillery() {
	    List<SalesByRegionDistilleryDTO> sales = productService.getSalesByRegionDistillery();
	    // rows are sorted by regionId and distilleryId (getters return primitive int)
	    sales.sort(Comparator.comparingInt(SalesByRegionDistilleryDTO::getRegionId)
	                         .thenComparingInt(SalesByRegionDistilleryDTO::getDistilleryId));
	    return ResponseEntity.ok(sales);
	}
	
	
}