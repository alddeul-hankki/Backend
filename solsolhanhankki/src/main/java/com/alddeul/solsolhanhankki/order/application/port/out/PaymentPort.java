package com.alddeul.solsolhanhankki.order.application.port.out;

import com.alddeul.solsolhanhankki.order.infra.dto.PaymentRequest;
import com.alddeul.solsolhanhankki.order.infra.dto.RefundRequest;

import java.util.List;

public interface PaymentPort {

    boolean  requestPayment(PaymentRequest request);

    boolean cancelHeldPayment(Long orderId);

    boolean requestRefunds(List<RefundRequest> refundRequests);

    boolean capturePayments(List<Long> orderIds);

}