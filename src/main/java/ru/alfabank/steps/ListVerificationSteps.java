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

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.en.When;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.FindBy;
import ru.alfabank.alfatest.cucumber.annotations.Name;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$$;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.alfabank.tests.core.helpers.PropertyLoader.getPropertyOrValue;

/**
 * Шаги для взаимодействия с коллекциями элементов, доступные по умолчанию в каждом новом проекте
 */

@Slf4j
public class ListVerificationSteps extends BaseMethods {

    /**
     * Проверка появления списка на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     */
    @Тогда("^список \"([^\"]*)\" отображается на странице$")
    @When("^list \"([^\"]*)\" is visible$")
    public void listIsPresentedOnPage(String elementName) {
        ElementsCollection elements = akitaScenario.getCurrentPage().getElementsList(elementName);
        akitaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, DEFAULT_TIMEOUT, elements);
    }

    /**
     * Проверка того, что значение из поля содержится в списке,
     * полученном из хранилища переменных по заданному ключу
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список из переменной \"([^\"]*)\" содержит значение (?:поля|элемента) \"([^\"]*)\"$")
    @When("^list from the variable \"([^\"]*)\" contains the value of the (?:field|element) \"([^\"]*)\"$")
    public void checkIfListContainsValueFromField(String variableListName, String elementName) {
        String actualValue = akitaScenario.getCurrentPage().getAnyElementText(elementName);
        List<String> listFromVariable = ((List<String>) akitaScenario.getVar(variableListName));
        assertTrue(listFromVariable.contains(actualValue),
                String.format("Список из переменной [%s] не содержит значение поля [%s]", variableListName, elementName));
    }

    /**
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод getText()
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов таблицы$")
    @When("^list named \"([^\"]*)\" contains elements from the table$")
    public void checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = akitaScenario.getCurrentPage().getAnyElementsListInnerTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), textTable, hasSize(numberOfTypes));
        assertTrue(actualValues.containsAll(textTable),
                String.format("Значения элементов в списке %s: %s не совпадают с ожидаемыми значениями из таблицы %s", listName, actualValues, textTable));
    }

    /**
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" со страницы совпадает со списком \"([^\"]*)\"$")
    @When("^list named \"([^\"]*)\" from current page matches \"([^\"]*)\" list$")
    public void compareListFromUIAndFromVariable(String listName, String listVariable) {
        HashSet<String> expectedList = new HashSet<>((List<String>) akitaScenario.getVar(listVariable));
        HashSet<String> actualList = new HashSet<>(akitaScenario.getCurrentPage().getAnyElementsListTexts(listName));
        assertThat(String.format("Список со страницы [%s] не совпадает с ожидаемым списком из переменной [%s]", listName, listVariable), actualList, equalTo(expectedList));
    }

    /**
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     * Не чувствителен к регистру
     */
    @Тогда("^элементы списка \"([^\"]*)\" содержат текст \"([^\"]*)\"$")
    @When("^elements of the \"([^\"]*)\" list contain text \"([^\"]*)\"$")
    public void checkListElementsContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim().toLowerCase())
                .collect(toList());
        assertTrue(elementsListText.stream().allMatch(item -> item.contains(value.toLowerCase())),
                String.format("Элемены списка %s: [%s] не содержат текст [%s] ", listName, elementsListText, value));
    }

    /**
     * Проверка, что каждый элемент списка не содержит ожидаемый текст
     */
    @Тогда("^элементы списка \"([^\"]*)\" не содержат текст \"([^\"]*)\"$")
    @When("^elements of the \"([^\"]*)\" list do not contain text \"([^\"]*)\"$")
    public void checkListElementsNotContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
                .map(element -> element.getText().trim().toLowerCase())
                .collect(toList());
        assertFalse(elementsListText.stream().allMatch(item -> item.contains(value.toLowerCase())),
                String.format("Элемены списка %s: [%s] содержат текст [%s] ", listName, elementsListText, value));
    }

    /**
     * Проход по списку и проверка текста у элемента на соответствие формату регулярного выражения
     */
    @И("элементы списка \"([^\"]*)\" соответствуют формату \"([^\"]*)\"$")
    @When("^elements of the \"([^\"]*)\" list match the \"([^\"]*)\" format$")
    public void checkListTextsByRegExp(String listName, String pattern) {
        akitaScenario.getCurrentPage().getElementsList(listName).forEach(element -> {
            String str = akitaScenario.getCurrentPage().getAnyElementText(element);
            assertTrue(isTextMatches(str, pattern),
                    String.format("Текст '%s' из списка '%s' не соответствует формату регулярного выражения", str, listName));
        });
    }

    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @Тогда("^в списке \"([^\"]*)\" содержится (\\d+) (?:элемент|элементов|элемента)")
    @When("^list named \"([^\"]*)\" contains (\\d+) element(|s)$")
    public void listContainsNumberOfElements(String listName, int quantity) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        assertTrue(listOfElementsFromPage.size() == quantity,
                String.format("Число элементов в списке отличается от ожидаемого: %s", listOfElementsFromPage.size()));
    }

    /**
     * Производится проверка соответствия числа элементов списка значению из property файла, из переменной сценария или указанному в шаге
     */
    @Тогда("^в списке \"([^\"]*)\" содержится количество элементов, равное значению из переменной \"([^\"]*)\"")
    @When("^list named \"([^\"]*)\" contains the number of elements equal to the value of the \"([^\"]*)\" variable$")
    public void listContainsNumberFromVariable(String listName, String quantity) {
        int numberOfElements = getCounterFromString(getPropertyOrStringVariableOrValue(quantity));
        listContainsNumberOfElements(listName, numberOfElements);
    }

    /**
     * Производится сопоставление числа элементов списка и значения, указанного в шаге
     */
    @Тогда("^в списке \"([^\"]*)\" содержится (более|менее) (\\d+) (?:элементов|элемента)")
    @When("^list named \"([^\"]*)\" contains (more|less) then (\\d+) element(?:|s)$")
    public void listContainsMoreOrLessElements(String listName, String moreOrLess, int quantity) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        if ("более".equals(moreOrLess)) {
            assertTrue(listOfElementsFromPage.size() > quantity,
                    String.format("Число элементов списка меньше ожидаемого: %s", listOfElementsFromPage.size()));
        } else
            assertTrue(listOfElementsFromPage.size() < quantity,
                    String.format("Число элементов списка превышает ожидаемое: %s", listOfElementsFromPage.size()));
    }

    /**
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     * Для получения текста из элементов списка используется метод innerText()
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" на странице совпадает со списком \"([^\"]*)\"$")
    @When("^list named \"([^\"]*)\" on page matches with the \"([^\"]*)\" list$")
    public void checkListInnerTextCorrespondsToListFromVariable(String listName, String listVariable) {
        List<String> expectedList = new ArrayList<>((List<String>) akitaScenario.getVar(listVariable));
        List<String> actualList = new ArrayList<>(akitaScenario.getCurrentPage().getAnyElementsListInnerTexts(listName));
        assertThat(String.format("Количество элементов списка %s = %s, ожидаемое значение = %s", listName, actualList.size(), expectedList.size()), actualList,
                hasSize(expectedList.size()));
        assertThat(String.format("Список со страницы %s: %s не совпадает с ожидаемым списком из переменной %s:%s", listName, actualList, listVariable, expectedList)
                , actualList, containsInAnyOrder(expectedList.toArray()));
    }
}