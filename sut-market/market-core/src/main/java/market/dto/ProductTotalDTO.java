package market.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// testing: add new class to get the total number of products
@JsonPropertyOrder({"id", "name", "totalQuantity"})
public interface ProductTotalDTO {
	int getId();
	String getName();
    int getTotalQuantity();
}