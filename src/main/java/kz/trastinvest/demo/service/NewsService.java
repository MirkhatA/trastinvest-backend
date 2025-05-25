package kz.trastinvest.demo.service;

import kz.trastinvest.demo.dto.request.NewsRequest;
import kz.trastinvest.demo.model.News;
import kz.trastinvest.demo.repositories.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public News create(NewsRequest request) {
        log.info("News request: {}", request);
        News news = new News();
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setImageUrl(request.getImageUrl());
        news.setCreatedAt(LocalDateTime.now());
        return newsRepository.save(news);
    }

    public News update(Long id, NewsRequest request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        news.setTitle(request.getTitle());
        news.setContent(request.getContent());

        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            news.setImageUrl(request.getImageUrl());
        }

        return newsRepository.save(news);
    }

    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            log.error("News not found");
        }
        newsRepository.deleteById(id);
    }

    public News getById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
    }

    public List<News> getAll() {
        return newsRepository.findAll();
    }
}
