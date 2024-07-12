package market.rest;

import java.sql.SQLException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import market.service.TestService;

/*
 *  New class for test: endpoints (GET and DELETE) to get and detele all data in the database
 */
@RestController
@RequestMapping(value = "test")
public class TestRestController {
	private final TestService testService;
	
	public TestRestController(TestService testService) {
		this.testService = testService;
	}
	/**
	 * New endpoint for test: get all the existing data
	 */
	@GetMapping("/getAll")
	public Object getAll() {
		final Object allData = testService.getAll();
		
		return allData;
	}
	
	/**
	 * New endpoint for test: delete all the existing data
	 */
	@DeleteMapping("/deleteAll")
	public void deleteAll() throws SQLException {
		testService.deleteAll();
	}
}
