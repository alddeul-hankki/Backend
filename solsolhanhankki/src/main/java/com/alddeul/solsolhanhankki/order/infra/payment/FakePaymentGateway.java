//package com.alddeul.solsolhanhankki.order.infra.payment;
//
//import com.alddeul.solsolhanhankki.order.application.port.out.PaymentPort;
//import com.alddeul.solsolhanhankki.order.infra.dto.PaymentRequest;
//import com.alddeul.solsolhanhankki.order.infra.dto.RefundRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * 실제 결제 시스템 없이 로직을 테스트하기 위한 가짜 PaymentPort 구현체입니다.
// */
//@Slf4j
//@Component
//@Profile("dev")
//public class FakePaymentGateway implements PaymentPort {
//
//    @Override
//    public boolean requestPayment(PaymentRequest request) {
//        String fakePaymentId = "payment-" + UUID.randomUUID();
//        log.info("[결제 시스템] {} 요청 수신. 사용자: {}, 주문ID: {}, 금액: {}. Payment ID: {}",
//                request.getUserId(),
//                request.getOrderId(),
//                request.getAmount(),
//                fakePaymentId);
//        return true; // 성공했다고 가정
//    }
//
//    @Override
//    public boolean cancelHeldPayment(Long orderId) {
//        log.info("[결제 시스템] order ID {}에 대한 결제 보류(Hold) 취소 성공.", orderId);
//        return true; // 성공했다고 가정
//    }
//
//    @Override
//    public boolean requestRefunds(List<RefundRequest> refundRequests) {
//        log.info("[결제 시스템] 총 {}건의 환불 요청 수신.", refundRequests.size());
//        for (RefundRequest request : refundRequests) {
//            log.info(" -> (환불) Payment ID: {}, 금액: {}", request.getOrderId(), request.getAmount());
//        }
//        return true; // 성공했다고 가정
//    }
//
//    @Override
//    public boolean capturePayments(List<Long> orderIds) {
//        log.info("[결제 시스템] 총 {}건의 최종 결제(Capture) 요청 수신.", orderIds.size());
//        for (Long orderId : orderIds) {
//            log.info(" -> (결제 확정) Payment ID: {}", orderId);
//        }
//
//        return true; // 성공했다고 가정
//    }
//
//}