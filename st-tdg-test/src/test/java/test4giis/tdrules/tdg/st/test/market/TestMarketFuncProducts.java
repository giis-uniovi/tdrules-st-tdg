package test4giis.tdrules.tdg.st.test.market;

import org.junit.Test;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.IAttrGen;

/**
 * Tests de funcionalidad de Products
 * Generación: QAGrow y claves se realiza en el backend
 */
public class TestMarketFuncProducts extends BaseMarket {

	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	/**
	 * Todos los productos de una destileria
	 */
	@Test
	public void testProductsByDistillery() {
		String query =  "tds ProductDTOReq where distillery ='Ardbeg'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistillery.txt", dg);
	}

	
	/**
	 * Productos de una destileria disponibles
	 */
	@Test
	public void testProductsByDistilleryAvaliable() {
		String query =  "tds ProductDTOReq where distillery ='Ardbeg' and available = 1";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistilleryAvailable.txt", dg);
	}
	
	/**
	 * Todos los productos de una destileria no disponibles
	 */
	@Test
	public void testProductsByDistilleryNotAvailable() {
		String query =  "tds ProductDTOReq where distillery ='Ardbeg' and available = 0";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistilleryNotAvailable.txt", dg);
	}
	
	/**
	 * Productos de una destileria disponibles entre dos precios
	 */
	@Test
	public void testProductsByDistilleryPrice() {
		String query =  "tds ProductDTOReq where distillery ='Ardbeg' and price < 100 and price > 5";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistilleryPrice.txt", dg);
	}
	
	/**
	 * Productos de una destileria disponibles entre dos precios
	 */
	@Test
	public void testProductsByDistilleryPriceAge() {
		String query =  "tds ProductDTOReq where distillery ='Ardbeg' and price < 100 and price > 5 and age = 12";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-ProductsByDistilleryPriceAge.txt", dg);
	}
	
	
	/**
	 * Prueba de generacion de distillery y region con claves en el backend, fijando distillery.title
	 */
	@Test
	public void testDistilleryByTitle() {
		String query =  "tds DistilleryDTOReq where title ='Ardbeg'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-DistilleryByTitle.txt", dg);
	}
	
	/**
	 * Prueba de generacion de distillery y region con claves en el backend, fijando distillery.region (region es maestro de distillery)
	 */
	@Test
	public void testDistilleryByRegion() {
		String query =  "tds DistilleryDTOReq where region ='Islay'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-DistilleryByRegion.txt", dg);
	}

}
