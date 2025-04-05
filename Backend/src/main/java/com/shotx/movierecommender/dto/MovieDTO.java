package com.shotx.movierecommender.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private String title;
    private String year;
    private String director;
    private String genre;
    private String plot;
    private Double matchScore;
    private String reasoning;
    private String imdbId;
    private Double imdbRating;
    private String imdbUrl;
}