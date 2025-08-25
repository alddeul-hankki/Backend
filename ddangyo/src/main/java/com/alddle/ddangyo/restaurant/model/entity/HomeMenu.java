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

@Document(collection = "homeMenus")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeMenu {

    @Id
    private String id;

    // patsto_no를 식별자로 사용하기 위해 별도 필드로 저장합니다.
    @Field("patsto_no")
    private String patstoNo;

    @Field("result")
    private Result result;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("menu_grp_list")
        private List<Menu.MenuGroup> menuGrpList;

        @JsonProperty("menu_list")
        private List<Menu.MenuItem> menuList;
    }
}