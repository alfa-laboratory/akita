package ru.alfabank.steps;

import cucumber.api.java.ru.Когда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.util.Set;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ru.alfabank.steps.DefaultApiSteps.resolveVars;

/**
 * Шаги для работы с cookies
 */
@Slf4j
public class DefaultManageBrowserSteps {

    private AkitaScenario akitascenario = AkitaScenario.getInstance();

    /**
     * Удаляем все cookies
     */
    @Когда("^cookies приложения очищены$")
    public void deleteCookies(){
        getWebDriver().manage().deleteAllCookies();
    }

    /**
     * Ищем cookie по имени. Сохраняем cookie в переменную для дальнейшего использования
     */
    @Когда("^cookie с именем \"([^\"]*)\" сохранена в переменную \"([^\"]*)\"$")
    public void saveCookieToVar(String nameCookie, String cookie){
        String cookieName = resolveVars(nameCookie);
        Cookie var = getWebDriver().manage().getCookieNamed(cookieName);
        akitascenario.setVar(cookie, var);
    }

    /**
     * Сохраняем все cookies в переменную для дальнейшего использования
     */
    @Когда("^cookies сохранены в переменную \"([^\"]*)\"$")
    public void saveAllCookies(String variableName){
        Set cookies = getWebDriver().manage().getCookies();
        akitascenario.setVar(variableName, cookies);
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
}
