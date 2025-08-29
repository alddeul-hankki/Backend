package com.alddeul.solsolhanhankki.analytics.application;

import com.alddeul.solsolhanhankki.analytics.presentation.response.UserPreferenceResponse;
import com.alddeul.solsolhanhankki.order.model.entity.OrderItems;
import com.alddeul.solsolhanhankki.order.model.entity.Orders;
import com.alddeul.solsolhanhankki.order.model.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserAnalyticsService {

    private final OrderRepository orderRepository;

    // AI 서버가 요구하는 전체 카테고리 목록 (변경 가능)
    private static final Set<String> ALL_CATEGORIES = Set.of("치킨", "피자", "한식");

    @Transactional(readOnly = true)
    public List<UserPreferenceResponse> getCategoryPreferences(List<Long> userIds) {
        return userIds.stream()
                .map(this::calculateSingleUserPreferences)
                .collect(Collectors.toList());
    }

    private UserPreferenceResponse calculateSingleUserPreferences(Long userId) {
        List<Orders> recentOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 20));

        Map<String, Integer> categoryCounts = new HashMap<>();
        long totalCategoryCount = 0;

        for (Orders order : recentOrders) {
            if (order.getOrderItems() != null) {
                for (OrderItems item : order.getOrderItems()) {
                    if (item.getCategory() != null) {
                        for (String category : item.getCategory()) {
                            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
                            totalCategoryCount++;
                        }
                    }
                }
            }
        }

        // 전체 카테고리 목록으로 맵을 초기화하고, 기본 가중치를 0.0으로 설정합니다.
        Map<String, Double> preferences = new HashMap<>();
        for (String category : ALL_CATEGORIES) {
            preferences.put(category, 0.0);
        }

        // 계산된 가중치를 덮어씌웁니다.
        if (totalCategoryCount > 0) {
            for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
                double weight = (double) entry.getValue() / totalCategoryCount;
                preferences.put(entry.getKey(), weight);
            }
        }

        return new UserPreferenceResponse(userId, preferences);
    }
}