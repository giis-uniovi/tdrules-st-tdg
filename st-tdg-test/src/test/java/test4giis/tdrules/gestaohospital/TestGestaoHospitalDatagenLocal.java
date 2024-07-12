package test4giis.tdrules.gestaohospital;

import org.junit.Test;

import giis.tdrules.model.io.TdRulesXmlSerializer;
import giis.tdrules.openapi.model.TdRules;
import giis.tdrules.store.loader.DataLoader;

/**
 * Generación de datos para gestaoHospital utilizando Datagen
 * Utiliza un un esquema y un DataAdapter local, que no requiere una conexion activa a un servidor.
 * 
 */
public class TestGestaoHospitalDatagenLocal extends BaseGestaoHospital {

	/**
	 * Una query simple con una unica tabla para la prueba inicial que comprueba que se genera correctamente
	 */
	public static String querySmoke = "select * from HospitalDTO where availableBeds=10";
	@Test
	public void testSmoke() {
		TdRules rules=getRules(querySmoke);
		assertModel("rules-smoke.xml", new TdRulesXmlSerializer().serialize(rules));
		
		//Las reglas buscan una Hospital con availableBeds=10 y otro que no 
		DataLoader dg = getDataLoader();
		dg.load("HospitalDTO","availableBeds=10");
		dg.load("HospitalDTO","availableBeds=1");
		assertData("datagen-local-smoke.txt", dg);
	}

	/**
	 * Una query que busca Productos de tipo "COMMON" que tenga disponibilidad :
	 *    TDS ProductDTO where ProductDTO.productType='COMMON' and ProductDTO.quantity>0   
	 */
	public static String queryProductByProductTypeAndQuantity = 
			"tds ProductDTO where ProductDTO.productType='COMMON' and ProductDTO.quantity>0";
	@Test
	public void testProductbyProductTypeAndQuantity() {
		TdRules rules = getRules(queryProductByProductTypeAndQuantity);
		assertModel("rules-product-by-producttype-and-quantity.xml", new TdRulesXmlSerializer().serialize(rules));
		
		DataLoader dg = getDataLoader();
		//Una fila ProductType=COMMON y quantity >0
		dg.load("ProductDTO","productType=COMMON, quantity=1");
		//dos filas en las que falla la igualdad en cada una de estas propiedades
		dg.load("ProductDTO","productType=BLOOD, quantity=1"); 
		dg.load("ProductDTO","productType=COMMON, quantity=0");
		
		assertData("datagen-local-product-by-productype-quantity.txt", dg);
	}
	
	/**
	 * Una query similar a la anterior buscando Productos de tipo "COMMON" que tenga disponibilidad 
	 * pero que estén en algún Hospital (join relacional de dos tablas)
	 *    TDS ProductDTO, HospitalDTO where ProductDTO.productType='COMMON' and ProductDTO.quantity>0   
	 */
	public static String queryHospitalProductByProductTypeAndQuantity = 
			"tds ProductDTO,HospitalDTO where ProductDTO.productType='COMMON' and ProductDTO.quantity>0";
	@Test
	public void testHospitalProductbyProductTypeAndQuantity() {
		TdRules rules = getRules(queryHospitalProductByProductTypeAndQuantity);
		assertModel("rules-hospital-product-by-producttype-and-quantity.xml", new TdRulesXmlSerializer().serialize(rules));
		
		DataLoader dg = getDataLoader();
		//La primera regla debe generar un maestro HospitalDTO con Productos
		//que cumplan la decisión del where
		dg.load("HospitalDTO","id=@hid1");
		dg.load("ProductDTO","id=@pid1,hospitalDTOId=@hid1 ,productType=COMMON, quantity=1");
		//Reutiliza el mismo hospital pero con productos que no cumplen las condiciones
		dg.load("ProductDTO","id=@pid2,hospitalDTOId=@hid1,productType=BLOOD,quantity=1");
		dg.load("ProductDTO","id=@pid3,hospitalDTOId=@hid1,productType=COMMON,quantity=0");
		//Un hospital sin productos
		dg.load("HospitalDTO","id=@hid2");
				
		assertData("datagen-local-hospital-product-by-productype-quantity.txt", dg);
	}
}
