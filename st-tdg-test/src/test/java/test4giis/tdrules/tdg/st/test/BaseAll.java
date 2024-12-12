package test4giis.tdrules.tdg.st.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giis.tdrules.client.TdRulesApi;
import giis.tdrules.model.io.ModelJsonSerializer;
import giis.tdrules.openapi.model.TdRules;
import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.IAttrGen;
import giis.tdrules.store.loader.oa.ApiWriter;
import giis.tdrules.store.loader.oa.IPathResolver;
import giis.tdrules.store.loader.oa.OaLiveAdapter;
import giis.tdrules.store.loader.oa.OaLocalAdapter;
import giis.tdrules.store.loader.oa.OaPathResolver;
import giis.tdrules.store.loader.oa.Reserializer;
import giis.visualassert.Framework;
import giis.visualassert.VisualAssert;
import in2test.application.qagrow.QAGrowApiProcess;

/**
 * This is the base class for all system and integration tests to
 * define basic configurations and manage the different items involved in testing:
 * 
 * - Abstract methods to configure the main test parameters
 * - Methods to configure the main test objects (can be overridden for fine tuning)
 * - Test utilities (making assertions, serialization, etc.)
 *
 * The two main configurations for every test are:
 * 
 * - getSubjectName() to identify the SUT, and
 * - isLiveBackend() to indicate whether the test generates and sends data to a live backend
 *   or if everything is performed locally (without running the SUT).
 * 
 * These values, in combination with the test inheritance, allow creating four
 * different flavours of the tests:
 * 
 * - datagen-local: simulates the data generation by specifying
 *   the commands sent to the Data Loader and gets the data to be loaded.
 * - datagen-live: manually specifies the commands sent to the Data Loader,
 *   but loads the data in a running (live) SUT backend.
 * - qagrow-local: automatically generates the test data, but working locally.
 * - qagrow-live: automatically generates and loads the test data
 *   in a running (live) SUT backend.
 *   
 * The last is the true system test that integrates all main components:
 * data loader, qagrow data generator, fpc rule generator and the SUT backend.
 */
public abstract class BaseAll {
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String TEST_PATH_BENCHMARK = "src/test/resources";
	private static final String TEST_PATH_OUTPUT = "target/out";

	@Rule
	public TestName testName = new TestName();

	@Before
	public void setUp() {
		log.info("****** Running {} test: {} ******", getSutName(), testName.getMethodName());
		if (isLiveBackend()) {
			log.info("Clear out data stored in the backend");
			deleteAllLiveData();
		}
	}

	/**
	 * Returns a string to identify the SUT (e.g. petstore, market)
	 * to allow separately identifying the expected and actual outputs
	 * (must be overridden by all test methods, usually in a base 
	 * class for the SUT)
	 */
	protected abstract String getSutName();
	
	/**
	 * Returns true if the tests are run against a live backend, false by default
	 * (must be overridden by all live test methods)
	 */
	protected boolean isLiveBackend() {
		return false;
	}
	
	/**
	 * Returns the url of the SUT backend
	 */
	protected abstract String getServerUrl();
	
	/**
	 * Returns the endpoint to get all data from the SUT backend 
	 */
	protected abstract String getAllDataLiveEndpoint();

	/**
	 * Returns the endpoint to reset all data in the SUT backend 
	 */
	protected abstract String getDeleteAllDataLiveEndpoint();

	/**
	 * Returns the data schema for each test.
	 * Each SUT base test should configure the appropriate IdResolver and location of the OpenApi specification
	 */
	protected abstract TdSchema getSchema();
	
	/**
	 * Gets the TdRules client used to obtain the FPC coverage rules
	 */
	protected TdRulesApi getRulesApi() {
		return new TdRulesApi().setCache("../.tdrules-cache");
	}
	
	/**
	 * Gets the TdRules model for a given query and reprocesses the version numbers
	 * to allow comparison of expected test results
	 */
	protected TdRules getRules(String query) {
		TdRules rules = getRulesApi()
				.getRules(getSchema(), query, "noboundaries gettransformedquery formatquery clientname=" + getSutName());
		return filterRulesVersion(rules); // remove version to allow result comparison
	}
	
	/**
	 * Gets a data loader for the current configuration:
	 * - In local tests, uses the default OaLocalAdapter.
	 * - In live tests, uses the default OaLiveAdapter and the default OaPathResolver
	 *   configured to resolve paths from the schema model.
	 *   
	 * If a custom path resolver is needed, the test must override the getLiveDataLoader()
	 * method with the appropriate configuration so that calling getDataLoader() invokes
	 * the overridden method.
	 */
	protected DataLoader getDataLoader() {
		return isLiveBackend() ? getLiveDataLoader() : getLocalDataLoader();
	}
	protected DataLoader getLocalDataLoader() {
		return new DataLoader(getSchema(), new OaLocalAdapter());
	}
	protected DataLoader getLiveDataLoader() {
		TdSchema model = getSchema();
		IPathResolver pathResolver = new OaPathResolver().setSchemaModel(model).setServerUrl(getServerUrl());
		return new DataLoader(model, new OaLiveAdapter(pathResolver));
	}
	
