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
import cucumber.api.java.en.Then;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;

import static com.codeborne.selenide.Condition.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Шаги, содержащие проверки элементов страницы, доступные по умолчанию в каждом новом проекте
 */

@Slf4j
public class ElementsVerificationSteps extends BaseMethods {

    /**
     * Проверка появления элемента(не списка) на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 15 секундам
     */
    @Тогда("^элемент \"([^\"]*)\" отображается на странице$")
    @Then("^element named \"([^\"]*)\" is visible$")
    public void elemIsPresentedOnPage(String elementName) {
        akitaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, DEFAULT_TIMEOUT, akitaScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     */
    @Тогда("^элемент \"([^\"]*)\" отобразился на странице в течение (\\d+) (?:секунд|секунды)")
    @Then("^element named \"([^\"]*)\" has been loaded in (\\d+) second(|s)$")
    public void testElementAppeared(String elementName, int seconds) {
        akitaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, seconds * 1000, akitaScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 15 секундам
     */
    @Тогда("^ожидается исчезновение элемента \"([^\"]*)\"")
    @Then("^waiting for the element named \"([^\"]*)\" to disappear$")
    public void elemDisappered(String elementName) {
        akitaScenario.getCurrentPage().waitElementsUntil(
                Condition.disappears, DEFAULT_TIMEOUT, akitaScenario.getCurrentPage().getElement(elementName));
    }

    /**
     * Проверка того, что значение из поля совпадает со значением заданной переменной из хранилища
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    @Then("^value of the (?:field|element) named \"([^\"]*)\" is equal to variable named \"([^\"]*)\"$")
    public void compareFieldAndVariable(String elementName, String variableName) {
        String actualValue = akitaScenario.getCurrentPage().getAnyElementText(elementName);
        String expectedValue = akitaScenario.getVar(variableName).toString();
        assertThat(String.format("Значение поля [%s] не совпадает со значением из переменной [%s]", elementName, variableName),
                actualValue, equalTo(expectedValue));
    }

    /**
     * Проверка того, что элемент не отображается на странице
     */
    @Тогда("^(?:поле|выпадающий список|элемент) \"([^\"]*)\" не отображается на странице$")
    @Then("^(?:field|drop-down list|element) named \"([^\"]*)\" is not visible$")
    public void elementIsNotVisible(String elementName) {
        akitaScenario.getCurrentPage().waitElementsUntil(
                not(Condition.appear), DEFAULT_TIMEOUT, akitaScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельно$")
    @Then("^(?:field|element) named \"([^\"]*)\" is clickable$")
    public void clickableField(String elementName) {
        SelenideElement element = akitaScenario.getCurrentPage().getElement(elementName);
        assertTrue(element.isEnabled(), String.format("Элемент [%s] не кликабелен", elementName));
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^элемент \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"(.*)\"$")
    @Then("^element named \"([^\"]*)\" contains attribute named \"([^\"]*)\" with value of \"(.*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        expectedAttributeValue = getPropertyOrStringVariableOrValue(expectedAttributeValue);
        SelenideElement currentElement = akitaScenario.getCurrentPage().getElement(elementName);
        String currentAtrValue = currentElement.attr(attribute);
        assertThat(String.format("Элемент [%s] не содержит атрибут [%s] со значением [%s]", elementName, attribute, expectedAttributeValue)
                , currentAtrValue, equalToIgnoringCase(expectedAttributeValue));
    }

    /**
     * Проверка, что элемент содержит указанный класс (в приоритете: из property, из переменной сценария, значение аргумента)
     * Например:
     * если нужно проверить что элемент не отображается на странице, но проверки Selenium отрабатывают неверно,
     * можно использовать данный метод и проверить, что среди его классов есть disabled
     */
    @Тогда("^элемент \"([^\"]*)\" содержит класс со значением \"(.*)\"$")
    @Then("^element named \"([^\"]*)\" contains class with value of \"(.*)\"$")
    public void checkElemClassContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement currentElement = akitaScenario.getCurrentPage().getElement(elementName);
        expectedClassValue = getPropertyOrStringVariableOrValue(expectedClassValue);
        String currentClassValue = currentElement.getAttribute("class");
        assertThat(String.format("Элемент [%s] не содержит класс со значением [%s]", elementName, expectedClassValue)
                , currentClassValue.toLowerCase(), containsString(expectedClassValue.toLowerCase()));
    }

    /**
     * Проверка, что элемент не содержит указанный класс
     */
    @Тогда("^элемент \"([^\"]*)\" не содержит класс со значением \"(.*)\"$")
    @Then("^element named \"([^\"]*)\" does not contain class with value of \"(.*)\"$")
    public void checkElemClassNotContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement currentElement = akitaScenario.getCurrentPage().getElement(elementName);
        assertThat(String.format("Элемент [%s] содержит класс со значением [%s]", elementName, expectedClassValue),
                currentElement.getAttribute("class").toLowerCase(),
                Matchers.not(containsString(getPropertyOrStringVariableOrValue(expectedClassValue).toLowerCase())));
    }

    /**
     * Проверка, что значение в поле содержит значение (в приоритете: из property, из переменной сценария, значение аргумента),
     * указанное в шаге
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" содержит значение \"(.*)\"$")
    @Then("^(?:field|element) named \"([^\"]*)\" contains value of \"(.*)\"$")
    public void testActualValueContainsSubstring(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        String actualValue = akitaScenario.getCurrentPage().getAnyElementText(elementName);
        assertThat(String.format("Поле [%s] не содержит значение [%s]", elementName, expectedValue), actualValue, containsString(expectedValue));
    }

    /**
     * Проверка, что значение в поле содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента).
     * Используется метод innerText(), который получает как видимый, так и скрытый текст из элемента,
     * обрезая перенос строк и пробелы в конце и начале строчки.
     * Не чувствителен к регистру
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" содержит внутренний текст \"(.*)\"$")
    @Then("^(?:field|element) named \"([^\"]*)\" contains inner text \"(.*)\"$")
    public void testFieldContainsInnerText(String fieldName, String expectedText) {
        expectedText = getPropertyOrStringVariableOrValue(expectedText);
        String field = akitaScenario.getCurrentPage().getElement(fieldName).innerText().trim().toLowerCase();
        assertThat(String.format("Поле [%s] не содержит текст [%s]", fieldName, expectedText), field, containsString(expectedText.toLowerCase()));
    }

    /**
     * Проверка, что значение в поле равно значению, указанному в шаге (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" равно \"(.*)\"$")
    @Then("^value of (?:field|element) named \"([^\"]*)\" is equal to \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        expectedValue = getPropertyOrStringVariableOrValue(expectedValue);
        String actualValue = akitaScenario.getCurrentPage().getAnyElementText(elementName);
        assertThat(String.format("Значение поля [%s] не равно ожидаемому [%s]", elementName, expectedValue), actualValue, equalTo(expectedValue));
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия, поле или элемент не доступен для редактирования
     */
    @Тогда("^(?:ссылка|кнопка|поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен|недоступна)$")
    @Then("^(?:link|button|field|element) named \"([^\"]*)\" is not (?:clickable|editable)$")
    public void fieldIsDisable(String elementName) {
        SelenideElement element = akitaScenario.getCurrentPage().getElement(elementName);
        assertTrue(element.is(Condition.disabled), String.format("Элемент [%s] доступен", elementName));
    }

    /**
     * Производится проверка количества символов в поле со значением, указанным в шаге
     */
    @Тогда("^в поле \"([^\"]*)\" содержится (\\d+) символов$")
    @Then("^field named \"([^\"]*)\" contains (\\d+) symbol(|s)$")
    public void checkFieldSymbolsCount(String element, int num) {
        int length = akitaScenario.getCurrentPage().getAnyElementText(element).length();
        assertEquals(num, length, String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length));
    }

    /**
     * Проверка, что поле для ввода пусто
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    @Then("^field named \"([^\"]*)\" is empty$")
    public void fieldInputIsEmpty(String fieldName) {
        assertThat(String.format("Поле [%s] не пусто", fieldName),
                akitaScenario.getCurrentPage().getAnyElementText(fieldName),
                is(emptyOrNullString()));
    }
}