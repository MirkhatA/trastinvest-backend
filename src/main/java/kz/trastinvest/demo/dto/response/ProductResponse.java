package kz.trastinvest.demo.dto.response;

import kz.trastinvest.demo.enums.DeliveryType;
import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String manufacturer;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private DeliveryType deliveryType;
}
