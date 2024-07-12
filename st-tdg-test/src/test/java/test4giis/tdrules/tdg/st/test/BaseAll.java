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
 * This is a base class for all system and integration tests that
 * defines basic configurations to manage the different items involved in the tests:
 * 
 * - Abstract methods to configure main test parameters
 * - Methods to configure the main test objects (can be overriden for fine tuning)
 * - Test utilities (making assertions, serialization, etc.)
 *
 * The two main configuration methods are getSubjectName (to identify the SUT) and
 * isLiveBackend to indicate whether the test generates and sends data to a live backend
 * or everything is performed in local (no SUT needed).
 * These values in combination with the test inheritance, allow creating four
 * different flavours of the tests:
 * 
 * - datagen-local: a simulation of the data generation that manually specifies
 *   the commands sent to the Data Loader and gets the data that would be loaded.
 * - datagen-live: manually specifies the commands sent to the Data Loader,
 *   but loads the data in a live SUT backend
 * - qagrow-local: Automatically generates the test data, but working in local
 * - qagrow-live: Automatically generates the test data and loads the data
 *   in a live SUT backend.
 *   
 * The last is the true system test that integrates all main components:
 * data loader, qagrow generator, fpc rule generator and the SUT backend
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
	 * Returns a string to identify the test subject (e.g. petstore, market)
	 * to allow identify separate the expected and actual outputs
	 */
	protected abstract String getSutName();
	
	/**
	 * Returns true if the tests are running against a live backend, false by default
	 * (must be overriden by all live test methods)
	 */
	protected boolean isLiveBackend() {
		return false;
	}
	
	/**
	 * Returns the url of the backend
	 */
	protected abstract String getServerUrl();
	
	/**
	 * Returns the endpoint to get all data from the backend 
	 */
	protected abstract String getAllDataLiveEndpoint();

	/**
	 * Returns the endpoint to reset all data in the backend 
	 */
	protected abstract String getDeleteAllDataLiveEndpoint();

	/**
	 * Returns the data schema for each test.
	 * Each base test should configure the appropriate IdResolver and location of the OpenApi specification
	 */
	protected abstract TdSchema getSchema();
	
	/**
	 * Gets the TdRules client used to obtain the FPC coverage rules
	 */
	protected TdRulesApi getRulesApi() {
		return new TdRulesApi().setCache("../.tdrules-cache");
	}
	
	/**
	 * Gets the TdRules model for a given query, and reprocess the version numbers
	 * to allow comparison of expected test results
	 */
	protected TdRules getRules(String query) {
		TdRules rules = getRulesApi()
				.getRules(getSchema(), query, "noboundaries gettransformedquery formatquery clientname=" + getSutName());
		// remove version to allow result comparison
		return filterRulesVersion(rules);
	}
	
	/**
	 * Gets a data loader for the current configuration:
	 * - In local tests uses the default OaLocalAdapter.
	 * - In live tests uses the default OaLiveAdapter and the default OaPathResolver
	 *   configured to resolve paths from the schema model.
	 * If a custom path resolver is needed the test must override the getLiveDataLoader()
	 * method with the appropriate configuration. Calling getDataLoader() will invoke
	 * the overriden method.
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
	 * Generates test data for a given query using QAGrow and loads
	 * the data as indicated by the specified data loader (that can be
	 * either local or live)
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
	 * Gets all data from the backend 
	 */
	protected String getAllLiveData() {
		ApiWriter api=new ApiWriter();
		return api.get(getAllDataLiveEndpoint()).getBody();
	}
	/**
	 * Resets all data in the backend 
	 */
	protected String deleteAllLiveData() {
		ApiWriter api=new ApiWriter();
		return api.delete(getDeleteAllDataLiveEndpoint()).getBody();
	}

	/**
	 * General assert on the content of a model (as string) against the expected.
	 * Actual outputs are saved and then comparison is made between the content of
	 * the expected and actual files.
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
	 * Comparison is different if the test data is locally generated,
	 * or if it has been loaded to a live backend (in this case
	 * a call to get the data content is made before comparison)
	 */
	/**
	 * Gets a data loader according to the current configuration.
	 * The actual output data is obtanied as indicated:
	 * - In local uses the output produced by the data adapter.
	 * - In live tests queries the backend to get all stored data.
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
		// Gets the data from the backend and
		// uses a more compact presentation for easier comparison (an object per line)
		String payload=getAllLiveData();
		payload=reserializeStoredData(payload);
		log.info("Actual data stored in the backend\n{}", reserializeStoredData(payload));
		// Rename the file to separate locally generated from live loaded
		assertModel(fileName.replace("-local-", "-live-"), payload); 
	}

	/**
	 * Removes the version number of the FPC rules to allow repeatable comparisons
	 * (saves the version in target to use during debugging)
	 */
	protected TdRules filterRulesVersion(TdRules rules) {
		String version=rules.getVersion();
		String outputPath = FilenameUtils.concat(TEST_PATH_OUTPUT, getSutName());
		fileWrite(outputPath, "last-fpc-version.txt", version);
		rules.version("0.0.0").environment("development");
		return rules;
	}
	
	/**
	 * Serializa un objeto cualquiera a json mostrando los atributos vacios o nulos
	 */
	protected String serialize(TdSchema model) {
		return new ModelJsonSerializer().serialize(model, true);
	}

	/**
	 * Serializa el contenido de toda la base de datos como:
	 * - un objeto cuyos items son el contenido de cada una de las tablas
	 * - cada item es un objeto de clave nombre de tabla y valor un array 
	 *   con el contenido de cada fila de la tabla
	 * Devuelve un string donde cada linea es un objeto de clave
	 * nombre de tabla y valor el objeto con los valores de la fila
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
