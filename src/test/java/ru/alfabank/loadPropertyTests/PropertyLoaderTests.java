/*
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
package ru.alfabank.loadPropertyTests;

import com.codeborne.selenide.WebDriverRunner;
import io.cucumber.java.Scenario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;

public class PropertyLoaderTests {
    private static final AkitaScenario AKITA_SCENARIO = AkitaScenario.getInstance();

    @BeforeAll
    static void init() {
    }

    @BeforeEach
    void prepare() {
        Scenario scenario = mock(Scenario.class);
        AKITA_SCENARIO.setEnvironment(new AkitaEnvironment(scenario));
    }

    @AfterAll
    static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    void someValuesFromMap() {
        AKITA_SCENARIO.setVar("first", "pervoe");
        AKITA_SCENARIO.setVar("second", "ne_rabotaet");
        String expected = "pervoe ne_rabotaet";
        String actual = resolveVars("{first} {second}");
        assertEquals(expected, actual, "Итоговый URL не равен 'pervoe ne_rabotaet'");
    }

    @Test
    void getValueFromPropertyFile() {
        AKITA_SCENARIO.setVar("first", "alfalab");
        AKITA_SCENARIO.setVar("second", "/ru/credit");
        String actual = resolveVars("{varFromPropertyFile1}");
        assertEquals("caramba", actual, "Итоговый URL не равен 'caramba'");
    }

    @Test
    void getSomeValuesFromPropertyFile() {
        AKITA_SCENARIO.setVar("first", "alfalab");
        AKITA_SCENARIO.setVar("second", "/ru/credit");
        String actual = resolveVars("{varFromPropertyFile1}/{varFromPropertyFile2}");
        assertEquals("caramba/kumkvat", actual, "Итоговый URL не равен 'caramba/kumkvat'");
    }

    @Test
    void getSomeValuesFromPropAndMap() {
        AKITA_SCENARIO.setVar("first", "alfalab");
        AKITA_SCENARIO.setVar("second", "/ru/credit");
        String actual = resolveVars("{varFromPropertyFile1}/{first}");
        assertEquals("caramba/alfalab", actual, "Итоговый URL не равен 'caramba/alfalab'");
    }

    @Test
    void getSomeValuesFromPropAndMapAndSpec() {
        AKITA_SCENARIO.setVar("first", "alfalab");
        AKITA_SCENARIO.setVar("second", "/ru/credit");
        String actual = resolveVars("/{second}/{varFromPropertyFile1}/{first}/");
        assertEquals("//ru/credit/caramba/alfalab/", actual, "Итоговый URL не равен '//ru/credit/caramba/alfalab/'");
    }

    @Test
    void getValuesByNameWithDot() {
        String resolvedString = resolveVars("{testUser.password}");
        assertEquals("testPassword", resolvedString, "успешно разрезолвилась переменная с .");
    }

    @Test
    void getValueFromMapByNameWithDot() {
        AKITA_SCENARIO.setVar("user.login", "superLogin");
        String resolvedString = AKITA_SCENARIO.replaceVariables("{user.login}");
        assertEquals("superLogin", resolvedString, "успешно разрезолвилась переменная с .");
    }

    @Test
    void getNotExistingValue() {
        assertThrows(IllegalArgumentException.class, () -> resolveVars("{RandomTestVariable3321}"));
    }

    @Test
    void getJsonStringType() {
        String expectedValue = "{\"accounts\": []}";
        String actualValue = resolveVars(expectedValue);
        assertEquals(actualValue, expectedValue);
    }

    @Test
    void testPropertyWhenLoadValueFromFileOrPropertyOrVariableOrDefault2() {
        assertEquals("alert('privet');", loadValueFromFileOrPropertyOrVariableOrDefault("testScript"));
    }

    @Test
    void testVariableWhenLoadValueFromFileOrPropertyOrVariableOrDefault3() {
        AKITA_SCENARIO.setVar("varName", "testVariable");
        assertEquals("testVariable", loadValueFromFileOrPropertyOrVariableOrDefault("varName"));
    }

}
