package test4giis.tdrules.tdg.st.eval.market;

import java.io.IOException;

import org.junit.Test;
import giis.tdrules.store.loader.oa.ApiResponse;

/**
 * Tests to generate, load and evaluate Test Data Generation (TDG) for
 * the market SUT using API requests that exercise the business logic.
 * 
 * Evaluation is carried out by QACover, measuring the sql fpc coverage or 
 * sql mutation score achieved when executing the API request.
 * Current results are shown for sql fpc rules:
 * - Actual and expected outputs are under a market folder at target 
 *   and src/test/resources, respectively
 * - Generated rules and reports are under target/market-qacover.
 *   The rules folder is mapped to the SUT to allow visibility from 
 *   outside of the container.
 * 
 * REQUIRED CONFIGURATION: QACover intercepts queries and evaluate the rules
 * inside the server, so that, some configuration is required before
 * starting the SUT to activate the evaluation.
 *
 * The CI workflow makes these changes, but you have to make them if runns
 * in a local development environment. Do not push the changes.
 * 
 * At sut-market/market-rest/src/main/resources:
 * - Replace the postgres driver by the spy driver in application.yml (spring.prfiles: prod)
 *     url: jdbc:p6spy:postgresql://${DB_HOST:localhost}:5432/market
 *     driverClassName: com.p6spy.engine.spy.P6SpyDriver
 * - If you want generate sql mutants instead of fpc rules, edit qacover properties
 *   and change fpc by mutation in the qacover.rule.criterion property
 * 
 * NOTE: As at this moment the SUT has simple queries only, this acts as a demo.
 */
public class TestMarketEval extends BaseMarketEval {

	/**
	 * As there is not yet business process queries, uses a simple query for demo
	 */
	
	@Test
	public void testProductsDemo() throws IOException {
		load("tds ProductDTORes, DistilleryDTORes, RegionDTORes where available = 1 and distillery ='Ardbeg'");
		// nota, buscar RegionDTORes.color='green' no obtiene regiones con diferentes colores
		ApiResponse data = callSutGet("/products/1", true);
		saveResults(data);
	}
	
	
}
