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

import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.util.Set;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;

/**
 * Шаги для управления браузером и работы с cookies
 */

@Slf4j
public class ManageBrowserSteps extends BaseMethods {

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
     *  Производится закрытие текущей вкладки
     */
    @И("выполнено закрытие текущей вкладки")
    public void closeCurrentTab() {
        getWebDriver().close();
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

}
