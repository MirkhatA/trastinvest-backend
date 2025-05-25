package kz.trastinvest.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.trastinvest.demo.dto.request.NewsRequest;
import kz.trastinvest.demo.model.News;
import kz.trastinvest.demo.service.FileStorageService;
import kz.trastinvest.demo.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final FileStorageService fileStorageService;

    private final NewsService newsService;

    @Operation(summary = "Create news", description = "Upload news with image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "News created")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<News> createNews(
            @Parameter(
                    description = "News JSON, example:\n" +
                            "{\n" +
                            "  \"title\": \"New product launched\",\n" +
                            "  \"content\": \"We are excited to launch our new product!\"\n" +
                            "}",
                    required = true)
            @RequestPart("data") String json,

            @Parameter(description = "Image file", required = true)
            @RequestPart("image") MultipartFile imageFile
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        NewsRequest request = objectMapper.readValue(json, NewsRequest.class);

        String imageUrl = fileStorageService.uploadFile(imageFile);
        request.setImageUrl(imageUrl);

        return ResponseEntity.ok(newsService.create(request));
    }


    @Operation(summary = "Update news", description = "Update news details and optionally replace image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "News updated")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<News> updateNews(
            @PathVariable Long id,

            @Parameter(
                    description = "News JSON, example:\n" +
                            "{\n" +
                            "  \"title\": \"Updated title\",\n" +
                            "  \"content\": \"Updated content\"\n" +
                            "}",
                    required = true)
            @RequestPart("data") String json,

            @Parameter(description = "Optional new image file")
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        NewsRequest request = objectMapper.readValue(json, NewsRequest.class);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.uploadFile(imageFile);
            request.setImageUrl(imageUrl);
        }

        return ResponseEntity.ok(newsService.update(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<News>> getAll() {
        return ResponseEntity.ok(newsService.getAll());
    }
}
