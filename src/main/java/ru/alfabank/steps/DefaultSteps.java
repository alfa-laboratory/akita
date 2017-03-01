package ru.alfabank.steps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Created by ruslanmikhalev on 27/01/17.
 */
@Slf4j
public class DefaultSteps {

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @И("^сохранено значение из глобальной перменной \"([^\"]*)\" в переменную \"([^\"]*)\"$")
    public void saveValueToVariable(String globalVarName, String varName) {
        setVar(varName, loadProperty(globalVarName));
    }

    @Когда("^я перешел по ссылке \"([^\"]*)\"$")
    public void goTo(String address) {
        String url = replaceVariables(address);
        getWebDriver().get(url);
        alfaScenario.write("Url = " + url);
    }

    @Тогда("^текущий URL равен \"([^\"]*)\"$")
    public void checkCurrentURL(String url) {
        String currentUrl = getWebDriver().getCurrentUrl();
        String expectedUrl = replaceVariables(url);
        alfaScenario.write("current URL = " + currentUrl + "\n" +
                "expected URL = " + expectedUrl);
        assertThat("Текущий URL не совпадает с ожидаемым", currentUrl, Matchers.is(expectedUrl));
    }

    @И("^нажал на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnThisButton(String buttonName) {
        alfaScenario.getCurrentPage().getElement(buttonName).click();
    }

