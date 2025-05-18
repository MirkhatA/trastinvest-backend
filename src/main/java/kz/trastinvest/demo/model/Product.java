package kz.trastinvest.demo.model;

import jakarta.persistence.*;
import kz.trastinvest.demo.enums.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String manufacturer;
    private String description;
    @ManyToOne
    private Category category;
    private String imageUrl;
    private DeliveryType deliveryType;
}
