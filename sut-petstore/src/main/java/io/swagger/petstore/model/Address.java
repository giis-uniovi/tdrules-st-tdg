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

package io.swagger.petstore.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//Added model to represent the addresses of Customers

@XmlRootElement(name = "Address")
public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;


    @XmlElement(name = "street")
    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    @XmlElement(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    @XmlElement(name = "state")
    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    @XmlElement(name = "zip")
    public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

}
