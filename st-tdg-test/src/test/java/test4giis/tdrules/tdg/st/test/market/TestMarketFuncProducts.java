package test4giis.tdrules.tdg.st.test.market;

import org.junit.Test;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.gen.IAttrGen;

/**
 * Products Functionality Tests
 * - Use the live SUT where the uids of each generated object are generated by the backend.
 * - Use the dictionary
 * - Include Products, Distilleries and Regions
 */
public class TestMarketFuncProducts extends BaseMarket {

	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	@Test
	public void testProductsNotAvailable() {
		// not available products 
		String query =  "tds ProductDTOReq where available=0";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsNotAvailable.txt", dg);
	}
	
	@Test
	public void testProductsAvailable() {
		// available products 
		String query =  "tds ProductDTOReq where available=1";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsAvailable.txt", dg);
	}
	
	@Test
	public void testProductsByPrice() {
		// products of a range of price
		String query =  "tds ProductDTOReq where price<=120";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByPrice.txt", dg);
	}
	
	@Test
	public void testProductsByAge() {
		// products of a range of age
		String query =  "tds ProductDTOReq where age>2 and age<=12";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByAge.txt", dg);
	}
	
	@Test
	public void testProductsByDistillery() {
		// products of a distillery
		String query =  "tds ProductDTOReq where distillery ='Ardbeg'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistillery.txt", dg);
	}
	
	@Test
	public void testProductsByRegion() {
		// products of a region
		String query =  "tds ProductDTOReq,DistilleryDTOReq where DistilleryDTOReq.region ='Islay'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByRegion.txt", dg);
	}
	
	@Test
	public void testProductsByDistilleryRegion() {
		// products of a distillery and a region
		String query =  "tds ProductDTOReq,DistilleryDTOReq,RegionDTOReq where DistilleryDTOReq.title='Ardbeg' and RegionDTOReq.name ='Islay'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistilleryRegion.txt", dg);
	}

	@Test
	public void testProductsByDistilleryAvaliable() {
		// available products of a distillery 
		String query =  "tds ProductDTOReq where distillery ='Ardbeg' and available = 1";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistilleryAvailable.txt", dg);
	}
	
	@Test
	public void testProductsByDistilleryNotAvailable() {
		// non-available products of a distillery
		String query =  "tds ProductDTOReq where distillery ='Ardbeg' and available = 0";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistilleryNotAvailable.txt", dg);
	}
	
	@Test
	public void testProductsByDistilleryPrice() {
		// products of a distillery, price between two values
		String query =  "tds ProductDTOReq where distillery ='Ardbeg' and price < 100 and price > 5";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistilleryPrice.txt", dg);
	}
	
	@Test
	public void testProductsByDistilleryPriceAge() {
		// products of a distillery and an age, price between two values
		String query =  "tds ProductDTOReq where distillery ='Ardbeg' and price < 100 and price > 5 and age = 12";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistilleryPriceAge.txt", dg);
	}
	
	@Test
	public void testDistilleriesByTitleRegion() {
		// distilleries by title and region
		String query =  "tds DistilleryDTOReq where title ='Ardbeg' and region ='Islay'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-DistilleriesByTitleRegion.txt", dg);
	}
	
	@Test
	public void testDistilleriesByTitle() {
		// distilleries by title 
		String query =  "tds DistilleryDTOReq where title ='Ardbeg'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-DistilleriesByTitle.txt", dg);
	}
	
	@Test
	public void testDistilleriesByRegion() {
		// distilleries of a region
		String query =  "tds DistilleryDTOReq where region ='Islay'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-DistilleriesByRegion.txt", dg);
	}
	
	@Test
	public void testRegionsByName() {
		// regions by name
		String query =  "tds RegionDTOReq where name ='Campbeltown'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-RegionsByName.txt", dg);
	}

}
