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
package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static com.codeborne.selenide.Selenide.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ElementStepsTest {

    private static ElementsVerificationSteps elvs;
    private static ElementsInteractionSteps elis;
    private static WebPageInteractionSteps wpis;
    private static AkitaScenario akitaScenario;


    @BeforeAll
    static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        elis = new ElementsInteractionSteps();
        elvs = new ElementsVerificationSteps();
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
    void storeFieldValueInVariablePositive() {
        String varName = "mockId";
        elis.storeElementValueInVariable(varName, varName);
        assertThat(akitaScenario.getVar(varName), equalTo("Serious testing page"));
    }

    @Test
    void clickOnElementPositive() {
        elis.clickOnElement("GoodButton");
        assertThat(akitaScenario.getPage("AkitaPageMock").getElement("GoodButton").isEnabled(),
                equalTo(false));
    }

    @Test
    void elemIsPresentedOnPagePositive() {
        elvs.elemIsPresentedOnPage("mockTagName");
    }

    @Test
    void findElementPositive() {
        elis.findElement("LINK");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().getCurrentUrl(),
                equalTo(akitaScenario.getVar("RedirectionPage")));
    }

    @Test
    void findElementMixedLanguagePositive() {
        elis.findElement("EnGliSh? РуСсКий.");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("mixedButton"))
                .isEnabled(), is(false));
    }

    @Test
    void findElementMixedLanguagePartialRuPositive() {
        elis.findElement("РуСсКий.");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("mixedButton"))
                .isEnabled(), is(false));
    }

    @Test
    void findElementMixedLanguagePartialEnPositive() {
        elis.findElement("EnGliSh");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("mixedButton"))
                .isEnabled(), is(false));
    }

    @Test
    void testCheckElemClassContainsExpectedValuePositive() {
        elvs.checkElemClassContainsExpectedValue("Кнопка Подписать и отправить", "disabled");
    }

    @Test
    void testCheckElemClassContainsExpectedValueNegative() {
        assertThrows(AssertionError.class, () ->
                elvs.checkElemClassContainsExpectedValue("Кнопка Подписать и отправить", "enabled"));
    }

    @Test
    void testCheckElemClassNotContainsExpectedValuePositive() {
        elvs.checkElemClassNotContainsExpectedValue("Кнопка Подписать и отправить", "enabled");
    }

    @Test
    void testCheckElemClassNotContainsExpectedValueNegative() {
        assertThrows(AssertionError.class, () ->
                elvs.checkElemClassNotContainsExpectedValue("Кнопка Подписать и отправить", "disabled"));
    }

    @Test
    void compareFieldAndVariablePositive() {
        akitaScenario.setVar("test", "Serious testing page");
        elvs.compareFieldAndVariable("mockTagName", "test");
    }

    @Test
    void compareValInFieldAndFromStepTest() {
        elvs.compareValInFieldAndFromStep("ul", "Serious testing page");
    }

    @Test
    void compareValInFieldAndFromStepTestWithProps() {
        elvs.compareValInFieldAndFromStep("ul", "testingPageTextProps");
    }

    @Test
    void testFieldContainsInnerTextPositive() {
        elvs.testFieldContainsInnerText("innerTextP", "inner text");
    }

    @Test
    void testActualValueContainsSubstringPositive() {
        elvs.testActualValueContainsSubstring("TextField", "xt");
    }

    @Test
    void testActualValueContainsSubstringPositiveWithProps() {
        elvs.testActualValueContainsSubstring("TextField", "textValueInProps");
    }

    @Test
    void fieldIsDisablePositive() {
        elvs.fieldIsDisable("DisabledField");
    }

    @Test
    void testFieldIsDisableNegative() {
        assertThrows(AssertionError.class, () ->
                elvs.fieldIsDisable("TextField"));
    }

    @Test
    void testClickOnButtonAndUploadFilePositive() {
        elis.clickOnButtonAndUploadFile("Кнопка загрузки файлов", "src/test/resources/example.pdf");
    }

    @Test
    void elementIsNotVisiblePositive() {
        elvs.elementIsNotVisible("HiddenDiv");
    }

    @Test
    void checkElemContainsAtrWithValuePositive() {
        elvs.checkElemContainsAtrWithValue("SUPERBUTTON", "onclick", "HIDEnSHOW()");
    }

    @Test
    void elementHoverTest() {
        elis.elementHover("NormalField");
    }

    @Test
    void clickableFieldTest() {
        elvs.clickableField("SUPERBUTTON");
    }

    @Test
    void testButtonIsActiveAnotherPositive() {
        elvs.clickableField("Link");
    }

    @Test
    void testButtonIsActiveNegative() {
        assertThrows(AssertionError.class, () ->
                elvs.clickableField("Кнопка Подписать и отправить"));
    }

    @Test
    void elementDisapperaredAndAppearedComplex() {
        elvs.testElementAppeared("ul", 1);
        elis.clickOnElement("SUPERBUTTON");
        elvs.elemDisappered("ul");
    }

    @Test
    void testCheckFieldsize() {
        elvs.checkFieldSymbolsCount("ul", 20);
    }

    @Test
    void fieldInputIsEmptyPositive() {
        elvs.fieldInputIsEmpty("NormalField");
    }
}