package com.shotx.movierecommender.service;

import com.shotx.movierecommender.dto.MovieDTO;
import com.shotx.movierecommender.dto.MovieDescriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class MovieService {

    private final LlmService llmService;

    @Autowired
    public MovieService(LlmService llmService) {
        this.llmService = llmService;
    }

    public List<MovieDTO> getMovieRecommendations(MovieDescriptionRequest request) {
        List<MovieDTO> recommendations = llmService.getMovieRecommendations(
                request.getDescription(),
                request.getMaxResults(),
                request.getLlmProvider()
        );

        recommendations.sort(Comparator.comparing(MovieDTO::getMatchScore).reversed());

        return recommendations;
    }


    public List<LlmService.ProviderInfo> getAvailableProviders() {
        return llmService.getAvailableProviders();
    }
}