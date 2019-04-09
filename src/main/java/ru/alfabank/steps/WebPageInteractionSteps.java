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

import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Пусть;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Шаги для взаимодействия с вэб-страницей, доступные по умолчанию в каждом новом проекте
 */

@Slf4j
public class WebPageInteractionSteps extends BaseMethods {

    /**
     * Выполняется обновление страницы
     */
    @И("^выполнено обновление текущей страницы$")
    @When("^page has been refreshed$")
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
    @When("^URL\"([^\"]*)\" has been opened$")
    public void goToUrl(String address) {
        String url = resolveVars(getPropertyOrStringVariableOrValue(address));
        open(url);
        akitaScenario.write("Url = " + url);
    }

    /**
     * Выполняется переход по заданной ссылке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по ссылке \"([^\"]*)\"$")
    @When("^opened page \"([^\"]*)\" by link \"([^\"]*)\"$")
    public void goToSelectedPageByLink(String pageName, String urlOrName) {
        String address = loadProperty(urlOrName, resolveVars(urlOrName));
        akitaScenario.write(" url = " + address);
        open(address);
        loadPage(pageName);
    }

    /**
     * Переход на страницу по клику и проверка, что страница загружена
     */
    @И("^выполнен переход на страницу \"([^\"]*)\" после нажатия на (?:ссылку|кнопку) \"([^\"]*)\"$")
    @When("^page \"([^\"]*)\" has been opened after pressing the \"([^\"]*)\" (?:link|button)$")
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
    @When("^user \"([^\"]*)\" entered login and password$")
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
    @When("^page has been scrolled to the bottom$")
    public void scrollDown() {
        Actions actions = new Actions(getWebDriver());
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).build().perform();
        actions.keyUp(Keys.CONTROL).perform();
    }

    /**
     * Скроллит экран до нужного элемента, имеющегося на странице, но видимого только в нижней/верхней части страницы.
     */
    @Когда("^страница прокручена до элемента \"([^\"]*)\"$")
    @When("^scrolled to \"([^\"]*)\" element$")
    public void scrollPageToElement(String elementName) {
        akitaScenario.getCurrentPage().getElement(elementName).scrollTo();
    }


    /**
     * Скроллит страницу вниз до появления элемента каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     */
    @И("^страница прокручена до появления элемента \"([^\"]*)\"$")
    @When("^page has been scrolled down till the \"([^\"]*)\" element is appeared$")
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
    @When("^page has been scrolled down till the element with text \"([^\"]*)\" is appeared$")
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
     *  Переключение на вкладку браузера с заголовком
     */
    @Когда("^выполнено переключение на вкладку с заголовком \"([^\"]*)\"$")
    @When("^switched to a tab with the title \"([^\"]*)\"$")
    public void switchToTheTabWithTitle(String title) {
        switchTo().window(getPropertyOrStringVariableOrValue(title));
        checkPageTitle(getPropertyOrStringVariableOrValue(title));
    }

    /**
     *  Производится сохранение заголовка страницы в переменную
     */
    @И("^заголовок страницы сохранен в переменную \"([^\"]*)\"$")
    @And("^page's header has been saved to the \"([^\"]*)\" variable$")
    public void savePageTitleToVariable(String variableName) {
        String titleName = getWebDriver().getTitle().trim();
        akitaScenario.setVar(variableName, titleName);
        akitaScenario.write("Значение заголовка страницы [" + titleName + "] сохранено в переменную [" + variableName + "]");
    }
}