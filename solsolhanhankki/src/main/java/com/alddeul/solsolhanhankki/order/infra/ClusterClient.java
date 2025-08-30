package com.alddeul.solsolhanhankki.order.infra;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class ClusterClient {
	 private final WebClient webClient;

	 public ClusterClient(WebClient.Builder builder, @Value("${cluster.api.url}") String clusterApiUrl) {
	        this.webClient = builder.baseUrl(clusterApiUrl).build();
	 }
	 
	 // 비동기
	 public Mono<List<Long>> getUserIds(long userId, int topK) {
		 Map<String, Object> requestBody = Map.of(
	         "userId", userId,
	         "topK", topK
	     );

		 return webClient.post()
	                .uri("/campuses/cluster-member/me")
	                .bodyValue(requestBody)
	                .retrieve()
	                .bodyToMono(new ParameterizedTypeReference<List<Long>>() {})
	                .switchIfEmpty(Mono.error(new IllegalStateException("빈 응답입니다.")));
	 }
	 
	 public List<Long> getUserIdsBlocking(long userId, int topK) {
	        return getUserIds(userId, topK)
	                .blockOptional(Duration.ofSeconds(5))
	                .orElseThrow(() -> new IllegalStateException("응답을 수신하지 못했습니다."));
	 }
}
