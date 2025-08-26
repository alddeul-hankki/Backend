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
    public RestaurantInfoResponse(RestaurantInfo entity) {
        this(
                new Result(entity),
                "0000",
                null,
                null
        );
    }

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

    public record ShopImg(
            @JsonProperty("rpsnt_img_file_nm") String rpsntImgFileNm
    ) {
    }
}