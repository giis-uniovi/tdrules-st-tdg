package market.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//testing: add new class to get the number of orders to delivery to an address
@JsonPropertyOrder({"address", "count"})
public interface AddressOrderDTO {
    String getAddress();
    long getCount();
}