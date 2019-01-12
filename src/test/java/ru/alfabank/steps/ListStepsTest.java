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
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class ListStepsTest {

    private static AkitaScenario akitaScenario;
    private static WebPageInteractionSteps wpis;
    private static ListInteractionSteps lis;
    private static ListVerificationSteps lvs;




    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        wpis = new WebPageInteractionSteps();
        lis = new ListInteractionSteps();
        lvs = new ListVerificationSteps();
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
    public void listIsPresentedOnPageTest() {
        lvs.listIsPresentedOnPage("List");
    }

    @Test
    public void checkIfSelectedListElementMatchesValueTest() {
        lis.checkIfSelectedListElementMatchesValue("List", "One");
    }

    @Test
    public void checkIfSelectedListElementMatchesValueWithProps() {
        lis.checkIfSelectedListElementMatchesValue("List", "oneValueInProps");
    }

    @Test
    public void selectElementInListIfFoundByTextTestPositive() {
        lis.selectElementInListIfFoundByText("List2", "item2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectElementInListIfFoundByTextTestNegative() {
        lis.selectElementInListIfFoundByText("List2", "item5");
    }

    @Test
    public void selectElementInListIfFoundByTextTestPositiveWithProps() {
        lis.selectElementInListIfFoundByText("List2", "item2ValueInProps");
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectElementInListIfFoundByTextTestNegativeWithProps() {
        lis.selectElementInListIfFoundByText("List2", "item5ValueInProps");
    }

    @Test
    public void testListContainsMoreOrLessElementsLessPositive() {
        lvs.listContainsMoreOrLessElements("List", "менее", 4);
    }

    @Test
    public void testListContainsMoreOrLessElementsMorePositive() {
        lvs.listContainsMoreOrLessElements("List", "более", 2);
    }

    @Test(expected = AssertionError.class)
    public void testListContainsMoreOrLessElementsLessNegative() {
        lvs.listContainsMoreOrLessElements("List", "менее", 3);
    }

    @Test(expected = AssertionError.class)
    public void testListContainsMoreOrLessElementsMoreNegative() {
        lvs.listContainsMoreOrLessElements("List", "более", 3);
    }

    @Test
    public void testListContainsNumberOfElementsOrContainsFromVariablePositive() {
        lvs.listContainsNumberFromVariable("List", "3");
    }

    @Test
    public void testListContainsNumberOfElementsOrContainsFromVariableAnotherPositive() {
        akitaScenario.setVar("variable", "3");
        lvs.listContainsNumberFromVariable("List", "variable");
    }

    @Test
    public void testListContainsNumberOfElementsOrContainsFromVariableOneMorePositive() {
        lvs.listContainsNumberFromVariable("List", "var3");
    }

    @Test(expected = AssertionError.class)
    public void testListContainsNumberOfElementsOrContainsFromVariableNegative() {
        lvs.listContainsNumberFromVariable("List", "4");
    }

    @Test
    public void testCheckListTextsByRegExpPositive() {
        lvs.checkListTextsByRegExp("List", "[A-z]*");
    }

    @Test(expected = AssertionError.class)
    public void testCheckListTextsByRegExpNegative() {
        lvs.checkListTextsByRegExp("List", "[0-9]*");
    }

    @Test
    public void testListContainsNumberOfElementsPositive() {
        lvs.listContainsNumberOfElements("List", 3);
    }

    @Test(expected = AssertionError.class)
    public void testListContainsNumberOfElementsNegative() {
        lvs.listContainsNumberOfElements("List", 4);
    }

    @Test
    public void checkIfListContainsValueFromFieldPositive() {
        List<String> list = new ArrayList<>();
        list.add("Serious testing page");
        akitaScenario.setVar("list", list);
        lvs.checkIfListContainsValueFromField("list", "mockTagName");
    }

    @Test
    public void selectElementNumberFromListMinBorder() {
        lis.selectElementNumberFromList(1, "List");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void selectElementNumberFromListUnderMinBorder() {
        lis.selectElementNumberFromList(0, "List");
    }

    @Test
    public void selectElementNumberFromListMaxBorder() {
        lis.selectElementNumberFromList(3, "List");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void selectElementNumberFromListOverMaxBorder() {
        lis.selectElementNumberFromList(4, "List");
    }

    @Test
    public void selectRandomElementFromListPositive() {
        lis.selectRandomElementFromList("List");
    }

    @Test(expected = IllegalArgumentException.class)
    public void selectRandomElementFromListAndSaveVarNegative() {
        lis.selectRandomElementFromListAndSaveVar("NormalField", "test");
    }

    @Test
    public void testCheckListElementsContainsTextPositive() {
        lvs.checkListElementsContainsText("List2", "item");
    }

    @Test
    public void testCheckListElementsContainsTextPositiveWithProps() {
        lvs.checkListElementsContainsText("List2", "itemValueInProps");
    }

    @Test(expected = AssertionError.class)
    public void testCheckListElementsContainsTextNegative() {
        lvs.checkListElementsContainsText("List2", "item1");
    }

    @Test
    public void testCheckListElementsNotContainsTextPositive() {
        lvs.checkListElementsNotContainsText("List2", "item1");
    }

    @Test(expected = AssertionError.class)
    public void testCheckListElementsNotContainsTextNegative() {
        lvs.checkListElementsNotContainsText("List2", "item");
    }

    @Test
    public void selectRandomElementFromListAndSaveVarPositive() {
        lis.selectRandomElementFromListAndSaveVar("List", "test");
        assertThat(akitaScenario.tryGetVar("test"), anyOf(equalTo("One"),
                equalTo("Two"), equalTo("Three")));
    }

    ;


    @Test(expected = AssertionError.class)
    public void testCheckListElementsNotContainsTextNegativeWithProps() {
        lvs.checkListElementsNotContainsText("List2", "itemValueInProps");
    }

    @Test
    public void checkIfListInnerTextConsistsOfTableElements() {
        ArrayList<String> types = new ArrayList<>();
        types.add("One 1");
        types.add("Two 2");
        types.add("Three 3");
        lvs.checkIfListInnerTextConsistsOfTableElements("List3", types);
    }

    @Test(expected = AssertionError.class)
    public void checkIfListInnerTextConsistsOfTableElementsNegative() {
        ArrayList<String> types = new ArrayList<>();
        types.add("One 1");
        types.add("Two 2");
        types.add("Null");
        lvs.checkIfListInnerTextConsistsOfTableElements("List3", types);
    }

    @Test
    public void testListInnerTextCorrespondsToListFromVariable() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One 1");
        arrayList.add("Two 2");
        arrayList.add("Three 3");
        akitaScenario.setVar("qwerty", arrayList);
        lvs.checkListInnerTextCorrespondsToListFromVariable("List3", "qwerty");
    }

    @Test(expected = AssertionError.class)
    public void testListInnerTextCorrespondsToListFromVariableNegativeSize() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One 1");
        arrayList.add("Two 2");
        arrayList.add("Three 3");
        arrayList.add("One 1");
        akitaScenario.setVar("qwerty", arrayList);
        lvs.checkListInnerTextCorrespondsToListFromVariable("List3", "qwerty");
    }

    @Test(expected = AssertionError.class)
    public void testListInnerTextCorrespondsToListFromVariableNegative() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One 1");
        arrayList.add("Two 2");
        arrayList.add("Null");
        akitaScenario.setVar("qwerty", arrayList);
        lvs.checkListInnerTextCorrespondsToListFromVariable("List3", "qwerty");
    }

    @Test
    public void testCompareListFromUIAndFromVariablePositive() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One");
        arrayList.add("Two");
        arrayList.add("Three");
        akitaScenario.setVar("qwerty", arrayList);
        lvs.compareListFromUIAndFromVariable("List", "qwerty");
    }

    @Test(expected = AssertionError.class)
    public void testCompareListFromUIAndFromVariableNegative() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Ten");
        arrayList.add("One");
        arrayList.add("Two");
        arrayList.add("Three");
        akitaScenario.setVar("qwerty", arrayList);
        lvs.compareListFromUIAndFromVariable("List", "qwerty");
    }

    @Test
    public void getElementsListInBlockPositiveTest() {
        lis.getElementsList("AkitaTable", "Rows", "ListTable");
        assertNotNull(akitaScenario.getVar("ListTable"));
    }

    @Test
    public void getListElementsTextInBlockPositiveTest() {
        lis.getListElementsText("AkitaTable", "Rows", "ListTable");
        assertNotNull(akitaScenario.getVar("ListTable"));
    }

    @Test
    public void testListContainsNumberOfElementsOrContainsFromVariableMuchMorePositive() {
        lvs.listContainsNumberFromVariable("List", "Проверка комплаенса 3");
    }

    @Test
    public void selectElementNumberFromListAndSaveToVarMinBorder() {
        lis.selectElementNumberFromListAndSaveToVar(1, "List", "varName");
        assertThat(akitaScenario.tryGetVar("varName"), equalTo("Three"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void selectElementNumberFromListAndSaveToVarUnderMinBorder() {
        lis.selectElementNumberFromListAndSaveToVar(0, "List", "varName");
    }

    @Test
    public void selectElementNumberFromListAndSaveToVarMaxBorder() {
        lis.selectElementNumberFromListAndSaveToVar(3, "List", "varName");
        assertThat(akitaScenario.tryGetVar("varName"), equalTo("Two"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void selectElementNumberFromListAndSaveToVarOverMaxBorder() {
        lis.selectElementNumberFromListAndSaveToVar(4, "List", "varName");
    }



}
