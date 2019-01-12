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
    private static ElementsVerificationSteps elis;
    private static InputInteractionSteps iis;


    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        wpis = new WebPageInteractionSteps();
        wpvs = new WebPageVerificationSteps();
        elis = new ElementsVerificationSteps();
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

    @Ignore
    @Test
    public void setWindowSizeSimple() {
        Dimension expectedDimension = new Dimension(800, 600);
        wpis.setBrowserWindowSize(800, 600);
        Dimension actualDimension = WebDriverRunner.getWebDriver().manage().window().getSize();
        assertThat(expectedDimension, equalTo(actualDimension));
    }

    @Test(expected = AssertionError.class)
    public void compareTwoDigitVarsNegative() {
        String number1Name = "number1", number1Value = "1234567890";
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567894";
        akitaScenario.setVar(number2Name, number2Value);

        wpvs.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void compareTwoDigitVars() {
        String number1Name = "number1", number1Value = "1234567890.97531";
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567890.97531";
        akitaScenario.setVar(number2Name, number2Value);

        wpvs.compareTwoVariables(number1Name, number2Name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareTwoDigitVarsAnotherNegative() {
        String number1Name = "number1", number1Value = null;
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = null;
        akitaScenario.setVar(number2Name, number2Value);

        wpvs.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void testCheckingTwoVariablesAreNotEqualsPositive() {
        String variable1Name = "number1";
        int variable1Value = 666;
        akitaScenario.setVar(variable1Name, variable1Value);

        String variable2Name = "number2";
        int variable2Value = 123;
        akitaScenario.setVar(variable2Name, variable2Value);
        wpvs.checkingTwoVariablesAreNotEquals(variable1Name, variable2Name);
    }

    @Test(expected = AssertionError.class)
    public void testCheckingTwoVariablesAreNotEqualsNegative() {
        String variable1Name = "number1";
        int variable1Value = 666;
        akitaScenario.setVar(variable1Name, variable1Value);

        String variable2Name = "number2";
        int variable2Value = 666;
        akitaScenario.setVar(variable2Name, variable2Value);
        wpvs.checkingTwoVariablesAreNotEquals(variable1Name, variable2Name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckingTwoVariablesAreNotEqualsAnotherNegative() {
        String variable1Name = "number1", variable1Value = null;
        akitaScenario.setVar(variable1Name, variable1Value);

        String variable2Name = "number2", variable2Value = null;
        akitaScenario.setVar(variable2Name, variable2Value);
        wpvs.checkingTwoVariablesAreNotEquals(variable1Name, variable2Name);
    }

    @Test
    public void saveValueToVarPositive() {
        wpis.saveValueToVar("testVar", "test");
        assertThat(akitaScenario.getVar("test"), equalTo("customTestValue"));
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
    public void testCheckIfValueFromVariableEqualPropertyVariablePositive() {
        akitaScenario.setVar("timeout", "1000");
        wpvs.checkIfValueFromVariableEqualPropertyVariable("timeout", "waitingAppearTimeout");
    }

    @Test(expected = AssertionError.class)
    public void testCheckIfValueFromVariableEqualPropertyVariableNegative() {
        akitaScenario.setVar("timeout", "500");
        wpvs.checkIfValueFromVariableEqualPropertyVariable("timeout", "waitingAppearTimeout");
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

    @Test
    public void testfillTemplate() {
        String templateName = "strTemplate";
        String varName = "varName";
        List<String> row1 = new ArrayList<>(Arrays.asList("_name_", "Jack"));
        List<String> row2 = new ArrayList<>(Arrays.asList("_age_", "35"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        allLists.add(row2);
        DataTable dataTable = dataTableFromLists(allLists);

        wpis.fillTemplate(templateName, varName, dataTable);
        Assert.assertEquals("{\"name\": \"Jack\", \"age\": 35}", (String) akitaScenario.getVar(varName));
    }

    @Test(expected = ElementShouldNot.class)
    public void blockDisappearedSimple() {
        wpvs.blockDisappeared("AkitaPageMock");
    }

    @Test
    public void pushButtonOnKeyboardSimple() {
        wpis.pushButtonOnKeyboard("alt");
    }

    @Test
    public void goToUrl() {
        wpis.goToUrl((String) akitaScenario.getVar("RedirectionPage"));
    }

    @Test
    public void setVariableTest() {
        wpis.setVariable("ul", "Serious testing page");
        assertThat(akitaScenario.getVar("ul"), equalTo("Serious testing page"));
    }

    @Test
    public void expressionExpressionPositive() {
        wpvs.expressionExpression("\"test\".equals(\"test\")");
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
    public void testTestScript() {
        wpis.executeJsScript("HIDEnSHOW()");
        elis.elementIsNotVisible("ul");
    }


}
