package test4giis.tdrules.tdg.st.test.petstore;

import org.junit.Test;

import giis.tdrules.model.io.TdRulesXmlSerializer;
import giis.tdrules.openapi.model.TdRules;
import giis.tdrules.store.loader.DataLoader;

/**
 * Prueba de concepto de generacion de datos de prueba a partir de una especificacion openapi.
 * 
 * El escenario inicial es estamos especificando un escenario de prueba en Gherkin para una aplicacion
 * que hace un uso intensivo de datos. Para ello sera necesario especificar una serie de ejemplos
 * que estan formados por diferentes conjuntos de datos que representen diferentes casuisticas
 * (situaciones a probar).
 * 
 * En vez de especificar estos datos se especificara una query en un lenguaje similar a sql
 * (en realidad es una extension de sql). Esta query se denomina "Test Data Specification" (TDS)
 * 
 * El modelo openapi sera trasformado en un modelo Test Data Model (TDM)
 * que se implementa como un modelo DbSchema que permite generar
 * reglas de cobertura fpc, las cuales determinaran las diferentes casuisticas a partir de esta especificacion.
 * Posteriormente se generaran los datos que satisfagan esas reglas 
 * y se insertaran en la aplicacion utilizando su propia api.
 * 
 * Esta clase genera las reglas e cobertura fpc y establece manualmente los comandos de generacion de datos para cubrirlas.
 * Utiliza un un esquema y un DataAdapter local, que no requiere una conexion activa a un servidor.
 */
public class TestPetstoreDatagenLocal extends BasePetstore {

	/**
	 * Una query simple con una unica tabla para la prueba inicial que comprueba que se genera correctamente
	 */
	public static String querySmoke = "select * from Category where name='Dogs'";
	@Test
	public void testSmoke() {
		TdRules rules=getRules(querySmoke);
		assertModel("rules-smoke.xml", new TdRulesXmlSerializer().serialize(rules));
		//Las reglas buscan una categoria que no sea Dogs y otra que si lo sea.
		DataLoader dg = getDataLoader();
		dg.load("Category","name=Dogs");
		dg.load("Category","");
		assertData("datagen-local-smoke.txt", dg);
	}

	/**
	 * Sobre una variante simplificada Pet0 sin arrays ni referencias externas
	 * Una query que busca Pets con categoria Dogs y que esten disponibles para la venta, la query es:
	 * 
	 *   TDS Pet0 where Pet0.category::name='Dogs' and Pet0.status='available'
	 * 
	 * Utiliza la notacion TDS para las queries (especificaciones)
	 * El tipo de datos de la propiedad category no es primitivo, sino que esta definida inline como otro objeto.
	 * La notacion :: indica el acceso a las propiedades un objeto interno.
	 */
	public static String queryPet0ByCategoryAndStatus = 
			"tds Pet0 where Pet0.category::name='Dogs' and Pet0.status='available'";
	@Test
	public void testPet0ByCategoryAndStatus() {
		TdRules rules = getRules(queryPet0ByCategoryAndStatus);
		assertModel("rules-pet0-by-category-status.xml", new TdRulesXmlSerializer().serialize(rules));
		
		DataLoader dg = getDataLoader();
		//Las reglas category=Dogs y status=available
		dg.load("Pet0","category::name=Dogs, status=available");
		//y luego otras dos filas en las que falla la igualdad en cada una de estas propiedades
		dg.load("Pet0","category::name=1, status=available"); //1 es un valor !=Dogs indicado por qagrow
		dg.load("Pet0","category::name=Dogs, status=sold"); //sold es un valor !=available indicado por qagrow entre los permitidos 
		assertData("datagen-local-pet0-by-category-status.txt", dg);
	}
	
