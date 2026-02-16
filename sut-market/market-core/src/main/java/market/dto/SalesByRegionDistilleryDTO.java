package market.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"regionId","regionName","distilleryId","distilleryTitle","totalSold"})
public interface SalesByRegionDistilleryDTO {
    int getRegionId();
    String getRegionName();
    int getDistilleryId();
    String getDistilleryTitle();
    int getTotalSold();
}
