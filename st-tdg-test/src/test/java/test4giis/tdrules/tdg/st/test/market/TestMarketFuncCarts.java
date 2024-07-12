package test4giis.tdrules.tdg.st.test.market;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.IAttrGen;

/**
 * Tests de funcionalidad de Carts
 * Generación: QAGrow y claves se realiza en el backend
 */
public class TestMarketFuncCarts extends BaseMarket {
	
	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	@Test
	public void testDictUserDTOResByName() {
		String query = queryUserByName;
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-UsersByName.txt", dg);
	}

	/*
	 * Inserta carts pero no items ni productos. Para cubrir las reglas de cobertura no son necesarios
	 * Son carts vacíos. 
	 */
	@Test
	public void testDictCartDTOByUser() {
		String query = "tds CartDTO where user='lucia@email.com'";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-CartsByUser.txt", dg);
	}
	
	/* Inserta carts y sus items.
	 * Para que se inserte un producto en un carrito, debe estar disponible, necesario incluirlo en la query.
	 * Si no se indica en la query, se genera por DataGenerator y es no disponible (available = false),
	 * entonces al intentar añadirlo via api, no se inserta en la tabla (lo que es correcto)
	 * Hay otra regla de cobertura que no se cubre con una única base de datos:
	 * - user='lucia...' and productId=1 and quantity != 5
	 * En este caso, desde QAGrow ya no se generan los datos.
	 */
	@Test
	public void testDictCartDTOByUserProductQuantity() {
		String query = queryCartByUserProductQuantity;
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, query, dict);
		assertData("func-CartsByUserProductQuantity.txt", dg);
	}
	
	/* Inserta order a un usuario.
	 * Para que cree una order, el carrito no puede estar vacio.
	 * Por tanto, habrá una query1 para generar carritos con productos y query2 para generar la orden del carrito
	 */
	@Test
	public void testOrderDTOByUser() {
		String query1= queryCartByUserProductQuantity;
		String query2 = "tds OrderDTO where userAccount='pepe@email.com' ";
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getLiveDataLoader().setAttrGen(dict);
		generateAndLoad(dg, new String[] {query1, query2}, dict);
		assertData("func-OrderByUser.txt", dg);
	}
}
