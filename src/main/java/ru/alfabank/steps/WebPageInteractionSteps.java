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

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.DataTable;
import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Пусть;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;
import static ru.alfabank.tests.core.helpers.PropertyLoader.*;

/**
 * Шаги для работы с вэб-страницей, переменными и property-файлами, доступные по умолчанию в каждом новом проекте
 */


@Slf4j
public class WebPageInteractionSteps extends BaseMethods {

    private AkitaScenario akitaScenario = AkitaScenario.getInstance();

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);

    /**
     * Значение заданной переменной из application.properties сохраняется в переменную в akitaScenario
     * для дальнейшего использования
     */
    @И("^сохранено значение \"([^\"]*)\" из property файла в переменную \"([^\"]*)\"$")
    public void saveValueToVar(String propertyVariableName, String variableName) {
        propertyVariableName = loadProperty(propertyVariableName);
        akitaScenario.setVar(variableName, propertyVariableName);
        akitaScenario.write("Значение сохраненной переменной " + propertyVariableName);
    }

    /**
     * Выполняется обновление страницы
     */
    @И("^выполнено обновление текущей страницы$")
    public void refreshPage() {
        refresh();
    }

    /**
     * Выполняется переход по заданной ссылке,
     * ссылка берется из property / переменной, если такая переменная не найдена,
     * то берется переданное значение
     * при этом все ключи переменных в фигурных скобках
     * меняются на их значения из хранилища akitaScenario
     */
    @Когда("^совершен переход по ссылке \"([^\"]*)\"$")
    public void goToUrl(String address) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(address));
        open(url);
        akitaScenario.write("Url = " + url);
    }

    /**
     * Устанавливается значение переменной в хранилище переменных. Один из кейсов: установка login пользователя
     */
    @И("^установлено значение переменной \"([^\"]*)\" равным \"(.*)\"$")
    public void setVariable(String variableName, String value) {
        value = getPropertyOrValue(value);
        akitaScenario.setVar(variableName, value);
    }

    /**
     * Выполняется переход по заданной ссылке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по ссылке \"([^\"]*)\"$")
    public void goToSelectedPageByLink(String pageName, String urlOrName) {
        String address = loadProperty(urlOrName, resolveVars(urlOrName));
        akitaScenario.write(" url = " + address);
        open(address);
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
     * Эмулирует нажатие клавиш на клавиатуре
     */
    @И("^выполнено нажатие на клавиатуре \"([^\"]*)\"$")
    public void pushButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        switchTo().activeElement().sendKeys(key);
    }

    /**
     * Эмулирует нажатие сочетания клавиш на клавиатуре.
     * Допустим, чтобы эмулировать нажатие на Ctrl+A, в таблице должны быть следующие значения
     * | CONTROL |
     * | a       |
     *
     * @param keyNames название клавиши
     */
    @И("^выполнено нажатие на сочетание клавиш из таблицы$")
    public void pressKeyCombination(List<String> keyNames) {
        Iterable<CharSequence> listKeys = keyNames.stream()
                .map(this::getKeyOrCharacter)
                .collect(Collectors.toList());
        String combination = Keys.chord(listKeys);
        switchTo().activeElement().sendKeys(combination);
    }

    private CharSequence getKeyOrCharacter(String key) {
        try {
            return Keys.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return key;
        }
    }

    /**
     * Устанавливает размеры окна браузера
     */
    @И("^установлено разрешение экрана (\\d+) х (\\d+)$")
    public void setBrowserWindowSize(int width, int height) {
        getWebDriver().manage().window().setSize(new Dimension(width, height));
        akitaScenario.write("Установлены размеры окна браузера: ширина " + width + " высота" + height);
    }

    /**
     * Разворачивает окно с браузером на весь экран
     */
    @Если("^окно развернуто на весь экран$")
    public void expandWindowToFullScreen() {
        getWebDriver().manage().window().maximize();
    }

    /**
     * Переход на страницу по клику и проверка, что страница загружена
     */
    @И("^выполнен переход на страницу \"([^\"]*)\" после нажатия на (?:ссылку|кнопку) \"([^\"]*)\"$")
    public void urlClickAndCheckRedirection(String pageName, String elementName) {
        akitaScenario.getCurrentPage().getElement(elementName).click();
        loadPage(pageName);
        akitaScenario.write(" url = " + url());
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
     * Выполняется переход в конец страницы
     */
    @И("^совершен переход в конец страницы$")
    public void scrollDown() {
        Actions actions = new Actions(getWebDriver());
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).build().perform();
        actions.keyUp(Keys.CONTROL).perform();
    }

    /**
     * Скроллит экран до нужного элемента, имеющегося на странице, но видимого только в нижней/верхней части страницы.
     */
    @Когда("^страница прокручена до элемента \"([^\"]*)\"")
    public void scrollPageToElement(String elementName) {
        akitaScenario.getCurrentPage().getElement(elementName).scrollTo();
    }

    /**
     * Выполняется запуск js-скрипта с указанием в js.executeScript его логики
     * Скрипт можно передать как аргумент метода или значение из application.properties
     */
    @Когда("^выполнен js-скрипт \"([^\"]*)\"")
    public void executeJsScript(String scriptName) {
        String content = loadValueFromFileOrPropertyOrVariableOrDefault(scriptName);
        Selenide.executeJavaScript(content);
    }

    /**
     * Скроллит страницу вниз до появления элемента каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     */
    @И("^страница прокручена до появления элемента \"([^\"]*)\"$")
    public void scrollWhileElemNotFoundOnPage(String elementName) {
        SelenideElement el = null;
        do {
            el = akitaScenario.getCurrentPage().getElement(elementName);
            if (el.exists()) {
                break;
            }
            executeJavaScript("return window.scrollBy(0, 250);");
            sleep(1000);
        } while (!atBottom());
        assertThat("Элемент " + elementName + " не найден", el.isDisplayed());
    }

    /**
     * Скроллит страницу вниз до появления элемента с текстом из property файла, из переменной сценария или указанному в шаге каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     */
    @И("^страница прокручена до появления элемента с текстом \"([^\"]*)\"$")
    public void scrollWhileElemWithTextNotFoundOnPage(String expectedValue) {
        SelenideElement el = null;
        do {
            el = $(By.xpath(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(expectedValue))));
            if (el.exists()) {
                break;
            }
            executeJavaScript("return window.scrollBy(0, 250);");
            sleep(1000);
        } while (!atBottom());
        assertThat("Элемент с текстом " + expectedValue + " не найден", el.isDisplayed());
    }

    /**
     * Метод осуществляет снятие скриншота и прикрепление его к cucumber отчету.
     */
    @И("^снят скриншот текущей страницы$")
    public void takeScreenshot() {
        final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
        AkitaScenario.getInstance().getScenario().embed(screenshot, "image/png");
    }

    /**
    * Выполняется чтение файла с шаблоном и заполнение его значениями из таблицы
    */
    @И("^шаблон \"([^\"]*)\" заполнен данными из таблицы и сохранён в переменную \"([^\"]*)\"$")
    public void fillTemplate(String templateName, String varName, DataTable table) {
        String template = loadValueFromFileOrPropertyOrVariableOrDefault(templateName);
        boolean error = false;
        for (List<String> list : table.raw()) {
            String regexp = list.get(0);
            String replacement = list.get(1);
            if (template.contains(regexp)) {
                template = template.replaceAll(regexp, replacement);
            } else {
                akitaScenario.write("В шаблоне не найден элемент " + regexp);
                error = true;
            }
        }
        if (error)
            throw new RuntimeException("В шаблоне не найдены требуемые регулярные выражения");
        akitaScenario.setVar(varName, template);
    }

}
