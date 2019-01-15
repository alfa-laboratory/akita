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
import com.codeborne.selenide.ex.ElementShouldNot;
import cucumber.api.DataTable;
import cucumber.api.Scenario;
import org.junit.*;
import org.openqa.selenium.Dimension;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.alfabank.util.DataTableUtils.dataTableFromLists;

public class WebPageStepsTest {

    private static WebPageInteractionSteps wpis;
    private static AkitaScenario akitaScenario;
    private static WebPageVerificationSteps wpvs;
    private static InputInteractionSteps iis;


    @BeforeClass
    public static void setup() {
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

    @Before
    public void prepare() {
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
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
    public void checkCurrentURLPositive() {
        wpvs.checkCurrentURL(akitaScenario.getVar("Page").toString());
    }

    @Test(expected = NullPointerException.class)
    public void checkCurrentURLNegative() {
        wpvs.checkCurrentURL(null);
    }

    @Test(expected = AssertionError.class)
    public void testCheckCurrentURLAnotherNegative() {
        String myURL = "https://google.ru/";
        wpvs.checkCurrentURL(myURL);
    }

    @Test
    public void testCheckCurrentURLIsNotEqualsPositive() {
        String myURL = "https://google.ru/";
        wpvs.checkCurrentURLIsNotEquals(myURL);
    }

    @Test(expected = AssertionError.class)
    public void testCheckCurrentURLIsNotEqualsNegative() {
        wpvs.checkCurrentURLIsNotEquals(akitaScenario.getVar("Page").toString());
    }

    @Test(expected = NullPointerException.class)
    public void testCheckCurrentURLIsNotEqualsAnotherNegative() {
        wpvs.checkCurrentURLIsNotEquals(null);
    }

    @Test
    public void testLoadPagePositive() {
        wpis.loadPage("AkitaPageMock");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadPageNegative() {
        wpis.loadPage("thisPageDoesNotExists");
    }

    @Test
    public void testLoadPageFailedPositive() {
        wpvs.loadPageFailed("RedirectionPage");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadPageFailedNegative() {
        wpvs.loadPageFailed("thisPageDoesNotExists");
    }

    @Test
    public void testScrollPageToElementPositive() {
        wpis.scrollPageToElement("mockTagName");
    }

    @Test(expected = AssertionError.class)
    public void testScrollPageToElementNegative() {
        wpis.scrollPageToElement("Кнопка Показать ещё");
    }

    @Test
    public void testScrollWhileElemNotFoundOnPagePositive() {
        wpis.scrollWhileElemNotFoundOnPage("mockTagName");
    }

    @Test(expected = AssertionError.class)
    public void testScrollWhileElemNotFoundOnPageNegative() {
        wpis.scrollWhileElemNotFoundOnPage("Кнопка Показать ещё");
    }

    @Test
    public void testScrollWhileElemWithTextNotFoundOnPagePositive() {
        wpis.scrollWhileElemWithTextNotFoundOnPage("Serious testing page");
    }

    @Test(expected = AssertionError.class)
    public void testScrollWhileElemWithTextNotFoundOnPageNegative() {
        wpis.scrollWhileElemWithTextNotFoundOnPage("Not serious testing page");
    }

    @Test(expected = ElementShouldNot.class)
    public void blockDisappearedSimple() {
        wpvs.blockDisappeared("AkitaPageMock");
    }

    @Test
    public void goToUrl() {
        wpis.goToUrl((String) akitaScenario.getVar("RedirectionPage"));
    }

    @Test
    public void urlClickAndCheckRedirectionPositive() {
        wpis.urlClickAndCheckRedirection("RedirectionPage", "Link");
    }

    @Test
    public void openReadOnlyFormPositive() {
        wpis.goToSelectedPageByLink("RedirectionPage",
                akitaScenario.getVar("RedirectionPage").toString());
        wpvs.openReadOnlyForm();
    }

    @Test
    public void currentDatePositive() {
        iis.currentDate("NormalField", "dd.MM.yyyy");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField")
                        .matches("[0-3][0-9].[0-1][0-9].[0-2][0-9]{3}"),
                equalTo(true));
    }

    @Test
    public void refreshPageSimple() {
        wpis.refreshPage();
    }

    @Test
    public void scrollDownSimple() {
        wpis.scrollDown();
    }

    @Test
    public void loginByUserDataPositive() {
        wpis.loginByUserData("testUser");
    }

    @Test
    public void testLinkShouldHaveText(){
        String text = "/RedirectionPage";
        akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getElement("Link")
                .click();
        wpvs.linkShouldHaveText(text);
    }

}
