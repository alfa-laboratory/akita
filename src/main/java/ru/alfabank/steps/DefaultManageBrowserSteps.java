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

import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.util.Set;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;

/**
 * Шаги для работы с cookies
 */
@Slf4j
public class DefaultManageBrowserSteps {

    private DefaultSteps ds = new DefaultSteps();
    private AkitaScenario akitaScenario = AkitaScenario.getInstance();

    /**
     * Удаляем все cookies
     */
    @Когда("^cookies приложения очищены$")
    public void deleteCookies(){
        clearBrowserCookies();
    }

    /**
     * Ищем cookie по имени. Сохраняем cookie в переменную для дальнейшего использования
     */
    @Когда("^cookie с именем \"([^\"]*)\" сохранена в переменную \"([^\"]*)\"$")
    public void saveCookieToVar(String nameCookie, String cookieVar){
        String cookieName = resolveVars(nameCookie);
        Cookie var = getWebDriver().manage().getCookieNamed(cookieName);
        akitaScenario.setVar(cookieVar, var);
    }

    /**
     * Сохраняем все cookies в переменную для дальнейшего использования
     */
    @Когда("^cookies сохранены в переменную \"([^\"]*)\"$")
    public void saveAllCookies(String variableName){
        Set cookies = getWebDriver().manage().getCookies();
        akitaScenario.setVar(variableName, cookies);
    }

    /**
     * Находим cookie по имени и подменяем ее значение. Имя cookie и домен не меняются
     */
    @Когда("^добавлена cookie с именем \"([^\"]*)\" и значением \"([^\"]*)\"$")
    public void replaceCookie(String cookieName, String cookieValue){
        String nameCookie = resolveVars(cookieName);
        String valueCookie = resolveVars(cookieValue);
        getWebDriver().manage().addCookie(new Cookie(nameCookie, valueCookie));
    }

    /**
     *  Переключение на следующую вкладку браузера
     */
    @Когда("выполнено переключение на следующую вкладку")
    public void switchToTheNextTab() {
        String nextWindowHandle = nextWindowHandle();
        getWebDriver().switchTo().window(nextWindowHandle);
        akitaScenario.write("Текущая вкладка " + nextWindowHandle);
    }

    /**
     *  Переключение на вкладку браузера с заголовком
     */
    @Когда("^выполнено переключение на вкладку с заголовком \"([^\"]*)\"$")
    public void switchToTheTabWithTitle(String title) {
        switchTo().window(title);
        checkPageTitle(title);
    }

    /**
     *  Производится сравнение заголовка страницы со значением, указанным в шаге
     *  (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^заголовок страницы равен \"([^\"]*)\"$")
    public void checkPageTitle(String pageTitleName) {
        pageTitleName = ds.getPropertyOrStringVariableOrValue(pageTitleName);
        String currentTitle = getWebDriver().getTitle().trim();
        assertThat(String.format("Заголовок страницы не совпадает с ожидаемым значением. Ожидаемый результат: %s, текущий результат: %s", pageTitleName, currentTitle),
                pageTitleName, equalToIgnoringCase(currentTitle));
    }

    /**
     *  Производится сохранение заголовка страницы в переменную
     */
    @И("^заголовок страницы сохранен в переменную \"([^\"]*)\"$")
    public void savePageTitleToVariable(String variableName) {
        String titleName = getWebDriver().getTitle().trim();
        akitaScenario.setVar(variableName, titleName);
        akitaScenario.write("Значение заголовка страницы [" + titleName + "] сохранено в переменную [" + variableName + "]");
    }

    /**
     *  Производится закрытие текущей вкладки
     */
    @И("выполнено закрытие текущей вкладки")
    public void closeCurrentTab() {
        getWebDriver().close();
    }

    private String nextWindowHandle() {
        String currentWindowHandle = getWebDriver().getWindowHandle();
        Set<String> windowHandles = getWebDriver().getWindowHandles();
        windowHandles.remove(currentWindowHandle);

        return windowHandles.iterator().next();
    }

}
