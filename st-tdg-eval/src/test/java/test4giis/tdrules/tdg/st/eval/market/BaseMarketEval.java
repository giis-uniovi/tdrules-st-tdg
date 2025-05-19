package test4giis.tdrules.tdg.st.eval.market;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import giis.tdrules.openapi.model.TdSchema;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.gen.IAttrGen;
import giis.tdrules.store.loader.oa.ApiResponse;
import giis.tdrules.store.loader.oa.ApiWriter;
import giis.tdrules.store.loader.oa.Reserializer;
import giis.visualassert.Framework;
import giis.visualassert.SoftVisualAssert;
import giis.visualassert.portable.FileUtil;
import in2test.application.qagrow.QAGrowApiProcess;
import test4giis.tdrules.tdg.st.test.market.BaseMarket;

public class BaseMarketEval extends BaseMarket {

	@Override
	protected boolean isLiveBackend() {
		return true; // ensure the common setup to clear-out existing data
	}
	
	protected String getSutRulesFolder() { // mapped folder in the SUT container
		return "../sut-market/market-rest/target/qacover/rules";
	}
	protected String getRulesFolder() { // rules copies from SUT, a folder per test
		return "./target/market-qacover/rules" + "-" + testName.getMethodName();
	}
	protected String getReportsFolder() { // a report folder per test
		return "./target/market-qacover/reports" + "-" + testName.getMethodName();
	}
	
	protected void report() {
		new giis.qacover.report.ReportManager()
			.run(getSutRulesFolder(), getReportsFolder(), "../sut-market/market-core/src/main/java", "");
		// Coverage data can be retrieved using giis.qacover.reader.CoverageReader
	}
	
	protected void load(String query) throws IOException {
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		TdSchema schema = getSchema();
		
		// Clear-out any rule from other tests, can fail if not exists
		cleanDirectory(getSutRulesFolder(), true);

		QAGrowApiProcess qagrow = new QAGrowApiProcess(schema, getRulesApi(), dg, dict);
		qagrow.genData4ApiQuery(query);
		
	}
	
	protected ApiResponse callSut(String path) throws IOException {
		// Before invoking the SUT, clear-out rules and reports previously generated
		cleanDirectory(getSutRulesFolder(), true);
		cleanDirectory(getRulesFolder(), false);
		cleanDirectory(getReportsFolder(), false);
		
		// after execution copies the rules for further reporting
		ApiWriter api = new ApiWriter();
		ApiResponse response = api.get(MARKET_URL_LIVE + path);
		FileUtils.copyDirectory(new File(getSutRulesFolder()), new File(getRulesFolder()), false);
		return response;
	}
	
	// to check get operations that do not modify the database
	protected void assertReadResults(ApiResponse result) throws JsonMappingException, JsonProcessingException {
		SoftVisualAssert sva = new SoftVisualAssert().setFramework(Framework.JUNIT4);
		ApiWriter api = new ApiWriter();
		ApiResponse data = api.get(MARKET_URL_LIVE + "/test/getAll");
		
		saveOutput(getResultString(data, "data"), "data");
		assertFiles(sva, "data");

		saveOutput(getResultString(result, "object"), "output");
		assertFiles(sva, "output");

		sva.assertAll();
	}
	
	protected String getResultString(ApiResponse result, String format) throws JsonMappingException, JsonProcessingException {
		String ret = "";
		if (result.getStatus() == 200) {
			if ("object".equals(format)) {
				ret = new ObjectMapper().readTree(result.getBody()).toPrettyString();
			} else if ("data".equals(format))
				ret = new Reserializer().reserializeData(result.getBody());
			else
				ret = result.getBody();
		} else {
			ret = result.getStatus() + " " + result.getBody();
		}
		return ret;
	}
	
	protected void cleanDirectory(String directory, boolean failIfNotExists) {
		try {
			FileUtils.cleanDirectory(new File(directory));
		} catch (IOException e) {
			if (failIfNotExists)
				throw new RuntimeException(e);
			// else ignore if no reports already generated
		}
		
	}

	protected void saveOutput(String content, String type) {
		log.info("*** Test {}:\n{}", type, content.trim());
		FileUtil.fileWrite("target/market/" + testName.getMethodName() + "-" + type + ".txt", content);
	}

	protected void assertFiles(SoftVisualAssert sva, String type) {
		String expected = FileUtil.fileRead("src/test/resources/market/" + testName.getMethodName() + "-" + type + ".txt",
				false);
		String actual = FileUtil.fileRead("target/market/" + testName.getMethodName() + "-" + type + ".txt", false);
		sva.assertEquals(expected == null ? "" : expected.replace("\r", ""),
				actual == null ? "" : actual.replace("\r", ""));
	}

}
