package ru.alfabank.steps;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.tests.core.helpers.PropertyLoader;
import ru.alfabank.tests.core.rest.RequestParam;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ru.alfabank.tests.core.helpers.PropertyLoader.getPropertyOrValue;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

@Slf4j
public class DefaultApiSteps {

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    /**
     * Посылается http GET/POST/PUT/POST/DELETE/HEAD/TRACE/OPTIONS/PATCH запрос по заданному урлу без параметров и BODY.
     * Результат сохраняется в заданную переменную
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     */
    @Deprecated
    @И("^отправлен http \"([^\"]*)\" запрос на URL \"([^\"]*)\" . Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequest(String typeOfRequest, String urlName, String variableName) throws Exception {
        String valueIfNotFoundInProperties = getURLwithPathParamsCalculated(urlName);
        urlName = loadProperty(urlName, valueIfNotFoundInProperties);
        sendHttpRequest(typeOfRequest, urlName, variableName, new ArrayList<>());
    }

    /**
     * Посылается http GET/POST/PUT/POST/DELETE/HEAD/TRACE/OPTIONS/PATCH запрос по заданному урлу без параметров и BODY.
     * Результат сохраняется в заданную переменную
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     */
    @И("^выполнен (GET|POST) запрос на URL \"([^\"]*)\". Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestWithoutParams(String typeOfRequest, String urlName, String variableName) throws Exception {
        String valueIfNotFoundInProperties = getURLwithPathParamsCalculated(urlName);
        urlName = loadProperty(urlName, valueIfNotFoundInProperties);
        sendHttpRequest(typeOfRequest, urlName, variableName, new ArrayList<>());
    }

    /**
     * Посылается http GET/POST/PUT/POST/DELETE/HEAD/TRACE/OPTIONS/PATCH запрос по заданному урлу с заданными параметрами.
     * Результат сохраняется в заданную переменную
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     */
    @И("^выполнен (GET|POST) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestSaveResponse(String typeOfRequest, String urlName, String variableName, List<RequestParam> table) throws Exception {
        String valueIfNotFoundInProperties = getURLwithPathParamsCalculated(urlName);
        urlName = loadProperty(urlName, valueIfNotFoundInProperties);
        RequestSender request = createRequestByParamsTable(table);
        Response response = request.request(Method.valueOf(typeOfRequest), urlName);
        getResponseAndSaveToVariable(variableName, response);
    }

    /**
     * Посылается http GET/POST/PUT/POST/DELETE/HEAD/TRACE/OPTIONS/PATCH запрос по заданному урлу с заданными параметрами.
     * Проверяется, что код ответа соответствует ожиданиям.
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     */
    @И("^выполнен (GET|POST) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
    public void checkResponseCode(String typeOfRequest, String urlName, int expectedStatusCode, List<RequestParam> table) throws Exception {
        String valueIfNotFoundInProperties = getURLwithPathParamsCalculated(urlName);
        urlName = loadProperty(urlName, valueIfNotFoundInProperties);
        assertTrue(checkStatusCode(typeOfRequest, urlName, expectedStatusCode, table));
    }

    private RequestSender createRequestByParamsTable() {
        return given().when();
    }

    private RequestSender createRequestByParamsTable(List<RequestParam> table) {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        String body = null;
        Gson gson = new Gson();
        for (RequestParam requestParam : table) {
            switch (requestParam.getType()) {
                case PARAMETER:
                    parameters.put(requestParam.getName(), getPropertyOrValue(requestParam.getValue()));
                    break;
                case HEADER:
                    headers.put(requestParam.getName(), getPropertyOrValue(requestParam.getValue()));
                    break;
                case BODY:
                    String folderNameForRequestBodies = loadProperty("jsonBody", "restBodies");
                    String path = String.join(File.separator, "src", "main", "java", folderNameForRequestBodies, getPropertyOrValue(requestParam.getValue()));
                    try (FileReader fileReader = new FileReader(path)) {
                        JsonElement json = gson.fromJson(fileReader, JsonElement.class);
                        body = gson.toJson(json);
                    } catch (IOException e) {
                        body = getPropertyOrValue(requestParam.getValue());
                    }
                    break;
                default:
                    throw new RuntimeException("Некорректно задан элемент таблицы : " + requestParam.getType());
            }
        }
        RequestSender request;
        if (body != null) {
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
        return request;
    }

    private void getResponseAndSaveToVariable(String variableName, Response response) {
        if (200 <= response.statusCode() && response.statusCode() < 300) {
            alfaScenario.setVar(variableName, response.getBody().asString());
            if (log.isDebugEnabled()) alfaScenario.write("Тело ответа : \n" + response.getBody().asString());
        } else {
            fail("Некорректный ответ на запрос: " + response.getBody().asString());
        }
    }

    public static String getURLwithPathParamsCalculated(String urlName) {
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(urlName);
        String newString = "";
        while (m.find()) {
            String varName = m.group(1);
            String value = loadProperty(varName, (String)AlfaScenario.getInstance().tryGetVar(varName));
            if (value == null)
                throw new IllegalArgumentException(
                        "Значение " + varName +
                                " не было найдено ни в application.properties, ни и в environment переменной");
            newString = m.replaceFirst(value);
            m = p.matcher(newString);
        }
        if (newString.isEmpty()) {
            newString = urlName;
        }
        return newString;
    }

    public boolean checkStatusCode(String typeOfRequest, String urlName, int expectedStatusCode, List<RequestParam> table) throws Exception {
        urlName = getURLwithPathParamsCalculated(urlName);
        RequestSender request = createRequestByParamsTable(table);
        Response response = request.request(Method.valueOf(typeOfRequest), urlName);
        int statusCode = response.getStatusCode();
        if (statusCode != expectedStatusCode) {
            write("Ожидали статус код: " + expectedStatusCode + ". Получили: " + statusCode);
        }
        return statusCode == expectedStatusCode;
    }

    private void sendHttpRequest(String typeOfRequest, String urlName, String variableName, List<RequestParam> table) throws Exception {
        String valueIfNotFoundInProperties = getURLwithPathParamsCalculated(urlName);
        urlName = loadProperty(urlName, valueIfNotFoundInProperties);
        RequestSender request = createRequestByParamsTable(table);
        Response response = request.request(Method.valueOf(typeOfRequest), urlName);
        getResponseAndSaveToVariable(variableName, response);
    }
}
