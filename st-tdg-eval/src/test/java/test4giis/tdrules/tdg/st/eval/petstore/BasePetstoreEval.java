package test4giis.tdrules.tdg.st.eval.petstore;

import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.gen.DictionaryAttrGen;
import giis.tdrules.store.loader.gen.IAttrGen;
import giis.tdrules.store.loader.oa.ApiResponse;
import giis.tdrules.store.loader.oa.ApiWriter;
import giis.tdrules.store.loader.oa.OaLiveAdapter;
import giis.tdrules.store.loader.oa.Reserializer;
import giis.visualassert.Framework;
import giis.visualassert.SoftVisualAssert;
import giis.visualassert.VisualAssert;
import giis.visualassert.portable.FileUtil;
import in2test.application.qagrow.QAGrowApiProcess;
import lombok.extern.slf4j.Slf4j;
import test4giis.tdrules.tdg.st.test.petstore.BasePetstore;

/**
 * Utilities and creation of the main objects to generate, load and evaluate the petstore
 * Test Data Generation (TDG)
 */
@Slf4j
public class BasePetstoreEval extends BasePetstore {

	// All these tests use a mock controller to run mutants in the same process that the test
	protected MockController mvc = new MockController();

	protected SoftVisualAssert sva = new SoftVisualAssert().setFramework(Framework.JUNIT4);

	@Override
	protected boolean isLiveBackend() {
		return false; // this eval runs a custom mock controller
	}
	
	protected void load(ApiWriter writer, String query) {
		TdSchema schema = getSchema();
		
		// Check the schema, because their changes may invalidate all results
		String modelStr = new giis.tdrules.model.io.ModelJsonSerializer().serialize(schema, true);
		FileUtil.fileWrite("target/schema-petstore.json", modelStr);
		VisualAssert va = new VisualAssert().setFramework(Framework.JUNIT4);
		va.assertEquals(FileUtil.fileRead("src/test/resources/schema-petstore.json"), modelStr);
		// Until here, this should be run once in BeforeClass, but some side effects on QAGrow#40 prevent doing that

		// The path resolver is created using a controller mock (writer)
		// and the data loader using a dictionary
		DataLoader loader = new DataLoader(schema, new OaLiveAdapter("").setApiWriter(writer))
				.setAttrGen(getPetstoreEvalDictionary().setMinYear(2024));

		// Generation and loading: Each test first delete all data previous to the generation and load
		writer.delete("/test/deleteAll");
		QAGrowApiProcess qagrow = new QAGrowApiProcess(schema, getRulesApi(), loader);
		qagrow.genData4ApiQuery(query);
	}

	/**
	 * Same dictionary, but constraining the interval of order.quantity
	 */
	protected IAttrGen getPetstoreEvalDictionary() {
		return ((DictionaryAttrGen)getDictionaryAttrGen()).with("Order", "quantity").setInterval(1, 13);
	}
	
	// utility methods for assertions
	
	// to check get operations that do not modify the database
	protected void assertReadResults(ApiResponse result) {
		sva.assertClear();
		assertResults(true, false, true, result);
		sva.assertAll();
	}
	
	protected void assertResults(boolean checkBefore, boolean checkAfter, boolean checkResult, ApiResponse result) {
		if (checkBefore) {
			ApiResponse data = mvc.get("/test/getAll");
			saveOutput(getResultString(data, "data"), "data");
			assertFiles(sva, "data");
		}
		if (checkAfter) {
			ApiResponse data = mvc.get("/test/getAll");
			saveOutput(getResultString(data, "dataout"), "dataout");
			assertFiles(sva, "dataout");
		}
		if (checkResult) {
			saveOutput(getResultString(result, "list"), "output");
			assertFiles(sva, "output");
		}
	}
	
	private String getResultString(ApiResponse result, String format) {
		String ret = "";
		if (result.getStatus() == 200) {
			if ("data".equals(format) || "dataout".equals(format))
				ret = new Reserializer().reserializeData(result.getBody());
			else if ("list".equals(format))
				ret = new Reserializer().reserializeList(result.getBody());
			else
				ret = result.getBody();
		} else {
			ret = result.getStatus() + " " + result.getBody();
		}
		return ret;
	}

	private void saveOutput(String content, String type) {
		log.info("*** Test {}:\n{}", type, content.trim());
		FileUtil.fileWrite("target/" + testName.getMethodName() + "-" + type + ".txt", content);
	}

	private void assertFiles(SoftVisualAssert sva, String type) {
		String expected = FileUtil.fileRead("src/test/resources/" + testName.getMethodName() + "-" + type + ".txt",
				false);
		String actual = FileUtil.fileRead("target/" + testName.getMethodName() + "-" + type + ".txt", false);
		sva.assertEquals(expected == null ? "" : expected.replace("\r", ""),
				actual == null ? "" : actual.replace("\r", ""));
	}

}