	/**	
	 * Sobre una variante simplificada Pet1 sin arrays pero con referencias externas
	 * La query es la misma que la anterior:
	 * 
	 * 	 tds Pet1 where Pet1.category::name='Dogs' and Pet1.status='available'
	 * 
	 * La primera transformacion realizada al crear el esquema es crear un tipo Pet1_category_xt
	 * que tiene una FK a Category, este sera el tipo asignado a Pet1.category
	 * 
	 * Como el esquema referencia un objeto independiente Category, los datos de Pet1::category
	 * deben ser consistentes con los que haya en Category (se esta desnormalizando/duplicando datos).
	 * Para ello en la generacion de reglas se hace otra transformacion anyadiendo un left join con Category
	 * (a la que se le asigna el alias Pet1_category_xref por si hay otros composites que referencian a Category).
	 * La query transformada sera:
	 * 
	 * SELECT * FROM Pet1
     *   LEFT JOIN Category Pet1_category_xref ON Pet1.category::id = Pet1_category_xref.id
     *   WHERE Pet1.category::name = 'Dogs' AND Pet1.status = 'available'
	 */
	public static String queryPet1ByCategoryAndStatus =
			"tds Pet1 where Pet1.category::name='Dogs' and Pet1.status='available'";
	@Test
	public void testPet1ByCategoryAndStatus() {
		TdRules rules = getRules(queryPet1ByCategoryAndStatus);
		assertModel("rules-pet1-by-category-status.xml", new TdRulesXmlSerializer().serialize(rules));

		DataLoader dg = getDataLoader();
		//Las tres primeras reglas son como en la anterior, pero ahora los datos estan en otra tabla Category
		//la primera crea una category=Dogs y Pet1 que satisface las dos condiciones del where
		dg.load("Category", "id=@cid1, name=Dogs");
		dg.load("Pet1", "id=@pid1, category::id=@cid1, status=available");
		//la segunda busca Pet1.status!=available, por lo que reutiliza la categoria anterior
		dg.load("Pet1", "id=@pid2, category::id=@cid1, status=sold");//sold es un valor !=available indicado por qagrow entre los permitidos 
		//la tercera busca Pet1.category diferente de Dogs, por lo que debe crear un maestro nuevo
		dg.load("Category", "id=@cid2, name=1"); //1 es un valor !=Dogs indicado por qagrow
		dg.load("Pet1", "id=@pid3, category::id=@cid2, status=available");
		
		//La regla de pet sin categoria no se genera porque hay integridad referencial y la categoria en el pet es no nullable
		//NOTA: el parser de swagger siempre devuelve no nullables los composites que provienen de una regla externa
		
		//La quinta busca una fila de Category sin Pet1, 
		//notar que las dos condiciones sobre pet se han reducido por estar en el outer increment
		dg.load("Category", "id=@cid3");
		
		assertData("datagen-local-pet1-by-category-status.txt", dg);
	}
	
	/**
	 * Join relacional entre tres tablas:
	 * Busca el Client de un nombre dado y sus Order0 que tienen status=placed para Pet1 con category=Dogs
	 * La notacion de esta query seria:
	 * 
	 *   tds Customer0 xjoin Order0 xjoin Pet where Pet0.category::name='Dogs' and Order0.status='placed'
	 *   
	 * que se representa de forma simplificada como:
	 * 
	 *   tds Customer0, Order0, Pet where Pet0.category::name='Dogs' and Order0.status='placed'
	 *   
	 * donde cada xjoin se transforma en una join por las pk y fk de las tablas involucradas (similar a natural join),
	 * dando lugar a una query transformada como la siguiente (usando notacion postgres):
	 * 
	 * SELECT * FROM Customer0
     *   INNER JOIN "Order0" ON Customer0.id = Order0.CustomerId
     *   INNER JOIN Pet0 ON Order0.petId = Pet0.id
     *   WHERE (Pet0.category).name = 'Dogs' AND "Order0".status = 'placed'
	 */
	public static String queryPlacedPet0OrdersByCategoryAndOrderStatus=
			"tds Customer0, \"Order0\", Pet0"
				+ " where Pet0.category::name='Dogs' and \"Order0\".status='placed'";
	@Test
	public void testPlacedPet0OrdersByCategoryAndOrderStatus() {
		TdRules rules = getRules(queryPlacedPet0OrdersByCategoryAndOrderStatus);
		assertModel("rules-placed-pet0-orders-by-category-order-status.xml", new TdRulesXmlSerializer().serialize(rules));
		
		DataLoader dg = getDataLoader();
		//la primera crea maestros Customer0 y Pet0 con un Order0 que satisface las dos condiciones del where
		dg.load("Customer0", "id=@cid1");
		dg.load("Pet0", "id=@pid1, category::name=Dogs");
		dg.load("Order0", "id=@oid1, customerId=@cid1, petId=@pid1, status=placed");
		
		//la segunda require (Pet0.category).name!=Dogs, necesita un nuevo maestro
		dg.load("Pet0","id=@pid2, category::name=1"); //name indicado por qagrow !=Dogs
		dg.load("Order0","id=@oid2, customerId=@cid1, petId=@pid2, status=placed"); //status indicado por qagrow !=placed
		//la tercera require status!=placed, reutiliza los primeros mestros
		dg.load("Order0","id=@oid3, customerId=@cid1, petId=@pid1, status=delivered"); //status indicado por qagrow !=placed
		
		//La cuarta es un Customer0 sin Order0, 
		//notar que como no hay order, tampoco habra pet y las condiciones del where han reducido por estar en el outer increment
		dg.load("Customer0", "id=@cid2");
		//La quinta es un Pet0 sin Order0, en este caso debe mantenerse ((Pet0.category).name = 'Dogs')
		dg.load("Pet0", "id=@pid2, category::name=Dogs");

		assertData("datagen-local-placed-pet0-orders-by-category-order-status.txt", dg);
	}
	
