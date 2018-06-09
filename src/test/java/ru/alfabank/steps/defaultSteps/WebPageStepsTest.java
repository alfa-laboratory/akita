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
import com.codeborne.selenide.ex.ElementShouldNot;
import cucumber.api.Scenario;
import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.openqa.selenium.Dimension;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.ScopedVariables;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.steps.DefaultManageBrowserSteps;

import java.io.File;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrDefault;

public class WebPageStepsTest {

    private static WebPageSteps wps;
    private static AkitaScenario akitaScenario;
    private static ElementSteps es;
    private static DefaultManageBrowserSteps dmbs;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        wps = new WebPageSteps();
        es = new ElementSteps();
        dmbs = new DefaultManageBrowserSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }

    @Before
    public void prepare() {
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
    public void checkCurrentURLPositive() {
        wps.checkCurrentURL(akitaScenario.getVar("Page").toString());
    }

    @Test(expected = NullPointerException.class)
    public void checkCurrentURLNegative() {
        wps.checkCurrentURL(null);
    }

    @Test(expected = AssertionError.class)
    public void testCheckCurrentURLAnotherNegative() {
        String myURL = "https://google.ru/";
        wps.checkCurrentURL(myURL);
    }

    @Test
    public void testCheckCurrentURLIsNotEqualsPositive(){
        String myURL = "https://google.ru/";
        wps.checkCurrentURLIsNotEquals(myURL);
    }

    @Test(expected = AssertionError.class)
    public void testCheckCurrentURLIsNotEqualsNegative(){
        wps.checkCurrentURLIsNotEquals(akitaScenario.getVar("Page").toString());
    }

    @Test(expected = NullPointerException.class)
    public void testCheckCurrentURLIsNotEqualsAnotherNegative(){
        wps.checkCurrentURLIsNotEquals(null);
    }

    @Test
    public void setWindowSizeSimple() {
        Dimension expectedDimension = new Dimension(800, 600);
        wps.setBrowserWindowSize(800, 600);
        Dimension actualDimension = WebDriverRunner.getWebDriver().manage().window().getSize();
        assertThat(expectedDimension, equalTo(actualDimension));
    }

    @Test(expected = AssertionError.class)
    public void compareTwoDigitVarsNegative() {
        String number1Name = "number1", number1Value = "1234567890";
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567894";
        akitaScenario.setVar(number2Name, number2Value);

        wps.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void compareTwoDigitVars() {
        String number1Name = "number1", number1Value = "1234567890.97531";
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567890.97531";
        akitaScenario.setVar(number2Name, number2Value);

        wps.compareTwoVariables(number1Name, number2Name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareTwoDigitVarsAnotherNegative() {
        String number1Name = "number1", number1Value = null;
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = null;
        akitaScenario.setVar(number2Name, number2Value);

        wps.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void testCheckingTwoVariablesAreNotEqualsPositive(){
        String variable1Name = "number1"; int variable1Value = 666;
        akitaScenario.setVar(variable1Name, variable1Value);

        String variable2Name = "number2"; int variable2Value = 123;
        akitaScenario.setVar(variable2Name, variable2Value);
        wps.checkingTwoVariablesAreNotEquals(variable1Name, variable2Name);
    }

    @Test(expected = AssertionError.class)
    public void testCheckingTwoVariablesAreNotEqualsNegative(){
        String variable1Name = "number1"; int variable1Value = 666;
        akitaScenario.setVar(variable1Name, variable1Value);

        String variable2Name = "number2"; int variable2Value = 666;
        akitaScenario.setVar(variable2Name, variable2Value);
        wps.checkingTwoVariablesAreNotEquals(variable1Name, variable2Name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckingTwoVariablesAreNotEqualsAnotherNegative(){
        String variable1Name = "number1", variable1Value = null;
        akitaScenario.setVar(variable1Name, variable1Value);

        String variable2Name = "number2", variable2Value = null;
        akitaScenario.setVar(variable2Name, variable2Value);
        wps.checkingTwoVariablesAreNotEquals(variable1Name, variable2Name);
    }

    @Test
    public void saveValueToVarPositive() {
        wps.saveValueToVar("testVar", "test");
        assertThat(akitaScenario.getVar("test"), equalTo("customTestValue"));
    }

    @Test
    public void testLoadPagePositive() {
        wps.loadPage("AkitaPageMock");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadPageNegative() {
        wps.loadPage("thisPageDoesNotExists");
    }

    @Test
    public void testLoadPageFailedPositive(){
        wps.loadPageFailed("RedirectionPage");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadPageFailedNegative(){
        wps.loadPageFailed("thisPageDoesNotExists");
    }

    @Test(expected = ElementShouldNot.class)
    public void blockDisappearedSimple() {
        wps.blockDisappeared("AkitaPageMock");
    }

    @Test
    public void pushButtonOnKeyboardSimple() {
        wps.pushButtonOnKeyboard("alt");
    }

    @Test
    public void expressionExpressionPositive() {
        wps.expressionIsTrue("\"test\".equals(\"test\")");
    }

    @Test
    public void urlClickAndCheckRedirectionPositive() {
        wps.urlClickAndCheckRedirection("RedirectionPage", "Link");
    }

    @Test
    public void openReadOnlyFormPositive() {
        wps.goToSelectedPageByLink("RedirectionPage",
            akitaScenario.getVar("RedirectionPage").toString());
        wps.openReadOnlyForm();
    }

    @Test
    public void refreshPageSimple() {
        wps.refreshPage();
    }

    @Test
    public void scrollDownSimple() {
        wps.scrollDown();
    }

    @Test
    public void goToUrl() {
        wps.goToUrl((String) akitaScenario.getVar("RedirectionPage"));
    }

    @Test
    public void setVariableTest() {
        wps.setVariable("ul", "Serious testing page");
        assertThat(akitaScenario.getVar("ul"), equalTo("Serious testing page"));
    }

    @Test
    public void getVarsTest() {
        akitaScenario.setVar("1", "1");
        akitaScenario.setVar("2", "2");
        ScopedVariables scopedVariables = akitaScenario.getVars();
        assertThat((String) scopedVariables.get("1") + (String) scopedVariables.get("2"),
            equalTo("12"));
    }

    @Test
    public void testGetPropertyOrStringVariableOrValueFromScopedVariable() {
        akitaScenario.setVar("akita.url", "shouldLoadMe");
        assertThat(akitaScenario.getPropertyOrStringVariableOrValue("akita.url"),
            equalTo("shouldLoadMe"));
    }

    @Test
    public void testGetPropertyOrStringVariableOrValueFromValue() {
        assertThat(akitaScenario.getPropertyOrStringVariableOrValue("getPropertyOrVariableOrValueTestValue"),
            equalTo("getPropertyOrVariableOrValueTestValue"));
    }

    @Test
    public void testGetPropertyOrStringVariableOrValueFromSystemVariable() {
        String propertyName = "akita.url";
        String expectedValue = "http://url";
        System.setProperty(propertyName, expectedValue);
        String actualValue = akitaScenario.getPropertyOrStringVariableOrValue(propertyName);
        System.clearProperty(propertyName);
        assertThat(actualValue, equalTo(expectedValue));
    }

    @Test
    public void testStringOrLoadFilePropertyOrDefault2() {
        assertThat(loadValueFromFileOrPropertyOrDefault("testScript"), equalTo("alert('privet');"));
    }

    @Test
    public void testScrollWhileElemNotFoundOnPagePositive() {
        wps.scrollWhileElemNotFoundOnPage("mockTagName");
    }

    @Test(expected = AssertionError.class)
    public void testScrollWhileElemNotFoundOnPageNegative() {
        wps.scrollWhileElemNotFoundOnPage("Кнопка Показать ещё");
    }

    @Test
    public void testScrollWhileElemWithTextNotFoundOnPagePositive() {
        wps.scrollWhileElemWithTextNotFoundOnPage("Serious testing page");
    }

    @Test(expected = AssertionError.class)
    public void testScrollWhileElemWithTextNotFoundOnPageNegative() {
        wps.scrollWhileElemWithTextNotFoundOnPage("Not serious testing page");
    }

    @Test
    public void testCheckIfValueFromVariableEqualPropertyVariablePositive(){
        akitaScenario.setVar("timeout","60000");
        wps.checkIfValueFromVariableEqualPropertyVariable("timeout","waitingAppearTimeout");
    }

    @Test(expected = AssertionError.class)
    public void testCheckIfValueFromVariableEqualPropertyVariableNegative(){
        akitaScenario.setVar("timeout","500");
        wps.checkIfValueFromVariableEqualPropertyVariable("timeout","waitingAppearTimeout");
    }

    @Test
    public void testScrollPageToElementPositive() {
        wps.scrollPageToElement("mockTagName");
    }

    @Test(expected = AssertionError.class)
    public void testScrollPageToElementNegative() {
        wps.scrollPageToElement("Кнопка Показать ещё");
    }

    @Test
    public void testTestScript() {
        wps.executeJsScript("HIDEnSHOW()");
        es.elementIsNotVisible("ul");
    }


    @Test
    public void testSwitchToTheNextTab() {
        executeJavaScript("window.open(\"RedirectionPage.html\")");
        dmbs.switchToTheNextTab();
        Assert.assertThat(getWebDriver().getTitle(), IsEqual.equalTo("RedirectionPage"));
        dmbs.switchToTheNextTab();
        Assert.assertThat(getWebDriver().getTitle(), IsEqual.equalTo("Title"));
    }

    @Test
    public void testCheckPageTitleSuccess() {
        dmbs.checkPageTitle("Title");
    }

    @Test(expected = AssertionError.class)
    public void testCheckPageTitleFailure() {
        dmbs.checkPageTitle("NoTitle");
    }


}
