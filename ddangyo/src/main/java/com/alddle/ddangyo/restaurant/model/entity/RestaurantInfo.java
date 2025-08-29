package com.alddle.ddangyo.restaurant.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "restaurant_info")
public class RestaurantInfo {

    @Id
    private String id;

    @Field("patsto_no")
    private String patstoNo;

    @Field("patsto_nm")
    private String patstoNm;

    @Field("biz_stat_msg_cont")
    private String bizStatMsgCont;

    @Field("rpsnt_cat_nm")
    private String rpsntCatNm;

    @Field("good_cnt")
    private int goodCnt;

    @Field("wish_cnt")
    private int wishCnt;

    @Field("delv_tm")
    private String delvTm;

    @Field("delv_fee")
    private String delvFee;

    @Field("deli_min_ord_amt")
    private String deliMinOrdAmt;

    @Field("shop_image_urls")
    private List<String> shopImageUrls;

}