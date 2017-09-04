package ru.alfabank.steps;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import cucumber.api.java.ru.И;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ru.alfabank.tests.core.helpers.PropertyLoader.getPropertyOrValue;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Шаги для тестирования API, доступные по умолчанию в каждом новом проекте
 */

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
     * Посылается http GET/POST запрос по заданному урлу без параметров и BODY.
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
     * Посылается http GET/POST запрос по заданному урлу с заданными параметрами.
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
     * Посылается http GET/POST запрос по заданному урлу с заданными параметрами.
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

    /**
     * Создание запроса
     *
     * @param table массив с параметрами
     * @return сформированный запрос
     */
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
                    throw new IllegalArgumentException(String.format("Некорректно задан тип %s для параметра запроса %s ", requestParam.getType(), requestParam.getName()));
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

    /**
     * Сохраняет ответ на http-запрос и сохраняет в переменную
     *
     * @param variableName имя переменной, в которую будет сохранен ответ
     * @param response     ответ от http запроса
     */
    private void getResponseAndSaveToVariable(String variableName, Response response) {
        if (200 <= response.statusCode() && response.statusCode() < 300) {
            alfaScenario.setVar(variableName, response.getBody().asString());
            if (log.isDebugEnabled()) alfaScenario.write("Тело ответа : \n" + response.getBody().asString());
        } else {
            fail("Некорректный ответ на запрос: " + response.getBody().asString());
        }
    }

    /**
     * Производит поиск в заданом url на наличие совпадений параметров.
     * В случае нахождения параметра в url заменяет его значение на значение из properties
     *
     * @param urlName заданный url
     * @return новый url
     */
    public static String getURLwithPathParamsCalculated(String urlName) {
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(urlName);
        String newString = "";
        while (m.find()) {
            String varName = m.group(1);
            String value = loadProperty(varName, (String) AlfaScenario.getInstance().tryGetVar(varName));
            if (value == null)
                throw new IllegalArgumentException(
                        "Значение " + varName +
                                " не было найдено ни в application.properties, ни в environment переменной");
            newString = m.replaceFirst(value);
            m = p.matcher(newString);
        }
        if (newString.isEmpty()) {
            newString = urlName;
        }
        return newString;
    }

    /**
     * Отправка зарпоса и сравнение кода http ответа
     *
     * @param typeOfRequest      тип http запроса
     * @param urlName            url хоста на который будет отправлен запрос
     * @param expectedStatusCode ожидаемый http статус код
     * @param table              список параметров для http запроса
     * @return возвращает true или false в зависимости от ожидаемого и полученного http кодов
     * @throws Exception
     */
    public boolean checkStatusCode(String typeOfRequest, String urlName, int expectedStatusCode, List<RequestParam> table) throws Exception {
        urlName = getURLwithPathParamsCalculated(urlName);
        RequestSender request = createRequestByParamsTable(table);
        Response response = request.request(Method.valueOf(typeOfRequest), urlName);
        int statusCode = response.getStatusCode();
        if (statusCode != expectedStatusCode) {
            write("Получен неверный статус код ответа " + statusCode + ". Ожидаемый статус код " + expectedStatusCode);
        }
        return statusCode == expectedStatusCode;
    }

    /**
     * @param typeOfRequest тип http запроса
     * @param urlName       url хоста на который будет направлен запрос
     * @param variableName  имя переменной в которую сохраняется http ответ
     * @param table         список параметров для http запроса
     * @throws Exception
     */
    private void sendHttpRequest(String typeOfRequest, String urlName, String variableName, List<RequestParam> table) throws Exception {
        String valueIfNotFoundInProperties = getURLwithPathParamsCalculated(urlName);
        urlName = loadProperty(urlName, valueIfNotFoundInProperties);
        RequestSender request = createRequestByParamsTable(table);
        Response response = request.request(Method.valueOf(typeOfRequest), urlName);
        getResponseAndSaveToVariable(variableName, response);
    }
}
