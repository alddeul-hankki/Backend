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

@Document(collection = "menuDetails")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuDetail {

    @Id
    private String id;

    // menu_id를 식별자로 사용하기 위해 별도 필드로 저장합니다.
    @Field("menu_id")
    private String menuId;

    @Field("patsto_no")
    private String patstoNo;

    @Field("result")
    private Result result;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("menu_info")
        private Menu.MenuItem menuInfo;

        @JsonProperty("optn_grp_list")
        private List<Menu.OptionGroup> optnGrpList;
    }
}