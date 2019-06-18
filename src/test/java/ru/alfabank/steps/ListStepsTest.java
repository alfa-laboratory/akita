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
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListStepsTest {

    private static AkitaScenario akitaScenario;
    private static WebPageInteractionSteps wpis;
    private static ListInteractionSteps lis;
    private static ListVerificationSteps lvs;

    @BeforeAll
    static void setup() {
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

    @BeforeEach
    void prepare() {
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterAll
    static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    void listIsPresentedOnPageTest() {
        lvs.listIsPresentedOnPage("List");
    }

    @Test
    void checkIfSelectedListElementMatchesValueTest() {
        lis.checkIfSelectedListElementMatchesValue("List", "One");
    }

    @Test
    void checkIfSelectedListElementMatchesValueWithProps() {
        lis.checkIfSelectedListElementMatchesValue("List", "oneValueInProps");
    }

    @Test
    void selectElementInListIfFoundByTextTestPositive() {
        lis.selectElementInListIfFoundByText("List2", "item2");
    }

    @Test
    void selectElementInListIfFoundByTextTestNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                lis.selectElementInListIfFoundByText("List2", "item5"));
    }

    @Test
    void selectElementInListIfFoundByTextTestPositiveWithProps() {
        lis.selectElementInListIfFoundByText("List2", "item2ValueInProps");
    }

    @Test
    void selectElementInListIfFoundByTextTestNegativeWithProps() {
        assertThrows(IllegalArgumentException.class, () ->
                lis.selectElementInListIfFoundByText("List2", "item5ValueInProps"));
    }

    @Test
    void testListContainsMoreOrLessElementsLessPositive() {
        lvs.listContainsMoreOrLessElements("List", "менее", 4);
    }

    @Test
    void testListContainsMoreOrLessElementsMorePositive() {
        lvs.listContainsMoreOrLessElements("List", "более", 2);
    }

    @Test
    void testListContainsMoreOrLessElementsLessNegative() {
        assertThrows(AssertionError.class, () ->
                lvs.listContainsMoreOrLessElements("List", "менее", 3));
    }

    @Test
    void testListContainsMoreOrLessElementsMoreNegative() {
        assertThrows(AssertionError.class, () ->
                lvs.listContainsMoreOrLessElements("List", "более", 3));
    }

    @Test
    void testListContainsNumberOfElementsOrContainsFromVariablePositive() {
        lvs.listContainsNumberFromVariable("List", "3");
    }

    @Test
    void testListContainsNumberOfElementsOrContainsFromVariableAnotherPositive() {
        akitaScenario.setVar("variable", "3");
        lvs.listContainsNumberFromVariable("List", "variable");
    }

    @Test
    void testListContainsNumberOfElementsOrContainsFromVariableOneMorePositive() {
        lvs.listContainsNumberFromVariable("List", "var3");
    }

    @Test
    void testListContainsNumberOfElementsOrContainsFromVariableNegative() {
        assertThrows(AssertionError.class, () ->
                lvs.listContainsNumberFromVariable("List", "4"));
    }

    @Test
    void testCheckListTextsByRegExpPositive() {
        lvs.checkListTextsByRegExp("List", "[A-z]*");
    }

    @Test
    void testCheckListTextsByRegExpNegative() {
        assertThrows(AssertionError.class, () ->
                lvs.checkListTextsByRegExp("List", "[0-9]*"));
    }

    @Test
    void testListContainsNumberOfElementsPositive() {
        lvs.listContainsNumberOfElements("List", 3);
    }

    @Test
    void testListContainsNumberOfElementsNegative() {
        assertThrows(AssertionError.class, () ->
                lvs.listContainsNumberOfElements("List", 4));
    }

    @Test
    void checkIfListContainsValueFromFieldPositive() {
        List<String> list = new ArrayList<>();
        list.add("Serious testing page");
        akitaScenario.setVar("list", list);
        lvs.checkIfListContainsValueFromField("list", "mockTagName");
    }

    @Test
    void selectElementNumberFromListMinBorder() {
        lis.selectElementNumberFromList(1, "List");
    }

    @Test
    void selectElementNumberFromListUnderMinBorder() {
        assertThrows(IndexOutOfBoundsException.class, () ->
                lis.selectElementNumberFromList(0, "List"));
    }

    @Test
    void selectElementNumberFromListMaxBorder() {
        lis.selectElementNumberFromList(3, "List");
    }

    @Test
    void selectElementNumberFromListOverMaxBorder() {
        assertThrows(IndexOutOfBoundsException.class, () ->
                lis.selectElementNumberFromList(4, "List"));
    }

    @Test
    void selectRandomElementFromListPositive() {
        lis.selectRandomElementFromList("List");
    }

    @Test
    void selectRandomElementFromListAndSaveVarNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                lis.selectRandomElementFromListAndSaveVar("NormalField", "test"));
    }

    @Test
    void testCheckListElementsContainsTextPositive() {
        lvs.checkListElementsContainsText("List2", "item");
    }

    @Test
    void testCheckListElementsContainsTextPositiveWithProps() {
        lvs.checkListElementsContainsText("List2", "itemValueInProps");
    }

    @Test
    void testCheckListElementsContainsTextNegative() {
        assertThrows(AssertionError.class, () ->
                lvs.checkListElementsContainsText("List2", "item1"));
    }

    @Test
    void testCheckListElementsNotContainsTextPositive() {
        lvs.checkListElementsNotContainsText("List2", "item1");
    }

    @Test
    void testCheckListElementsNotContainsTextNegative() {
        assertThrows(AssertionError.class, () ->
                lvs.checkListElementsNotContainsText("List2", "item"));
    }

    @Test
    void selectRandomElementFromListAndSaveVarPositive() {
        lis.selectRandomElementFromListAndSaveVar("List", "test");
        assertThat(akitaScenario.tryGetVar("test"), anyOf(equalTo("One"),
                equalTo("Two"), equalTo("Three")));
    }

    @Test
    void testCheckListElementsNotContainsTextNegativeWithProps() {
        assertThrows(AssertionError.class, () ->
                lvs.checkListElementsNotContainsText("List2", "itemValueInProps"));
    }

    @Test
    void checkIfListInnerTextConsistsOfTableElements() {
        ArrayList<String> types = new ArrayList<>();
        types.add("One 1");
        types.add("Two 2");
        types.add("Three 3");
        lvs.checkIfListInnerTextConsistsOfTableElements("List3", types);
    }

    @Test
    void checkIfListInnerTextConsistsOfTableElementsNegative() {
        ArrayList<String> types = new ArrayList<>();
        types.add("One 1");
        types.add("Two 2");
        types.add("Null");
        assertThrows(AssertionError.class, () ->
                lvs.checkIfListInnerTextConsistsOfTableElements("List3", types));
    }

    @Test
    void testListInnerTextCorrespondsToListFromVariable() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One 1");
        arrayList.add("Two 2");
        arrayList.add("Three 3");
        akitaScenario.setVar("qwerty", arrayList);
        lvs.checkListInnerTextCorrespondsToListFromVariable("List3", "qwerty");
    }

    @Test
    void testListInnerTextCorrespondsToListFromVariableNegativeSize() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One 1");
        arrayList.add("Two 2");
        arrayList.add("Three 3");
        arrayList.add("One 1");
        akitaScenario.setVar("qwerty", arrayList);
        assertThrows(AssertionError.class, () ->
                lvs.checkListInnerTextCorrespondsToListFromVariable("List3", "qwerty"));
    }

    @Test
    void testListInnerTextCorrespondsToListFromVariableNegative() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One 1");
        arrayList.add("Two 2");
        arrayList.add("Null");
        akitaScenario.setVar("qwerty", arrayList);
        assertThrows(AssertionError.class, () ->
                lvs.checkListInnerTextCorrespondsToListFromVariable("List3", "qwerty"));
    }

    @Test
    void testCompareListFromUIAndFromVariablePositive() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One");
        arrayList.add("Two");
        arrayList.add("Three");
        akitaScenario.setVar("qwerty", arrayList);
        lvs.compareListFromUIAndFromVariable("List", "qwerty");
    }

    @Test
    void testCompareListFromUIAndFromVariableNegative() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Ten");
        arrayList.add("One");
        arrayList.add("Two");
        arrayList.add("Three");
        akitaScenario.setVar("qwerty", arrayList);
        assertThrows(AssertionError.class, () ->
                lvs.compareListFromUIAndFromVariable("List", "qwerty"));
    }

    @Test
    void testListContainsNumberOfElementsOrContainsFromVariableMuchMorePositive() {
        lvs.listContainsNumberFromVariable("List", "Проверка комплаенса 3");
    }

    @Test
    void selectElementNumberFromListAndSaveToVarMinBorder() {
        lis.selectElementNumberFromListAndSaveToVar(1, "List", "varName");
        assertThat(akitaScenario.tryGetVar("varName"), equalTo("Three"));
    }

    @Test
    void selectElementNumberFromListAndSaveToVarUnderMinBorder() {
        assertThrows(IndexOutOfBoundsException.class, () ->
                lis.selectElementNumberFromListAndSaveToVar(0, "List", "varName"));
    }

    @Test
    void selectElementNumberFromListAndSaveToVarMaxBorder() {
        lis.selectElementNumberFromListAndSaveToVar(3, "List", "varName");
        assertThat(akitaScenario.tryGetVar("varName"), equalTo("Two"));
    }

    @Test
    void selectElementNumberFromListAndSaveToVarOverMaxBorder() {
        assertThrows(IndexOutOfBoundsException.class, () ->
                lis.selectElementNumberFromListAndSaveToVar(4, "List", "varName"));
    }
}