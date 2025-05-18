package kz.trastinvest.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContactRequest {
    private String name;
    private String phone;
    private String message;
    private List<ProductSelectionRequest> products;
}
