/**
 * Copyright 2017 Alfa Laboratory
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.loadPropertyTests;

import com.codeborne.selenide.WebDriverRunner;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static org.junit.Assert.assertThat;
import static ru.alfabank.steps.DefaultApiSteps.resolveVars;

public class PropertyLoaderTests {
    private static AkitaScenario akitaScenario = AkitaScenario.getInstance();

    @BeforeClass
    public static void init() {
    }

    @Before
    public void prepare() {
        akitaScenario.setEnvironment(new AkitaEnvironment());
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void someValuesFromMap() {
        akitaScenario.setVar("first", "pervoe");
        akitaScenario.setVar("second", "ne_rabotaet");
        String expected = "pervoe ne_rabotaet";
        String actual = resolveVars("{first} {second}");
        assertThat("Итоговый URL не равен 'pervoe ne_rabotaet'", actual, Matchers.equalTo(expected));
    }

    @Test
    public void getValueFromPropertyFile() {
        akitaScenario.setVar("first", "alfalab");
        akitaScenario.setVar("second", "/ru/credit");
        String actual = resolveVars("{varFromPropertyFile1}");
        assertThat("Итоговый URL не равен 'caramba'", actual, Matchers.equalTo("caramba"));
    }

    @Test
    public void getSomeValuesFromPropertyFile() {
        akitaScenario.setVar("first", "alfalab");
        akitaScenario.setVar("second", "/ru/credit");
        String actual = resolveVars("{varFromPropertyFile1}/{varFromPropertyFile2}");
        assertThat("Итоговый URL не равен 'caramba/kumkvat'", actual, Matchers.equalTo("caramba/kumkvat"));
    }

    @Test
    public void getSomeValuesFromPropAndMap() {
        akitaScenario.setVar("first", "alfalab");
        akitaScenario.setVar("second", "/ru/credit");
        String actual = resolveVars("{varFromPropertyFile1}/{first}");
        assertThat("Итоговый URL не равен 'caramba/alfalab'", actual, Matchers.equalTo("caramba/alfalab"));
    }

    @Test
    public void getSomeValuesFromPropAndMapAndSpec() {
        akitaScenario.setVar("first", "alfalab");
        akitaScenario.setVar("second", "/ru/credit");
        String actual = resolveVars("/{second}/{varFromPropertyFile1}/{first}/");
        assertThat("Итоговый URL не равен '//ru/credit/caramba/alfalab/'", actual, Matchers.equalTo("//ru/credit/caramba/alfalab/"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNotExistingValue() {
        resolveVars("{RandomTestVariable3321}");
    }
}
