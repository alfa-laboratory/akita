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
import com.codeborne.selenide.ex.ElementShouldNot;
import cucumber.api.Scenario;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WebPageStepsTest {

    private static WebPageInteractionSteps wpis;
    private static AkitaScenario akitaScenario;
    private static WebPageVerificationSteps wpvs;
    private static InputInteractionSteps iis;
    private static ManageBrowserSteps mbs;


    @BeforeAll
    static void setup() {
        mbs = new ManageBrowserSteps();
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        wpis = new WebPageInteractionSteps();
        wpvs = new WebPageVerificationSteps();
        iis = new InputInteractionSteps();
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
    void navigateToUrl() {
        assertThat(WebDriverRunner.getWebDriver().getTitle(), equalTo("Title"));
    }

    @Test
    void checkCurrentURLPositive() {
        wpvs.checkCurrentURL(akitaScenario.getVar("Page").toString());
    }

    @Test
    void checkCurrentURLNegative() {
        assertThrows(NullPointerException.class, () ->
                wpvs.checkCurrentURL(null));
    }

    @Test
    void testCheckCurrentURLAnotherNegative() {
        String myURL = "https://google.ru/";
        assertThrows(AssertionError.class, () ->
                wpvs.checkCurrentURL(myURL));
    }

    @Test
    void testCheckCurrentURLIsNotEqualsPositive() {
        String myURL = "https://google.ru/";
        wpvs.checkCurrentURLIsNotEquals(myURL);
    }

    @Test
    void testCheckCurrentURLIsNotEqualsNegative() {
        assertThrows(AssertionError.class, () ->
                wpvs.checkCurrentURLIsNotEquals(akitaScenario.getVar("Page").toString()));
    }

    @Test
    void testCheckCurrentURLIsNotEqualsAnotherNegative() {
        assertThrows(NullPointerException.class, () ->
                wpvs.checkCurrentURLIsNotEquals(null));
    }

    @Test
    void testLoadPagePositive() {
        wpis.loadPage("AkitaPageMock");
    }

    @Test
    void testLoadPageNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                wpis.loadPage("thisPageDoesNotExists"));
    }

    @Test
    void testLoadPageFailedPositive() {
        wpvs.loadPageFailed("RedirectionPage");
    }

    @Test
    void testLoadPageFailedNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                wpvs.loadPageFailed("thisPageDoesNotExists"));
    }

    @Test
    void testScrollPageToElementPositive() {
        wpis.scrollPageToElement("mockTagName");
    }

    @Test
    void testScrollPageToElementNegative() {
        assertThrows(AssertionError.class, () ->
                wpis.scrollPageToElement("Кнопка Показать ещё"));
    }

    @Test
    void testScrollWhileElemNotFoundOnPagePositive() {
        wpis.scrollWhileElemNotFoundOnPage("mockTagName");
    }

    @Test
    void testScrollWhileElemNotFoundOnPageNegative() {
        assertThrows(AssertionError.class, () ->
                wpis.scrollWhileElemNotFoundOnPage("Кнопка Показать ещё"));
    }

    @Test
    void testScrollWhileElemWithTextNotFoundOnPagePositive() {
        wpis.scrollWhileElemWithTextNotFoundOnPage("Serious testing page");
    }

    @Test
    void testScrollWhileElemWithTextNotFoundOnPageNegative() {
        assertThrows(AssertionError.class, () ->
                wpis.scrollWhileElemWithTextNotFoundOnPage("Not serious testing page"));
    }

    @Test
    void blockDisappearedSimple() {
        assertThrows(ElementShouldNot.class, () ->
                wpvs.blockDisappeared("AkitaPageMock"));
    }

    @Test
    void goToUrl() {
        wpis.goToUrl((String) akitaScenario.getVar("RedirectionPage"));
    }

    @Test
    void urlClickAndCheckRedirectionPositive() {
        wpis.urlClickAndCheckRedirection("RedirectionPage", "Link");
    }

    @Test
    void openReadOnlyFormPositive() {
        wpis.goToSelectedPageByLink("RedirectionPage",
                akitaScenario.getVar("RedirectionPage").toString());
        wpvs.openReadOnlyForm();
    }

    @Test
    void currentDatePositive() {
        iis.currentDate("NormalField", "dd.MM.yyyy");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField")
                        .matches("[0-3][0-9].[0-1][0-9].[0-2][0-9]{3}"),
                equalTo(true));
    }

    @Test
    void refreshPageSimple() {
        wpis.refreshPage();
    }

    @Test
    void scrollDownSimple() {
        wpis.scrollDown();
    }

    @Test
    void loginByUserDataPositive() {
        wpis.loginByUserData("testUser");
    }

    @Test
    void testLinkShouldHaveText() {
        String text = "/RedirectionPage";
        akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getElement("Link")
                .click();
        wpvs.linkShouldHaveText(text);
    }

    @Test
    void savePageTitleToVariablePositive() {
        wpis.savePageTitleToVariable("TitleVariable");
        MatcherAssert.assertThat(akitaScenario.getVar("TitleVariable"), Matchers.equalTo("Title"));
    }

    @Test
    void testSwitchToTheTabWithTitle() {
        executeJavaScript("window.open(\"RedirectionPage.html\")");
        wpis.switchToTheTabWithTitle("Page with redirection");
        wpvs.checkPageTitle("Page with redirection");
        wpis.switchToTheTabWithTitle("Title");
        wpvs.checkPageTitle("Title");
    }

    @Test
    void testCloseCurrentTab() {
        executeJavaScript("window.open(\"RedirectionPage.html\")");
        wpis.switchToTheTabWithTitle("Page with redirection");
        mbs.closeCurrentTab();
        wpis.switchToTheTabWithTitle("Title");
    }
}