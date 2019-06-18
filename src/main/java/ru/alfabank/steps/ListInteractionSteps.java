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

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.en.When;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Шаги для взаимодействия с коллекциями элементов, доступные по умолчанию в каждом новом проекте
 */

@Slf4j
public class ListInteractionSteps extends BaseMethods {

    /**
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Когда("^в списке \"([^\"]*)\" выбран элемент с (?:текстом|значением) \"(.*)\"$")
    @When("^selected element from the \"([^\"]*)\" list with (?:text|value) \"(.*)\"$")
    public void checkIfSelectedListElementMatchesValue(String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsText = listOfElementsFromPage.stream()
            .map(element -> element.getText().trim())
            .collect(toList());
        listOfElementsFromPage.stream()
            .filter(element -> element.getText().trim().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Элемент [%s] не найден в списке %s: [%s] ", value, listName, elementsText)))
            .click();
    }

    /**
     * Выбор из списка со страницы элемента, который содержит заданный текст
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     * Не чувствителен к регистру
     */
    @Когда("^в списке \"([^\"]*)\" выбран элемент содержащий текст \"([^\"]*)\"$")
    @When("^selected element from the \"([^\"]*)\" list that contains text \"([^\"]*)\"$")
    public void selectElementInListIfFoundByText(String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
            .map(element -> element.getText().trim().toLowerCase())
            .collect(toList());
        listOfElementsFromPage.stream()
            .filter(element -> element.getText().trim().toLowerCase().contains(value.toLowerCase()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Элемент [%s] не найден в списке %s: [%s] ", value, listName, elementsListText)))
            .click();
    }

    /**
     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
     */
    @Когда("^выбран любой элемент из списка \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\"$")
    @When("^random element in \"([^\"]*)\" list has been selected and its value has been saved to the \"([^\"]*)\" variable$")
    public void selectRandomElementFromListAndSaveVar(String listName, String varName) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        SelenideElement element = listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size()));
        element.shouldBe(Condition.visible).click();
        akitaScenario.setVar(varName, akitaScenario.getCurrentPage().getAnyElementText(element).trim());
        akitaScenario.write(String.format("Переменной [%s] присвоено значение [%s] из списка [%s]", varName,
            akitaScenario.getVar(varName), listName));
    }

    /**
     * Выбор n-го элемента из списка со страницы
     * Нумерация элементов начинается с 1
     */
    @Когда("^выбран (\\d+)-й элемент в списке \"([^\"]*)\"$")
    @When("^selected the (\\d+)(st|nd|rd|th) element from the \"([^\"]*)\" list$")
    public void selectElementNumberFromList(Integer elementNumber, String listName) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        SelenideElement elementToSelect;
        Integer selectedElementNumber = elementNumber - 1;
        if (selectedElementNumber < 0 || selectedElementNumber >= listOfElementsFromPage.size()) {
            throw new IndexOutOfBoundsException(
                String.format("В списке %s нет элемента с номером %s. Количество элементов списка = %s",
                    listName, elementNumber, listOfElementsFromPage.size()));
        }
        elementToSelect = listOfElementsFromPage.get(selectedElementNumber);
        elementToSelect.shouldBe(Condition.visible).click();
    }

    /**
     * Выбор из списка со страницы любого случайного элемента
     */
    @Когда("^выбран любой элемент в списке \"([^\"]*)\"$")
    @When("^random element in \"([^\"]*)\" list has been selected$")
    public void selectRandomElementFromList(String listName) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size()))
            .shouldBe(Condition.visible).click();
        akitaScenario.write("Выбран случайный элемент: " + listOfElementsFromPage);
    }

    /**
     * Выбор n-го элемента из списка со страницы и сохранение его значения в переменную
     * Нумерация элементов начинается с 1
     */
    @Тогда("^выбран (\\d+)-й элемент в списке \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\"$")
    @When("^selected the (\\d+)(st|nd|rd|th) element from the \"([^\"]*)\" list and its value has been saved to the \"([^\"]*)\" variable$")
    public void selectElementNumberFromListAndSaveToVar(Integer elementNumber, String listName, String varName) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        SelenideElement elementToSelect;
        Integer selectedElementNumber = elementNumber - 1;
        if (selectedElementNumber < 0 || selectedElementNumber >= listOfElementsFromPage.size()) {
            throw new IndexOutOfBoundsException(
                String.format("В списке %s нет элемента с номером %s. Количество элементов списка = %s",
                    listName, elementNumber, listOfElementsFromPage.size()));
        }
        elementToSelect = listOfElementsFromPage.get(selectedElementNumber);
        elementToSelect.shouldBe(Condition.visible).click();
        akitaScenario.setVar(varName, akitaScenario.getCurrentPage().getAnyElementText(elementToSelect).trim());
    }
}