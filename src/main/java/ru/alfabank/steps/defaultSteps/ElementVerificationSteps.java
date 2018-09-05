package ru.alfabank.steps.defaultSteps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static com.codeborne.selenide.Condition.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadPropertyInt;

/**
 * Шаги для тестирования элементов страницы, доступные по умолчанию в каждом новом проекте.
 */

@Slf4j
public class ElementVerificationSteps {

    private AkitaScenario akitaScenario = AkitaScenario.getInstance();

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);


    /**
     * Проверка появления элемента(не списка) на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     */
    @Тогда("^элемент \"([^\"]*)\" отображается на странице$")
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
    public void testElementAppeared(String elementName, int seconds) {
        akitaScenario.getCurrentPage().waitElementsUntil(
            Condition.appear, seconds * 1000, akitaScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     */
    @Тогда("^ожидается исчезновение элемента \"([^\"]*)\"")
    public void elemDisappered(String elementName) {
        akitaScenario.getCurrentPage().waitElementsUntil(
            Condition.disappears, DEFAULT_TIMEOUT, akitaScenario.getCurrentPage().getElement(elementName));
    }

    /**
     * Проверка того, что значение из поля совпадает со значением заданной переменной из хранилища
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    public void compareFieldAndVariable(String elementName, String variableName) {
        String actualValue = akitaScenario.getCurrentPage().getAnyElementText(elementName);
        String expectedValue = akitaScenario.getVar(variableName).toString();
        assertThat(String.format("Значение поля [%s] не совпадает со значением из переменной [%s]", elementName, variableName),
            actualValue, equalTo(expectedValue));
    }

    /**
     * Сохранение значения элемента в переменную
     */
    @Когда("^значение (?:элемента|поля) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeElementValueInVariable(String elementName, String variableName) {
        akitaScenario.setVar(variableName, akitaScenario.getCurrentPage().getAnyElementText(elementName));
        akitaScenario.write("Значение [" + akitaScenario.getCurrentPage().getAnyElementText(elementName) + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     * Проверка того, что элемент не отображается на странице
     */
    @Тогда("^(?:поле|выпадающий список|элемент) \"([^\"]*)\" не отображается на странице$")
    public void elementIsNotVisible(String elementName) {
        akitaScenario.getCurrentPage().waitElementsUntil(
            not(Condition.appear), DEFAULT_TIMEOUT, akitaScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" (?:кликабельно|кликабелен)$")
    public void clickableField(String elementName) {
        SelenideElement element = akitaScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] не кликабелен", elementName), element.isEnabled());
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^элемент \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"(.*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        expectedAttributeValue = akitaScenario.getPropertyOrStringVariableOrValue(expectedAttributeValue);
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
    public void checkElemClassContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement currentElement = akitaScenario.getCurrentPage().getElement(elementName);
        expectedClassValue = akitaScenario.getPropertyOrStringVariableOrValue(expectedClassValue);
        String currentClassValue = currentElement.getAttribute("class");
        assertThat(String.format("Элемент [%s] не содержит класс со значением [%s]", elementName, expectedClassValue)
            , currentClassValue.toLowerCase(), containsString(expectedClassValue.toLowerCase()));
    }

    /**
     * Проверка, что элемент не содержит указанный класс
     */
    @Тогда("^элемент \"([^\"]*)\" не содержит класс со значением \"(.*)\"$")
    public void checkElemClassNotContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement currentElement = akitaScenario.getCurrentPage().getElement(elementName);
        assertThat(String.format("Элемент [%s] содержит класс со значением [%s]", elementName, expectedClassValue),
            currentElement.getAttribute("class").toLowerCase(),
            Matchers.not(containsString(akitaScenario.getPropertyOrStringVariableOrValue(expectedClassValue).toLowerCase())));
    }

    /**
     * Проверка, что значение в поле содержит значение (в приоритете: из property, из переменной сценария, значение аргумента),
     * указанное в шаге
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" содержит значение \"(.*)\"$")
    public void testActualValueContainsSubstring(String elementName, String expectedValue) {
        expectedValue = akitaScenario.getPropertyOrStringVariableOrValue(expectedValue);
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
    public void testFieldContainsInnerText(String fieldName, String expectedText) {
        expectedText = akitaScenario.getPropertyOrStringVariableOrValue(expectedText);
        String field = akitaScenario.getCurrentPage().getElement(fieldName).innerText().trim().toLowerCase();
        assertThat(String.format("Поле [%s] не содержит текст [%s]", fieldName, expectedText), field, containsString(expectedText.toLowerCase()));
    }

    /**
     * Проверка, что значение в поле равно значению, указанному в шаге (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" равно \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        expectedValue = akitaScenario.getPropertyOrStringVariableOrValue(expectedValue);
        String actualValue = akitaScenario.getCurrentPage().getAnyElementText(elementName);
        assertThat(String.format("Значение поля [%s] не равно ожидаемому [%s]", elementName, expectedValue), actualValue, equalTo(expectedValue));
    }

}
