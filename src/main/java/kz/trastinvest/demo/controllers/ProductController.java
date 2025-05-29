package kz.trastinvest.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.trastinvest.demo.dto.request.ProductRequest;
import kz.trastinvest.demo.dto.response.ProductResponse;
import kz.trastinvest.demo.service.FileStorageService;
import kz.trastinvest.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @Operation(summary = "Create product", description = "Upload product with image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(
                    description = """
                            Product JSON, example:
                            {
                              "name": "Sneakers",
                              "manufacturer": "Nike",
                              "description": "Comfortable running shoes",
                              "categoryId": 1,
                              "deliveryType": "DELIVERY"
                            }""",
                    required = true)
            @RequestPart("data") String json,

            @Parameter(description = "Image file", required = true)
            @RequestPart("image") MultipartFile imageFile
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequest request = objectMapper.readValue(json, ProductRequest.class);

        String imageUrl = fileStorageService.uploadFile(imageFile);
        request.setImageUrl(imageUrl);

        return ResponseEntity.ok(productService.create(request));
    }

    @Operation(summary = "Update product", description = "Update product details and optionally replace image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,

            @Parameter(
                    description = """
                            Product JSON, example:
                            {
                              "name": "Updated name",
                              "manufacturer": "Adidas",
                              "description": "New description",
                              "categoryId": 2,
                              "deliveryType": "PICKUP"
                            }""",
                    required = true)
            @RequestPart("data") String json,

            @Parameter(description = "Optional new image file")
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequest request = objectMapper.readValue(json, ProductRequest.class);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.uploadFile(imageFile);
            request.setImageUrl(imageUrl);
        }

        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<Page<ProductResponse>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getAll(pageable));
    }

    @GetMapping("/getFilteredProducts")
    public ResponseEntity<Page<ProductResponse>> getFiltered(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getFilteredProducts(keyword, categoryId, pageable));
    }

    @GetMapping("/getProductCount")
    public ResponseEntity<Long> getProductCount() {
        return ResponseEntity.ok(productService.getProductCount());
    }
}
