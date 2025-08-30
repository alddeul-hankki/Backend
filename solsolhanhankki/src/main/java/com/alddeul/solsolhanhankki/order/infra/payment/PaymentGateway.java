package com.alddeul.solsolhanhankki.order.infra.payment;

import com.alddeul.solsolhanhankki.order.application.port.out.PaymentPort;
import com.alddeul.solsolhanhankki.order.infra.dto.PaymentRequest;
import com.alddeul.solsolhanhankki.order.infra.dto.RefundRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class PaymentGateway implements PaymentPort {

    private final WebClient webClient;

    public PaymentGateway(WebClient.Builder builder, @Value("${payment.api.url}") String paymentApiUrl) {
        this.webClient = builder.baseUrl(paymentApiUrl).build();
    }

    @Override
    public String requestPayment(PaymentRequest request) {
        log.info("[실제 결제 시스템] 결제 요청. 사용자: {}, 주문ID: {}, 금액: {}",
                request.getUserId(), request.getOrderId(), request.getAmount());

        return webClient.post()
                .uri("/api/payments") // 결제 서버의 API
                .bodyValue(request)
                .exchangeToMono(response -> {
                    System.out.println("responseCode"+response.statusCode());

                    if (response.statusCode().is2xxSuccessful()) {
                        // 2. 응답 Body에서 URL 문자열을 직접 추출합니다.
                        return response.bodyToMono(String.class)
                                .doOnNext(redirectUrl -> log.info("결제 페이지 리다이렉트 URL 수신: {}", redirectUrl));
                    }

                    // 3. 200번대 성공이 아닌 모든 경우는 에러로 처리합니다.
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                log.error("결제 요청 실패. 상태코드: {}, 응답: {}", response.statusCode(), errorBody);
                                return Mono.error(new RuntimeException("결제 서버로부터 예상치 못한 응답: " + response.statusCode()));
                            });
                })
                .block();
    }

    @Override
    public boolean cancelHeldPayment(Long orderId) {
        log.info("[실제 결제 시스템] order ID {}에 대한 결제 보류(Hold) 취소 요청.", orderId);

        return Boolean.TRUE.equals(webClient.post()
                .uri("/api/payments/" + orderId + "/cancel-hold")
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse ->
                        handleClientError(clientResponse, "결제 보류 취소 요청 실패"))
                .onStatus(httpStatus -> httpStatus.is5xxServerError(), clientResponse ->
                        handleServerError(clientResponse, "결제 보류 취소 처리 중 서버 오류"))
                .bodyToMono(Void.class)
                .then(Mono.just(true))
                .onErrorReturn(false)
                .block());
    }

    @Override
    public boolean requestRefunds(List<RefundRequest> refundRequests) {
        log.info("[실제 결제 시스템] 총 {}건의 환불 요청.", refundRequests.size());

        return Boolean.TRUE.equals(webClient.post()
                .uri("/api/payments/refunds")
                .bodyValue(refundRequests)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse ->
                        handleClientError(clientResponse, "환불 요청 실패"))
                .onStatus(httpStatus -> httpStatus.is5xxServerError(), clientResponse ->
                        handleServerError(clientResponse, "환불 처리 중 서버 오류"))
                .bodyToMono(Void.class)
                .then(Mono.just(true))
                .onErrorReturn(false)
                .block());
    }

    @Override
    public boolean capturePayments(List<Long> orderIds) {
        log.info("[실제 결제 시스템] 총 {}건의 최종 결제(Capture) 요청.", orderIds.size());

        return Boolean.TRUE.equals(webClient.post()
                .uri("/api/payments/capture")
                .bodyValue(orderIds)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse ->
                        handleClientError(clientResponse, "최종 결제 요청 실패"))
                .onStatus(httpStatus -> httpStatus.is5xxServerError(), clientResponse ->
                        handleServerError(clientResponse, "최종 결제 처리 중 서버 오류"))
                .bodyToMono(Void.class)
                .then(Mono.just(true))
                .onErrorReturn(false)
                .block());
    }

    private Mono<? extends Throwable> handleClientError(ClientResponse res, String message) {
        log.error("{} (4xx): {}", message, res.statusCode());
        return Mono.error(new RuntimeException(message));
    }

    private Mono<? extends Throwable> handleServerError(ClientResponse res, String message) {
        log.error("{} (5xx): {}", message, res.statusCode());
        return Mono.error(new RuntimeException(message));
    }
}