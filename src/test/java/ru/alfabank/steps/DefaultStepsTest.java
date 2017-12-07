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
import cucumber.api.Scenario;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.ScopedVariables;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DefaultStepsTest {
    private static DefaultSteps ds;
    private static AkitaScenario akitaScenario;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        ds = new DefaultSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }

    @Before
    public void prepare() {
        ds.goToSelectedPageByLinkFromPropertyFile("AkitaPageMock", akitaScenario.getVar("Page").toString());
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
        ds.checkCurrentURL(akitaScenario.getVar("Page").toString());
    }

    @Test(expected = NullPointerException.class)
    public void checkCurrentURLNegative() {
        ds.checkCurrentURL(null);
    }

    @Ignore
    @Test
    public void setWindowSizeSimple() {
        Dimension expectedDimension = new Dimension(800, 600);
        ds.setWindowSize("800", "600");
        Dimension actualDimension = WebDriverRunner.getWebDriver().manage().window().getSize();
        assertThat(expectedDimension, equalTo(actualDimension));
    }

    @Test
    public void storeFieldValueInVariablePositive() {
        String varName = "mockId";
        ds.storeElementValueInVariable(varName, varName);
        assertThat(akitaScenario.getVar(varName), equalTo("Serious testing page"));
    }

    @Test(expected = AssertionError.class)
    public void compareTwoDigitVarsNegative() {
        String number1Name = "number1", number1Value = "1234567890";
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567894";
        akitaScenario.setVar(number2Name, number2Value);

        ds.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void compareTwoDigitVars() {
        String number1Name = "number1", number1Value = "1234567890.97531";
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567890.97531";
        akitaScenario.setVar(number2Name, number2Value);

        ds.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void saveValueToVarPositive() {
        ds.saveValueToVar("testVar", "test");
        assertThat(akitaScenario.getVar("test"), equalTo("customTestValue"));
    }

    @Test
    public void clickOnElementPositive() {
        ds.clickOnElement("GoodButton");
        assertThat(akitaScenario.getPage("AkitaPageMock").getElement("GoodButton").isEnabled(),
                equalTo(false));
    }

    @Test
    public void elemIsPresentedOnPagePositive() {
        ds.elemIsPresentedOnPage("mockTagName");
    }

    @Test
    public void listIsPresentedOnPageTest() {
        ds.listIsPresentedOnPage("List");
    }

    @Test
    public void loadPageSimple() {
        ds.loadPage("AkitaPageMock");
    }

    @Test
    public void compareFieldAndVariablePositive() {
        akitaScenario.setVar("test", "Serious testing page");
        ds.compareFieldAndVariable("mockTagName", "test");
    }

    @Test
    public void checkIfListContainsValueFromFieldPositive() {
        List<String> list = new ArrayList<>();
        list.add("Serious testing page");
        akitaScenario.setVar("list", list);
        ds.checkIfListContainsValueFromField("list", "mockTagName");
    }

    @Test(expected = ElementShouldNot.class)
    public void blockDisappearedSimple() {
        ds.blockDisappeared("AkitaPageMock");
    }

    @Test
    public void pushButtonOnKeyboardSimple() {
        ds.pushButtonOnKeyboard("alt");
    }

    @Test
    public void setFieldValuePositive() {
        ds.setFieldValue("NormalField", "test");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField"),
                equalTo("test"));
    }

    @Test
    public void setFieldValuePositiveWithProps() {
        ds.setFieldValue("NormalField", "testValueInProps");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField"),
                equalTo("test"));
    }

    @Test
    public void cleanFieldPositive() {
        ds.cleanField("TextField");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("TextField"),
                equalTo(""));
    }

    @Test
    public void fieldInputIsEmptyPositive() {
        ds.fieldInputIsEmpty("NormalField");
    }

    @Test
    public void checkIfListConsistsOfTableElementsTest() {
        ArrayList<String> types = new ArrayList<>();
        types.add("One");
        types.add("Two");
        types.add("Three");
        ds.checkIfListConsistsOfTableElements("List", types);
    }

    @Test
    public void checkIfSelectedListElementMatchesValueTest() {
        ds.checkIfSelectedListElementMatchesValue("List", "One");
    }

    @Test
    public void checkIfSelectedListElementMatchesValueWithProps() {
        ds.checkIfSelectedListElementMatchesValue("List", "oneValueInProps");
    }

    @Test
    public void selectElementInListIfFoundByTextTest() {
        ds.selectElementInListIfFoundByText("List2", "item2");
    }

    @Test
    public void selectElementInListIfFoundByTextTestWithProps() {
        ds.selectElementInListIfFoundByText("List2", "item2ValueInProps");
    }

    @Test
    public void expressionExpressionPositive() {
        ds.expressionExpression("\"test\".equals(\"test\")");
    }

    @Test
    public void urlClickAndCheckRedirectionPositive() {
        ds.urlClickAndCheckRedirection("RedirectionPage", "Link");
    }

    @Test
    public void elementIsNotVisiblePositive() {
        ds.elementIsNotVisible("HiddenDiv");
    }

    @Test
    public void checkElemContainsAtrWithValuePositive() {
        ds.checkElemContainsAtrWithValue("SUPERBUTTON", "onclick", "HIDEnSHOW()");
    }

    @Test
    public void testFieldContainsMessageTextTest() {
ds.testFieldContainsMessageText("DisabledButton", "Disabled :p");
    }

    @Test
    public void testActualValueContainsSubstringPositive() {
        ds.testActualValueContainsSubstring("TextField", "xt");
    }

    @Test
    public void testActualValueContainsSubstringPositiveWithProps() {
        ds.testActualValueContainsSubstring("TextField", "textValueInProps");
    }

    @Test
    public void buttonIsNotActivePositive() {
        ds.buttonIsNotActive("DisabledButton");
    }

    @Test
    public void fieldIsDisablePositive() {
        ds.fieldIsDisable("DisabledField");
    }

    @Test
    public void compareListFromUIAndFromVariableTest() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One");
        arrayList.add("Two");
        arrayList.add("Three");
        akitaScenario.setVar("qwerty", arrayList);
        ds.compareListFromUIAndFromVariable("List", "qwerty");
    }

    @Test
    public void openReadOnlyFormPositive() {
        ds.goToSelectedPageByLinkFromPropertyFile("RedirectionPage",
                akitaScenario.getVar("RedirectionPage").toString());
        ds.openReadOnlyForm();
    }

    @Test
    public void addValuePositive() {
        ds.addValue("TextField", "Super");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("TextField"),
                equalTo("textSuper"));
    }

    @Test
    public void addValuePositiveWithProps() {
        ds.addValue("TextField", "itemValueInProps");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("TextField"),
                equalTo("textitem"));
    }

    @Test
    public void findElementPositive() {
        ds.findElement("LINK");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().getCurrentUrl(),
                equalTo(akitaScenario.getVar("RedirectionPage")));
    }

    @Test
    public void currentDatePositive() {
        ds.currentDate("NormalField", "dd.MM.yyyy");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField")
                        .matches("[0-3][0-9].[0-1][0-9].[0-2][0-9]{3}"),
                equalTo(true));
    }

    @Test
    public void refreshPageSimple() {
        ds.refreshPage();
    }

    @Test
    public void elementHoverTest() {
        ds.elementHover("NormalField");
    }

    @Test
    public void clickableFieldTest() {
        ds.clickableField("SUPERBUTTON");
    }

    @Test
    public void scrollDownSimple() {
        ds.scrollDown();
    }

    @Test
    public void loginByUserDataPositive() {
        ds.loginByUserData("testUser");
    }

    @Test
    public void pasteValuePositive() {
        ds.pasteValueToTextField("testVal", "NormalField");
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("normalField")).getAttribute("value"),
                equalTo("testVal"));
    }

    @Test
    public void pasteValuePositiveWithProps() {
        ds.pasteValueToTextField("textValueInProps", "NormalField");
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("normalField")).getAttribute("value"),
                equalTo("text"));
    }

    @Test
    public void elementDisapperaredAndAppearedComplex() {
        ds.testElementAppeared("ul", 1);
        ds.clickOnElement("SUPERBUTTON");
        ds.elemDisappered("ul");
    }

    @Test
    public void goToUrl() {
        ds.goToUrl((String) akitaScenario.getVar("RedirectionPage"));
    }

    @Test
    public void compareValInFieldAndFromStepTest() {
        ds.compareValInFieldAndFromStep("ul", "Serious testing page");
    }

    @Test
    public void setVariableTest() {
        ds.setVariable("ul", "Serious testing page");
        assertThat(akitaScenario.getVar("ul"), equalTo("Serious testing page"));
    }

    @Test
    public void compareValInFieldAndFromStepTestWithProps() {
        ds.compareValInFieldAndFromStep("ul", "testingPageTextProps");
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
    public void selectElementNumberFromListMinBorder() {
        ds.selectElementNumberFromList(1, "List");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void selectElementNumberFromListUnderMinBorder() {
        ds.selectElementNumberFromList(0, "List");
    }

    @Test()
    public void selectElementNumberFromListMaxBorder() {
        ds.selectElementNumberFromList(3, "List");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void selectElementNumberFromListOverMaxBorder() {
        ds.selectElementNumberFromList(4, "List");
    }

    @Test
    public void selectRandomElementFromListPositive() {
        ds.selectRandomElementFromList("List");
    }

    @Test
    public void checkListElementsContainsTextPositive() {
        ds.checkListElementsContainsText("List2", "item");
    }

    @Test
    public void checkListElementsContainsTextPositiveWithProps() {
        ds.checkListElementsContainsText("List2", "itemValueInProps");
    }

    @Test(expected = AssertionError.class)
    public void checkListElementsContainsTextNegative() {
        ds.checkListElementsContainsText("List2", "item1");
    }


}