package ru.alfabank.steps;

import cucumber.api.java.ru.Когда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Шаги для работы с куками
 */
@Slf4j
public class DefaultManageBrowserSteps {

    private AkitaScenario akitascenario = AkitaScenario.getInstance();

    /**
     * Удаляем все куки
     */
    @Когда("^куки приложения очищены$")
    public void deleteCookies(){
        getWebDriver().manage().deleteAllCookies();
    }

    /**
     * Ищем куку по имени (из property-файла). Сохраняем куку в переменную для дальнейшего использования
     */
    @Когда("^кука из property файла с именем \"([^\"]*)\" сохранена в переменную \"([^\"]*)\"$")
    public void saveCookieToVar(String nameCookie, String cookie){
        String cookieName = loadProperty(nameCookie);
        String var = getWebDriver().manage().getCookieNamed(cookieName).toString();
        akitascenario.setVar(cookie, var);
    }

    /**
     * Сохраняем все куки в переменную для дальнейшего использования
     */
    @Когда("^куки сохранены в переменную \"([^\"]*)\"$")
    public void saveAllCookies(String cookies){
        String var = getWebDriver().manage().getCookies().toString();
        akitascenario.setVar(cookies, var);
    }

    /**
     * Собираем куку из параметров из property-файла и добавляем ее
     */
    @Когда("^добавлена кука с параметрами domain \"([^\"]*)\" name \"([^\"]*)\" и value \"([^\"]*)\" из property файла$")
    public void addCookie( String cookieDomain, String cookieName, String cookieValue){
        Cookie cookie = new Cookie(cookieDomain, cookieName, cookieValue);
        getWebDriver().manage().addCookie(cookie);
    }

    /**
     * Находим куку по имени и подменяем ее значение. Имя куки и домен не меняются
     */
    @Когда("^для куки с именем \"([^\"]*)\" подменяем значение на значение \"([^\"]*)\" из property файла$")
    public void replaceCookie(String cookieName, String cookieValue){
        String nameCookie = loadProperty(cookieName);
        String valueCookie = loadProperty(cookieValue);
        getWebDriver().manage().addCookie(new Cookie(nameCookie, valueCookie));
    }
}
