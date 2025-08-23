package com.alddle.ddangyo.restaurant.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {
    @Id
    private String id; // patsto_no
    private String name; // patsto_nm
    private Instant bizStaDt;
    private int delvFee;
    private String bizStat;
    private BigDecimal mapLatt; // 위도
    private BigDecimal mapLngt; // 경도
    private String admtnDongCd;
    private String patstoImageFile;
    private int reCnt;
    private int goodCnt;
    private String coupYn;
    private Instant createdAt;

    private List<String> categories;
    private List<MenuGroup> menuGroups;

    @Getter
    @AllArgsConstructor
    public static class MenuGroup {
        private String menuGrpId;
        private String name; // menu_grp_nm
        private String menuGrpExpl;
        private int sortOrd;
        private List<Menu> menus;
    }



    @Getter
    @AllArgsConstructor
    public static class Menu {
        private String menuId;
        private String name; // menu_nm
        private String menuCmpsCont;
        private String menuImgFile;
        private BigDecimal minMenuPrc;
        private String sldotYn;
        private String grpYn;
    }

}
