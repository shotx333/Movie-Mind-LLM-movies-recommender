package com.shotx.movierecommender.service.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shotx.movierecommender.dto.MovieDTO;
import com.shotx.movierecommender.enums.LlmProvider;
import com.shotx.movierecommender.service.LlmProviderService;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.shotx.movierecommender.util.JsonUtils.cleanJsonContent;
import static com.shotx.movierecommender.util.JsonUtils.extractJsonArray;

@Service
public class MistralProviderService implements LlmProviderService {

    private final MistralAiChatModel chatClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    @Value("${spring.ai.mistralai.options.max-tokens:2000}")
    private int maxTokens;

    @Autowired
    public MistralProviderService(
            MistralAiChatModel chatClient,
            @Value("${spring.ai.mistralai.api-key:}") String apiKey) {
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
    }

    @Override
    public LlmProvider getProviderType() {
        return LlmProvider.MISTRALAI;
    }

    @Override
    public List<MovieDTO> getMovieRecommendations(String description, int maxResults) {
        int adjustedMaxTokens = Math.max(maxTokens, maxResults * 500);

        String systemPromptText = """
                You are a highly knowledgeable film expert. Your task is to recommend movies based on a user's description.
                
                Consider movies from all eras, countries, and genres. Think about plot elements, themes, cinematography, atmosphere, and style when matching movies to the description.
                
                Provide a list of %d movies that match the description. For each movie, include:
                - Title (exactly as it appears officially)
                - Year of release
                - Director
                - Genre (primary genre)
                - A brief plot summary (keep it under 100 words)
                - A match score between 0.0-1.0 indicating how well the movie matches the description
                - A brief reasoning for why this movie matches the description (keep it under 50 words)
                - IMDB ID (the identifier used in IMDB URLs, e.g., "tt0111161" for The Shawshank Redemption)
                - IMDB Rating (the current rating on IMDB, as a number between 1.0-10.0)
                - IMDB URL (the full URL to the movie's IMDB page, e.g., "https://www.imdb.com/title/tt0111161/")
                
                IMPORTANT: Structure your response as a valid JSON array with ONLY the movie data and NO explanatory text.
                Each object in the array should have these fields exactly: "title", "year", "director", "genre", "plot", "matchScore", "reasoning", "imdbId", "imdbRating", "imdbUrl".
                Do not include any non-JSON text in your response at all.
                
                Important: Ensure your JSON is properly formatted and valid, with no trailing commas or syntax errors. Be accurate with IMDB IDs and ratings based on your knowledge.
                """.formatted(maxResults);

        SystemMessage systemMessage = new SystemMessage(systemPromptText);
        UserMessage userMessage = new UserMessage(description);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        try {
            ChatResponse response = chatClient.call(prompt);
            String content = response.getResult().getOutput().getText();

            content = cleanJsonContent(content);

            try {
                return objectMapper.readValue(content, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                String extractedJson = extractJsonArray(content);
                if (!extractedJson.equals(content)) {
                    return objectMapper.readValue(extractedJson, new TypeReference<>() {
                    });
                }
                throw e;
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing Mistral response: " + e.getMessage());
            if (maxResults > 3) {
                System.out.println("Retrying with fewer results (3)...");
                return getMovieRecommendations(description, 3);
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error calling Mistral API: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}