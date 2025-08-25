package com.alddle.ddangyo.restaurant.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MenuDetailResponse(
        Result result
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            @JsonProperty("menu_info") MenuInfo menuInfo,
            @JsonProperty("optn_grp_list") List<OptionGroup> optnGrpList
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MenuInfo(
            @JsonProperty("menu_nm") String menuNm,
            @JsonProperty("menu_cmps_cont") String menuCmpsCont,
            @JsonProperty("menu_img_file_nm") String menuImgFileNm
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OptionGroup(
            @JsonProperty("optn_grp_nm") String optnGrpNm,
            @JsonProperty("essntl_optn_yn") String essntlOptnYn,
            @JsonProperty("optn_list") List<OptionItem> optnList
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OptionItem(
            @JsonProperty("optn_nm") String optnNm,
            @JsonProperty("optn_unitprc") Integer optnUnitprc
    ) {}
}