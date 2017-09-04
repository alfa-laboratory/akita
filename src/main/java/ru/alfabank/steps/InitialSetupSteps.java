package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Slf4j
public class InitialSetupSteps {

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    /**
     * Создает окружение(среду) для запуска сценария
     *
     * @param scenario сценарий
     * @throws Exception
     */
    @Before(order = 10)
    public void setScenario(Scenario scenario) throws Exception {
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
    }

    /**
     * Уведомление о месте запуска тестов
     *
     * @throws Exception
     */
    @Before(order = 20)
    public static void setEnvironmentToTest() throws Exception {
        if (!Strings.isNullOrEmpty(System.getProperty("remote"))) {
            log.info("Тесты запущены на удаленной машине: " + System.getProperty("remote"));
        } else
            log.info("Тесты будут запущены локально");
    }

    /**
     * Удаляет все cookies
     *
     * @throws Exception
     */
    @Before(order = 21)
    public static void clearCash() throws Exception {
        getWebDriver().manage().deleteAllCookies();
    }

    /**
     * Если сценарий завершился со статусом "fail" будет создан скриншот и сохранен в директорию
     * <project>/build/reports/tests
     *
     * @param scenario текущий сценарий
     */
    @After(order = 20)
    public void takeScreenshot(Scenario scenario) {
        if (scenario.isFailed()) {
            AlfaScenario.sleep(1);
            final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png");
        }
    }

    /**
     * По завершению теста удаляет все куки и закрывает веб-браузер
     */

    @After(order = 10)
    public void closeWebDriver() {
        if (getWebDriver() != null) {
            getWebDriver().manage().deleteAllCookies();
            WebDriverRunner.closeWebDriver();
        }
    }
}
