package com.alddle.ddangyo.restaurant.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@Document(collection = "menus")
public class Menu {
    @Id
    private String id; // MongoDB 자동 생성 ID

    @Field("patsto_no")
    private String patstoNo;

    private List<MenuGroup> menuGroups;
    private List<MenuItem> menuList;

    @Getter
    @AllArgsConstructor
    public static class MenuGroup {
        private String menuGrpId;
        @Field("menu_grp_nm")
        private String name;
        private String menuGrpExpl;
        private int sortOrd;
    }

    @Getter
    @AllArgsConstructor
    public static class MenuItem {
        private String menuId;
        private String menuGrpId;
        @Field("menu_nm")
        private String name;
        private String menuCmpsCont;
        private String menuImgFile;
        private BigDecimal minMenuPrc;
        private String sldotYn;
        private String grpYn;
    }
}