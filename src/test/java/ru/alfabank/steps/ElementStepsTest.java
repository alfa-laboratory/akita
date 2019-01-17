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

package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static com.codeborne.selenide.Selenide.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

public class ElementStepsTest {

    private static ElementsVerificationSteps elvs;
    private static ElementsInteractionSteps elis;
    private static WebPageInteractionSteps wpis;
    private static AkitaScenario akitaScenario;



    @BeforeClass
    public static void setup() {
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

    @Before
    public void prepare() {
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void storeFieldValueInVariablePositive() {
        String varName = "mockId";
        elis.storeElementValueInVariable(varName, varName);
        assertThat(akitaScenario.getVar(varName), equalTo("Serious testing page"));
    }

    @Test
    public void clickOnElementPositive() {
        elis.clickOnElement("GoodButton");
        assertThat(akitaScenario.getPage("AkitaPageMock").getElement("GoodButton").isEnabled(),
                equalTo(false));
    }

    @Test
    public void elemIsPresentedOnPagePositive() {
        elvs.elemIsPresentedOnPage("mockTagName");
    }

    @Test
    public void findElementPositive() {
        elis.findElement("LINK");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().getCurrentUrl(),
                equalTo(akitaScenario.getVar("RedirectionPage")));
    }

    @Test
    public void findElementMixedLanguagePositive() {
        elis.findElement("EnGliSh? РуСсКий.");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("mixedButton"))
                .isEnabled(), is(false));
    }

    @Test
    public void findElementMixedLanguagePartialRuPositive() {
        elis.findElement("РуСсКий.");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("mixedButton"))
                .isEnabled(), is(false));
    }

    @Test
    public void findElementMixedLanguagePartialEnPositive() {
        elis.findElement("EnGliSh");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("mixedButton"))
                .isEnabled(), is(false));
    }

    @Test
    public void testCheckElemClassContainsExpectedValuePositive() {
        elvs.checkElemClassContainsExpectedValue("Кнопка Подписать и отправить", "disabled");
    }

    @Test(expected = AssertionError.class)
    public void testCheckElemClassContainsExpectedValueNegative() {
        elvs.checkElemClassContainsExpectedValue("Кнопка Подписать и отправить", "enabled");
    }

    @Test
    public void testCheckElemClassNotContainsExpectedValuePositive() {
        elvs.checkElemClassNotContainsExpectedValue("Кнопка Подписать и отправить", "enabled");
    }

    @Test(expected = AssertionError.class)
    public void testCheckElemClassNotContainsExpectedValueNegative() {
        elvs.checkElemClassNotContainsExpectedValue("Кнопка Подписать и отправить", "disabled");
    }

    @Test
    public void compareFieldAndVariablePositive() {
        akitaScenario.setVar("test", "Serious testing page");
        elvs.compareFieldAndVariable("mockTagName", "test");
    }

    @Test
    public void compareValInFieldAndFromStepTest() {
        elvs.compareValInFieldAndFromStep("ul", "Serious testing page");
    }

    @Test
    public void compareValInFieldAndFromStepTestWithProps() {
        elvs.compareValInFieldAndFromStep("ul", "testingPageTextProps");
    }

    @Test
    public void testFieldContainsInnerTextPositive() {
        elvs.testFieldContainsInnerText("innerTextP", "inner text");
    }

    @Test
    public void testActualValueContainsSubstringPositive() {
        elvs.testActualValueContainsSubstring("TextField", "xt");
    }

    @Test
    public void testActualValueContainsSubstringPositiveWithProps() {
        elvs.testActualValueContainsSubstring("TextField", "textValueInProps");
    }

    @Test
    public void fieldIsDisablePositive() {
        elvs.fieldIsDisable("DisabledField");
    }

    @Test(expected = AssertionError.class)
    public void testFieldIsDisableNegative() {
        elvs.fieldIsDisable("TextField");
    }

    @Test
    public void testClickOnButtonAndUploadFilePositive() {
        elis.clickOnButtonAndUploadFile("Кнопка загрузки файлов", "src/test/resources/example.pdf");
    }

    @Test
    public void elementIsNotVisiblePositive() {
        elvs.elementIsNotVisible("HiddenDiv");
    }

    @Test
    public void checkElemContainsAtrWithValuePositive() {
        elvs.checkElemContainsAtrWithValue("SUPERBUTTON", "onclick", "HIDEnSHOW()");
    }

    @Test
    public void elementHoverTest() {
        elis.elementHover("NormalField");
    }

    @Test
    public void clickableFieldTest() {
        elvs.clickableField("SUPERBUTTON");
    }

    @Test
    public void testButtonIsActiveAnotherPositive() {
        elvs.clickableField("Link");
    }

    @Test(expected = AssertionError.class)
    public void testButtonIsActiveNegative() {
        elvs.clickableField("Кнопка Подписать и отправить");
    }

    @Test
    public void elementDisapperaredAndAppearedComplex() {
        elvs.testElementAppeared("ul", 1);
        elis.clickOnElement("SUPERBUTTON");
        elvs.elemDisappered("ul");
    }

    @Test
    public void testCheckFieldsize() {
        elvs.checkFieldSymbolsCount("ul", 20);
    }

    @Test
    public void fieldInputIsEmptyPositive() {
        elvs.fieldInputIsEmpty("NormalField");
    }

}
