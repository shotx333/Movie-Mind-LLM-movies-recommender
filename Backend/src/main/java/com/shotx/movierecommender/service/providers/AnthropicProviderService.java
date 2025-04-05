package com.shotx.movierecommender.service.providers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shotx.movierecommender.dto.MovieDTO;
import com.shotx.movierecommender.enums.LlmProvider;
import com.shotx.movierecommender.service.LlmProviderService;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shotx.movierecommender.util.JsonUtils.*;

@Service
public class AnthropicProviderService implements LlmProviderService {

    private final AnthropicChatModel chatClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    @Value("${spring.ai.anthropic.options.max-tokens:4000}")
    private int maxTokens;

    @Autowired
    public AnthropicProviderService(
            AnthropicChatModel chatClient,
            @Value("${spring.ai.anthropic.api-key}") String apiKey) {
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        this.apiKey = apiKey;
    }

    @Override
    public LlmProvider getProviderType() {
        return LlmProvider.ANTHROPIC;
    }

    @Override
    public List<MovieDTO> getMovieRecommendations(String description, int maxResults) {
        maxResults = Math.min(maxResults, 5);

        int adjustedMaxTokens = Math.max(maxTokens, maxResults * 800);

        String systemPromptText = """
                You are a highly knowledgeable film expert. Your task is to recommend movies based on a user's description.
                
                Consider movies from all eras, countries, and genres. Think about plot elements, themes, cinematography, atmosphere, and style when matching movies to the description.
                
                Provide a list of %d movies that match the description. For each movie, include:
                - Title (exactly as it appears officially)
                - Year of release
                - Director (just the director's name)
                - Genre (just the primary genre - one word if possible)
                - A VERY BRIEF plot summary (maximum 50 words)
                - A match score between 0.0-1.0 indicating how well the movie matches the description
                - A VERY BRIEF reasoning (maximum 30 words)
                - IMDB ID (just the ID, e.g., "tt0111161" for The Shawshank Redemption)
                - IMDB Rating (just the number, e.g., 9.3)
                - IMDB URL (just the URL, e.g., "https://www.imdb.com/title/tt0111161/")
                
                BE EXTREMELY CONCISE and keep the plot and reasoning as brief as possible.
                
                Your response must be a valid JSON array format with NO additional explanatory text and NO markdown formatting.
                Each object must have exactly these fields: "title", "year", "director", "genre", "plot", "matchScore", "reasoning", "imdbId", "imdbRating", "imdbUrl".
                
                Keep all text fields very short to avoid any issues with the response length.
                """.formatted(maxResults);

        SystemMessage systemMessage = new SystemMessage(systemPromptText);
        UserMessage userMessage = new UserMessage(description);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        try {
            ChatResponse response = chatClient.call(prompt);
            String content = response.getResult().getOutput().getText();
            content = cleanJsonContent(content);

            return parseJsonWithFallbacks(content);

        } catch (Exception e) {
            return createFallbackMovies(description, maxResults);
        }
    }


    private List<MovieDTO> parseJsonWithFallbacks(String content) {
        List<String> strategies = Arrays.asList(
                content, extractJsonArray(content), fixCommonJsonIssues(content));

        for (String jsonContent : strategies) {
            try {
                return objectMapper.readValue(jsonContent, new TypeReference<>() {
                });
            } catch (Exception e) {
                System.out.println("Parsing attempt failed: " + e.getMessage());
            }
        }

        return parseMoviesIndividually(content);
    }


    private List<MovieDTO> parseMoviesIndividually(String content) {
        List<MovieDTO> results = new ArrayList<>();

        Pattern objectPattern = Pattern.compile("\\{[^{}]*}", Pattern.DOTALL);
        Matcher matcher = objectPattern.matcher(content);

        while (matcher.find()) {
            String jsonObject = matcher.group();
            try {
                MovieDTO movie = objectMapper.readValue(jsonObject, MovieDTO.class);
                results.add(movie);
            } catch (Exception ignored) {
            }
        }

        return results;
    }


    private List<MovieDTO> createFallbackMovies(String description, int count) {
        List<MovieDTO> fallbacks = new ArrayList<>();

        MovieDTO fallback = new MovieDTO();
        fallback.setTitle("Sorry, recommendation failed");
        fallback.setYear("2025");
        fallback.setDirector("System");
        fallback.setGenre("Error");
        fallback.setPlot("There was an error generating movie recommendations. Please try again with a more specific description.");
        fallback.setMatchScore(0.0);
        fallback.setReasoning("Error processing the request. Please try again.");
        fallback.setImdbId("tt0000000");
        fallback.setImdbRating(0.0);
        fallback.setImdbUrl("https://www.imdb.com/");

        fallbacks.add(fallback);
        return fallbacks;
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}