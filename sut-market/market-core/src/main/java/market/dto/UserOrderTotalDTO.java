package market.dto;


// testing: add new class to get the total costs of an user's order
public interface UserOrderTotalDTO {
    String getEmail();
    double getTotalAmount();
}