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
package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import io.cucumber.java.Scenario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.tests.core.helpers.PropertyLoader;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

public class BaseMethodsTest {

    private static AkitaScenario akitaScenario;
    private static BaseMethods bm;
    private static WebPageInteractionSteps wpis;

    @BeforeAll
    static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = mock(Scenario.class);
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        bm = new BaseMethods();
        wpis = new WebPageInteractionSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }

    @BeforeEach
    void prepare() {
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterAll
    static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    void testGetPropertyOrStringVariableOrValueFromProperty() {
        akitaScenario.setVar("testVar", "shouldNotLoadMe");
        assertThat(bm.getPropertyOrStringVariableOrValue("testVar"),
                equalTo(PropertyLoader.loadProperty("testVar")));
    }

    @Test
    void testGetPropertyOrStringVariableOrValueFromScopedVariable() {
        akitaScenario.setVar("akita.url", "shouldLoadMe");
        assertThat(bm.getPropertyOrStringVariableOrValue("akita.url"),
                equalTo("shouldLoadMe"));
    }

    @Test
    void testGetPropertyOrStringVariableOrValueFromValue() {
        String getPropertyOrVariableOrValueTestValue =
                bm.getPropertyOrStringVariableOrValue("getPropertyOrVariableOrValueTestValue");
        assertThat(getPropertyOrVariableOrValueTestValue,
                equalTo("getPropertyOrVariableOrValueTestValue"));
    }

    @Test
    void testGetPropertyOrStringVariableOrValueFromSystemVariable() {
        String propertyName = "akita.url";
        String expectedValue = "http://url";
        System.setProperty(propertyName, expectedValue);
        String actualValue = bm.getPropertyOrStringVariableOrValue(propertyName);
        System.clearProperty(propertyName);
        assertThat(actualValue, equalTo(expectedValue));
    }
}