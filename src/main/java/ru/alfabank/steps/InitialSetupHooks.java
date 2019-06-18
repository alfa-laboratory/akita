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

import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static com.codeborne.selenide.WebDriverRunner.*;
import static ru.alfabank.tests.core.drivers.CustomDriverProvider.REMOTE_URL;

@Slf4j
public class InitialSetupHooks extends BaseMethods {

    /**
     * Создает настойки прокси для запуска драйвера
     */
    @Before(order = 1)
    public void setDriverProxy() {
        if (!Strings.isNullOrEmpty(System.getProperty("proxy"))) {
            Proxy proxy = new Proxy().setHttpProxy(System.getProperty("proxy"));
            setProxy(proxy);
            log.info("Проставлена прокси: " + proxy);
        }
    }

    /**
     * Создает окружение(среду) для запуска сценария
     *
     * @param scenario сценарий
     * @throws Exception
     */
    @Before(order = 10)
    public void setScenario(Scenario scenario) throws Exception {
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
    }

    /**
     * Уведомление о месте запуска тестов
     *
     * @throws Exception
     */
    @Before(order = 20)
    public static void setEnvironmentToTest() throws Exception {
        if (!Strings.isNullOrEmpty(System.getProperty(REMOTE_URL))) {
            log.info("Тесты запущены на удаленной машине: " + System.getProperty(REMOTE_URL));
        } else
            log.info("Тесты будут запущены локально");
    }

    /**
     * Удаляет все cookies
     *
     * @throws Exception
     */
    public static void clearCash() throws Exception {
        getWebDriver().manage().deleteAllCookies();
    }

    /**
     * Если сценарий завершился со статусом "fail" будет создан скриншот и сохранен в директорию
     * {@code <project>/build/reports/tests}
     *
     * @param scenario текущий сценарий
     */
    @After(order = 20)
    public void takeScreenshot(Scenario scenario) {
        if (scenario.isFailed() && hasWebDriverStarted()) {
            AkitaScenario.sleep(1);
            final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png");
        }
    }

    /**
     * По завершению теста удаляет все куки и закрывает веб-браузер
     */

    @After(order = 10)
    public void closeWebDriver() {
        if (hasWebDriverStarted()) {
            getWebDriver().manage().deleteAllCookies();
            WebDriverRunner.closeWebDriver();
        }
    }
}