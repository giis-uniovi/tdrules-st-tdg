package market.dto;

//testing: add new class to get the number of orders to delivery to an address
public interface AddressOrderDTO {
    String getAddress();
    long getCount();
}