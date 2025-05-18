package kz.trastinvest.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.trastinvest.demo.dto.request.ProductRequest;
import kz.trastinvest.demo.model.Product;
import kz.trastinvest.demo.service.FileStorageService;
import kz.trastinvest.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(
            @RequestPart("data") String json,
            @RequestPart("image") MultipartFile imageFile
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequest request = objectMapper.readValue(json, ProductRequest.class);

        String imageUrl = fileStorageService.uploadFile(imageFile);
        request.setImageUrl(imageUrl);

        return ResponseEntity.ok(productService.create(request));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/getFilteredProducts")
    public ResponseEntity<List<Product>> getFiltered(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(productService.getFilteredProducts(keyword, categoryId));
    }
}
