// solsolhanhankki/analytics/presentation/AnalyticsController.java
package com.alddeul.solsolhanhankki.analytics.presentation;

import com.alddeul.solsolhanhankki.analytics.application.UserAnalyticsService;
import com.alddeul.solsolhanhankki.analytics.presentation.request.UserPreferencesRequest;
import com.alddeul.solsolhanhankki.analytics.presentation.response.UserPreferenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sol/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final UserAnalyticsService userAnalyticsService;

    @PostMapping("/user-preferences")
    public ResponseEntity<List<UserPreferenceResponse>> getUserCategoryPreferences(@RequestBody UserPreferencesRequest request) {
        List<UserPreferenceResponse> response = userAnalyticsService.getCategoryPreferences(request.userIds());
        return ResponseEntity.ok(response);
    }
}