package com.alddeul.solsolhanhankki.order.infra.ddangyo;

import com.alddeul.solsolhanhankki.order.application.port.out.DdangyoOrderPort;
import com.alddeul.solsolhanhankki.order.model.entity.Groups;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 실제 땡겨요 주문 서버 없이 로직을 테스트하기 위한 가짜 구현체입니다.
 */
@Slf4j
@Component
public class FakeDdangyoOrderClient implements DdangyoOrderPort {

    @Override
    public void placeGroupOrder(Groups group) {
        log.info("✅ [땡겨요 주문 시스템] 그룹 ID {}의 주문을 가게({})로 전송 완료.", group.getId(), group.getStoreName());
    }
}