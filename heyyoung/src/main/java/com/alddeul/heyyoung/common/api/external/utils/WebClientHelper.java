package com.alddeul.heyyoung.common.api.external.utils;

import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.common.api.external.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientHelper {
    private final WebClient webClient;

    public <T> FinanceApiResponse<T> postRequest(String uri, Object body, Class<T> responseType) {
        try {
            return webClient.post()
                    .uri(uri)
                    .bodyValue(body)
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(responseType)
                                    .map(FinanceApiResponse::ofData);
                        } else if (clientResponse.statusCode().is4xxClientError()) {
                            return clientResponse.bodyToMono(ErrorResponse.class)
                                    .map(FinanceApiResponse::<T>ofError);
                        } else { // 5XX 등 서버 오류는 예외로 처리
                            return clientResponse.createException()
                                    .flatMap(Mono::error);
                        }
                    })
                    .block();
        } catch (WebClientResponseException e) {
            log.error("HTTP 상태 코드: {}, 응답 본문: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("API 호출 실패", e);
        }
    }
}
