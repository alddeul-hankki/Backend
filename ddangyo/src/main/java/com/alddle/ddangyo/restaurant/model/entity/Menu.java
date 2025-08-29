package com.alddle.ddangyo.restaurant.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Menu {

    @Id
    private String id;

    @Field("patsto_no")
    private String patstoNo;

    @JsonProperty("menu_grp_list")
    private List<MenuGroup> menuGroups;

    @JsonProperty("menu_list")
    private List<MenuItem> menuList;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MenuGroup {
        @JsonProperty("menu_grp_id")
        private String menuGrpId;

        @JsonProperty("menu_grp_nm")
        private String name;

        @JsonProperty("menu_grp_expl")
        private String menuGrpExpl;

        @JsonProperty("sort_ord")
        private int sortOrd;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MenuItem {
        @JsonProperty("menu_id")
        private String menuId;

        @JsonProperty("menu_grp_id")
        private String menuGrpId;

        @JsonProperty("menu_nm")
        private String name;

        @JsonProperty("menu_cmps_cont")
        private String menuCmpsCont;

        @JsonProperty("menu_img_file")
        private String menuImgFile;

        @JsonProperty("min_menu_prc")
        private Integer minMenuPrc;

        @JsonProperty("sldot_yn")
        private String sldotYn;

        @JsonProperty("grp_yn")
        private String grpYn;
    }

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