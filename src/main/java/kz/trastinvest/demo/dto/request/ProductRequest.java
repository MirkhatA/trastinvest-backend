package kz.trastinvest.demo.dto.request;

import kz.trastinvest.demo.enums.DeliveryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private String manufacturer;
    private String description;
    private Long categoryId;
    private String imageUrl;
    private DeliveryType deliveryType;
}
