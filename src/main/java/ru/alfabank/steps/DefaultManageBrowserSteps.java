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

import cucumber.api.java.ru.Когда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.util.Set;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ru.alfabank.steps.DefaultApiSteps.resolveVars;

/**
 * Шаги для работы с cookies
 */
@Slf4j
public class DefaultManageBrowserSteps {

    private AkitaScenario akitascenario;
    private WebDriver webDriver;

    public DefaultManageBrowserSteps(WebDriver webDriver, AkitaScenario akitaScenario) {
        this.webDriver = webDriver;
        this.akitascenario = akitaScenario;
    }
    public DefaultManageBrowserSteps() {
        this(getWebDriver(), AkitaScenario.getInstance());
    }

    /**
     * Удаляем все cookies
     */
    @Когда("^cookies приложения очищены$")
    public void deleteCookies(){
        webDriver.manage().deleteAllCookies();
    }

    /**
     * Ищем cookie по имени. Сохраняем cookie в переменную для дальнейшего использования
     */
    @Когда("^cookie с именем \"([^\"]*)\" сохранена в переменную \"([^\"]*)\"$")
    public void saveCookieToVar(String nameCookie, String cookieVar){
        String cookieName = resolveVars(nameCookie);
        Cookie var = webDriver.manage().getCookieNamed(cookieName);
        akitascenario.setVar(cookieVar, var);
    }

    /**
     * Сохраняем все cookies в переменную для дальнейшего использования
     */
    @Когда("^cookies сохранены в переменную \"([^\"]*)\"$")
    public void saveAllCookies(String variableName){
        Set cookies = webDriver.manage().getCookies();
        akitascenario.setVar(variableName, cookies);
    }

    /**
     * Находим cookie по имени и подменяем ее значение. Имя cookie и домен не меняются
     */
    @Когда("^добавлена cookie с именем \"([^\"]*)\" и значением \"([^\"]*)\"$")
    public void replaceCookie(String cookieName, String cookieValue){
        String nameCookie = resolveVars(cookieName);
        String valueCookie = resolveVars(cookieValue);
        webDriver.manage().addCookie(new Cookie(nameCookie, valueCookie));
    }
}
