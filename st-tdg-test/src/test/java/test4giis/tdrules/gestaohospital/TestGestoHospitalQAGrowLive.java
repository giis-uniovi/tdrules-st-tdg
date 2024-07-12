package test4giis.tdrules.gestaohospital;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;

public class TestGestoHospitalQAGrowLive extends BaseGestaoHospital {
	
	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	//Una query sencilla sobre hospital para prueba inicial de generación live
	//con datos representativos de hospitales tomados de un diccionario 
	@Test
	public void testSmokewithDict() { 
		String query =  "tds HospitalDTO where HospitalDTO.availableBeds=10";
		DataLoader dg = getDataLoader().setAttrGen(getDictionaryAttrGen());
		generateAndLoad(dg, query);
		assertData("qagrow-live-smoke-dict.txt", dg);
	}
	
	//Una query sobre los objetos de Hospital y Productos para comprobar que guarda hospitales y productos asociados
	//Lista de productos que tienen existencias (estoque) 
	@Test
	public void testAvalProductsbyHospital () {
		String query =  "tds HospitalDTO, ProductDTO where ProductDTO.quantity>0";
		DataLoader dg = getDataLoader().setAttrGen(getDictionaryAttrGen());
		generateAndLoad(dg, query);
		assertData("qagrow-live-aval-products-by-hospital.txt", dg);
	}
	
	//Otra sobre Hospital y Productos
	//Lista de productos sin existencias en hospitales con camas disponibles
	@Test
	public void testUnavProductsbyHospital () {
		String query =  "tds HospitalDTO, ProductDTO where HospitalDTO.availableBeds>0 and ProductDTO.quantity=0";
		DataLoader dg = getDataLoader().setAttrGen(getDictionaryAttrGen());
		generateAndLoad(dg, query);
		assertData("qagrow-live-unav-products-by-hospital.txt", dg);
	}
	
}
