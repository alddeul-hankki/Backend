package com.alddle.ddangyo.restaurant.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Document(collection = "menu_detail")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuDetail {

    @Id
    private String id;

    @Field("patsto_no")
    private String patstoNo;

    @Field("menu_id")
    private String menuId;

    @JsonProperty("result")
    private Result result;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("menu_info")
        private MenuInfo menuInfo;

        @JsonProperty("optn_grp_list")
        private List<OptionGroup> optnGrpList;

        @JsonProperty("optn_list")
        private List<OptionItem> optnList;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MenuInfo {
        @JsonProperty("menu_id")
        private String menuId;

        @JsonProperty("menu_nm")
        private String name;

        @JsonProperty("menu_cmps_cont")
        private String menuCmpsCont;

        @JsonProperty("sldot_yn")
        private String sldotYn;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OptionGroup {
        @JsonProperty("optn_grp_id")
        private String optnGrpId;

        @JsonProperty("optn_grp_nm")
        private String optnGrpNm;

        @JsonProperty("ess_slct_yn")
        private String essSlctYn;

        @JsonProperty("min_slct_cnt")
        private int minSlctCnt;

        @JsonProperty("max_slct_cnt")
        private int maxSlctCnt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OptionItem {
        @JsonProperty("optn_grp_id")
        private String optnGrpId;

        @JsonProperty("optn_id")
        private String optnId;

        @JsonProperty("optn_nm")
        private String optnNm;

        @JsonProperty("optn_unitprc")
        private double optnUnitprc;

        @JsonProperty("sldot_yn")
        private String sldotYn;
    }
}