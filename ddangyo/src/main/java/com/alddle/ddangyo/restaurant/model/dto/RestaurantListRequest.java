package com.alddle.ddangyo.restaurant.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestaurantListRequest {

    private double mapLngt;
    private double mapLatt;
    private int pageNo;
    private String sortCd;
    private String delvFeeFilterCd;
    private String admtnDongCd;
    private String minOrdAmtFilterCd;
    private String categoryCd;
    private int pageSize;
}