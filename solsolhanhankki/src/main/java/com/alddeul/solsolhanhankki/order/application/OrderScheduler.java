package com.alddeul.solsolhanhankki.order.application;

import com.alddeul.solsolhanhankki.order.application.port.out.DdangyoOrderPort;
import com.alddeul.solsolhanhankki.order.application.port.out.PaymentPort;
import com.alddeul.solsolhanhankki.order.infra.dto.RefundRequest;
import com.alddeul.solsolhanhankki.order.model.entity.Groups;
import com.alddeul.solsolhanhankki.order.model.entity.Orders;
import com.alddeul.solsolhanhankki.order.model.enums.GroupStatus;
import com.alddeul.solsolhanhankki.order.model.repository.GroupRepository;
import com.alddeul.solsolhanhankki.order.model.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final GroupRepository groupRepository;
    private final OrderRepository orderRepository;
    private final PaymentPort paymentPort;
    private final DdangyoOrderPort ddangyoOrderPort; // 가상의 땡겨요 주문 서버

    @Scheduled(fixedRate = 30000)
    public void processTriggeredGroups() {
        OffsetDateTime threshold = OffsetDateTime.now().minusMinutes(5);
        List<Groups> targetGroups = groupRepository.findAllByStatusAndTriggeredAtBefore(GroupStatus.TRIGGERED, threshold);

        for (Groups group : targetGroups) {
            try {
                processGroupSettlement(group.getId());
            } catch (Exception e) {
                log.error("그룹 정산 처리 중 오류 발생. groupId: {}", group.getId(), e);
            }
        }
    }

    @Async
    @Transactional
    public void processGroupSettlement(Long groupId) {
        log.info("그룹 정산 시작. groupId: {}", groupId);
        Groups group = groupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);
        List<Orders> orders = orderRepository.findAllByGroupId(groupId);

        //  최종 1/N 배달비 및 각 주문의 환불 금액 계산
        long finalPerPersonFee = group.getPerPersonDeliveryFee();
        orders.forEach(order -> order.finalizeFee(finalPerPersonFee));

        //  환불이 필요한 주문들만 필터링하여 환불 요청 DTO 리스트 생성
        List<RefundRequest> refundRequests = orders.stream()
                .filter(order -> order.getRefundAmount() != null && order.getRefundAmount() > 0)
                .map(RefundRequest::from)
                .collect(Collectors.toList());

        //  결제 서버로 환불 요청
        if (!refundRequests.isEmpty()) {
            boolean refundSuccess = paymentPort.requestRefunds(refundRequests);
            if(!refundSuccess) {
                throw new RuntimeException("환불 요청에 실패했습니다.");
            }
            log.info("그룹 ID {}에 대해 총 {}건의 환불 요청 완료.", groupId, refundRequests.size());
        }

        //  결제 서버로 최종 결제(Capture) 요청
        List<Long> orderIdsToCapture = orders.stream()
                .map(Orders::getId)
                .collect(Collectors.toList());

        boolean captureSuccess = paymentPort.capturePayments(orderIdsToCapture);

        if(!captureSuccess) {
            throw new RuntimeException("최종 결제 요청에 실패했습니다.");
        }
        log.info("그룹 ID {}에 대해 총 {}건의 최종 결제(Capture) 요청 완료.", groupId, orderIdsToCapture.size());

        ddangyoOrderPort.placeGroupOrder(group);

        group.completeOrder();
        orders.forEach(Orders::confirm);
        log.info("그룹 정산 및 상태 변경 완료. groupId: {}", groupId);
    }
}