package ru.alfabank.steps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.ru.*;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.tests.core.helpers.PropertyLoader;

import java.math.BigDecimal;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ru.alfabank.steps.DefaultApiSteps.getURLwithPathParamsCalculated;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * В alfaScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем AlfaPage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.
 */
@Slf4j
public class DefaultSteps {

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @Deprecated
    @И("^сохранено значение из глобальной перменной \"([^\"]*)\" в переменную \"([^\"]*)\"$")
    public void saveValueToVariable(String globalVarName, String varName) {
        setVar(varName, loadProperty(globalVarName));
    }

    /**
     * Читаем значение переменной из application.properties и сохраняем в переменную в alfaScenario,
     * для дальнейшего переиспользования
     */
    @И("^сохранено значение \"([^\"]*)\" из property файла в переменную \"([^\"]*)\"$")
    public void saveValueToVar(String globalVarName, String varName) {
        alfaScenario.setVar(varName, loadProperty(globalVarName));
    }

    /**
     * Обновляем страницу страницы
     */
    @И("^выполнено обновление текущей страницы$")
    public void refreshPage() {
        getWebDriver().navigate().refresh();
    }

    /**
     * Переходим по ссылке, разрезолвливая переменные из хранилища alfaScenario
     */
    @Deprecated
    @Когда("^я перешел по ссылке \"([^\"]*)\"$")
    public void goTo(String address) {
        String url = replaceVariables(address);
        getWebDriver().get(url);
        alfaScenario.write("Url = " + url);
    }

    @Когда("^совершен переход по ссылке \"([^\"]*)\"$")
    public void goToUrl(String address) {
        String url = replaceVariables(address);
        getWebDriver().get(url);
        alfaScenario.write("Url = " + url);
    }

    /**
     * Проверка, что текущий URL совпадает с ожидаемым
     */
    @Тогда("^текущий URL равен \"([^\"]*)\"$")
    public void checkCurrentURL(String url) {
        String currentUrl = getWebDriver().getCurrentUrl();
        String expectedUrl = replaceVariables(url);
        alfaScenario.write("current URL = " + currentUrl + "\n" +
                "expected URL = " + expectedUrl);
        assertThat("Текущий URL не совпадает с ожидаемым", currentUrl, Matchers.is(expectedUrl));
    }

    /**
     * На странице ищется элемент и по нему кликается
     */
    @Deprecated
    @И("^нажал на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnThisButton(String buttonName) {
        alfaScenario.getCurrentPage().getElement(buttonName).click();
    }

    @И("^выполнено нажатие на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        alfaScenario.getCurrentPage().getElement(elementName).click();
    }

