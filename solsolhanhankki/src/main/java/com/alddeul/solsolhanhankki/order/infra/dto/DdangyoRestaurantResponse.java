package com.alddeul.solsolhanhankki.order.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DdangyoRestaurantResponse {
    private Restaurant restaurant;
    private List<MenuGroup> menuGroups;
    private List<MenuItem> menuList;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Restaurant {
        private String id;
        private String name;
        @JsonProperty("delvFee")
        private List<DeliveryFeePolicyDTO> deliveryFees;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeliveryFeePolicyDTO {
        private Long orderAmountThreshold;
        private Long fee;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MenuGroup {
        @JsonProperty("menu_grp_id")
        private String menuGrpId;
        @JsonProperty("menu_grp_nm")
        private String menuGrpNm;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MenuItem {
        @JsonProperty("menu_id")
        private String menuId;
        @JsonProperty("menu_nm")
        private String menuNm;
        @JsonProperty("min_menu_prc")
        private Long minMenuPrc;
    }
}