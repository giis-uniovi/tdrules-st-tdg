package test4giis.tdrules.tdg.st.test.market;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;

/**
 * Generacion de datos para Market utilizando QAGrow.
 * Utiliza un un esquema y un DataAdapter local, que no requiere una conexion activa a un servidor.
 */
public class TestMarketQagrowLocal extends BaseMarket {

	@Test
	public void testProduct() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, queryProductByAge);
		assertData("qagrow-local-product.txt", dg);
	}
	
	@Test
	public void testUserByName() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, queryUserByName);
		assertData("qagrow-local-userbyname.txt", dg);
	}
	
	@Test
	public void testUserByEmail() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, queryUserByEmail);
		assertData("qagrow-local-userbyemail.txt", dg);
	}
}
