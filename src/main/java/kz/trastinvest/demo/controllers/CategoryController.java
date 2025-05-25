package kz.trastinvest.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.trastinvest.demo.dto.request.CategoryRequest;
import kz.trastinvest.demo.model.Category;
import kz.trastinvest.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create category", description = "Add a new category with optional parent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Category> createCategory(
            @Parameter(
                    description = "Category JSON, example:\n" +
                            "{\n" +
                            "  \"name\": \"Spare Parts\",\n" +
                            "  \"parentId\": 1\n" +
                            "}",
                    required = true)
            @RequestPart("data") String json
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CategoryRequest request = objectMapper.readValue(json, CategoryRequest.class);
        return ResponseEntity.ok(categoryService.create(request));
    }

    @Operation(summary = "Update category", description = "Update category details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @Parameter(
                    description = "Category JSON, example:\n" +
                            "{\n" +
                            "  \"name\": \"Updated Category\",\n" +
                            "  \"parentId\": null\n" +
                            "}",
                    required = true)
            @RequestPart("data") String json
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CategoryRequest request = objectMapper.readValue(json, CategoryRequest.class);
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/getRoots")
    public ResponseEntity<List<Category>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }
}