	/**
	 * La misma query debe procesarse correctamente si se usan alias o no se usan prefijos:
	 * En Pet la category se usa sin prefijo, en Order el status con alias (para distinguir del status de un Pet)
	 */
	public static String queryPlacedPet0OrdersWithAlias=
			"tds Customer0 c, \"Order0\" o, Pet0"
				+ " where category::name='Dogs' and o.status='placed'";
	@Test
	public void testPlacedPet0OrdersWithAlias() {
		TdRules rules = getRules(queryPlacedPet0OrdersWithAlias);
		assertModel("rules-placed-pet0-orders-with-alias.xml", new TdRulesXmlSerializer().serialize(rules));
	}
	
	////////////////////////////// Generacion de arrays //////////////////////////////

	/**	
	 * La query es la misma que la que usa Pet1, pero ahora se usa Pet que contiene dos arrays
	 * 
	 * 	 tds Pet where Pet.category::name='Dogs' and Pet.status='available'
	 * 
	 * Las transformaciones del esquema, ademas de crear el tipo Pet_category_xref
	 * crean dos arrays Pet_Tags_xa y Pet_photoUrls_xa.
	 * Estos arrays se muestran como tablas de tipo array
	 * con sus propias claves pk y fk que referencian a Pet
	 * 
	 * Aunque son tablas detalle, en la generacion se deberan incluir antes que la 
	 * generacion de la correspondiente fila de Pet usando como fk el valor simbolico de la pk de Pet
	 * (nota: la pk se genera automaticamente de forma secuencial,
	 * podria ser innecesaria, dependiendo de si la necesita o no QAGrow)
	 * 
	 * Las transformaciones de reglas anyaden joins para estas dos tablas.
	 * La query transformada (usando notacion postgres) sera:
	 * 
	 * SELECT * FROM Pet
     *   LEFT JOIN Pet_tags_xa ON Pet.id = Pet_tags_xa.fk_xa
     *   LEFT JOIN Pet_photoUrls_xa ON Pet.id = Pet_photoUrls_xa.fk_xa
     *   LEFT JOIN Category Pet_category_xref ON Pet.category::id = Pet_category_xref.id
     *   WHERE Pet.category::name = 'Dogs' AND Pet.status = 'available'
     *   
     * Este test contiene dos variante una usando el AttrGen por defecto y otra usando un diccionario
	 */
	public static String queryPetByCategoryAndStatus =
			"tds Pet where Pet.category::name='Dogs' and Pet.status='available'";
	@Test
	public void testPetByCategoryAndStatus() {
		TdRules rules = getRules(queryPetByCategoryAndStatus);
		assertModel("rules-pet-by-category-status.xml", new TdRulesXmlSerializer().serialize(rules));
		DataLoader dg = getDataLoader();
		doTestPetByCategoryAndStatus(dg, "datagen-local-pet-by-category-status.txt");
	}
	@Test
	public void testPetByCategoryAndStatusWithDictionary() {
		//no comprueba reglas aqui, son las mismas que el anterior
		DataLoader dg = getDataLoader().setAttrGen(getDictionaryAttrGen());
		doTestPetByCategoryAndStatus(dg, "datagen-local-pet-by-category-status-dict.txt");
	}
	private void doTestPetByCategoryAndStatus(DataLoader dg, String outputFileName) {
		//Las reglas son como en testPet1ByCategoryAndStatus pero ahora hay arrays, con lo que antes de generar cada Pet
		//se anyadiria al menos una fila con Pet_photoUrls_xa y Pet_Tags_xa, 
		//poniendo como fk la pk del pet que se generara a continuacion
		dg.load("Pet_Tags_xa", "fk_xa=@pid1");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid1");
		dg.load("Category", "id=@cid1, name=Dogs");
		dg.load("Pet", "id=@pid1, category::id=@cid1, status=available");

		dg.load("Pet_Tags_xa", "fk_xa=@pid2");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid2");
		dg.load("Pet", "id=@pid2, category::id=@cid1, status=sold");//sold es un valor !=available indicado por qagrow entre los permitidos 
		
		dg.load("Pet_Tags_xa", "fk_xa=@pid3");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid3");
		dg.load("Category", "id=@cid2, name=1"); //1 es un valor !=Dogs indicado por qagrow
		dg.load("Pet", "id=@pid3, category::id=@cid2, status=available");

		dg.load("Category", "id=@cid3");
		
		//estas dos ultimas son para dos reglas nuevas que implican pets que no tengan tags y pets que no tengan urls
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid4");
		dg.load("Pet", "id=@pid4, category::id=@cid1, status=available");
		
		dg.load("Pet_Tags_xa", "fk_xa=@pid5");
		dg.load("Pet", "id=@pid5, category::id=@cid1, status=available");
		
		assertData(outputFileName, dg);
	}

