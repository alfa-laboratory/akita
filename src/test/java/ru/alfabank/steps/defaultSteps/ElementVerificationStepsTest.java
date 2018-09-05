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
package ru.alfabank.steps.defaultSteps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ElementVerificationStepsTest {

    private static ElementVerificationSteps evs;
    private static AkitaScenario akitaScenario;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        evs = new ElementVerificationSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }


    @Before
    public void prepare() {
        WebPageSteps wps = new WebPageSteps();
        wps.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void navigateToUrl() {
        assertThat(WebDriverRunner.getWebDriver().getTitle(), equalTo("Title"));
    }

    @Test
    public void storeFieldValueInVariablePositive() {
        String varName = "mockId";
        evs.storeElementValueInVariable(varName, varName);
        assertThat(akitaScenario.getVar(varName), equalTo("Serious testing page"));
    }

    @Test
    public void elemIsPresentedOnPagePositive() {
        evs.elemIsPresentedOnPage("mockTagName");
    }

    @Test
    public void compareFieldAndVariablePositive() {
        akitaScenario.setVar("test", "Serious testing page");
        evs.compareFieldAndVariable("mockTagName", "test");
    }

    @Test
    public void elementIsNotVisiblePositive() {
        evs.elementIsNotVisible("HiddenDiv");
    }

    @Test
    public void checkElemContainsAtrWithValuePositive() {
        evs.checkElemContainsAtrWithValue("SUPERBUTTON", "onclick", "HIDEnSHOW()");
    }

    @Test
    public void testFieldContainsInnerTextPositive() {
        evs.testFieldContainsInnerText("innerTextP", "inner text");
    }

    @Test
    public void testActualValueContainsSubstringPositive() {
        evs.testActualValueContainsSubstring("TextField", "xt");
    }

    @Test
    public void testActualValueContainsSubstringPositiveWithProps() {
        evs.testActualValueContainsSubstring("TextField", "textValueInProps");
    }

    @Test
    public void clickableFieldTest() {
        evs.clickableField("SUPERBUTTON");
    }

    @Test
    public void testButtonIsActiveAnotherPositive() {
        evs.clickableField("Link");
    }

    @Test(expected = AssertionError.class)
    public void testButtonIsActiveNegative() {
        evs.clickableField("Кнопка Подписать и отправить");
    }

    @Test
    public void compareValInFieldAndFromStepTest() {
        evs.compareValInFieldAndFromStep("ul", "Serious testing page");
    }

    @Test
    public void compareValInFieldAndFromStepTestWithProps() {
        evs.compareValInFieldAndFromStep("ul", "testingPageTextProps");
    }

    @Test
    public void testCheckElemClassContainsExpectedValuePositive() {
        evs.checkElemClassContainsExpectedValue("Кнопка Подписать и отправить", "disabled");
    }

    @Test(expected = AssertionError.class)
    public void testCheckElemClassContainsExpectedValueNegative() {
        evs.checkElemClassContainsExpectedValue("Кнопка Подписать и отправить", "enabled");
    }

    @Test
    public void testCheckElemClassNotContainsExpectedValuePositive() {
        evs.checkElemClassNotContainsExpectedValue("Кнопка Подписать и отправить", "enabled");
    }

    @Test(expected = AssertionError.class)
    public void testCheckElemClassNotContainsExpectedValueNegative() {
        evs.checkElemClassNotContainsExpectedValue("Кнопка Подписать и отправить", "disabled");
    }

}
