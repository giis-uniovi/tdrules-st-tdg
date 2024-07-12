package test4giis.tdrules.tdg.st.test.gestaohospital;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;

/**
 * Generación de datos para gestaoHospital utilizando QAGrow
 * Mismos test que TestGestaoHospitalDatagenLocal, 
 * generando los datos de prueba en un archivo utilizando QAGrow.
 * Utiliza un un esquema y un DataAdapter local, que no requiere una conexion activa a un servidor.
 *  
 */
public class TestGestaoHospitalQagrowLocal extends BaseGestaoHospital {

	@Test
	public void testSmoke() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestGestaoHospitalDatagenLocal.querySmoke);
		assertData("qagrow-local-smoke.txt", dg);
	}

	@Test
	public void testProductbyProductTypeAndQuantity() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestGestaoHospitalDatagenLocal.queryProductByProductTypeAndQuantity);
		assertData("qagrow-local-product-by-productype-quantity.txt", dg);
	}
	
	@Test
	public void testHospitalProductbyProductTypeAndQuantity() {
		DataLoader dg = getDataLoader();
		generateAndLoad(dg, TestGestaoHospitalDatagenLocal.queryHospitalProductByProductTypeAndQuantity);
		assertData("qagrow-local-hospital-product-by-productype-quantity.txt", dg);
	}	
}
