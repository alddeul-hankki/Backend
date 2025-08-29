package com.alddeul.solsolhanhankki.analytics.presentation.request;

import java.util.List;

public record UserPreferencesRequest(
        List<Long> userIds
) {}