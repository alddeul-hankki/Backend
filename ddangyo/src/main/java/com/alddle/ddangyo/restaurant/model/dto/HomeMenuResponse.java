package com.alddle.ddangyo.restaurant.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// @JsonIgnoreProperties는 record에서 클래스 레벨에 선언합니다.
@JsonIgnoreProperties(ignoreUnknown = true)
public record HomeMenuResponse(
        Result result
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            @JsonProperty("menu_grp_list") List<MenuGroup> menuGrpList,
            @JsonProperty("menu_list") List<MenuItem> menuList
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MenuGroup(
            @JsonProperty("menu_grp_id") String menuGrpId,
            @JsonProperty("menu_grp_nm") String menuGrpNm
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MenuItem(
            @JsonProperty("menu_id") String menuId,
            @JsonProperty("menu_nm") String menuNm,
            @JsonProperty("menu_cmps_cont") String menuCmpsCont,
            @JsonProperty("menu_img_file") String menuImgFile,
            @JsonProperty("min_menu_prc") Integer minMenuPrc
    ) {}
}