	/**
	 * Generates test data for a given query using qagrow and loads
	 * the data as indicated by the specified data loader (which can be
	 * local or live)
	 */
	protected void generateAndLoad(DataLoader loader, String query) {
		log.info("Generate test data for query\n{}", query);
		QAGrowApiProcess qagrow = new QAGrowApiProcess(getSchema(), getRulesApi(), loader);
		qagrow.genData4ApiQuery(query);
	}
	
	protected void generateAndLoad(DataLoader loader, String query, IAttrGen dictionary) {
		log.info("Generate test data for query\n{}", query);
		QAGrowApiProcess qagrow = new QAGrowApiProcess(getSchema(), getRulesApi(), loader, dictionary);
		qagrow.genData4ApiQuery(query);
	}
	
	protected void generateAndLoad(DataLoader loader, String[] queries, IAttrGen dictionary) {
		log.info("Generate test data for query\n{}", queries.toString());
		QAGrowApiProcess qagrow = new QAGrowApiProcess(getSchema(), getRulesApi(), loader, dictionary);
		qagrow.genData4ApiQueries(Arrays.asList(queries));
	}
	
	
	/**
	 * Gets all data stored in the SUT backend 
	 */
	protected String getAllLiveData() {
		ApiWriter api=new ApiWriter();
		return api.get(getAllDataLiveEndpoint()).getBody();
	}
	/**
	 * Resets all data stored in the SUT backend 
	 */
	protected String deleteAllLiveData() {
		ApiWriter api=new ApiWriter();
		return api.delete(getDeleteAllDataLiveEndpoint()).getBody();
	}

	/**
	 * General assert on a model (as string) against the expected.
	 * Actual outputs are saved and then the comparison is made between the content of
	 * the expected and actual files
	 */
	protected void assertModel(String fileName, String actualModel) {
		String outFolder = FilenameUtils.concat(TEST_PATH_OUTPUT, getSutName());
		String bmkFolder = FilenameUtils.concat(TEST_PATH_BENCHMARK, getSutName());
		actualModel = actualModel.replace("\r", ""); // normalize end of line
		fileWrite(outFolder, fileName, actualModel);
		
		String expected = fileRead(bmkFolder, fileName).replace("\r", "");
		new VisualAssert().setFramework(Framework.JUNIT4)
				.assertEquals(expected, actualModel, "failed " + fileName, "diff-" + getSutName() + "-" + fileName + ".html");
		assertEquals(expected, actualModel);
	}
	
	/**
	 * Assert to compare the test data that has been generated or loaded.
	 * The comparison is different if the test data is generated locally,
	 * or if it has been loaded into a live backend (in this case
	 * a call is made to get the data content before the comparison)
	 */
	protected void assertData(String fileName, DataLoader dg) {
		if (isLiveBackend())
			assertLiveData(fileName, dg);
		else
			assertLocalData(fileName, dg);
	}
	protected void assertLocalData(String fileName, DataLoader dg) {
		assertModel(fileName, dg.getDataAdapter().getAllAsString());
	}
	protected void assertLiveData(String fileName, DataLoader dg) {
		// Gets the data from the backend and transforms it into
		// a more compact presentation for easier comparison (one object per line)
		String payload=getAllLiveData();
		payload=reserializeStoredData(payload);
		log.info("Actual data stored in the backend\n{}", reserializeStoredData(payload));
		// Rename the file to separate the locally generated from the live loaded
		assertModel(fileName.replace("-local-", "-live-"), payload); 
	}

	/**
	 * Removes the version number of the FPC rules to allow repeatable comparisons
	 * (saves the renamed version in target for use during debugging)
	 */
	protected TdRules filterRulesVersion(TdRules rules) {
		String version=rules.getVersion();
		String outputPath = FilenameUtils.concat(TEST_PATH_OUTPUT, getSutName());
		fileWrite(outputPath, "last-fpc-version.txt", version);
		rules.version("0.0.0").environment("development");
		return rules;
	}
	
	/**
	 * General purpose serialization of an object for use in test comparison
	 */
	protected String serialize(TdSchema model) {
		return new ModelJsonSerializer().serialize(model, true);
	}

	/**
	 * Given a serialized json string that represents the data stored in a SUT,
	 * transforms the json to get a compact representation of the data
	 * (a line for each object) suitable for display and test comparison
	 */
	protected String reserializeStoredData(String payload) {
		return new Reserializer().reserializeData(payload);
	}

	protected void fileWrite(String path, String fileName, String value) {
		try {
			FileUtils.writeStringToFile(new File(FilenameUtils.concat(path, fileName)), value, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String fileRead(String path, String fileName) {
		try {
			return FileUtils.readFileToString(new File(FilenameUtils.concat(path, fileName)), "UTF-8");
		} catch (FileNotFoundException e) {
			return "";
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