	/**
	 * Una variante de la anterior, 
	 * pero ahora las condiciones del where son referencias a items de los arrays
	 * (uno es array de primitivos y otro array de objetos)
	 * 
	 * 	 tds Pet where Pet.photoUrls[]='URL' and Pet.tags[]::name='kitty'"
	 * 
	 * Ademas de las joins con los arrays se mantiene la join con Pet_category_xref 
	 * puesto que aunque no se referencie en el where es necesaria para tener integridad referencial. 
	 * Las transformaciones de los joins tambien transforma estas referencias en el where
	 * 
	 * SELECT * FROM Pet
     *   LEFT JOIN Pet_tags_xa ON Pet.id = Pet_tags_xa.fk_xa
     *   LEFT JOIN Pet_photoUrls_xa ON Pet.id = Pet_photoUrls_xa.fk_xa
     *   LEFT JOIN Category Pet_category_xref ON Pet.category::id = Pet_category_xref.id
     *   WHERE Pet_photoUrls_xa.photoUrls = 'URL' AND Pet_tags_xa.name = 'kitty'
	 */
	public static String queryPetByUrlAndTag =
			"tds Pet where Pet.photoUrls[]='URL' and Pet.tags[]::name='kitty'";
	@Test
	public void testPetByUrlAndTag() {
		TdRules rules = getRules(queryPetByUrlAndTag);
		assertModel("rules-pet-by-url-tag.xml", new TdRulesXmlSerializer().serialize(rules));
		//a partir de aqui ya compruebo solo las reglas en la mayor parte de los tests
	}
	
