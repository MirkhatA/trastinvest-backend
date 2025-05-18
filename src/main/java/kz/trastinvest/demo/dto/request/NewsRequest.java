package kz.trastinvest.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsRequest {
    private String title;
    private String content;
    private String imageUrl;
}
