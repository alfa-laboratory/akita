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

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.alfabank.tests.core.helpers.PropertyLoader.getPropertyOrValue;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadPropertyInt;


/**
 * Шаги для тестирования списков элементов, доступные по умолчанию в каждом новом проекте.
 *
 * В akitaScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем AkitaPage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.
 */

@Slf4j
public class ListSteps {

    private AkitaScenario akitaScenario = AkitaScenario.getInstance();

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);


    /**
     * Проверка появления списка на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     */
    @Тогда("^список \"([^\"]*)\" отображается на странице$")
    public void listIsPresentedOnPage(String elementName) {
        akitaScenario.getCurrentPage().waitElementsUntil(
            Condition.appear, DEFAULT_TIMEOUT, akitaScenario.getCurrentPage().getElementsList(elementName)
        );
    }

    /**
     * Проверка того, что значение из поля содержится в списке,
     * полученном из хранилища переменных по заданному ключу
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список из переменной \"([^\"]*)\" содержит значение (?:поля|элемента) \"([^\"]*)\"$")
    public void checkIfListContainsValueFromField(String variableListName, String elementName) {
        String actualValue = akitaScenario.getCurrentPage().getAnyElementText(elementName);
        List<String> listFromVariable = ((List<String>) akitaScenario.getVar(variableListName));
        assertTrue(String.format("Список из переменной [%s] не содержит значение поля [%s]", variableListName, elementName),
            listFromVariable.contains(actualValue));
    }

    /**
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод getText()
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов из таблицы$")
    public void checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = akitaScenario.getCurrentPage().getAnyElementsListTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), textTable, hasSize(numberOfTypes));
        assertTrue(String.format("Значения элементов в списке [%s] не совпадают с ожидаемыми значениями из таблицы", listName), actualValues.containsAll(textTable));
    }

    /**
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     * Для получения текста из элементов списка используется метод innerText()
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов таблицы$")
    public void checkIfListInnerTextConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = akitaScenario.getCurrentPage().getAnyElementsListInnerTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), textTable, hasSize(numberOfTypes));
        assertTrue(String.format("Значения элементов в списке %s: %s не совпадают с ожидаемыми значениями из таблицы %s", listName, actualValues, textTable),
            actualValues.containsAll(textTable));
    }


    /**
     * Выбор из списка со страницы элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^в списке \"([^\"]*)\" выбран элемент с (?:текстом|значением) \"(.*)\"$")
    public void checkIfSelectedListElementMatchesValue(String listName, String expectedValue) {
        final String value = akitaScenario.getPropertyOrStringVariableOrValue(expectedValue);
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
    @Тогда("^в списке \"([^\"]*)\" выбран элемент содержащий текст \"([^\"]*)\"$")
    public void selectElementInListIfFoundByText(String listName, String expectedValue) {
        final String value = akitaScenario.getPropertyOrStringVariableOrValue(expectedValue);
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
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     * Для получения текста из элементов списка используется метод innerText()
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" на странице совпадает со списком \"([^\"]*)\"$")
    public void checkListInnerTextCorrespondsToListFromVariable(String listName, String listVariable) {
        List<String> expectedList = new ArrayList<>((List<String>) akitaScenario.getVar(listVariable));
        List<String> actualList = new ArrayList<>(akitaScenario.getCurrentPage().getAnyElementsListInnerTexts(listName));
        assertThat(String.format("Количество элементов списка %s = %s, ожидаемое значение = %s", listName, actualList.size(), expectedList.size()), actualList,
            hasSize(expectedList.size()));
        assertThat(String.format("Список со страницы %s: %s не совпадает с ожидаемым списком из переменной %s:%s", listName, actualList, listVariable, expectedList)
            , actualList, containsInAnyOrder(expectedList.toArray()));
    }

    /**
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" со страницы совпадает со списком \"([^\"]*)\"$")
    public void compareListFromUIAndFromVariable(String listName, String listVariable) {
        HashSet<String> expectedList = new HashSet<>((List<String>) akitaScenario.getVar(listVariable));
        HashSet<String> actualList = new HashSet<>(akitaScenario.getCurrentPage().getAnyElementsListTexts(listName));
        assertThat(String.format("Список со страницы [%s] не совпадает с ожидаемым списком из переменной [%s]", listName, listVariable), actualList, equalTo(expectedList));
    }

    /**
     * Выбор из списка со страницы любого случайного элемента
     */
    @Тогда("^выбран любой элемент в списке \"([^\"]*)\"$")
    public void selectRandomElementFromList(String listName) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size()))
            .shouldBe(Condition.visible).click();
        akitaScenario.write("Выбран случайный элемент: " + listOfElementsFromPage);
    }

    /**
     * Выбор из списка со страницы любого случайного элемента и сохранение его значения в переменную
     */
    @Когда("^выбран любой элемент из списка \"([^\"]*)\" и его значение сохранено в переменную \"([^\"]*)\"$")
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
    @Тогда("^выбран (\\d+)-й элемент в списке \"([^\"]*)\"$")
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
     * Проверка, что каждый элемент списка содержит ожидаемый текст
     * Не чувствителен к регистру
     */
    @Тогда("^элементы списка \"([^\"]*)\" содержат текст \"([^\"]*)\"$")
    public void checkListElementsContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
            .map(element -> element.getText().trim().toLowerCase())
            .collect(toList());
        assertTrue(String.format("Элемены списка %s: [%s] не содержат текст [%s] ", listName, elementsListText, value),
            elementsListText.stream().allMatch(item -> item.contains(value.toLowerCase())));
    }


    /**
     * Проверка, что каждый элемент списка не содержит ожидаемый текст
     */
    @Тогда("^элементы списка \"([^\"]*)\" не содержат текст \"([^\"]*)\"$")
    public void checkListElementsNotContainsText(String listName, String expectedValue) {
        final String value = getPropertyOrValue(expectedValue);
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
            .map(element -> element.getText().trim().toLowerCase())
            .collect(toList());
        assertFalse(String.format("Элемены списка %s: [%s] содержат текст [%s] ", listName, elementsListText, value),
            elementsListText.stream().allMatch(item -> item.contains(value.toLowerCase())));
    }

    /**
     * Проход по списку и проверка текста у элемента на соответствие формату регулярного выражения
     */
    @И("элементы списка \"([^\"]*)\" соответствуют формату \"([^\"]*)\"$")
    public void checkListTextsByRegExp(String listName, String pattern) {
        akitaScenario.getCurrentPage().getElementsList(listName).forEach(element -> {
            String str = akitaScenario.getCurrentPage().getAnyElementText(element);
            assertTrue(format("Текст '%s' из списка '%s' не соответствует формату регулярного выражения", str, listName),
                isTextMatches(str, pattern));
        });
    }

    /**
     * Производится проверка соответствия числа элементов списка значению, указанному в шаге
     */
    @Тогда("^в списке \"([^\"]*)\" содержится (\\d+) (?:элемент|элементов|элемента)")
    public void listContainsNumberOfElements(String listName, int quantity) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        assertTrue(String.format("Число элементов в списке отличается от ожидаемого: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() == quantity);
    }

    /**
     * Производится проверка соответствия числа элементов списка значению из property файла, из переменной сценария или указанному в шаге
     */
    @Тогда("^в списке \"([^\"]*)\" содержится количество элементов, равное значению из переменной \"([^\"]*)\"")
    public void listContainsNumberFromVariable(String listName, String quantity) {
        int numberOfElements = Integer.parseInt(akitaScenario.getPropertyOrStringVariableOrValue(quantity));
        listContainsNumberOfElements(listName, numberOfElements);
    }

    /**
     * Производится сопоставление числа элементов списка и значения, указанного в шаге
     */
    @Тогда("^в списке \"([^\"]*)\" содержится (более|менее) (\\d+) (?:элементов|элемента)")
    public void listContainsMoreOrLessElements(String listName, String moreOrLess, int quantity) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        if ("более".equals(moreOrLess)) {
            assertTrue(String.format("Число элементов списка меньше ожидаемого: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() > quantity);
        } else
            assertTrue(String.format("Число элементов списка превышает ожидаемое: %s", listOfElementsFromPage.size()), listOfElementsFromPage.size() < quantity);

    }


    /**
     * Проверка на соответствие строки паттерну
     */
    public boolean isTextMatches(String str, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return m.matches();
    }

    /**
     * Возвращает случайное число от нуля до maxValueInRange
     *
     * @param maxValueInRange максимальная граница диапазона генерации случайных чисел
     */
    private int getRandom(int maxValueInRange) {
        return (int) (Math.random() * maxValueInRange);
    }

}
