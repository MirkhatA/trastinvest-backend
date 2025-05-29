package kz.trastinvest.demo.service;

import kz.trastinvest.demo.dto.request.ProductRequest;
import kz.trastinvest.demo.dto.response.ProductResponse;
import kz.trastinvest.demo.model.Category;
import kz.trastinvest.demo.model.Product;
import kz.trastinvest.demo.repositories.CategoryRepository;
import kz.trastinvest.demo.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse create(ProductRequest request) {
        Product product = mapRequestToProduct(request, new Product());
        return toDto(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDto(productRepository.save(mapRequestToProduct(request, product)));
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDto(product);
    }

    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toDto);
    }


    public Page<ProductResponse> getFilteredProducts(String keyword, Long categoryId, Pageable pageable) {
        if (keyword != null) {
            return productRepository.findByNameContainingIgnoreCase(keyword, pageable).map(this::toDto);
        } else if (categoryId != null) {
            return productRepository.findByCategory_Id(categoryId, pageable).map(this::toDto);
        } else {
            return productRepository.findAll(pageable).map(this::toDto);
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

    public ProductResponse toDto(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setManufacturer(product.getManufacturer());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setDeliveryType(product.getDeliveryType());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        return dto;
    }

    public long getProductCount() {
        return productRepository.count();
    }
}
