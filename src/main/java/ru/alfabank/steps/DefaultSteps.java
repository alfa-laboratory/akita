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

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Пусть;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ru.alfabank.steps.DefaultApiSteps.resolveVars;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadPropertyInt;

/**
 * В akitaScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем AkitaPage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.
 */
@Slf4j
public class DefaultSteps {

    AkitaScenario akitaScenario = AkitaScenario.getInstance();

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);

    /**
     * Значение заданной переменной из application.properties сохраняется в переменную в akitaScenario
     * для дальнейшего использования
     */
    @И("^сохранено значение \"([^\"]*)\" из property файла в переменную \"([^\"]*)\"$")
    public void saveValueToVar(String propertyVariableName, String variableName) {
        akitaScenario.setVar(variableName, loadProperty(propertyVariableName));
    }

    /**
     * Выполняется обновление страницы
     */
    @И("^выполнено обновление текущей страницы$")
    public void refreshPage() {
        getWebDriver().navigate().refresh();
    }

    /**
     * Выполняется переход по заданной ссылке,
     * при этом все ключи переменных в фигурных скобках
     * меняются на их значения из хранилища akitaScenario
     */
    @Когда("^совершен переход по ссылке \"([^\"]*)\"$")
    public void goToUrl(String address) {
        String url = akitaScenario.replaceVariables(address);
        getWebDriver().get(url);
        akitaScenario.write("Url = " + url);
    }

    /**
     * Проверка, что текущий URL совпадает с ожидаемым
     */
    @Тогда("^текущий URL равен \"([^\"]*)\"$")
    public void checkCurrentURL(String url) {
        String currentUrl = getWebDriver().getCurrentUrl();
        String expectedUrl = akitaScenario.replaceVariables(url);
        assertThat("Текущий URL не совпадает с ожидаемым", currentUrl, is(expectedUrl));
    }

    /**
     * На странице происходит клик по заданному элементу
     */
    @И("^выполнено нажатие на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        akitaScenario.getCurrentPage().getElement(elementName).click();
    }

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
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional появились на странице
     * в течение WAITING_APPEAR_TIMEOUT, которое равно значению свойства "waitingAppearTimeout"
     * из application.properties. Если свойство не найдено, время таймаута равно 8 секундам
     */
    @Тогда("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        akitaScenario.setCurrentPage(akitaScenario.getPage(nameOfPage));
        akitaScenario.getCurrentPage().appeared();
    }

    /**
     * Устанавливается значение переменной в хранилище переменных. Один из кейсов: установка login пользователя
     */
    @И("^установлено значение переменной \"([^\"]*)\" равным \"(.*)\"$")
    public void setVariable(String variableName, String value) {
        akitaScenario.setVar(variableName, value);
    }

    /**
     * Проверка равенства двух переменных из хранилища
     */
    @Тогда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    public void compareTwoVariables(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = akitaScenario.getVar(firstVariableName).toString();
        String secondValueToCompare = akitaScenario.getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] не совпадают", firstVariableName, secondVariableName),
            firstValueToCompare, equalTo(secondValueToCompare));
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
     * Выполняется переход по заданной ссылке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по (?:ссылке|ссылке из property файла) \"([^\"]*)\"$")
    public void goToSelectedPageByLinkFromPropertyFile(String pageName, String urlOrName) {
        String address = loadProperty(urlOrName, resolveVars(urlOrName));
        akitaScenario.write(" url = " + address);
        WebDriverRunner.getWebDriver().get(address);
        loadPage(pageName);
    }

    /**
     * Ожидание в течение заданного количества секунд
     */
    @Когда("^выполнено ожидание в течение (\\d+) (?:секунд|секунды)")
    public void waitForSeconds(long seconds) {
        sleep(1000 * seconds);
    }

    /**
     * Проверка того, что блок исчез/стал невидимым
     */
    @Тогда("^(?:страница|блок|форма) \"([^\"]*)\" (?:скрыт|скрыта)")
    public void blockDisappeared(String nameOfPage) {
        akitaScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * Эмулирует нажатие клавиш на клавиатуре
     */
    @И("^выполнено нажатие на клавиатуре \"([^\"]*)\"$")
    public void pushButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        WebDriverRunner.getWebDriver().switchTo().activeElement().sendKeys(key);
    }

    /**
     * Эмулирует нажатие сочетания клавиш на клавиатуре.
     * Допустим, чтобы эмулировать нажатие на Ctrl+A, в таблице должны быть следующие значения
     *  | CONTROL |
     *  | a       |
     *
     * @param keyNames название клавиши
     */
    @И("^выполнено нажатие на сочетание клавиш из таблицы$")
    public void pressKeyCombination(List<String> keyNames) {
        Iterable<CharSequence> listKeys = keyNames.stream()
            .map(this::getKeyOrCharacter)
            .collect(Collectors.toList());
        String combination = Keys.chord(listKeys);
        WebDriverRunner.getWebDriver().switchTo().activeElement().sendKeys(combination);
    }

    private CharSequence getKeyOrCharacter(String key) {
        try {
            return Keys.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return key;
        }
    }

    /**
     * Устанавливается значение в заданное поле. Перед использованием поле нужно очистить
     */
    @Когда("^в поле \"([^\"]*)\" введено значение \"(.*)\"$")
    public void setFieldValue(String elementName, String value) {
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        valueInput.setValue(String.valueOf(value));
    }

    /**
     * Очищается заданное поле
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String nameOfField) {
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(nameOfField);
        valueInput.doubleClick().sendKeys(Keys.DELETE);
    }

    /**
     * Проверка, что поле для ввода пусто
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String fieldName) {
        assertThat(String.format("Поле [%s] не пусто", fieldName),
            akitaScenario.getCurrentPage().getAnyElementText(fieldName),
            isEmptyOrNullString());
    }

    /**
     * Устанавливает размеры окна браузера
     */
    @И("^установить разрешение экрана \"([^\"]*)\" ширина и \"([^\"]*)\" высота$")
    public void setWindowSize(String widthRaw, String heightRaw) {
        int width = Integer.valueOf(widthRaw);
        int height = Integer.valueOf(heightRaw);
        WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(width, height));
    }

    /**
     * Разворачивает окно с браузером на весь экран
     */
    @Если("^окно развернуто на весь экран$")
    public void expandWindowToFullScreen() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    /**
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов из таблицы$")
    public void checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = akitaScenario.getCurrentPage().getAnyElementsListTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), textTable, hasSize(numberOfTypes));
        assertTrue(String.format("Значения элементов в списке [%s] не совпадают с ожидаемыми значениями из таблицы", listName), actualValues.containsAll(textTable));
    }

    /**
     * Выбор из списка со страницы элемента с заданным значением
     */
    @Тогда("^в списке \"([^\"]*)\" выбран элемент с (?:текстом|значением) \"(.*)\"$")
    public void checkIfSelectedListElementMatchesValue(String listName, String value) {
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
     * Не чувствителен к регистру
     */
    @Тогда("^в списке \"([^\"]*)\" выбран элемент содержащий текст \"([^\"]*)\"$")
    public void selectElementInListIfFoundByText(String listName, String value) {
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
     * Сохранение значения элемента в переменную
     */
    @Когда("^значение (?:элемента|поля) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"")
    public void storeElementValueInVariable(String elementName, String variableName) {
        akitaScenario.setVar(variableName, akitaScenario.getCurrentPage().getAnyElementText(elementName));
    }

    /**
     * Проверка выражения на истинность
     * Например, string1.equals(string2)
     * OR string.equals("string")
     * Любое Java-выражение, возвращающие boolean
     */
    @Тогда("^верно, что \"([^\"]*)\"$")
    public void expressionExpression(String expression) {
        akitaScenario.getVars().evaluate("assert(" + expression + ")");
    }

    /**
     * Переход на страницу по клику и проверка, что страница загружена
     */
    @И("^выполнен переход на страницу \"([^\"]*)\" после нажатия на (?:ссылку|кнопку) \"([^\"]*)\"$")
    public void urlClickAndCheckRedirection(String pageName, String elementName) {
        akitaScenario.getCurrentPage().getElement(elementName).click();
        loadPage(pageName);
        akitaScenario.write(" url = " + WebDriverRunner.getWebDriver().getCurrentUrl());
    }

    /**
     * Шаг авторизации.
     * Для того, чтобы шаг работал, на текущей странице должны быть указаны элементы
     * со значениями аннотации @Name:
     * "Логин" - для поля ввода логина,
     * "Пароль" - для поля ввода пароля и
     * "Войти" - для кнопки входа.
     * Также должны быть указаны логин и пароль в файле application.properties.
     * Например для шага: "Пусть пользователь user ввел логин и пароль"
     * логин и пароль должны быть указаны со следующими ключами:
     * user.login - для логина и
     * user.password - для пароля
     */
    @Пусть("^пользователь \"([^\"]*)\" ввел логин и пароль$")
    public void loginByUserData(String userCode) {
        String login = loadProperty(userCode + ".login");
        String password = loadProperty(userCode + ".password");
        cleanField("Логин");
        akitaScenario.getCurrentPage().getElement("Логин").sendKeys(login);
        cleanField("Пароль");
        akitaScenario.getCurrentPage().getElement("Пароль").sendKeys(password);
        akitaScenario.getCurrentPage().getElement("Войти").click();
    }

    /**
     * Выполняется наведение курсора на элемент
     */
    @Когда("^выполнен ховер на (?:поле|элемент) \"([^\"]*)\"$")
    public void elementHover(String elementName) {
        SelenideElement field = akitaScenario.getCurrentPage().getElement(elementName);
        field.hover();
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
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String elementName) {
        SelenideElement element = akitaScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] не кликабелен", elementName), element.isEnabled());
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением
     */
    @Тогда("^элемент \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"(.*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        SelenideElement currentElement = akitaScenario.getCurrentPage().getElement(elementName);
        String currentAtrValue = currentElement.attr(attribute);
        assertThat(String.format("Элемент [%s] не содержит атрибут [%s] со значением [%s]", elementName, attribute, expectedAttributeValue)
            , currentAtrValue, equalToIgnoringCase(expectedAttributeValue));
    }

    /**
     * Проверка, что элемент содержит указанный класс
     * Например:
     * если нужно проверить что элемент не отображается на странице, но проверки Selenium отрабатывают неверно,
     * можно использовать данный метод и проверить, что среди его классов есть disabled
     */
    @Тогда("^элемент \"([^\"]*)\" содержит класс со значением \"(.*)\"$")
    public void checkElemClassContainsExpectedValue(String elementName, String expectedClassValue) {
        SelenideElement currentElement = akitaScenario.getCurrentPage().getElement(elementName);
        String currentClassValue = currentElement.getAttribute("class");
        assertThat(String.format("Элемент [%s] не содержит класс со значением [%s]", elementName, expectedClassValue)
            , currentClassValue.toLowerCase(), containsString(expectedClassValue.toLowerCase()));
    }

    /**
     * Выполняется переход в конец страницы
     */
    @И("^совершен переход в конец страницы$")
    public void scrollDown() {
        Actions actions = new Actions(getWebDriver());
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).build().perform();
        actions.keyUp(Keys.CONTROL).perform();
    }

    /**
     * Проверка, что значение в поле содержит значение, указанное в шаге
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" содержит значение \"(.*)\"$")
    public void testActualValueContainsSubstring(String elementName, String expectedValue) {
        String actualValue = akitaScenario.getCurrentPage().getAnyElementText(elementName);
        assertThat(String.format("Поле [%s] не содержит значение [%s]", elementName, expectedValue), actualValue, containsString(expectedValue));
    }

    /**
     * Проверка, что значение в поле содержит видимый текст, указанный в шаге
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" содержит видимый текст \"(.*)\"$")
    public void testFieldContainsMessageText(String fieldName, String messageText) {
        String field = akitaScenario.getCurrentPage().getElement(fieldName).getText();
        assertThat(String.format("Поле [%s] не содержит видимый текст [%s]", fieldName, messageText), field, containsString(messageText));
    }

    /**
     * Проверка, что значение в поле равно значению, указанному в шаге
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" равно \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        String actualValue = akitaScenario.getCurrentPage().getAnyElementText(elementName);
        assertThat(String.format("Значение поля [%s] не равно ожидаемому [%s]", elementName, expectedValue), actualValue, equalTo(expectedValue));
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @Тогда("^(?:ссылка|кнопка) \"([^\"]*)\" недоступна для нажатия$")
    public void buttonIsNotActive(String elementName) {
        SelenideElement element = akitaScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] кликабелен", elementName), element.is(Condition.disabled));
    }

    /**
     * Проверка, что поле нередактируемо
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String elementName) {
        SelenideElement element = akitaScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] доступен для редактирования", elementName), element.is(Condition.disabled));
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
     * Проверка, что на странице не отображаются редактируемые элементы, такие как:
     * -input
     * -textarea
     */
    @Тогда("^открыта read-only форма$")
    public void openReadOnlyForm() {
        int inputsCount = getDisplayedElementsByCss("input").size();
        assertTrue("Форма не read-only. Количество input-полей: " + inputsCount, inputsCount == 0);
        int textareasCount = getDisplayedElementsByCss("textarea").size();
        assertTrue("Форма не read-only. Количество элементов textarea: " + textareasCount, textareasCount == 0);
    }

    private List<SelenideElement> getDisplayedElementsByCss(String cssSelector) {
        return $$(cssSelector).stream()
            .filter(SelenideElement::isDisplayed)
            .collect(Collectors.toList());
    }

    /**
     * Добавление строки в поле к уже заполненой строке
     */
    @Когда("^в элемент \"([^\"]*)\" дописывается значение \"(.*)\"$")
    public void addValue(String elementName, String value) {
        SelenideElement field = akitaScenario.getCurrentPage().getElement(elementName);
        String oldValue = field.getValue();
        if (oldValue.isEmpty()) {
            oldValue = field.getText();
        }
        field.setValue("");
        field.setValue(oldValue + value);
    }

    /**
     * Нажатие на элемент по его тексту
     */
    @И("^выполнено нажатие на элемент с текстом \"(.*)\"$")
    public void findElement(String text) {
        $(By.xpath("//*[text()='" + text + "']")).click();
    }

    /**
     * Ввод в поле текущей даты в заданном формате
     * При неверном формате, используется dd.MM.yyyy
     */
    @Когда("^элемент \"([^\"]*)\" заполняется текущей датой в формате \"([^\"]*)\"$")
    public void currentDate(String fieldName, String dateFormat) {
        long date = System.currentTimeMillis();
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(dateFormat).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
            log.error("Неверный формат даты. Будет использоваться значание по умолчанию в формате dd.MM.yyyy");
        }
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(fieldName);
        valueInput.setValue("");
        valueInput.setValue(currentStringDate);
    }

    /**
     * Ввод в поле указанного текста, используя буфер обмена и клавиши SHIFT + INSERT
     */
    @Когда("^вставлено значение \"([^\"]*)\" в элемент \"([^\"]*)\" с помощью горячих клавиш$")
    public void pasteValueToTextField(String value, String fieldName) {
        ClipboardOwner clipboardOwner = (clipboard, contents) -> {
        };
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(value);
        clipboard.setContents(stringSelection, clipboardOwner);
        akitaScenario.getCurrentPage().getElement(fieldName).sendKeys(Keys.chord(Keys.SHIFT, Keys.INSERT));
    }

    /**
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     */
    @Тогда("^файл \"(.*)\" загрузился в папку /Downloads$")
    public void testFileDownloaded(String fileName) {
        File downloads = getDownloadsDir();
        File[] expectedFiles = downloads.listFiles((files, file) -> file.contains(fileName));
        assertNotNull("Ошибка поиска файла", expectedFiles);
        assertFalse("Файл не загрузился", expectedFiles.length == 0);
        assertTrue(String.format("В папке присутствуют более одного файла с одинаковым названием, содержащим текст [%s]", fileName),
            expectedFiles.length == 1);
        deleteFiles(expectedFiles);
    }

    /**
     *  Скроллит экран до появления элемента. Полезно, если сайт длинный и элемент может быть не виден.
     */
    @Тогда("^экран проскроллен до элемента \"([^\"]*)\"")
    public void scrollToElement(String elementName) {
        akitaScenario.getCurrentPage().getElement(elementName).scrollTo();
    }

    /**
     * Выбор из списка со страницы любого случайного элемента
     */
    @Тогда("^выбран любой элемент в списке \"([^\"]*)\"$")
    public void selectRandomElementFromList(String listName) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        listOfElementsFromPage.get(getRandom(listOfElementsFromPage.size()))
            .shouldBe(Condition.visible).click();
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
    public void checkListElementsContainsText(String listName, String value) {
        List<SelenideElement> listOfElementsFromPage = akitaScenario.getCurrentPage().getElementsList(listName);
        List<String> elementsListText = listOfElementsFromPage.stream()
            .map(element -> element.getText().trim().toLowerCase())
            .collect(toList());
        assertTrue(String.format("Элемены списка %s: [%s] не содержат текст [%s] ", listName, elementsListText, value),
            elementsListText.stream().allMatch(item -> item.contains(value.toLowerCase())));
    }

    /**
     * Возвращает каталог "Downloads" в домашней директории
     *
     * @return
     */
    private File getDownloadsDir() {
        String homeDir = System.getProperty("user.home");
        return new File(homeDir + "/Downloads");
    }

    /**
     * Удаляет файлы, переданные в метод
     *
     * @param filesToDelete массив файлов
     */
    private void deleteFiles(File[] filesToDelete) {
        for (File file : filesToDelete) {
            file.delete();
        }
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
