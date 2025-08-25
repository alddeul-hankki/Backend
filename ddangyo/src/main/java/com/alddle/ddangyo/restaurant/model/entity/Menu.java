package com.alddle.ddangyo.restaurant.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "menus")
@Getter
@NoArgsConstructor // 1. 기본 생성자 추가
@AllArgsConstructor
public class Menu {

    @Id
    private String id;

    @Field("patsto_no")
    private String patstoNo;

    @JsonProperty("menu_grp_list") // 3. JSON 필드명 매핑
    private List<MenuGroup> menuGroups;

    @JsonProperty("menu_list") // 3. JSON 필드명 매핑
    private List<MenuItem> menuList;

    @Getter
    @NoArgsConstructor // 1. 기본 생성자 추가
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MenuGroup {
        @JsonProperty("menu_grp_id") // 2. JSON 필드명 매핑
        private String menuGrpId;

        @JsonProperty("menu_grp_nm") // 2. JSON 필드명 매핑
        private String name;

        @JsonProperty("menu_grp_expl") // 2. JSON 필드명 매핑
        private String menuGrpExpl;

        @JsonProperty("sort_ord") // 2. JSON 필드명 매핑
        private int sortOrd;
    }

    @Getter
    @NoArgsConstructor // 1. 기본 생성자 추가
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MenuItem {
        @JsonProperty("menu_id") // 2. JSON 필드명 매핑
        private String menuId;

        @JsonProperty("menu_grp_id") // 2. JSON 필드명 매핑
        private String menuGrpId;

        @JsonProperty("menu_nm") // 2. JSON 필드명 매핑
        private String name;

        @JsonProperty("menu_cmps_cont") // 2. JSON 필드명 매핑
        private String menuCmpsCont;

        @JsonProperty("menu_img_file") // 2. JSON 필드명 매핑
        private String menuImgFile;

        @JsonProperty("min_menu_prc") // 2. JSON 필드명 매핑
        private Integer minMenuPrc; // 4. Integer 타입 유지

        @JsonProperty("sldot_yn") // 2. JSON 필드명 매핑
        private String sldotYn;

        @JsonProperty("grp_yn") // 2. JSON 필드명 매핑
        private String grpYn;
    }

    // Option 관련 클래스는 이미 @NoArgsConstructor가 있으므로 수정 필요 없음
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OptionGroup {
        @JsonProperty("optn_grp_nm")
        private String optnGrpNm;
        @JsonProperty("essntl_optn_yn")
        private String essntlOptnYn;
        @JsonProperty("optn_list")
        private List<OptionItem> optnList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OptionItem {
        @JsonProperty("optn_nm")
        private String optnNm;
        @JsonProperty("optn_unitprc")
        private Integer optnUnitprc;
    }
}