package com.shotx.movierecommender.service;

import com.shotx.movierecommender.dto.MovieDTO;
import com.shotx.movierecommender.enums.LlmProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LlmService {

    private final Map<LlmProvider, LlmProviderService> providerServices;
    private final LlmProvider defaultProvider;

    @Autowired
    public LlmService(
            List<LlmProviderService> services,
            @Value("${app.llm.default-provider:anthropic}") String defaultProviderId) {

        this.providerServices = services.stream()
                .collect(Collectors.toMap(LlmProviderService::getProviderType, Function.identity()));

        this.defaultProvider = LlmProvider.fromId(defaultProviderId);
    }


    public List<MovieDTO> getMovieRecommendations(String description, int maxResults, LlmProvider provider) {
        LlmProviderService service = providerServices.get(provider);

        if (service == null || !service.isAvailable()) {
            service = findAvailableAlternative();

            if (service == null) {
                System.err.println("No available LLM providers found!");
                return new ArrayList<>();
            }
        }

        return service.getMovieRecommendations(description, maxResults);
    }


    private LlmProviderService findAvailableAlternative() {
        LlmProviderService defaultService = providerServices.get(defaultProvider);
        if (defaultService != null && defaultService.isAvailable()) {
            return defaultService;
        }

        return providerServices.values().stream()
                .filter(LlmProviderService::isAvailable)
                .findFirst()
                .orElse(null);
    }


    public List<ProviderInfo> getAvailableProviders() {
        return providerServices.values().stream()
                .filter(LlmProviderService::isAvailable)
                .map(service -> {
                    LlmProvider provider = service.getProviderType();
                    return new ProviderInfo(provider.getId(), provider.getDisplayName());
                }).toList();
    }


    public record ProviderInfo(String id, String name) {

    }
}