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

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static ru.alfabank.steps.DefaultApiSteps.getURLwithPathParamsCalculated;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * В alfaScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Для каждой страницы, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем AlfaPage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.
 */
@Slf4j
public class DefaultSteps {

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    /**
     * Читаем значение переменной из application.properties и сохраняем в переменную в alfaScenario,
     * для дальнейшего переиспользования
     */
    @И("^сохранено значение из глобальной перменной \"([^\"]*)\" в переменную \"([^\"]*)\"$")
    public void saveValueToVariable(String globalVarName, String varName) {
        setVar(varName, loadProperty(globalVarName));
    }

    /**
     * Переходим по ссылке, разрезолвливая переменные из хранилища alfaScenario
     */
    @Когда("^я перешел по ссылке \"([^\"]*)\"$")
    public void goTo(String address) {
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
    @И("^нажал на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnThisButton(String buttonName) {
        alfaScenario.getCurrentPage().getElement(buttonName).click();
    }

    /**
     * Проверка, что в течении 10 секунд ожидается появления элемента(не списка) на странице
     */
    @И("^элемент \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, 10000, alfaScenario.getCurrentPage().getElement(elemName)
        );
    }

    /**
     * Проверка, что в течении 10 секунд ожидается появления списка на странице
     */
    @И("^список \"([^\"]*)\" отображается на странице$")
    public void listIsPresentedOnPage(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, 10000, alfaScenario.getCurrentPage().getElementsList(elemName)
        );
    }

    /**
     * Проверка, что элемента нет на странице. (перед этим ждем 3 секунды, зачем - не знаю)
     */
    @И("^элемент \"([^\"]*)\" не найден на странице$")
    public void elemIsNotPresentedOnPage(String elemName) {
        sleep(3000);
        alfaScenario.getCurrentPage().getElement(elemName).shouldBe(not(exist));
    }

    /**
     * Проверка. В течении 10 секунд ожидаем пока элемент исчезнет (станет невидимым)
     */
    @И("^ждем пока элемент \"([^\"]*)\" исчезнет")
    public void waitUntilDisapper(String elemName) {
        if (alfaScenario.getCurrentPage().getElement(elemName) != null) {
            alfaScenario.getCurrentPage().waitElementsUntil(
                    Condition.disappears, 10000, alfaScenario.getCurrentPage().getElement(elemName));
        }
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
    @Когда("^установить \"([^\"]*)\" на весь тестовый suit: \"([^\"]*)\"$")
    public void setUserCus(String varName, String value) {
        setVar(varName, value);
    }

    /**
     * Задать значение переменной в хранилище переменных. Один из кейсов: установка userCus для степов, использующих его.
     * Полная копия предыдущего.
     */
    @И("^установить \"([^\"]*)\" равным \"([^\"]*)\"$")
    public void setVar(String varName, String value) {
        alfaScenario.setVar(varName, value);
    }

    /**
     * Проверка. Из хранилища достаются значения двух перменных, и сравниваются на равенство. (для числел)
     */
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

    /**
     * Проверка. Из хранилища достаются значения двух перменных, и сравниваются на равенство. (для строк)
     */
    @Когда("^текстовые значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    public void compageTwoVars(String varName1, String varName2) {
        String s1 = getVar(varName1).toString();
        String s2 = getVar(varName2).toString();
        assertThat("строки совпадают", s1, equalTo(s2));
    }

    /**
     * Проверка. Совершается переход по заданной ссылке и ждется, пока заданная страница полностью загрузится (встренная проверка,
     * что загружается та страница, которая ожидается)
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по прямой ссылке = \"([^\"]*)\"$")
    public void goToSelectedPageByLink(String pageName, String urlName) {
        String url = getURLwithPathParamsCalculated(urlName);
        alfaScenario.write(" url = " + url);
        WebDriverRunner.getWebDriver().get(url);
        loadPage(pageName);
    }

    /**
     * Ожидание заданное количество секунд
     */
    @Когда("^выполнено ожидание в течение (\\d+) секунд$")
    public void waitDuring(int seconds) {
        sleep(1000 * seconds);
    }

    /**
     * проверка, что блок исчез/стал невидимым
     */
    @Тогда("^блок \"([^\"]*)\" исчез$")
    public void blockIsDisappears(String nameOfPage) {
        alfaScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * Эмулирует нажатие на клавиатуре клавиш. Для кейса, когда нужно промотать страицу вниз по Page Down
     */
    @И("^нажать на клавиатуре \"([^\"]*)\"$")
    public void pressButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        alfaScenario.getCurrentPage().getPrimaryElements().get(0).sendKeys(key);
    }

    /**
     * Ищется указанное текстовое поле и устанавливается в него заданное значение. Перед использованием поле нужно очистить
     */
    @Когда("^установлено значение \"([^\"]*)\" в поле \"([^\"]*)\"$")
    public void setValueToField(String amount, String nameOfField) {
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        valueInput.setValue(String.valueOf(amount));
        valueInput.should(not(Condition.empty));
    }

    /**
     * Ищется поле и очишается
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String nameOfField) {
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        valueInput.clear();
        valueInput.setValue("");
        valueInput.doubleClick().sendKeys(Keys.DELETE);
    }

    /**
     * Проверка, что поле для ввода пустое
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String nameOfField) {
        SelenideElement fieldInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        assertThat("Поле '" + nameOfField + "' содержит значение", fieldInput.val(), Matchers.isEmptyOrNullString());
        assertThat("Поле '" + nameOfField + "' содержит значение", fieldInput.innerText(), Matchers.isEmptyOrNullString());
    }

    /**
     * Проверка, что появляется уведомление с заданным текстом.
     * Редкий случай, когда css селектор используется в степах
     */
    @Тогда("^ожидается появление уведомления с текстом \"([^\"]*)\"$")
    public void notificationAppearsWithText(String text) throws Throwable {
        SelenideElement el = $(".notification");
        el.waitUntil(Condition.appear, 5000);
        assertEquals(text, el.innerText());
    }

    /**
     * Проверка, что переданное выражение верно. Должно содержать '='. Переменные разрезолвливаются из хранилища
     */
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

    /**
     * Устанавливает размеры окна с браузером
     */
    @И("^установить разрешение \"([^\"]*)\" на \"([^\"]*)\"$")
    public void setupWindowSize(String widthRaw, String heightRaw) {
        int width = Integer.valueOf(widthRaw);
        int height = Integer.valueOf(heightRaw);
        WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(width, height));
    }

    /**
     * Разворачивает окно с браузером на весь экран
     */
    @Если("^развернуть окно на весь экран$")
    public void развернутьОкноНаВесьЭкран() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    /**
     * Проверка, что в списке со страницы есть перечисленные в таблице элементы
     */
    @Тогда("^в списке \"([^\"]*)\" содержатся элементы$")
    public void checkTypesOfPay(String nameOfList, List<String> listOfType) {
        List<SelenideElement> listOfTypeFromPage = alfaScenario.getCurrentPage().getElementsList(nameOfList);
        int numberOfTypes = listOfTypeFromPage.size();
        assertThat("Количество элементов в списке не соответсвует ожиданию",numberOfTypes, Matchers.is(listOfType.size()));
        List<String> listOfRealNames = new ArrayList<>();
        listOfTypeFromPage.forEach(type -> listOfRealNames.add(type.innerText()));
        assertTrue("Списки не совпадают", listOfRealNames.containsAll(listOfType));
    }

    /**
     * В списке со страницу кликаем по элементу, содержащим заданное значение
     */
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