    /**
     * Проверка, что в течении 10 секунд ожидается появление элемента(не списка) на странице
     */
    @И("^элемент \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, 10000, alfaScenario.getCurrentPage().getElement(elemName)
        );
    }

    /**
     * Проверка. В течение заданного количества секунд ожидается появление элемента(не списка) на странице
     */
    @Deprecated
    @И("^элемент \"([^\"]*)\" отобразился на странице в течение (\\d+) секунд$")
    public void elemAppeared(String elemName, int seconds) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, seconds * 1000, alfaScenario.getCurrentPage().getElement(elemName)
        );
    }

    /**
     * Проверка. В течение заданного количества секунд ожидается появление элемента(не списка) на странице
     */
    @И("^элемент \"([^\"]*)\" отобразился на странице в течение (\\d+) (?:секунд|секунды)")
    public void testElementAppeared(String elemName, int seconds) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, seconds * 1000, alfaScenario.getCurrentPage().getElement(elemName)
        );
    }

    /**
     * Время задается в application.properties как "waitingCustomElementsTimeout" или по дефолту 10 секунд
     * Проверка, что в течении нескольких секунд ожидается появление списка на странице
     */
    @И("^список \"([^\"]*)\" отображается на странице$")
    public void listIsPresentedOnPage(String elemName) {
        int time = Integer.parseInt(PropertyLoader.loadProperty("waitingCustomElementsTimeout", "10000"));
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, time, alfaScenario.getCurrentPage().getElementsList(elemName)
        );
    }

    /**
     * Проверка. В течение заданного количества секунд ожидается появление списка на странице
     */
    @Deprecated
    @И("^список \"([^\"]*)\" отобразился на странице в течение (\\d+) секунд$")
    public void listIsPresentedOnPage(String elemName, int seconds) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, seconds * 1000, alfaScenario.getCurrentPage().getElementsList(elemName)
        );
    }

    /**
     * Проверка. В течении 10 секунд ожидаем пока элемент исчезнет (станет невидимым)
     */
    @Deprecated
    @И("^ждем пока элемент \"([^\"]*)\" исчезнет")
    public void waitUntilDisapper(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.disappears, 10000, alfaScenario.getCurrentPage().getElement(elemName));
    }

    @И("^ожидается исчезновение элемента \"([^\"]*)\"")
    public void elemDisappered(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.disappears, 10000, alfaScenario.getCurrentPage().getElement(elemName));
    }

    /**
     * Проверяем, что все элементы, которые описаны в классе страницы с аннотацией @Name, но без аннотации @Optional,
     * видны на странице. При необходимости ждем.
     */
    @Когда("^(?:страница|блок|форма) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        alfaScenario.setCurrentPage(alfaScenario.getPage(nameOfPage));
        alfaScenario.getCurrentPage().appeared();
    }

    /**
     * Задать значение переменной в хранилище переменных. Один из кейсов: установка userCus для степов, использующих его.
     */
    @Deprecated
    @Когда("^установить \"([^\"]*)\" на весь тестовый suit: \"([^\"]*)\"$")
    public void setUserCus(String varName, String value) {
        setVar(varName, value);
    }

    /**
     * Задать значение переменной в хранилище переменных. Один из кейсов: установка userCus для степов, использующих его.
     * Полная копия предыдущего.
     */
    @Deprecated
    @И("^установить \"([^\"]*)\" равным \"([^\"]*)\"$")
    public void setVar(String varName, String value) {
        alfaScenario.setVar(varName, value);
    }

    @И("^установлено значение переменной \"([^\"]*)\" равным \"(.*)\"$")
    public void setVariable(String varName, String value) {
        alfaScenario.setVar(varName, value);
    }

    /**
     * Проверка. Из хранилища достаются значения двух перменных, и сравниваются на равенство. (для строк)
     */
    @Deprecated
    @Когда("^текстовые значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    public void compareTwoVars(String varName1, String varName2) {
        String s1 = getVar(varName1).toString();
        String s2 = getVar(varName2).toString();
        assertThat("строки не совпадают", s1, equalTo(s2));
    }

    @Когда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    public void compareTwoVariables(String varName1, String varName2) {
        String s1 = getVar(varName1).toString();
        String s2 = getVar(varName2).toString();
        assertThat("строки не совпадают", s1, equalTo(s2));
    }

    /**
     * Значение из поля сохраняется в заданную переменную.
     */
    @Deprecated
    @И("^значение поля \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void saveFieldValueToVariable(String fieldName, String variableName) {
        String value = alfaScenario.getCurrentPage().getElement(fieldName).innerText();
        if (value.isEmpty()) throw new IllegalStateException("Поле " + fieldName + " пусто!");
        alfaScenario.setVar(variableName, value);
    }

    @И("^значение из (?:поля|элемента) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void storeFieldValueInVariable(String fieldName, String variableName) {
        String value = alfaScenario.getCurrentPage().getAnyElementText(fieldName);
        if (value.isEmpty()) throw new IllegalStateException("Поле " + fieldName + " пусто!");
        alfaScenario.setVar(variableName, value);
    }


    /**
     * Значение из input-поля сохраняется в заданную переменную.
     */
    @Deprecated
    @И("^значение input-поля \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"$")
    public void saveInputValueToVariable(String fieldName, String variableName) {
        String value = alfaScenario.getCurrentPage().getElement(fieldName).getValue();
        if (value.isEmpty()) throw new IllegalStateException("Поле " + fieldName + " пусто!");
        alfaScenario.setVar(variableName, value);
    }

    /**
     * Проверка. Текстовое значение из поля совпадает со значением заданной переменной из хранилища.
     */
    @Deprecated
    @Тогда("^значение в поле \"([^\"]*)\" совпадает со значением переменной \"([^\"]*)\"$")
    public void compareFieldAndVariableValues(String fieldName, String variableName) {
        String actualValue = alfaScenario.getCurrentPage().getElement(fieldName).innerText();
        String expectedValue = alfaScenario.getVar(variableName).toString();
        assertEquals("Значения не совпадают", expectedValue, actualValue);
    }

    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    public void compareFieldAndVariable(String fieldName, String variableName) {
        String actualValue = alfaScenario.getCurrentPage().getAnyElementText(fieldName);
        String expectedValue = alfaScenario.getVar(variableName).toString();
        assertEquals("Значения не совпадают", expectedValue, actualValue);
    }

    /**
     * Проверка. Из хранилища достаём список по заданному ключу. Проверяем, что текстовое значение из поля содержится в списке.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    @Тогда("^значение в поле \"([^\"]*)\" есть в списке из переменной \"([^\"]*)\"$")
    public void checkListContainsValueFromField(String fieldName, String variableListName) {
        String actualValue = alfaScenario.getCurrentPage().getElement(fieldName).innerText();
        List<String> listFromVariable = ((List<String>) alfaScenario.getVar(variableListName));
        assertTrue("Значения нет в списке", listFromVariable.contains(actualValue));
    }

    @SuppressWarnings("unchecked")
    @Тогда("^список из переменной \"([^\"]*)\" содердит значение (?:поля|элемента) \"([^\"]*)\" $")
    public void checkIfListContainsValueFromField(String fieldName, String variableListName) {
        String actualValue = alfaScenario.getCurrentPage().getAnyElementText(fieldName);
        List<String> listFromVariable = ((List<String>) alfaScenario.getVar(variableListName));
        assertTrue("Значения нет в списке", listFromVariable.contains(actualValue));
    }

    /**
     * Проверка. Совершается переход по заданной ссылке и ждется, пока заданная страница полностью загрузится (встренная проверка,
     * что загружается та страница, которая ожидается)
     */
    @Deprecated
    @И("^совершен переход на страницу \"([^\"]*)\" по прямой ссылке = \"([^\"]*)\"$")
    public void goToSelectedPageByLink(String pageName, String urlName) {
        String url = getURLwithPathParamsCalculated(urlName);
        alfaScenario.write(" url = " + url);
        WebDriverRunner.getWebDriver().get(url);
        loadPage(pageName);
    }

    /**
     * Совершается переход по заданной ссылке.
     * Ссылка может передаваться как строка, так и как ключь из application.properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по (?:ссылке|ссылке из property файла) = \"([^\"]*)\"$")
    public void goToSelectedPageByLinkFromProperty(String pageName, String urlName) {
        String valueIfNotFoundInProperties = getURLwithPathParamsCalculated(urlName);
        urlName = PropertyLoader.loadProperty(urlName, valueIfNotFoundInProperties);
        alfaScenario.write(" url = " + urlName);
        WebDriverRunner.getWebDriver().get(urlName);
        loadPage(pageName);
    }


    /**
     * Ожидание заданное количество секунд
     */
    @Deprecated
    @Когда("^выполнено ожидание в течение (\\d+) секунд$")
    public void waitDuring(long seconds) {
        sleep(1000 * seconds);
    }

    /**
     * Ожидание заданное количество секунд
     */
    @Когда("^выполнено ожидание в течение (\\d+) (?:секунд|секунды)")
    public void waitForSeconds(long seconds) {
        sleep(1000 * seconds);
    }

    /**
     * проверка, что блок исчез/стал невидимым
     */
    @Deprecated
    @Тогда("^блок \"([^\"]*)\" исчез$")
    public void blockIsDisappears(String nameOfPage) {
        alfaScenario.getPage(nameOfPage).disappeared();
    }

    @Тогда("^(?:поле|блок|форма|выпадающий список|элемент) \"([^\"]*)\" (?:скрыто|скрыт|скрыта)")
    public void blockDisappeared(String nameOfPage) {
        alfaScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * Эмулирует нажатие на клавиатуре клавиш.
     */
    @Deprecated
    @И("^нажать на клавиатуре \"([^\"]*)\"$")
    public void pressButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        WebDriverRunner.getWebDriver().switchTo().activeElement().sendKeys(key);
    }
    @И("^выполнено нажатие на клавиатуре \"([^\"]*)\"$")
    public void pushButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        WebDriverRunner.getWebDriver().switchTo().activeElement().sendKeys(key);
    }

    /**
     * Ищется указанное текстовое поле и устанавливается в него заданное значение. Перед использованием поле нужно очистить
     */
    @Deprecated
    @Когда("^установлено значение \"(.*)\" в поле \"([^\"]*)\"$")
    public void setValueToField(String value, String elementName) {
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(elementName);
        valueInput.setValue(String.valueOf(value));
        valueInput.should(not(Condition.empty));
    }
    @Когда("^в поле \"([^\"]*)\" введено значение \"(.*)\"$")
    public void setFieldValue(String elementName, String value) {
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(elementName);
        valueInput.setValue(String.valueOf(value));
    }

    /**
     * Ищется поле и очишается
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String nameOfField) {
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        valueInput.click();
        valueInput.clear();
        valueInput.setValue("");
        valueInput.doubleClick().sendKeys(Keys.DELETE);
    }

    /**
     * Проверка, что поле для ввода пустое
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String fieldName) {
        SelenideElement field = alfaScenario.getCurrentPage().getElement(fieldName);
        assertThat("Поле '" + fieldName + "' содержит значение",
                alfaScenario.getCurrentPage().getAnyElementText(fieldName),
                Matchers.isEmptyOrNullString());
    }

    /**
     * Проверка, что появляется уведомление с заданным текстом.
     * Редкий случай, когда css селектор используется в степах
     */
    @Deprecated
    @Тогда("^ожидается появление уведомления с текстом \"([^\"]*)\"$")
    public void notificationAppearsWithText(String text) throws Throwable {
        SelenideElement el = $(".notification");
        el.waitUntil(Condition.appear, 5000);
        assertEquals(text, el.innerText());
    }

    /**
     * Проверка, что переданное выражение верно. Должно содержать '='. Переменные разрезолвливаются из хранилища
     */
    @Deprecated
    @И("^верно выражение \"([^\"]*)\"$")
    public void evaluate(String expression) {
        alfaScenario.write("Начал обрабатывать выражение: " + expression);
        String[] parts = expression.split("=");
        if (parts.length != 2) throw new AssertionError("выражение не выглядит как равенство: " + expression);
        int leftPart = Integer.valueOf(
                alfaScenario.getVars().evaluate(parts[0]).toString());
        int rightPart = Integer.valueOf(
                alfaScenario.getVars().evaluate(parts[1]).toString());
        MatcherAssert.assertThat("выражение не верное", leftPart, equalTo(rightPart));
    }

    /**
     * Устанавливает размеры окна с браузером
     */
    @Deprecated
    @И("^установить разрешение \"([^\"]*)\" на \"([^\"]*)\"$")
    public void setupWindowSize(String widthRaw, String heightRaw) {
        int width = Integer.valueOf(widthRaw);
        int height = Integer.valueOf(heightRaw);
        WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(width, height));
    }

    /**
     * Устанавливает размеры окна с браузером
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
    @Если("^развернуть окно на весь экран$")
    public void expandWindowToAllScreen() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    @Если("^окно развернуто на весь экран$")
    public void expandWindowToFullScreen() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    /**
     * Проверка, что в списке со страницы есть перечисленные в таблице элементы
     */
    @Deprecated
    @Тогда("^в списке \"([^\"]*)\" содержатся элементы$")
    public void checkTypesOfPay(String nameOfList, List<String> listOfType) {
        List<String> actualValues = alfaScenario.getCurrentPage().getAnyElementsListTexts(nameOfList);
        int numberOfTypes = actualValues.size();
        assertThat("Количество элементов в списке не соответсвует ожиданию", numberOfTypes, Matchers.is(listOfType.size()));
        assertTrue("Списки не совпадают", actualValues.containsAll(listOfType));
    }

    @Тогда("^список \"([^\"]*)\" состоит из элементов из таблицы$")
    public void checkIfListConsistsOfTableElements(String nameOfList, List<String> listOfType) {
        List<String> actualValues = alfaScenario.getCurrentPage().getAnyElementsListTexts(nameOfList);
        int numberOfTypes = actualValues.size();
        assertThat("Количество элементов в списке не соответсвует ожиданию", numberOfTypes, Matchers.is(listOfType.size()));
        assertTrue("Списки не совпадают", actualValues.containsAll(listOfType));
    }

    /**
     * В списке со страницу кликаем по элементу, содержащим заданное значение
     */
    @Deprecated
    @Тогда("^в списке \"([^\"]*)\" выбран элемент со значением \"(.*)\"$")
    public void checkTypesOfPay(String nameOfList, String nameOfValue) {
        List<SelenideElement> listOfTypeFromPage = alfaScenario.getCurrentPage().getElementsList(nameOfList);
        Optional<SelenideElement> itemFound = listOfTypeFromPage.stream().filter(type -> type.innerText().equals(nameOfValue)).findFirst();
        if (itemFound.isPresent()) {
            itemFound.get().click();
        } else {
            throw new IllegalStateException("Элемент не найден в списке");
        }
    }

    @Тогда("^в списке \"([^\"]*)\" выбран элемент с (?:текстом|значением) \"(.*)\"$")
    public void checkIfSelectedListElementMatchesValue(String nameOfList, String nameOfValue) {
        List<SelenideElement> listOfTypeFromPage = alfaScenario.getCurrentPage().getElementsList(nameOfList);
        Optional<SelenideElement> itemFound = listOfTypeFromPage.stream().filter(type -> type.innerText().equals(nameOfValue)).findFirst();
        if (itemFound.isPresent()) {
            itemFound.get().click();
        } else {
            throw new IllegalStateException("Элемент не найден в списке");
        }
    }

    /**
     *  Сохранение значения элемента в переменную
     * */
    @Deprecated
    @Когда("^я сохранил значение элемента \"([^\"]*)\" в переменную \"([^\"]*)\"")
    public void saveElementToVariable(String element, String variableName) {
        alfaScenario.setVar(variableName, alfaScenario.getCurrentPage().getAnyElementText(element));
    }

    @Когда("^значение (?:элемента|поля) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"")
    public void storeElementValueInVariable(String element, String variableName) {
        alfaScenario.setVar(variableName, alfaScenario.getCurrentPage().getAnyElementText(element));
    }

    /**
     * Проверка выражения на истинность
     * Например, string1.equals(string2)
     * OR string.equals("string")
     * Любое Java-выражение, возвращающие boolean
     */
    @Тогда("^верно, что \"([^\"]*)\"$")
    public void expressionExpression(String expression) {
        alfaScenario.getVars().evaluate("assert(" + expression + ")");
    }

    /**
     * Переход на страницу по клику и проверка, что страница загружена
     */
    @И("^выполнен переход на страницу \"([^\"]*)\" после нажатия на (?:ссылку|кнопку) \"([^\"]*)\"$")
    public void urlClickAndCheckRedirection(String pageName, String elementName) {
        alfaScenario.getCurrentPage().getElement(elementName).click();
        loadPage(pageName);
        alfaScenario.write(" url = " + WebDriverRunner.getWebDriver().getCurrentUrl());
    }

    /**
     * Ввод логин/пароля
     */
    @Пусть("^пользователь \"([^\"]*)\" ввел логин и пароль$")
    public void loginByUserData(String userCode) {
        String login = loadProperty(userCode + ".login");
        String password = loadProperty(userCode + ".password");
        cleanField("Логин");
        alfaScenario.getCurrentPage().getElement("Логин").sendKeys(login);
        cleanField("Пароль");
        alfaScenario.getCurrentPage().getElement("Пароль").sendKeys(password);
        alfaScenario.getCurrentPage().getElement("Войти").click();
    }

    /**
     * Проверка. Из хранилища достаются значения двух перменных, и сравниваются на равенство. (для числел)
     */
    @Deprecated
    @Когда("^числовые значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают")
    public void compareTwoDigitVars(String firstValue, String secondValue) {
        BigDecimal bigReal1 = new BigDecimal(
                alfaScenario.getVar(firstValue).toString()
        );
        BigDecimal bigReal2 = new BigDecimal(
                alfaScenario.getVar(secondValue).toString()
        );
        alfaScenario.write("Сравниваю на равенство переменные " + firstValue + " = " + bigReal1 + " и " +
                secondValue + " = " + bigReal2);
        assertThat("значения переменных совпали", bigReal1, equalTo(bigReal2));
    }

    /**
     * Выполнено наведение курсора на элемент
     */
    @Когда("^выполнен ховер на (?:поле|элемент) \"([^\"]*)\"$")
    public void saveToVariable(String fieldname) {
        SelenideElement field = alfaScenario.getCurrentPage().getElement(fieldname);
        field.hover();
    }

    /**
     * Проверка, что элемента нет на странице
     */
    @Deprecated
    @И("^элемент \"([^\"]*)\" не найден на странице$")
    public void elemIsNotPresentedOnPage(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                not(Condition.appear), 10000, alfaScenario.getCurrentPage().getElement(elemName)
        );
    }

    /**
     * Проверка, что элемент не отображается на странице
     */
    @Тогда("^(?:поле|блок|форма|выпадающий список|элемент) \"([^\"]*)\" не отображается на странице$")
    public void elementIsNotVisible(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                not(Condition.appear), 10000, alfaScenario.getCurrentPage().getElement(elemName)
        );
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String field) {
        SelenideElement element = alfaScenario.getCurrentPage().getElement(field);
        assertTrue(String.format("элемент [%s] не кликабелен", field), element.isEnabled());
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением
     */
    @Тогда("^элемент \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"(.*)\"$")
    public void checkElemContainsAtrWithValue(String elemName, String atrName, String expectedAtrValue) {
        SelenideElement currentElement = alfaScenario.getCurrentPage().getElement(elemName);
        String currentAtrValue = currentElement.attr(atrName);
        assertThat("значения не совпали", currentAtrValue, equalToIgnoringCase(expectedAtrValue));
    }

    @И("^совершен переход в конец страницы$")
    public void scrollDown() {
        Actions actions = new Actions(getWebDriver());
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).build().perform();
        actions.keyUp(Keys.CONTROL).perform();
    }

    /**
     * Проверка, что значение в поле содержит значению, указанное в шаге
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" содержит значение \"(.*)\"$")
    public void testActualValueContainsSubstring(String fieldName, String expectedValue) {
        String actualValue = alfaScenario.getCurrentPage().getAnyElementText(fieldName);
        assertThat("В поле нет ожидаемой подстроки", actualValue, containsString(expectedValue));
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @Тогда("^(?:ссылка|кнопка) \"([^\"]*)\" недоступна для нажатия$")
    public void buttonIsNotActive(String fieldName) {
        SelenideElement element = alfaScenario.getCurrentPage().getElement(fieldName);
        assertTrue("Элемент доступен для нажатия", element.is(Condition.disabled));
    }

    /**
     * Проверка, что поле нередактируемо
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String fieldName) {
        SelenideElement element = alfaScenario.getCurrentPage().getElement(fieldName);
        assertTrue("Элемент доступен для редактирования", element.is(Condition.disabled));
    }

    /**
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" со страницы совпадает со списком \"([^\"]*)\"$")
    public void compareListFromUIAndFromVariable(String listName, String variableName) {
        HashSet<String> expectedList = new HashSet<>((List<String>) alfaScenario.getVar(variableName));
        HashSet<String> actualList = new HashSet<>(alfaScenario.getCurrentPage().getAnyElementsListTexts(listName));
        assertEquals("Списки не совпадают", expectedList, actualList);
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
    public void addValue(String fieldName, String value) {
        SelenideElement field = alfaScenario.getCurrentPage().getElement(fieldName);
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
    public void findElement(String textName) {
        $(By.xpath("//*[text()='" + textName + "']")).click();
    }

    /**
     * Ввод в поле текущую дату в заданном формате
     * При неверном формате, используется dd.mm.yyyy
     */
    @Когда("^элемент \"([^\"]*)\" заполняется текущей датой в формате \"([^\"]*)\"&")
    public void currentDate(String fieldName, String formatDate) {
        long date = System.currentTimeMillis();
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(formatDate).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.mm.yyyy").format(date);
            log.error("Неверный формат. Дата будет использована в формате dd.mm.yyyy");
        }
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(fieldName);
        valueInput.setValue("");
        valueInput.setValue(currentStringDate);
    }
}
