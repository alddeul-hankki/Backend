package com.alddeul.solsolhanhankki.global.client.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientHelper {
    private final WebClient webClient;

    public <T> T postRequest(String uri, Object body, Class<T> responseType) {
        try {
            return webClient.post()
                    .uri(uri)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            this::handleErrorResponse)
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("HTTP 상태 코드: {}, 응답 본문: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("API 호출 실패", e);
        }
    }

    private Mono<Throwable> handleErrorResponse(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    log.error("API 오류 응답: {}", errorBody);
                    return Mono.error(new RuntimeException("API 호출 실패: " + errorBody));
                });
    }
}
