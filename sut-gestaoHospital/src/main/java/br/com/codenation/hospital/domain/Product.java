package br.com.codenation.hospital.domain;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="product_collection")
public class Product implements Serializable{
	private static final long serialVersionUID = 1L;
	

	@Id
	//Cambia el tipo del _id (ObjectId a String) para facilitar comparación de resultados de test
	private String _id;
	private String name;
	private String description;
	private int quantity;
	private ProductType productType;
	private String hospitalId; //added reference to the hospital

	public Product() {
	
	}	
	
	public Product(String _id, String name, String description, int quantity, ProductType productType) {
		this._id = _id;
		this.name = name;
		this.description = description;
		this.quantity = quantity;
		this.productType = productType;
	}
	
	public Product(String name, String description, int quantity, ProductType productType) {
		this.name = name;
		this.description = description;
		this.quantity = quantity;
		this.productType = productType;
	}
	
	//added constructor with the reference to an hospital 
	public Product(String id, String name, String description, int quantity, ProductType productType,String hospitalId) {
		this._id = id;
		this.name = name;
		this.description = description;
		this.quantity = quantity;
		this.productType = productType;
		this.hospitalId = hospitalId;
	}

	public String getId() {
		//return _id.toHexString();
		return _id;
	}
	
	//add setter for product _id
	public void setId(String id) {
		this._id=id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public ProductType getProductType() {
		return productType;
	}
	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public void diminuiQuantidade(int quantity){
		this.quantity-=quantity;
	}
	
	//added getters and setters for idHospital
	public String getHospitalId() {
		return hospitalId;
	}
	public void setIdHospital(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + quantity;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (quantity != other.quantity)
			return false;
		return true;
	}
}