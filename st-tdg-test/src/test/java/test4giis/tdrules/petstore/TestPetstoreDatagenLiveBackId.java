package test4giis.tdrules.petstore;

import org.junit.Test;

import giis.tdrules.store.loader.DataLoader;
import giis.tdrules.store.loader.oa.IPathResolver;
import giis.tdrules.store.loader.oa.OaLiveAdapter;
import giis.tdrules.store.loader.oa.OaLiveUidGen;
import giis.tdrules.store.loader.oa.OaPathResolver;

/**
 * Tests adicionales para comprobar la generacion de claves en el backend
 * extendiendo algunas funcionalidades del petstore para que generen valores de estas
 */
public class TestPetstoreDatagenLiveBackId extends BasePetstore {

	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
	//los endpoints estan bajo el path backid
	//no se pueden resolver utilizando el resolver estandar que utiliza el modelo
	public class CustomPathResolver extends OaPathResolver {
		@Override
		public String getEndpointPath(String tableName) {
			if ("Pet".equals(tableName) || "Category".equals(tableName))
				return super.getEndpointPath("backid/" + tableName);
			else
				return super.getEndpointPath(tableName);
		}
	}
	@Override
	protected DataLoader getLiveDataLoader() {
		IPathResolver pathResolver=new CustomPathResolver().setServerUrl(getServerUrl());
		return new DataLoader(getSchema(), new OaLiveAdapter(pathResolver)).setUidGen(new OaLiveUidGen());
	}
	
	/**
	 * Prueba de con dos entidades con referencias, no genera reglas, solo los comandos de generacion
	 * que se tienen en tests similares para comprobacion del valor de las claves generadas en el backend
	 */
	@Test
	public void testPetByCategoryAndStatus() {
		DataLoader dg=getDataLoader();
		dg.load("Category", "id=@cid1, name=Dogs");
		dg.load("Pet_Tags_xa", "fk_xa=@pid1");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid1");
		dg.load("Pet", "id=@pid1, category::id=@cid1, status=available");
		
		dg.load("Pet_Tags_xa", "fk_xa=@pid2");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid2");
		dg.load("Pet", "id=@pid2, category::id=@cid1, status=sold");//sold es un valor !=available indicado por qagrow entre los permitidos 

		dg.load("Category", "id=@cid2, name=1"); //1 es un valor !=Dogs indicado por qagrow
		dg.load("Pet_Tags_xa", "fk_xa=@pid3");
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid3");
		dg.load("Pet", "id=@pid3, category::id=@cid2, status=available");
		
		dg.load("Category", "id=@cid3");
		
		dg.load("Pet_photoUrls_xa", "fk_xa=@pid4");
		dg.load("Pet", "id=@pid4, category::id=@cid1, status=available");
		
		dg.load("Pet_Tags_xa", "fk_xa=@pid5");
		dg.load("Pet", "id=@pid5, category::id=@cid1, status=available");
		
		assertData("datagen-livebackid-pet-by-category-status.txt", dg);
	}

}
