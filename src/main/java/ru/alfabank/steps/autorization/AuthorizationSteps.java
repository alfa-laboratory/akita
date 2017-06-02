package ru.alfabank.steps.autorization;

import cucumber.api.java.ru.Пусть;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.steps.base.DefaultSteps;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ru.alfabank.steps.base.DefaultApiSteps.getURLwithPathParamsCalculated;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 *  Экспериментальный класс для шагов авторизации
 */
@Slf4j
public class AuthorizationSteps {

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    DefaultSteps defaultSteps = new DefaultSteps();

    /**
     *  Авторизация по прямой ссылке в приложении с указанием конечной страницы
     * */
    @Пусть("^авторизация по прямой ссылке \"([^\"]*)\" выполнена с переходом на страницу \"([^\"]*)\"$")
    public void loginByCurrentLink(String urlExpression, String nameUrl) {
        String url = getURLwithPathParamsCalculated(urlExpression);
        alfaScenario.write(" url = " + url);
        getWebDriver().get(url);
        defaultSteps.loadPage(nameUrl);
    }

    /**
     *  Стандартная авторизация через логин/пароль
     * */
    @Пусть("^пользователь  \"([^\"]*)\" авторизован в приложении и находится на странице \"([^\"]*)\"$")
    public void loginByUserData(String userCode, String nameUrl) {
        String login = loadProperty(userCode+".login");
        String password = loadProperty(userCode+".password");
        defaultSteps.cleanField("Логин");
        alfaScenario.getCurrentPage().getElement("Логин").sendKeys(login);
        defaultSteps.cleanField("Пароль");
        alfaScenario.getCurrentPage().getElement("Пароль").sendKeys(password);
        alfaScenario.getCurrentPage().getElement("Войти").click();
        defaultSteps.loadPage(nameUrl);
    }
}
