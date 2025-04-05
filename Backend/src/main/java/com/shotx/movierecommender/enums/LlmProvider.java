package com.shotx.movierecommender.enums;

import lombok.Getter;

@Getter
public enum LlmProvider {
    ANTHROPIC("anthropic", "Claude"),
    OPENAI("openai", "ChatGPT"),
    MISTRALAI("mistralai", "Mistral AI");

    private final String id;
    private final String displayName;

    LlmProvider(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static LlmProvider fromId(String id) {
        for (LlmProvider provider : values()) {
            if (provider.getId().equalsIgnoreCase(id)) {
                return provider;
            }
        }
        return ANTHROPIC;
    }
}