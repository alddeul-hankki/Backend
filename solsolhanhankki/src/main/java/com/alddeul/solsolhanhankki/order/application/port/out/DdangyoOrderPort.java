package com.alddeul.solsolhanhankki.order.application.port.out;

import com.alddeul.solsolhanhankki.order.model.entity.Groups;
import org.springframework.stereotype.Component;

@Component
public interface DdangyoOrderPort {
    // 간단하게 그룹 정보만 전달
    void placeGroupOrder(Groups group);
}