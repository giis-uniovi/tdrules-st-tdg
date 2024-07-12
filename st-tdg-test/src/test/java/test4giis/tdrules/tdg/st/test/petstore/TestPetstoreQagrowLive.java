package test4giis.tdrules.tdg.st.test.petstore;

/**
 * Mismos test que TestPetstoreQagrowLocal, pero accediendo al servidor de swagger-petstore,
 * heredan de la base salvo la forma de crear el generador y comparar los datos
 * (asegurar que el container ha sido arrancado con docker-run)
 */
public class TestPetstoreQagrowLive extends TestPetstoreQagrowLocal {

	@Override
	protected boolean isLiveBackend() {
		return true;
	}
	
}
