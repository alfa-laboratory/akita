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
import ru.alfabank.steps.DefaultManageBrowserSteps;
import ru.alfabank.tests.core.helpers.PropertyLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;


public class ListStepsTest {

    private static ListSteps ls;
    private static AkitaScenario akitaScenario;
    private static DefaultManageBrowserSteps dmbs;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        ls = new ListSteps();
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
    public void listIsPresentedOnPageTest() {
        ls.listIsPresentedOnPage("List");
    }

    @Test
    public void checkIfListContainsValueFromFieldPositive() {
        List<String> list = new ArrayList<>();
        list.add("Serious testing page");
        akitaScenario.setVar("list", list);
        ls.checkIfListContainsValueFromField("list", "mockTagName");
    }

    @Test
    public void checkIfListConsistsOfTableElementsTest() {
        ArrayList<String> types = new ArrayList<>();
        types.add("One");
        types.add("Two");
        types.add("Three");
        ls.checkIfListConsistsOfTableElements("List", types);
    }

    @Test
    public void checkIfSelectedListElementMatchesValueTest() {
        ls.checkIfSelectedListElementMatchesValue("List", "One");
    }

    @Test
    public void checkIfSelectedListElementMatchesValueWithProps() {
        ls.checkIfSelectedListElementMatchesValue("List", "oneValueInProps");
    }

    @Test
    public void selectElementInListIfFoundByTextTestPositive() {
        ls.selectElementInListIfFoundByText("List2", "item2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectElementInListIfFoundByTextTestNegative() {
        ls.selectElementInListIfFoundByText("List2", "item5");
    }

    @Test
    public void selectElementInListIfFoundByTextTestPositiveWithProps() {
        ls.selectElementInListIfFoundByText("List2", "item2ValueInProps");
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectElementInListIfFoundByTextTestNegativeWithProps() {
        ls.selectElementInListIfFoundByText("List2", "item5ValueInProps");
    }

    @Test
    public void testCompareListFromUIAndFromVariablePositive() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One");
        arrayList.add("Two");
        arrayList.add("Three");
        akitaScenario.setVar("qwerty", arrayList);
        ls.compareListFromUIAndFromVariable("List", "qwerty");
    }

    @Test(expected = AssertionError.class)
    public void testCompareListFromUIAndFromVariableNegative() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Ten");
        arrayList.add("One");
        arrayList.add("Two");
        arrayList.add("Three");
        akitaScenario.setVar("qwerty", arrayList);
        ls.compareListFromUIAndFromVariable("List", "qwerty");
    }

