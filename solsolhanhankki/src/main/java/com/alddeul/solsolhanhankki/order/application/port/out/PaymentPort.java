package com.alddeul.solsolhanhankki.order.application.port.out;

import com.alddeul.solsolhanhankki.order.infra.dto.PaymentRequest;
import com.alddeul.solsolhanhankki.order.infra.dto.RefundRequest;

import java.util.List;

public interface PaymentPort {

    boolean  requestPayment(PaymentRequest request);

    void cancelHeldPayment(Long orderId);

    void requestRefunds(List<RefundRequest> refundRequests);

    void capturePayments(List<Long> orderIds);

}