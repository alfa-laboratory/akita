/**
 * Copyright 2017 Alfa Laboratory
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ru.alfabank.tests.core.rest.RequestParam;
import ru.alfabank.tests.core.rest.RequestParamType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RequestParamTests {
    private static RequestParam requestParam;
    private static RequestParam requestParamForCompare;

    @BeforeAll
    static void init() {
        requestParam = RequestParam.builder()
                .name("test")
                .value("testValue")
                .type(RequestParamType.HEADER)
                .build();
    }

    @Test
    void getNameTest() {
        requestParam.setType("hEaDer");
        assertThat(requestParam.getType(), equalTo(RequestParamType.HEADER));
    }

    @Test
    void equalsTest() {
        RequestParam requestParamForCompare = RequestParam.builder()
                .name("test")
                .value("testValue")
                .type(RequestParamType.HEADER)
                .build();
        assertTrue(requestParam.equals(requestParamForCompare));
    }

    @Test
    void hashTest() {
        RequestParam requestParamForCompare = RequestParam.builder()
                .name("test")
                .value("testValue")
                .type(RequestParamType.HEADER)
                .build();
        int hashToCompare = requestParamForCompare.hashCode();
        assertThat(requestParam.hashCode(), equalTo(hashToCompare));
    }

    @Test
    void toStringTest() {
        RequestParam requestParamForCompare = RequestParam.builder()
                .name("test")
                .value("testValue")
                .type(RequestParamType.HEADER)
                .build();
        String stringToCompare = requestParamForCompare.toString();
        assertThat(requestParam.toString(), equalTo(stringToCompare));
    }
}
