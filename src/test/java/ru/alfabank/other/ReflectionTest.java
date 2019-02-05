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
package ru.alfabank.other;

import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.alfabank.alfatest.cucumber.utils.Reflection.extractFieldValue;

public class ReflectionTest {

    @Getter
    public class MockClass {
        public String mockField;

        MockClass() {
            mockField = "123";
        }
    }

    @Test
    void extractFieldValueNegative() {
        MockClass mockClass = new MockClass();
        Field field = null;
        assertThrows(NullPointerException.class, () ->
                extractFieldValue(field, mockClass));
    }

    @Test
    void extractFieldValuePositive() throws NoSuchFieldException {
        MockClass mockClass = new MockClass();
        Class reflectClass = mockClass.getClass();
        Field field = reflectClass.getField("mockField");
        assertThat(extractFieldValue(field, mockClass), equalTo(mockClass.getMockField()));
    }
}
