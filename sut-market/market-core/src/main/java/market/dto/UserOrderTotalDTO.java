package market.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// testing: add new class to get the total costs of an user's order
@JsonPropertyOrder({"count","email","totalAmount"})
public interface UserOrderTotalDTO {
    String getEmail();
    int getCount();
    double getTotalAmount();
}