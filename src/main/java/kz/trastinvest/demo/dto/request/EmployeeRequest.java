package kz.trastinvest.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequest {
    private String name;
    private String position;
    private String number;
    private String photo;
}
