package ru.alfabank.steps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import org.hamcrest.Matchers;
import ru.alfabank.tests.core.rest.RequestParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Configuration.remote;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Created by ruslanmikhalev on 27/01/17.
 */
@Slf4j
public class DefaultSteps {

    @Before(order = 1)
    public static void clearCashAndDeleteCookies() throws Exception {
        if (!Strings.isNullOrEmpty(System.getProperty("remoteHub"))) {
            remote = System.getProperty("remoteHub");
            log.info("Тесты запущены на удаленной машине");
        } else
            log.info("Тесты будут запущены локально");

        Configuration.pageLoadStrategy = "none";
    }

    @Before(order = 2)
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

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @И("^сохранено значение из глобальной перменной \"([^\"]*)\" в переменную \"([^\"]*)\"$")
    public void saveValueToVariable(String globalVarName, String varName) {
        setVar(varName, loadProperty(globalVarName));
    }

    @Когда("^я перешел по ссылке \"([^\"]*)\"$")
    public void goTo(String address) {
        String url = replaceVariables(address);
        getWebDriver().get(url);
        alfaScenario.write("Url = " + url);
    }

    @Тогда("^текущий URL равен \"([^\"]*)\"$")
    public void checkCurrentURL(String url) {
        String currentUrl = getWebDriver().getCurrentUrl();
        String expectedUrl = replaceVariables(url);
        alfaScenario.write("current URL = " + currentUrl + "\n" +
                "expected URL = " + expectedUrl);
        assertThat("Текущий URL не совпадает с ожидаемым", currentUrl, Matchers.is(expectedUrl));
    }

    @И("^нажал на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnThisButton(String buttonName) {
        alfaScenario.getCurrentPage().getElement(buttonName).click();
    }

