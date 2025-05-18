package kz.trastinvest.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<News> createNews(
            @RequestPart("data") String json,
            @RequestPart("image") MultipartFile imageFile
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        NewsRequest request = objectMapper.readValue(json, NewsRequest.class);

        String imageUrl = fileStorageService.uploadFile(imageFile);
        request.setImageUrl(imageUrl);

        return ResponseEntity.ok(newsService.create(request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<News> updateNews(
            @PathVariable Long id,
            @RequestPart("data") String json,
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
