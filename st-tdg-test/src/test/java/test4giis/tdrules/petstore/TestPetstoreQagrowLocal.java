package test4giis.tdrules.petstore;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.IAttrGen;

/**
 * Mismos test que TestPetstoreDatagenLocal, 
 * generando los datos de prueba en un archivo utilizando QAGrow.
 * Utiliza un un esquema y un DataAdapter local, que no requiere una conexion activa a un servidor.
 * 
 * Está implementado de forma similar a TestPetstoreDatagenLocal, 
 * usando las mismas queries que define como constantes
 * y los mismos convenios (los ficheros para comparacion de salidas empiezan por qagrow-local-)
 * 
 */
public class TestPetstoreQagrowLocal extends BasePetstore {

	@Test
	public void testSmoke() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.querySmoke);
		assertData("qagrow-local-smoke.txt", dg);
	}

	@Test
	public void testPet0ByCategoryAndStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPet0ByCategoryAndStatus);
		assertData("qagrow-local-pet0-by-category-status.txt", dg);
	}
	
	@Test
	public void testPet1ByCategoryAndStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPet1ByCategoryAndStatus);
		assertData("qagrow-local-pet1-by-category-status.txt", dg);
	}
	
	@Test
	public void testPlacedPet0OrdersByCategoryAndOrderStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPlacedPet0OrdersByCategoryAndOrderStatus);
		assertData("qagrow-local-placed-pet0-orders-by-category-order-status.txt", dg);
	}
	
	@Test
	public void testPlacedPet0OrdersWithAlias() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPlacedPet0OrdersWithAlias);
		assertData("qagrow-local-placed-pet0-orders-by-category-order-status.txt", dg);

	}
	
	////////////////////////////// Generacion datos de arrays //////////////////////////////

	@Test
	public void testPetByCategoryAndStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPetByCategoryAndStatus);
		assertData("qagrow-local-pet-by-category-status.txt", dg);
	}

	@Test
	public void testPetByCategoryAndStatusWithDictionary() {
		//Repetir la misma anterior pero especificando el AttrGen con diccionario:
		//usar getDictionaryAttrGen() e inyectarlo en el DataGenerator
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getDataLoader().setAttrGen(dict);
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPetByCategoryAndStatus, dict);
		assertData("qagrow-local-pet-by-category-status-dict.txt", dg);
		
	}

	@Test
	public void testPetByUrlAndTag() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPetByUrlAndTag);
		assertData("qagrow-local-pet-by-url-tag.txt", dg);
	}
	
	@Test
	public void testPlacedPetOrdersByCategoryAndOrderStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPlacedPetOrdersByCategoryAndOrderStatus);
		assertData("qagrow-local-placed-pet-orders-by-category-order-status.txt", dg);
	}
	
	@Test
	public void testPlacedPetOrdersByAddressAndOrderStatus() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPlacedPetOrdersByAddressAndOrderStatus);
		assertData("qagrow-local-placed-pet-orders-by-address-order-status.txt", dg);
	}
	
	@Test
	public void testPlacedPetOrdersByAddressAndOrderStatusWithDictionary() {
		IAttrGen dict=getDictionaryAttrGen();
		DataLoader dg = getDataLoader().setAttrGen(dict);
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPlacedPetOrdersByAddressAndOrderStatus, dict);
		assertData("qagrow-local-placed-pet-orders-by-address-order-status-dict.txt", dg);
	}
	
	////////////////////////////// Generacion de datos con group by //////////////////////////////

	@Test
	public void testTotalOrdersToDeliverByAddress() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryTotalOrdersToDeliverByAddress);
		assertData("qagrow-local-total-pet-orders-by-address.txt", dg);
	}
	
	@Test
	public void testTotalPetsToDeliverByAddress() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryTotalPetsToDeliverByAddress);
		assertData("qagrow-local-total-pets-by-address.txt", dg);
	}

}
