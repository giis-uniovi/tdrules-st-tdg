package test4giis.tdrules.tdg.st.test.market;

import org.junit.Test;
import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.IAttrGen;

/**
 * Tests para generacion de datos con QAGrow
 * Las generacion de claves se realiza en el backend
 */
public class TestMarketQagrowLiveBackId extends BaseMarket {

	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	/**
	 * Prueba de generacion de regiones con claves en el backend
	 */
	@Test
	public void testRegionByName() {
		String query =  "tds RegionDTOReq where name ='Campbeltown'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("qagrow-livebackid-RegionByName.txt", dg);
	}

	/**
	 * Prueba de generacion de distillery y region con claves en el backend, fijando distillery.title
	 */
	@Test
	public void testDistilleryByTitle() {
		String query =  "tds DistilleryDTOReq where title ='Balvenie'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("qagrow-livebackid-DistilleryByTitle.txt", dg);
	}
	
	/**
	 * Prueba de generacion de distillery y region con claves en el backend, fijando distillery.region (region es maestro de distillery)
	 */
	@Test
	public void testDistilleryByRegion() {
		String query =  "tds DistilleryDTOReq where region ='Highland'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("qagrow-livebackid-DistilleryByRegion.txt", dg);
	}

}
