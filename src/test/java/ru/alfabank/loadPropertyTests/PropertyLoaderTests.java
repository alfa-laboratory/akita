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
package ru.alfabank.loadPropertyTests;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;

public class PropertyLoaderTests {
    private static AkitaScenario akitaScenario = AkitaScenario.getInstance();

    @BeforeAll
    static void init() {
    }

    @BeforeEach
    void prepare() {
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
    }

    @AfterAll
    static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    void someValuesFromMap() {
        akitaScenario.setVar("first", "pervoe");
        akitaScenario.setVar("second", "ne_rabotaet");
        String expected = "pervoe ne_rabotaet";
        String actual = resolveVars("{first} {second}");
        assertEquals(expected, actual, "Итоговый URL не равен 'pervoe ne_rabotaet'");
    }

    @Test
    void getValueFromPropertyFile() {
        akitaScenario.setVar("first", "alfalab");
        akitaScenario.setVar("second", "/ru/credit");
        String actual = resolveVars("{varFromPropertyFile1}");
        assertEquals("caramba", actual, "Итоговый URL не равен 'caramba'");
    }

    @Test
    void getSomeValuesFromPropertyFile() {
        akitaScenario.setVar("first", "alfalab");
        akitaScenario.setVar("second", "/ru/credit");
        String actual = resolveVars("{varFromPropertyFile1}/{varFromPropertyFile2}");
        assertEquals("caramba/kumkvat", actual, "Итоговый URL не равен 'caramba/kumkvat'");
    }

    @Test
    void getSomeValuesFromPropAndMap() {
        akitaScenario.setVar("first", "alfalab");
        akitaScenario.setVar("second", "/ru/credit");
        String actual = resolveVars("{varFromPropertyFile1}/{first}");
        assertEquals("caramba/alfalab", actual, "Итоговый URL не равен 'caramba/alfalab'");
    }

    @Test
    void getSomeValuesFromPropAndMapAndSpec() {
        akitaScenario.setVar("first", "alfalab");
        akitaScenario.setVar("second", "/ru/credit");
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
        akitaScenario.setVar("user.login", "superLogin");
        String resolvedString = akitaScenario.replaceVariables("{user.login}");
        assertEquals("superLogin", resolvedString,"успешно разрезолвилась переменная с .");
    }

    @Test
    void getNotExistingValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            resolveVars("{RandomTestVariable3321}");
        });
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
        akitaScenario.setVar("varName", "testVariable");
        assertEquals("testVariable", loadValueFromFileOrPropertyOrVariableOrDefault("varName"));
    }

}
