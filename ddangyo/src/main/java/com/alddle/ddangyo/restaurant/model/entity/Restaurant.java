package com.alddle.ddangyo.restaurant.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {
    @Id
    @Field("patsto_no")
    private String id; // patsto_no
    @Field("patsto_nm")
    private String name;
    private LocalDate bizStaDt;
    private int delvFee;
    private String delvFeeNm;
    private int reCnt;
    private int goodCnt;
    private int orderCnt;
    private String bizStat;
    private String onnuriGiftCardPatstoYn;
    private String stampYn;
    private String newYn;
    private String coupYn;
    private String coupMsg;
    private String patstoImageFile;
    private String minDelvTm;
    private String maxDelvTm;
    private String delvTm;
    private String bizStatMsgCont;
    private String admtnDongCd;
    private Instant createdAt;
    private List<String> categories;
}