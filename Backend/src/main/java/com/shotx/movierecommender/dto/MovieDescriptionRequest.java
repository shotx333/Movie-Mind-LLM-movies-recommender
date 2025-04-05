package com.shotx.movierecommender.dto;

import com.shotx.movierecommender.enums.LlmProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MovieDescriptionRequest {
    @NotBlank(message = "Movie description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    private int maxResults = 3;
    private String provider = "anthropic";

    public LlmProvider getLlmProvider() {
        return LlmProvider.fromId(provider);
    }

}