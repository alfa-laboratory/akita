/**
 * Copyright 2017 Alfa Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
