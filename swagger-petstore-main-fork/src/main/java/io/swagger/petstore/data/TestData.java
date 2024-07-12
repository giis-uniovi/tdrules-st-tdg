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

import io.swagger.petstore.model.Category;
import io.swagger.petstore.model.Customer;
import io.swagger.petstore.model.Order;
import io.swagger.petstore.model.Pet;

import java.util.ArrayList;
import java.util.List;

//Added class for test to clean and get the whole database

public class TestData {
    
    public class AllData {
    	//public para que jackson los serialize
    	public List<Category> category=new ArrayList<>();
    	public List<Pet> pet=new ArrayList<>();
    	public List<Order> order=new ArrayList<>();
    	public List<Customer> customer=new ArrayList<>();
    }

    //se define como objeto generico en el yml para simplificar codigo
    public Object getAll() {
    	AllData allData=new AllData();
    	PetData petData=new PetData();
    	allData.category=petData.getCategories();
    	allData.pet=petData.getPets();
    	OrderData orderData=new OrderData();
    	allData.order=orderData.getOrders();
    	allData.customer=orderData.getCustomers();
    	return allData;
    }
    
    public void deleteAll() {
    	new PetData().deleteAll();
    	new OrderData().deleteAll();
    }
    
}