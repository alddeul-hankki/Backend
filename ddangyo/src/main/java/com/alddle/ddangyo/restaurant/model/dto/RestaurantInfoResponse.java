package com.alddle.ddangyo.restaurant.model.dto;

import com.alddle.ddangyo.restaurant.model.entity.RestaurantInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record RestaurantInfoResponse(
        Result result,
        @JsonProperty("result_code") String resultCode,
        @JsonProperty("message_code") String messageCode,
        String message
) {
    /**
     * RestaurantInfo 엔티티를 파라미터로 받는 생성자
     */
    public RestaurantInfoResponse(RestaurantInfo entity) {
        this(
                new Result(entity),
                "0000",
                null,
                null
        );
    }

    // Result 레코드: dma_shop_home_info와 shop_img_list만 포함하도록 단순화
    public record Result(
            @JsonProperty("dma_shop_home_info") DmaShopHomeInfo dmaShopHomeInfo,
            @JsonProperty("shop_img_list") List<ShopImg> shopImgList
    ) {
        public Result(RestaurantInfo entity) {
            this(
                    new DmaShopHomeInfo(entity),
                    entity.getShopImageUrls() != null ?
                            entity.getShopImageUrls().stream()
                                    .map(ShopImg::new)
                                    .collect(Collectors.toList()) : Collections.emptyList()
            );
        }
    }

    // DmaShopHomeInfo 레코드: Entity와 동일한 필드만 갖도록 단순화
    public record DmaShopHomeInfo(
            @JsonProperty("patsto_no") String patstoNo,
            @JsonProperty("patsto_nm") String patstoNm,
            @JsonProperty("biz_stat_msg_cont") String bizStatMsgCont,
            @JsonProperty("rpsnt_cat_nm") String rpsntCatNm,
            @JsonProperty("good_cnt") int goodCnt,
            @JsonProperty("wish_cnt") int wishCnt,
            @JsonProperty("delv_tm") String delvTm,
            @JsonProperty("delv_fee") String delvFee,
            @JsonProperty("deli_min_ord_amt") String deliMinOrdAmt
    ) {
        public DmaShopHomeInfo(RestaurantInfo entity) {
            this(
                    entity.getPatstoNo(),
                    entity.getPatstoNm(),
                    entity.getBizStatMsgCont(),
                    entity.getRpsntCatNm(),
                    entity.getGoodCnt(),
                    entity.getWishCnt(),
                    entity.getDelvTm(),
                    entity.getDelvFee(),
                    entity.getDeliMinOrdAmt()
            );
        }
    }

    // ShopImg 레코드 (변경 없음)
    public record ShopImg(
            @JsonProperty("rpsnt_img_file_nm") String rpsntImgFileNm
    ) {
    }
}