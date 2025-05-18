package kz.trastinvest.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSelectionRequest {
    private String name;
    private Integer quantity;
}
