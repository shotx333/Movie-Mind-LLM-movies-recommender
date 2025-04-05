package com.shotx.movierecommender.service;

import com.shotx.movierecommender.dto.MovieDTO;
import com.shotx.movierecommender.enums.LlmProvider;

import java.util.List;


public interface LlmProviderService {


    LlmProvider getProviderType();


    List<MovieDTO> getMovieRecommendations(String description, int maxResults);


    boolean isAvailable();
}