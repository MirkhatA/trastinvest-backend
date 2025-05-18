package kz.trastinvest.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String email;
    private String resetCode;
    private String newPassword;
}
