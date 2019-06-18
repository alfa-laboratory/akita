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

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.ScopedVariables;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScopedVariablesTest {
    private static ScopedVariables variables;

    @BeforeAll
    static void setup() {
        AkitaScenario akitaScenario = AkitaScenario.getInstance();
        akitaScenario.setEnvironment(new AkitaEnvironment(new StubScenario()));
        variables = new ScopedVariables();
    }

    @AfterAll
    static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    void evaluatePositive() {
        assertThat(variables.evaluate("\"test\".equals(\"test\")"), equalTo(true));
    }

    @Test
    void evaluateNegative() {
        assertThat(variables.evaluate("\"test1\".equals(\"test\")"), equalTo(false));
    }

    @Test
    void putGetPositive() {
        variables.put("Test", "text");
        assertThat(variables.get("Test"), equalTo("text"));
    }

    @Test
    void putGetNull() {
        Object nullObject = nullValue();
        variables.put("Test", nullObject);
        assertThat(variables.get("Test"), equalTo(nullObject));
    }

    @Test
    void getNegative() {
        assertThat(variables.get("asdfg"), equalTo(null));
    }

    @Test
    void clearPositive() {
        variables.put("test", "text");
        variables.clear();
        assertThat(variables.get("test"), equalTo(null));
    }


    @Test
    void removePositive() {
        variables.put("test", "text");
        variables.remove("test");
        assertThat(variables.get("test"), equalTo(null));
    }

    @Test
    void removeNegative() {
        assertThat(variables.remove("WRONG_KEY"), equalTo(null));
    }

    @Test
    void resolveVariableFromJsonString() {
        String inputJsonString = "{\"object1\": {\"var1\": 1}, " +
                "\"person\": {\"name\": \"{bodyWithParams1}\", \"age\": {bodyWithParams2}}, " +
                "\"object\": {\"var1\": 1}, " +
                "\"length\": {resolve.Переменная-1_2}}";
        String expectedJsonString = "{\"object1\": {\"var1\": 1}, " +
                "\"person\": {\"name\": \"Jack\", \"age\": 35}, " +
                "\"object\": {\"var1\": 1}, " +
                "\"length\": 180}";
        assertThat(ScopedVariables.resolveVars(inputJsonString), equalTo(expectedJsonString));
    }

    @Test
    void resolveVariableFromXmlString() {
        String inputJsonString = "<note>" +
                "<from>{bodyWithParams1}</from>" +
                "</note>";
        String expectedJsonString = "<note>" +
                "<from>Jack</from>" +
                "</note>";
        assertThat(ScopedVariables.resolveVars(inputJsonString), equalTo(expectedJsonString));
    }

    @Test
    void resolveVariableFromJsonStringWithUnknownVariable() {
        String inputJsonString = "{\"unknown\": {unknownVariable}, " +
                "\"object1\": {\"var1\": 1}, " +
                "\"person\": {\"name\": \"{bodyWithParams1}\", \"age\": {bodyWithParams2}}, " +
                "\"object\": {\"var1\": 1}, " +
                "\"length\": {resolve.Переменная-1_2}}";
        assertThrows(IllegalArgumentException.class, () ->
                ScopedVariables.resolveVars(inputJsonString));
    }

    @Test
    void resolveVariableFromXmlStringWithUnknownVariable() {
        String inputJsonString = "<note>" +
                "<from>{bodyWithParams1}</from>" +
                "<from>{unknownVariable}</from>" +
                "</note>";
        assertThrows(IllegalArgumentException.class, () ->
                ScopedVariables.resolveVars(inputJsonString));
    }

}
