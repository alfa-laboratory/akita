package ru.alfabank.tests.core.rest;

import lombok.Builder;
import lombok.Data;

/**
 * Builder для формирования http запроса
 */

@Data
@Builder
public class RequestParam {

    private RequestParamType type;
    private String name;
    private String value;

    public void setType(String type) {
        this.type = RequestParamType.valueOf(type.toUpperCase());
    }
}
