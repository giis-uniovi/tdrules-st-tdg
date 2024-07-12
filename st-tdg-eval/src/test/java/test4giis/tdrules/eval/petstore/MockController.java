package test4giis.tdrules.eval.petstore;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import giis.tdrules.store.loader.oa.ApiResponse;
import giis.tdrules.store.loader.oa.ApiWriter;
import io.swagger.petstore.data.OrderData;
import io.swagger.petstore.data.PetData;
import io.swagger.petstore.data.TestData;
import io.swagger.petstore.model.Category;
import io.swagger.petstore.model.Customer;
import io.swagger.petstore.model.Order;
import io.swagger.petstore.model.Pet;
import lombok.SneakyThrows;

public class MockController extends ApiWriter {

	@Override
	@SneakyThrows
	public ApiResponse post(String url, String requestBody, boolean usePut) {
		Map<String, String> params = new HashMap<>();
		url = parseUrl(url, params);
		if ("/pet".equals(url)) {
			Pet pet = (Pet) deserialize(requestBody, Pet.class);
			new PetData().addPet(pet);
			return new ApiResponse(200, "OK", serialize(pet));
		} else if ("/category".equals(url)) {
			Category category = (Category) deserialize(requestBody, Category.class);
			new PetData().addCategory(category);
			return new ApiResponse(200, "OK", serialize(category));
		} else if ("/store/customer".equals(url)) {
			Customer customer = (Customer) deserialize(requestBody, Customer.class);
			new OrderData().addCustomer(customer);
			return new ApiResponse(200, "OK", serialize(customer));
		} else if ("/store/order".equals(url)) {
			Order order = (Order) deserialize(requestBody, Order.class);
			new OrderData().addOrder(order);
			return new ApiResponse(200, "OK", serialize(order));
		} else if ("/store/updateDeliveryToCustomer".equals(url)) {
			java.util.List<Order> orders = new OrderData().updateDeliveryToCustomer(Long.valueOf(params.get("customerId")));
			return new ApiResponse(200, "OK", serialize(orders));
		} else
			return new ApiResponse(404, "Not Found", "No controller for " + url);
	}

	@Override
	public ApiResponse get(String url) {
		Map<String, String> params = new HashMap<>();
		url = parseUrl(url, params);
		if ("/test/getAll".equals(url))
			return new ApiResponse(200, "OK", serialize(new TestData().getAll()));
		else if ("/pet/findByStatus".equals(url))
			return new ApiResponse(200, "OK", serialize(new PetData().findPetByStatus(params.get("status"))));
		else if ("/pet/findByCategoryAndStatus".equals(url))
			return new ApiResponse(200, "OK", serialize(new PetData().findPetByCategoryAndStatus(params.get("category"), params.get("status"))));
		else if ("/store/findOrdersByCategoryAndOrderStatus".equals(url))
			return new ApiResponse(200, "OK", serialize(new OrderData().findOrdersByCategoryAndOrderStatus(params.get("category"), params.get("status"))));
		else if ("/store/totalPetsToDeliverByAddress".equals(url))
			return new ApiResponse(200, "OK", serialize(new OrderData().totalPetsToDeliverByAddress()));
		else
			return new ApiResponse(404, "Not Found", "No controller for " + url);
	}

	@Override
	public ApiResponse delete(String url) {
		if ("/test/deleteAll".equals(url)) {
			new TestData().deleteAll();
			return new ApiResponse(200, "OK", "");
		} else
			return new ApiResponse(404, "Not Found", "No controller for " + url);
	}

	@SneakyThrows
	private String parseUrl(String urlAndParams, Map<String, String> parameters) {
		String[] urlSplit = urlAndParams.split("\\?");
		if (urlSplit.length < 2)
			return urlSplit[0];// no args
		String[] pairs = urlSplit[1].split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			parameters.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return urlSplit[0];
	}

	/**
	 * Serializa un objeto cualquiera a json mostrando los atributos vacios o nulos
	 */
	@SneakyThrows
	protected String serialize(Object dto) {
		ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
				.setSerializationInclusion(Include.NON_EMPTY)
				.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		return mapper.writeValueAsString(dto);
	}

	@SneakyThrows
	protected Object deserialize(String json, Class<?> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, clazz);
	}

}
