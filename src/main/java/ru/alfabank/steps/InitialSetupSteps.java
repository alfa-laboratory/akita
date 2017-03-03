package ru.alfabank.steps;

import com.codeborne.selenide.Configuration;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.codeborne.selenide.Configuration.remote;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadPropertySafe;

@Slf4j
public class InitialSetupSteps {
    private static Boolean isEnvironmentAlive = null;
    private final static String HEALTH_CHECK = "healthCheck.list";
    private static String healthCheckErrorDescriptions = "";

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @Before(order = 5)
    public void healthCheck() throws FileNotFoundException {

        if (isEnvironmentAlive == null) {
            synchronized (InitialSetupSteps.class) {
                if (isEnvironmentAlive == null) {
                    isEnvironmentAlive = true;

                    String enableHealthCheck = loadPropertySafe("enableEnvHealthCheck");
                    if (null == enableHealthCheck || ! enableHealthCheck.toLowerCase().equals("yes")) {
                        return;
                    }
                    List<String> urls = loadListFromFile(HEALTH_CHECK);

                    for (String url: urls) {
                        String urlAliveError = checkUrlForAlive(url);
                        if (! urlAliveError.equals("")) {
                            isEnvironmentAlive = false;
                            healthCheckErrorDescriptions += urlAliveError + "\n\n";
                        }
                    }
                }
            }
        }

        if (! isEnvironmentAlive) {
            throw new RuntimeException("ENVIRONMENT IS DEAD!!!:\n\n" + healthCheckErrorDescriptions);
        }
    }

    @Before(order = 10)
    public static void clearCashAndDeleteCookies() throws Exception {
        if (!Strings.isNullOrEmpty(System.getProperty("remoteHub"))) {
            remote = System.getProperty("remoteHub");
            log.info("Тесты запущены на удаленной машине");
        } else
            log.info("Тесты будут запущены локально");

        Configuration.pageLoadStrategy = "none";
    }

    @Before(order = 20)
    public void setScenario(Scenario scenario) throws Exception {
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
    }

    @After
    public void takeScreenshot(Scenario scenario) {
        if (scenario.isFailed()) {
            AlfaScenario.sleep(1);
            final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png");
        }
    }

    @After
    public void closeWebdriver() {
        if (getWebDriver() != null) {
            WebDriverRunner.closeWebDriver();
        }
    }

    private String checkUrlForAlive(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != 200) {

                return "URL: " + link + "\nResponse Message: " + connection.getResponseMessage() +
                        "\nResonseCode: " + connection.getResponseCode();
            }
        } catch (IOException e) {
            return "Couldn't resolve host: " + link + "\n" + e.getMessage();
        }
        return "";
    }

    private List<String> loadListFromFile(String filename) throws FileNotFoundException {
        ClassLoader loader = getClass().getClassLoader();
        List<String> strings = new ArrayList<>();
        Scanner scanner;

        try {
            File file = new File(loader.getResource(HEALTH_CHECK).getFile());
            scanner = new Scanner(file);
        } catch (FileNotFoundException|NullPointerException e) {
            write("Файл " + filename + " не найден. Проверь его наличие в папке resources");
            throw new FileNotFoundException(filename);
        }
        while(scanner.hasNextLine()) {
            strings.add(scanner.nextLine());
        }
        return strings;
    }
}
