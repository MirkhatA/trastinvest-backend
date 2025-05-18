package kz.trastinvest.demo.service;

import kz.trastinvest.demo.dto.request.ProductRequest;
import kz.trastinvest.demo.model.Category;
import kz.trastinvest.demo.model.Product;
import kz.trastinvest.demo.repositories.CategoryRepository;
import kz.trastinvest.demo.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product create(ProductRequest request) {
        Product product = mapRequestToProduct(request, new Product());
        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productRepository.save(mapRequestToProduct(request, product));
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getFilteredProducts(String keyword, Long categoryId) {
        if (keyword != null) {
            return productRepository.findByNameContainingIgnoreCase(keyword);
        } else if (categoryId != null) {
            return productRepository.findByCategory_Id(categoryId);
        } else {
            return productRepository.findAll();
        }
    }

    private Product mapRequestToProduct(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setManufacturer(request.getManufacturer());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setDeliveryType(request.getDeliveryType());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        return product;
    }
}