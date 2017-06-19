package ru.alfabank.steps.base;

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

    @Before(order = 10)
    public void setScenario(Scenario scenario) throws Exception {
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
    }

    @Before(order = 20)
    public static void setEnvironmentToTest() throws Exception {
        if (!Strings.isNullOrEmpty(System.getProperty("remote"))) {
            log.info("Тесты запущены на удаленной машине: " + System.getProperty("remote"));
        } else
            log.info("Тесты будут запущены локально");
    }

    @Before(order = 21)
    public static void clearCash() throws Exception {
        getWebDriver().manage().deleteAllCookies();
    }

    @After(order = 20)
    public void takeScreenshot(Scenario scenario) {
        if (scenario.isFailed()) {
            AlfaScenario.sleep(1);
            final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png");
        }
    }

    @After(order = 10)
    public void closeWebDriver() {
        if (getWebDriver() != null) {
            WebDriverRunner.getWebDriver().manage().deleteAllCookies();
            WebDriverRunner.closeWebDriver();
        }
    }
}
