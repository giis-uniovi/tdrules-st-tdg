package test4giis.tdrules.tdg.st.eval.petstore;

import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.gen.DictionaryAttrGen;
import giis.tdrules.store.loader.gen.IAttrGen;
import giis.tdrules.store.loader.oa.ApiWriter;
import giis.tdrules.store.loader.oa.OaLiveAdapter;
import giis.visualassert.Framework;
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

	protected static TdSchema model; // readonly, created before all tests

	@Override
	protected boolean isLiveBackend() {
		return false; // this eval runs a custom mock controller
	}
	
	protected void load(ApiWriter writer, String query) {
		model = getSchema();
		
		// Check the schema, because their changes may invalidate all results
		String modelStr = new giis.tdrules.model.io.ModelJsonSerializer().serialize(model, true);
		FileUtil.fileWrite("target/schema-petstore.json", modelStr);
		VisualAssert va = new VisualAssert().setFramework(Framework.JUNIT4);
		va.assertEquals(FileUtil.fileRead("src/test/resources/schema-petstore.json"), modelStr);
		// Until here, this should be run once in BeforeClass, but some side effects on QAGrow#40 prevent doing that

		// The path resolver is created using a controller mock (writer)
		// and the data loader using a dictionary
		DataLoader loader = new DataLoader(model, new OaLiveAdapter("").setApiWriter(writer))
				.setAttrGen(getPetstoreEvalDictionary().setMinYear(2024));

		// Generation and loading: Each test first delete all data previous to the generation and load
		writer.delete("/test/deleteAll");
		QAGrowApiProcess qagrow = new QAGrowApiProcess(model, getRulesApi(), loader);
		qagrow.genData4ApiQuery(query);
	}

	/**
	 * Same dictionary, but constraining the interval of order.quantity
	 */
	protected IAttrGen getPetstoreEvalDictionary() {
		return ((DictionaryAttrGen)getDictionaryAttrGen()).with("Order", "quantity").setInterval(1, 13);
	}

}
