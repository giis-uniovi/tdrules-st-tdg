package io.swagger.petstore.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//Added model to represent Customers (without arrays)

@XmlRootElement(name = "Customer")
public class Customer0 {
  private long id;
  private String username;

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

}