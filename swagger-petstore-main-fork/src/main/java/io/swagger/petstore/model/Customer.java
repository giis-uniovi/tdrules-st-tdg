package io.swagger.petstore.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//Added model to represent Customers

@XmlRootElement(name = "Customer")
public class Customer {
  private long id;
  private String username;
  private List<Address> address = new ArrayList<>();

  @XmlElement(name = "id")
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @XmlElement(name = "username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @XmlElement(name = "address")
  public List<Address> getAddress() {
      return address;
  }

  public void setAddress(final List<Address> address) {
      this.address = address;
  }

}