    @Test
    public void selectElementNumberFromListMinBorder() {
        ls.selectElementNumberFromList(1, "List");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void selectElementNumberFromListUnderMinBorder() {
        ls.selectElementNumberFromList(0, "List");
    }

    @Test()
    public void selectElementNumberFromListMaxBorder() {
        ls.selectElementNumberFromList(3, "List");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void selectElementNumberFromListOverMaxBorder() {
        ls.selectElementNumberFromList(4, "List");
    }

    @Test
    public void selectRandomElementFromListPositive() {
        ls.selectRandomElementFromList("List");
    }

    @Test
    public void selectRandomElementFromListAndSaveVarPositive() {
        ls.selectRandomElementFromListAndSaveVar("List", "test");
        assertThat(akitaScenario.tryGetVar("test"), anyOf(equalTo("One"),
            equalTo("Two"), equalTo("Three")));
    };

    @Test(expected = IllegalArgumentException.class)
    public void selectRandomElementFromListAndSaveVarNegative() {
        ls.selectRandomElementFromListAndSaveVar("NormalField", "test");
    }

    @Test
    public void testCheckListElementsContainsTextPositive() {
        ls.checkListElementsContainsText("List2", "item");
    }

    @Test
    public void testCheckListElementsContainsTextPositiveWithProps() {
        ls.checkListElementsContainsText("List2", "itemValueInProps");
    }

    @Test(expected = AssertionError.class)
    public void testCheckListElementsContainsTextNegative() {
        ls.checkListElementsContainsText("List2", "item1");
    }

    @Test
    public void testCheckListElementsNotContainsTextPositive() {
        ls.checkListElementsNotContainsText("List2", "item1");
    }

    @Test(expected = AssertionError.class)
    public void testCheckListElementsNotContainsTextNegative() {
        ls.checkListElementsNotContainsText("List2", "item");
    }

    @Test(expected = AssertionError.class)
    public void testCheckListElementsNotContainsTextNegativeWithProps() {
        ls.checkListElementsNotContainsText("List2", "itemValueInProps");
    }

    @Test
    public void checkIfListInnerTextConsistsOfTableElements() {
        ArrayList<String> types = new ArrayList<>();
        types.add("One 1");
        types.add("Two 2");
        types.add("Three 3");
        ls.checkIfListInnerTextConsistsOfTableElements("List3", types);
    }

    @Test()
    public void testListInnerTextCorrespondsToListFromVariable() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One 1");
        arrayList.add("Two 2");
        arrayList.add("Three 3");
        akitaScenario.setVar("qwerty", arrayList);
        ls.checkListInnerTextCorrespondsToListFromVariable("List3", "qwerty");
    }

    @Test(expected = AssertionError.class)
    public void testListInnerTextCorrespondsToListFromVariableNegativeSize() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One 1");
        arrayList.add("Two 2");
        arrayList.add("Three 3");
        arrayList.add("One 1");
        akitaScenario.setVar("qwerty", arrayList);
        ls.checkListInnerTextCorrespondsToListFromVariable("List3", "qwerty");
    }

    @Test
    public void testGetPropertyOrStringVariableOrValueFromProperty() {
        akitaScenario.setVar("testVar", "shouldNotLoadMe");
        assertThat(akitaScenario.getPropertyOrStringVariableOrValue("testVar"),
            equalTo(PropertyLoader.loadProperty("testVar")));
    }

    @Test
    public void testCheckListTextsByRegExpPositive() {
        ls.checkListTextsByRegExp("List", "[A-z]*");
    }

    @Test(expected = AssertionError.class)
    public void testCheckListTextsByRegExpNegative() {
        ls.checkListTextsByRegExp("List", "[0-9]*");
    }

    @Test
    public void testListContainsNumberOfElementsPositive() {
        ls.listContainsNumberOfElements("List", 3);
    }

    @Test(expected = AssertionError.class)
    public void testListContainsNumberOfElementsNegative() {
        ls.listContainsNumberOfElements("List", 4);
    }

    @Test
    public void testListContainsNumberOfElementsOrContainsFromVariablePositive() {
        ls.listContainsNumberFromVariable("List", "3");
    }

    @Test
    public void testListContainsNumberOfElementsOrContainsFromVariableAnotherPositive() {
        akitaScenario.setVar("variable", "3");
        ls.listContainsNumberFromVariable("List", "variable");
    }

    @Test
    public void testListContainsNumberOfElementsOrContainsFromVariableOneMorePositive() {
        ls.listContainsNumberFromVariable("List", "var3");
    }

    @Test(expected = AssertionError.class)
    public void testListContainsNumberOfElementsOrContainsFromVariableNegative() {
        ls.listContainsNumberFromVariable("List", "4");
    }

    @Test
    public void testListContainsMoreOrLessElementsLessPositive(){
        ls.listContainsMoreOrLessElements("List", "менее", 4);
    }

    @Test
    public void testListContainsMoreOrLessElementsMorePositive(){
        ls.listContainsMoreOrLessElements("List", "более", 2);
    }

    @Test(expected = AssertionError.class)
    public void testListContainsMoreOrLessElementsLessNegative(){
        ls.listContainsMoreOrLessElements("List", "менее", 3);
    }

    @Test(expected = AssertionError.class)
    public void testListContainsMoreOrLessElementsMoreNegative(){
        ls.listContainsMoreOrLessElements("List", "более", 3);
    }

}
