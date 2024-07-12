package br.com.codenation.hospital.resource;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.codenation.hospital.constant.Constant;
import br.com.codenation.hospital.services.TestService;

@RestController
@RequestMapping(path = Constant.V1)

// Added a resource for test

public class TestResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestResource.class);
	
	@Autowired
	private TestService service;	
	
	@DeleteMapping(path = "/test/deleteAll")
	public ResponseEntity<String> deleteAll() {
		try {
			service.deleteAll();
			return ResponseEntity.ok().body("Deleted all data");
		
		} catch (Exception e) {
			LOGGER.error("deleteAll - Handling error with message: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
	
	@GetMapping(path = "/test/getAll")
	public ResponseEntity<Object> getAll() {
		try {
			Object allData = service.getAll();
			return Optional.ofNullable(allData).map(testResponse -> ResponseEntity.ok().body(testResponse))
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception e) {
			LOGGER.error("getAll - Handling error with message: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
}
