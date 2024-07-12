package test4giis.tdrules.petstore;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;

/**
 * Tests adicionales con QAGrow para comprobar la generacion de claves en el backend
 * extendiendo algunas funcionalidades del petstore para que generen valores de estas
 */
public class TestPetstoreQagrowLiveBackId extends TestPetstoreDatagenLiveBackId {

	/**
	 * Prueba de con dos entidades con referencias generando claves en el backend
	 */
	@Test
	public void testPetByCategoryAndStatus() {
		//Deben ser los mismos que en el correspondiente live, pero cambia el valor
		//que tienen las claves primarias (Category son multiplos de 10 y Pet multiplos de 1000)
		
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestPetstoreDatagenLocal.queryPetByCategoryAndStatus);
		assertData("qagrow-livebackid-pet-by-category-status.txt", dg);
	}

}
