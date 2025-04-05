package com.shotx.movierecommender.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {

    public static String cleanJsonContent(String content) {
        int startIndex = content.indexOf('[');
        if (startIndex >= 0) {
            content = content.substring(startIndex);
        }

        int endIndex = content.lastIndexOf(']');
        if (endIndex >= 0 && endIndex < content.length() - 1) {
            content = content.substring(0, endIndex + 1);
        }

        content = content.replaceAll(",\\s*}", "}");
        content = content.replaceAll(",\\s*]", "]");

        return content;
    }

    public static String extractJsonArray(String content) {
        Pattern pattern = Pattern.compile("\\[\\s*\\{.*}\\s*]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return content;
    }

    public static String fixCommonJsonIssues(String content) {
        content = content.replaceAll(",\\s*}", "}");
        content = content.replaceAll(",\\s*]", "]");

        content = content.replaceAll("\\{\\s*([a-zA-Z0-9_]+)\\s*:", "{\"$1\":");
        content = content.replaceAll(",\\s*([a-zA-Z0-9_]+)\\s*:", ",\"$1\":");

        content = content.replaceAll("(?<!\\\\)'", "\"");
        return content;
    }

}