/**
 * Copyright 2018 SmartBear Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.swagger.petstore.data;

import io.swagger.petstore.model.Address;
import io.swagger.petstore.model.Customer;
import io.swagger.petstore.model.Customer0;
import io.swagger.petstore.model.Order;
import io.swagger.petstore.model.Order0;
import io.swagger.petstore.model.Pet;
import io.swagger.petstore.model.QuantityByZip;

import java.util.*;

public class OrderData {
    private static List<Order> orders = new ArrayList<>();
    // Add customer data (not included in the original version)
    private static List<Customer> customers = new ArrayList<>();

    static {
        orders.add(createOrder(1, 1, 1, 100, new Date(), "placed", true));
        orders.add(createOrder(2, 1, 1, 50, new Date(), "approved", true));
        orders.add(createOrder(3, 1, 1, 50, new Date(), "delivered", true));
    	customers.add(createCustomer(1,"me"));
    }
    
    //Added methods for test
    
    public List<Order> getOrders() {
    	return orders;
    }

    public List<Customer> getCustomers() {
    	return customers;
    }

    public void deleteAll() {
    	orders = new ArrayList<>();
    	customers = new ArrayList<>();
    }
    
    public void addOrder0(final Order0 order) {
        if (orders.size() > 0) {
            orders.removeIf(orderN -> orderN.getId() == order.getId());
        }
        orders.add(createOrder(order.getId(), order.getPetId(), order.getCustomerId(), order.getQuantity(), order.getShipDate(), 
        		order.getStatus(), order.isComplete()));
     }

    public static Order0 createOrder0(final long id, final long petId, final long customerId, final int quantity, final Date shipDate,
    		final String status, final boolean complete) {
    	final Order0 order = new Order0();
    	order.setId(id);
    	order.setPetId(petId);
    	order.setCustomerId(customerId);
    	order.setComplete(complete);
    	order.setQuantity(quantity);
    	order.setShipDate(shipDate);
    	order.setStatus(status);
    	return order;
    }

    public void addCustomer0(final Customer0 customer) {
        if (customers.size() > 0) {
        	customers.removeIf(customerN -> customerN.getId() == customer.getId());
        }
        customers.add(createCustomer(customer.getId(), customer.getUsername()));
    }

    //Customer does not have methods to create from the api, include here
    
    public void addCustomer(final Customer customer) {
        if (customers.size() > 0) {
        	customers.removeIf(customerN -> customerN.getId() == customer.getId());
        }
        customers.add(customer);
    }

    public static Customer createCustomer(final long id, String username) {
    	final Customer customer = new Customer();
    	customer.setId(id);
    	customer.setUsername(username);
    	return customer;
    }
    
    //End added methods for test

    public Order getOrderById(final long orderId) {
        for (final Order order : orders) {
            if (order.getId() == orderId) {
                return order;
            }
        }
        return null;
    }

    public Map<String, Integer> getCountByStatus() {

        final Map<String, Integer> countByStatus = new HashMap<>();

        for (final Order order : orders) {
            final String status = order.getStatus();
            if (countByStatus.containsKey(status)) {
                countByStatus.put(status, countByStatus.get(status) + order.getQuantity());
            } else {
                countByStatus.put(status, order.getQuantity());
            }
        }

        return countByStatus;
    }

    // Add methods to support other queries
    
    public List<Order> findOrdersByCategoryAndOrderStatus(final String category, final String status) {
        final List<Order> result = new ArrayList<>();
        for (final Order order: orders) {
        	if (order.getStatus().equals(status)) {
        		final long petId = order.getPetId();
        		for (final Pet pet : new PetData().getPets())
        			if (pet.getId() == petId) {
        				if (pet.getCategory().getName().equals(category))
        					result.add(order);
        			}
        	}
        }
        return result;
    }

    public List<QuantityByZip> totalPetsToDeliverByAddress() {
        final TreeMap<String, Long> result = new TreeMap<>();
        for (final Customer customer: customers)
        	if (customer.getAddress() != null)
        		for (final Address address: customer.getAddress()) { // collect zips
        			String zip = address.getZip();
        			if (!result.containsKey(zip))
        				result.put(zip, (long) 0); // new zip
        			// count all orders of this customer and add to this zip
        			for (final Order order: orders)
        				if (order.getCustomerId() == customer.getId() && "approved".equals(order.getStatus()))
        					result.put(zip, result.get(zip) + order.getQuantity());
        		}
        List<QuantityByZip> resultDto = new ArrayList<>();
        for (Map.Entry<String, Long> entry : result.entrySet())
        	resultDto.add(new QuantityByZip(entry.getKey(), entry.getValue()));
        return resultDto;
    }

    public List<Order> updateDeliveryToCustomer(long customerId) {
        final List<Order> result = new ArrayList<>();
        for (final Order order: orders) {
        	if ("approved".equals(order.getStatus()) && order.getCustomerId() == customerId) {
        		order.setStatus("delivered");
        		result.add(order);
        	}
        }
        return result;
    }

    // End added methods

    public void addOrder(final Order order) {
        if (orders.size() > 0) {
            orders.removeIf(orderN -> orderN.getId() == order.getId());
        }
        orders.add(order);
    }

    public void deleteOrderById(final Long orderId) {
        orders.removeIf(order -> order.getId() == orderId);
    }

    public static Order createOrder(final long id, final long petId, final long customerId, final int quantity, final Date shipDate,
                                     final String status, final boolean complete) {
        final Order order = new Order();
        order.setId(id);
        order.setPetId(petId);
        order.setCustomerId(customerId);
        order.setComplete(complete);
        order.setQuantity(quantity);
        order.setShipDate(shipDate);
        order.setStatus(status);
        return order;
    }
}
