package com.alddle.ddangyo.restaurant.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "restaurant_info") // MongoDB 컬렉션 이름 지정
public class RestaurantInfo {

    @Id
    private String id; // MongoDB Document ID

    // --- 핵심 정보 ---
    @Field("patsto_no")
    private String patstoNo; // 가게 고유 번호

    @Field("patsto_nm")
    private String patstoNm; // 가게 이름

    @Field("biz_stat_msg_cont")
    private String bizStatMsgCont; // 영업 상태 메시지 (ex: "영업중")

    @Field("rpsnt_cat_nm")
    private String rpsntCatNm; // 대표 카테고리 (ex: "치킨")

    @Field("good_cnt")
    private int goodCnt; // 좋아요 수

    @Field("wish_cnt")
    private int wishCnt; // 찜 수

    // --- 배달 정보 ---
    @Field("delv_tm")
    private String delvTm; // 예상 배달 시간 (ex: "19~36분 예상")

    @Field("delv_fee")
    private String delvFee; // 배달비 (ex: "3,000원")

    @Field("deli_min_ord_amt")
    private String deliMinOrdAmt; // 최소 주문 금액

    // --- 가게 이미지 ---
    @Field("shop_image_urls")
    private List<String> shopImageUrls; // 가게 대표 이미지 URL 목록

    // 참고: JSON의 dma_shop_home_info와 dma_shop_home_vd_od_info 객체 내의
    //      수많은 필드 중, 실제 화면 표시에 필요한 데이터만 선별하여 정의했습니다.
}