    @И("^элемент \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, 10000, alfaScenario.getCurrentPage().getElement(elemName)
        );
    }

    @И("^элемент \"([^\"]*)\" не найден на странице$")
    public void elemIsNotPresentedOnPage(String elemName) {
        sleep(3000);
        alfaScenario.getCurrentPage().getElement(elemName).shouldBe(not(exist));
    }

    @И("^ждем пока элемент \"([^\"]*)\" исчезнет")
    public void waitUntilDisapper(String elemName) {
        if (alfaScenario.getCurrentPage().getElement(elemName) != null) {
            alfaScenario.getCurrentPage().waitElementsUntil(
                    Condition.disappears, 10000, alfaScenario.getCurrentPage().getElement(elemName));
        }
    }

    @Когда("^(?:страница|блок|форма) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        alfaScenario.setCurrentPage(alfaScenario.getPage(nameOfPage));
        alfaScenario.getCurrentPage().appeared();
    }

    @Когда("^установить \"([^\"]*)\" на весь тестовый suit: \"([^\"]*)\"$")
    public void setUserCus(String varName, String value) {
        setVar(varName, value);
    }

    @И("^установить \"([^\"]*)\" равным \"([^\"]*)\"$")
    public void setVar(String varName, String value) {
        alfaScenario.setVar(varName, value);
    }

    @Когда("^числовые значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают")
    public void compareTwoDigitVars(String firstValue, String secondValue) {
        BigInteger bigInt1 = new BigInteger(
                alfaScenario.getVar(firstValue).toString()
        );
        BigInteger bigInt2 = new BigInteger(
                alfaScenario.getVar(secondValue).toString()
        );
        alfaScenario.write("Сравниваю на равенство переменные " + firstValue + " = " + bigInt1 + " и " +
                secondValue + " = " + bigInt2);
        assertThat("значения переменных совпали", bigInt1, equalTo(bigInt2));
    }

    @Когда("^текстовые значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    public void compageTwoVars(String varName1, String varName2) {
        String s1 = getVar(varName1).toString();
        String s2 = getVar(varName2).toString();
        assertThat("строки совпадают", s1, equalTo(s2));
    }

    @И("^совершен переход на страницу \"([^\"]*)\" по прямой ссылке = \"([^\"]*)\"$")
    public void goToSelectedPageByLink(String pageName, String urlName) {
        String url = getURLwithPathParamsCalculated(urlName);
        alfaScenario.write(" url = " + url);
        WebDriverRunner.getWebDriver().get(url);
        loadPage(pageName);
    }

    @Когда("^выполнено ожидание в течение (\\d+) секунд$")
    public void waitDuring(int seconds) {
        sleep(1000 * seconds);
    }

    @Тогда("^блок \"([^\"]*)\" исчез$")
    public void blockIsDisappears(String nameOfPage) {
        alfaScenario.getPage(nameOfPage).disappeared();
    }

    @И("^нажать на клавиатуре \"([^\"]*)\"$")
    public void pressButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        alfaScenario.getCurrentPage().getPrimaryElements().get(0).sendKeys(key);
    }

    @Когда("^установлено значение \"([^\"]*)\" в поле \"([^\"]*)\"$")
    public void setValueToField(String amount, String nameOfField) {
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        valueInput.setValue(String.valueOf(amount));
        valueInput.should(not(Condition.empty));
        alfaScenario.write("Поле непустое");
    }

    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String nameOfField) {
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        valueInput.clear();
        valueInput.setValue("");
        valueInput.doubleClick().sendKeys(Keys.DELETE);
    }

    @Тогда("^input-поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String nameOfField) {
        SelenideElement summInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        assertThat("Поле '" + nameOfField + "' содержит значение", summInput.val(), Matchers.isEmptyOrNullString());
        assertThat("Поле '" + nameOfField + "' содержит значение", summInput.innerText(), Matchers.isEmptyOrNullString());
    }

    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldIsEmpty(String nameOfField) {
        SelenideElement summInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        assertThat("Поле '" + nameOfField + "' содержит значение", summInput.innerText(), Matchers.isEmptyOrNullString());
    }

    @Тогда("^ожидается появление уведомления с текстом \"([^\"]*)\"$")
    public void notificationAppearsWithText(String text) throws Throwable {
        SelenideElement el = $(".notification");
        el.waitUntil(Condition.appear, 5000);
        assertEquals(text, el.innerText());
    }

    @И("^дождался завершения загрузки страницы \"([^\"]*)\"$")
    public void waitUntilPageLoaded(String pageName) throws Throwable {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.disappears, 10000, alfaScenario.getCurrentPage().getElement("Кругляш"));
        loadPage(pageName);
    }

    @И("^верно выражение \"([^\"]*)\"$")
    public void evaluate(String expression) {
        alfaScenario.write("Начал обрабатывать выражение: " + expression);
        String[] parts = expression.split("=");
        if (parts.length != 2) throw new AssertionError("выражение не выглядит как равенство: " + expression);
        int leftPart = Integer.valueOf(
                alfaScenario.getVars().evaluate(parts[0]).toString());
        int rightPart = Integer.valueOf(
                alfaScenario.getVars().evaluate(parts[1]).toString());
        MatcherAssert.assertThat("выражение верное", leftPart, equalTo(rightPart));
    }

    @И("^(?:кнопка|ссылка|поле) \"([^\"]*)\" видима$")
    public void elementVisible(String elementName) throws Throwable {
        MatcherAssert.assertThat("Элемент видим", alfaScenario.getCurrentPage().getElement(elementName),
                notNullValue());
    }

    @И("^установить разрешение \"([^\"]*)\" на \"([^\"]*)\"$")
    public void setupWindowSize(String widthRaw, String heightRaw) {
        int width = Integer.valueOf(widthRaw);
        int height = Integer.valueOf(heightRaw);
        WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(width, height));
    }

    @Если("^развернуть окно на весь экран$")
    public void развернутьОкноНаВесьЭкран() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    static String getURLwithPathParamsCalculated(String urlName) {
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(urlName);
        String newString = "";
        while (m.find()) {
            String varName = m.group(1);
            String value = AlfaScenario.getInstance().getVar(varName).toString();
            newString = m.replaceFirst(value);
            m = p.matcher(newString);
        }
        if (newString.isEmpty()) {
            newString = urlName;
        }
        return newString;
    }

    @Тогда("^в списке \"([^\"]*)\" содержатся элементы$")
    public void checkTypesOfPay(String nameOfList, List<String> listOfType) {
        List<SelenideElement> listOfTypeFromPage = alfaScenario.getCurrentPage().getElementsList(nameOfList);
        int numberOfTypes = listOfTypeFromPage.size();
        assertThat("Количество элементов в списке не соответсвует ожиданию",numberOfTypes, Matchers.is(listOfType.size()));
        List<String> listOfRealNames = new ArrayList<>();
        listOfTypeFromPage.forEach(type -> listOfRealNames.add(type.innerText()));
        assertTrue("Списки не совпадают", listOfRealNames.containsAll(listOfType));
    }

    @Тогда("^в списке \"([^\"]*)\" выбран элемент со значением \"([^\"]*)\"$")
    public void checkTypesOfPay(String nameOfList, String nameOfValue) {
        List<SelenideElement> listOfTypeFromPage = alfaScenario.getCurrentPage().getElementsList(nameOfList);
        Optional<SelenideElement> itemFound = listOfTypeFromPage.stream().filter(type -> type.innerText().equals(nameOfValue)).findFirst();
        if (itemFound.isPresent()) {
            itemFound.get().click();
        } else {
            throw new IllegalStateException("Элемент не найден в списке");
        }
    }
}
