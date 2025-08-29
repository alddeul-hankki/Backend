package com.alddeul.solsolhanhankki.analytics.presentation.response;

import java.util.Map;

public record UserPreferenceResponse(
        Long userId,
        Map<String, Double> preferences
) {
}