	/**
	 * Join relacional entre tres tablas utilizando los objetos orignales Customer, Order y Pet
	 * 
	 *   tds Customer, Order, Pet where Pet.category::name='Dogs' and Order.status='placed'
	 *   
	 * Es la misma que testPlacedPet0OrdersByCategoryAndOrderStatus) pero ahora saldran otras tablas
	 * debido a los arrays (dos de Pet mas uno de Customer) mas el maestro de Category:
	 * 
	 * SELECT * FROM Customer
     *   LEFT JOIN Customer_address_xa ON Customer.id = Customer_address_xa.fk_xa
     *   INNER JOIN "Order" ON Customer.id = Order.customerId
     *   INNER JOIN Pet ON Order.petId = Pet.id
     *   LEFT JOIN Pet_tags_xa ON Pet.id = Pet_tags_xa.fk_xa
     *   LEFT JOIN Pet_photoUrls_xa ON Pet.id = Pet_photoUrls_xa.fk_xa
     *   LEFT JOIN Category Pet_category_xref ON Pet.category::id = Pet_category_xref.id
     *   WHERE Pet.category::name = 'Dogs' AND "Order".status = 'placed'
	 */
	public static String queryPlacedPetOrdersByCategoryAndOrderStatus=
			"tds Customer, \"Order\", Pet"
				+ " where Pet.category::name='Dogs' and \"Order\".status='placed'";
	@Test
	public void testPlacedPetOrdersByCategoryAndOrderStatus() {
		TdRules rules = getRules(queryPlacedPetOrdersByCategoryAndOrderStatus);
		assertModel("rules-placed-pet-orders-by-category-order-status.xml", new TdRulesXmlSerializer().serialize(rules));
	}

	/**
	 * Otra join similar para ver las Orders a enviar a un zip de cliente dado,
	 * incluyen condiciones sobre otros atributos diferentes de la anterior, uno de ellos es array
	 * 
     * En esta se comprobara la generacion de una fila sin y con diccionario
	 */
	public static String queryPlacedPetOrdersByAddressAndOrderStatus=
			"tds Customer, \"Order\", Pet"
				+ " where Customer.address[]::zip='99999' and \"Order\".status='placed'";
	@Test
	public void testPlacedPetOrdersByAddressAndOrderStatus() {
		TdRules rules = getRules(queryPlacedPetOrdersByAddressAndOrderStatus);
		assertModel("rules-placed-pet-orders-by-address-order-status.xml", new TdRulesXmlSerializer().serialize(rules));
		DataLoader dg = getDataLoader();
		doPlacedPetOrdersByAddressAndOrderStatus(dg, "datagen-local-placed-pet-orders-by-address-order-status.txt");
	}
	@Test
	public void testPlacedPetOrdersByAddressAndOrderStatusWithDictionary() {
		DataLoader dg=getDataLoader().setAttrGen(getDictionaryAttrGen());
		doPlacedPetOrdersByAddressAndOrderStatus(dg, "datagen-local-placed-pet-orders-by-address-order-status-dict.txt");
	}
	private void doPlacedPetOrdersByAddressAndOrderStatus(DataLoader dg, String outputFileName) {
		dg.load("Pet_Tags_xa", "fk_xa=@pid1");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid1");
		dg.load("Category", "id=@cid1, name=Dogs");
		dg.load("Pet", "id=@pid1, category::id=@cid1, status=available");
		dg.load("Customer_address_xa", "fk_xa=@cuid1");
		dg.load("Customer", "id=@cuid1,");
		dg.load("Order", "id=@oid1,petId=@pid1,customerId=@cuid1");
		assertData(outputFileName, dg);
	}
	
	////////////////////////////// Generacion de datos con group by //////////////////////////////

	/**
	 * Total de Orders a enviar (estado approved) agrupadas por zip de la direccion del cliente.
	 * Notar que la transformacion de la clausula tds introduce en el select las columnas del groupby y un count
	 */
	public static String queryTotalOrdersToDeliverByAddress=
			"tds Customer, \"Order\", Pet"
				+ " where \"Order\".status='approved'"
				+ " group by Customer.address[]::zip";
	@Test
	public void testTotalOrdersToDeliverByAddress() {
		TdRules rules = getRules(queryTotalOrdersToDeliverByAddress);
		assertModel("rules-total-pet-orders-by-address.xml", new TdRulesXmlSerializer().serialize(rules));
	}
	
	/**
	 * Como la anterior pero suma todas las unidades pedidas en cada Order,
	 * en este caso se pone un select explícito
	 */
	public static String queryTotalPetsToDeliverByAddress=
			"select Customer.address[]::zip, sum(\"Order\".quantity) from Customer, \"Order\", Pet"
				+ " where \"Order\".status='approved'"
				+ " group by Customer.address[]::zip";
	@Test
	public void testTotalPetsToDeliverByAddress() {
		TdRules rules = getRules(queryTotalPetsToDeliverByAddress);
		assertModel("rules-total-pets-by-address.xml", new TdRulesXmlSerializer().serialize(rules));
	}
}
