package ru.alfabank.tests.core.rest;

import lombok.Data;

/**
 * Created by U_M0UKA on 16.01.2017.
 */
@Data
public class RequestParam {

    private RequestParamType type;
    private String name;
    private String value;

    public void setType(String type) {
        this.type = RequestParamType.valueOf(type.toUpperCase());
    }
}