    @И("^элемент \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, 10000, alfaScenario.getCurrentPage().getElement(elemName)
        );
    }

    @И("^элемент \"([^\"]*)\" не найден на странице$")
    public void elemIsNotPresentedOnPage(String elemName) {
        sleep(3000);
        alfaScenario.getCurrentPage().getElement(elemName).shouldBe(not(exist));
    }

    @И("^ждем пока элемент \"([^\"]*)\" исчезнет")
    public void waitUntilDisapper(String elemName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.disappears, 10000, alfaScenario.getCurrentPage().getElement(elemName));
    }

    @Когда("^(?:страница|блок|форма) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        alfaScenario.setCurrentPage(alfaScenario.getPage(nameOfPage));
        alfaScenario.getCurrentPage().appeared();
    }

    @Когда("^установить \"([^\"]*)\" на весь тестовый suit: \"([^\"]*)\"$")
    public void setUserCus(String varName, String value) {
        setVar(varName, value);
    }

    @И("^ установить \"([^\"]*)\" равным \"([^\"]*)\"$")
    public void setVar(String varName, String value) {
        alfaScenario.setVar(varName, value);
    }

    @Когда("^числовые значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают")
    public void compareTwoDigitVars(String firstValue, String secondValue) {
        BigInteger bigInt1 = new BigInteger(
                alfaScenario.getVar(firstValue).toString()
        );
        BigInteger bigInt2 = new BigInteger(
                alfaScenario.getVar(secondValue).toString()
        );
        assertThat("значения переменных совпали", bigInt1, equalTo(bigInt2));
    }

    @И("^вызван \"([^\"]*)\" c URL \"([^\"]*)\", headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendRequest(String typeOfRequest, String urlName, String variableName, List<RequestParam> table) throws Exception {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        String body = "";
        String path = "";
        Gson gson = new Gson();
        urlName = getURLwithPathParamsCalculated(urlName);
        for (RequestParam requestParam : table) {
            switch (requestParam.getType()) {
                case PARAMETER:
                    parameters.put(requestParam.getName(), requestParam.getValue());
                    break;
                case HEADER:
                    headers.put(requestParam.getName(), requestParam.getValue());
                    break;
                case BODY:
                    try {
                        path = String.join(File.separator, new String[]
                                {"src", "main", "java", "restBodies", requestParam.getValue()});
                        JsonElement json = gson.fromJson(new FileReader(path), JsonElement.class);
                        body = gson.toJson(json);
                    } catch (FileNotFoundException e) {
                        fail("Файл с телом запроса не найден по пути: " + path);
                    }
                    break;
                default:
                    throw new RuntimeException("Некорректно задан элемент таблицы : " + requestParam.getType());
            }
        }
        RequestSender request;
        if (!body.isEmpty()) {
            alfaScenario.write("Тело запроса:\n" + body);
            request = given()
                    .contentType(ContentType.JSON)
                    .headers(headers)
                    .params(parameters)
                    .body(body)
                    .when();
        } else {
            request = given()
                    .headers(headers)
                    .params(parameters)
                    .when();
        }
        getResponseAndSaveToVariable(request, variableName, urlName, typeOfRequest);
    }

    private void getResponseAndSaveToVariable(RequestSender request, String variableName, String url, String typeOfRequest) {
        Response response = request.request(Method.valueOf(typeOfRequest), url);
        if (response.statusCode() == 200) {
            alfaScenario.setVar(variableName, response.getBody().asString());
            alfaScenario.write("Тело ответа : \n" + response.getBody().asString());
        } else {
            fail("Некорректный ответ на запрос " + response.getBody().asString());
        }
    }

    private Response makePostRequestWithBody(Map<String, String> headers, String jsonBody, Method methodType, String apiUrl) {
        RequestSpecification requestSender = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when();

        if (headers != null) requestSender = requestSender.headers(headers);
        return requestSender.request(methodType, apiUrl);
    }

    @И("^совершен переход на страницу \"([^\"]*)\" по прямой ссылке = \"([^\"]*)\"$")
    public void goToSelectedPageByLink(String pageName, String urlName) {
        String url = getURLwithPathParamsCalculated(urlName);
        alfaScenario.write(" url = " + url);
        WebDriverRunner.getWebDriver().get(url);
        loadPage(pageName);
    }

    @Когда("^выполнено ожидание в течение (\\d+) секунд$")
    public void waitDuring(int seconds) {
        sleep(1000 * seconds);
    }

    @Тогда("^блок \"([^\"]*)\" исчез$")
    public void blockIsDisappears(String nameOfPage) {
        alfaScenario.getCurrentPage().disappeared();
    }

    @Когда("^установлено значение \"([^\"]*)\" в поле \"([^\"]*)\"$")
    public void setSummToField(String amount, String nameOfField) {
        SelenideElement summInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        summInput.setValue(String.valueOf(amount));
        summInput.should(not(Condition.empty));
        alfaScenario.write("Поле непустое");
    }

    @Когда("^очищено поле \"([^\"]*)\"$")
    public void setSummToField(String nameOfField) {
        SelenideElement summInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        summInput.clear();
        summInput.setValue("");
        summInput.doubleClick().sendKeys(Keys.DELETE);
    }

    @Тогда("^input-поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String nameOfField) {
        SelenideElement summInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        assertThat("Поле '" + nameOfField + "' содержит значение", summInput.val(), Matchers.isEmptyOrNullString());
        assertThat("Поле '" + nameOfField + "' содержит значение", summInput.innerText(), Matchers.isEmptyOrNullString());
    }

    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldIsEmpty(String nameOfField) {
        SelenideElement summInput = alfaScenario.getCurrentPage().getElement(nameOfField);
        assertThat("Поле '" + nameOfField + "' содержит значение", summInput.innerText(), Matchers.isEmptyOrNullString());
    }

    @Тогда("^ожидается появление уведомления с текстом \"([^\"]*)\"$")
    public void notificationAppearsWithText(String text) throws Throwable {
        SelenideElement el = $(".notification");
        el.waitUntil(Condition.appear, 5000);
        assertEquals(text, el.innerText());
    }

    @И("^дождался завершения загрузки страницы \"([^\"]*)\"$")
    public void waitUntilPageLoaded(String pageName) throws Throwable {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.disappears, 10000, alfaScenario.getCurrentPage().getElement("Кругляш"));
        loadPage(pageName);
    }

    @И("^верно выражение \"([^\"]*)\"$")
    public void evaluate(String expression) {
        alfaScenario.write("Начал обрабатывать выражение: " + expression);
        String[] parts = expression.split("=");
        if (parts.length != 2) throw new AssertionError("выражение не выглядит как равенство: " + expression);
        int leftPart = Integer.valueOf(
                alfaScenario.getVars().evaluate(parts[0]).toString());
        int rightPart = Integer.valueOf(
                alfaScenario.getVars().evaluate(parts[1]).toString());
        MatcherAssert.assertThat("выражение верное", leftPart, equalTo(rightPart));
    }

    @И("^(?:кнопка|ссылка|поле) \"([^\"]*)\" видима$")
    public void elementVisible(String elementName) throws Throwable {
        MatcherAssert.assertThat("Элемент видим", alfaScenario.getCurrentPage().getElement(elementName),
                notNullValue());
    }

    @И("^установить разрешение \"([^\"]*)\" на \"([^\"]*)\"$")
    public void setupWindowSize(String widthRaw, String heightRaw) {
        int width = Integer.valueOf(widthRaw);
        int height = Integer.valueOf(heightRaw);
        WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(width, height));
    }

    @Если("^развернуть окно на весь экран$")
    public void развернутьОкноНаВесьЭкран() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    private static String getURLwithPathParamsCalculated(String urlName) {
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(urlName);
        String newString = "";
        while (m.find()) {
            String varName = m.group(1);
            String value = AlfaScenario.getInstance().getVar(varName).toString();
            newString = m.replaceFirst(value);
            m = p.matcher(newString);
        }
        if (newString.isEmpty()) {
            newString = urlName;
        }
        return newString;
    }

    public static String convertDateFromTo(String dateToConvert, String inputPattern, String outputPatter) {
        LocalDate date = LocalDate.parse(dateToConvert,
                DateTimeFormatter.ofPattern(inputPattern));
        return date.format(
                DateTimeFormatter.ofPattern(outputPatter)
        );
    }

    public void setValue(String varName, Object value) {
        alfaScenario.setVar(varName, value);
    }

    public Object getValue(String varName) {
        return alfaScenario.getVar(varName);
    }

    public void clickOnButton(String buttonName) {
        alfaScenario.getCurrentPage().getElement(buttonName).click();
    }
}
