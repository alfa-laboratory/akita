package ru.alfabank.steps;

import cucumber.api.java.ru.И;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.tests.core.rest.RequestParam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public void sendHttpRequest(String typeOfRequest, String address, String variableName) throws Exception {
        String valueIfNotFoundInProperties = resolveVars(address);
        address = loadProperty(address, valueIfNotFoundInProperties);
        sendHttpRequest(typeOfRequest, address, variableName, new ArrayList<>());
    }

    /**
     * Посылается http GET/POST запрос по заданному урлу без параметров и BODY.
     * Результат сохраняется в заданную переменную
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     */
    @И("^выполнен (GET|POST) запрос на URL \"([^\"]*)\". Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestWithoutParams(String typeOfRequest, String address, String variableName) throws Exception {
        String valueIfNotFoundInProperties = resolveVars(address);
        address = loadProperty(address, valueIfNotFoundInProperties);
        sendHttpRequest(typeOfRequest, address, variableName, new ArrayList<>());
    }

    /**
     * Посылается http GET/POST запрос по заданному урлу с заданными параметрами.
     * И в URL, и в значениях в таблице можно использовать переменные и из application.properties, и из хранилища переменных
     * из AlfaScenario. Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     * Content-Type при необходимости должен быть указан в качестве header.
     * Результат сохраняется в заданную переменную
     */
    @И("^выполнен (GET|POST) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestSaveResponse(String typeOfRequest, String address, String variableName, List<RequestParam> paramsTable) throws Exception {
        String valueIfNotFoundInProperties = resolveVars(address);
        address = loadProperty(address, valueIfNotFoundInProperties);
        RequestSender request = createRequestByParamsTable(paramsTable);
        Response response = request.request(Method.valueOf(typeOfRequest), address);
        getResponseAndSaveToVariable(variableName, response);
    }

    /**
     * Посылается http GET/POST запрос по заданному урлу с заданными параметрами.
     * Проверяется, что код ответа соответствует ожиданиям.
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     * Content-Type при необходимости должен быть указан в качестве header.
     */
    @И("^выполнен (GET|POST) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
    public void checkResponseCode(String typeOfRequest, String address, int expectedStatusCode, List<RequestParam> paramsTable) throws Exception {
        String valueIfNotFoundInProperties = resolveVars(address);
        address = loadProperty(address, valueIfNotFoundInProperties);
        assertTrue(checkStatusCode(typeOfRequest, address, expectedStatusCode, paramsTable));
    }

    private RequestSender createRequestByParamsTable() {
        return given().when();
    }

    /**
     * Создание запроса
     *
     * @param paramsTable массив с параметрами
     * @return сформированный запрос
     */
    private RequestSender createRequestByParamsTable(List<RequestParam> paramsTable) {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        String body = null;
        for (RequestParam requestParam : paramsTable) {
            String paramValue = resolveVars(requestParam.getValue());
            String paramName = requestParam.getName();
            switch (requestParam.getType()) {
                case PARAMETER:
                    parameters.put(paramName, paramValue);
                    break;
                case HEADER:
                    headers.put(paramName, paramValue);
                    break;
                case BODY:
                    String folderNameForRequestBodies = getPropertyOrValue("requestBodies");
                    Path path = Paths.get(File.separator, "src", "main", "java", folderNameForRequestBodies, paramValue);
                    try {
                        body = new String(Files.readAllBytes(path), "UTF-8");
                    } catch (IOException e) {
                        body = paramValue;
                    }
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Некорректно задан тип %s для параметра запроса %s ", requestParam.getType(), paramName));
            }
        }
        RequestSender request;
        if (body != null) {
            alfaScenario.write("Тело запроса:\n" + body);
            request = given()
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
     * Получает ответ на http запрос и сохраняет в переменную
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
     * Производит поиск в заданной строке на наличие совпадений параметров.
     * В случае нахождения параметра в строке заменяет его значение на значение из properties или хранилище переменных
     *
     * @param inputString заданная строка
     * @return новая строка
     */
    public static String resolveVars(String inputString) {
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(inputString);
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
            newString = inputString;
        }
        return newString;
    }

    /**
     * Отправка запроса и сравнение кода http ответа
     *
     * @param typeOfRequest      тип http запроса
     * @param address            url, на который будет отправлен запрос
     * @param expectedStatusCode ожидаемый http статус код
     * @param paramsTable        список параметров для http запроса
     * @return возвращает true или false в зависимости от ожидаемого и полученного http кодов
     * @throws Exception
     */
    public boolean checkStatusCode(String typeOfRequest, String address, int expectedStatusCode, List<RequestParam> paramsTable) throws Exception {
        address = resolveVars(address);
        RequestSender request = createRequestByParamsTable(paramsTable);
        Response response = request.request(Method.valueOf(typeOfRequest), address);
        int statusCode = response.getStatusCode();
        if (statusCode != expectedStatusCode) {
            write("Получен неверный статус код ответа " + statusCode + ". Ожидаемый статус код " + expectedStatusCode);
        }
        return statusCode == expectedStatusCode;
    }


    /**
     * @param typeOfRequest тип http запроса
     * @param address       url, на который будет направлен запрос
     * @param variableName  имя переменной, в которую сохраняется http ответ
     * @param paramsTable   список параметров для http запроса
     * @throws Exception
     */
    private void sendHttpRequest(String typeOfRequest, String address, String
            variableName, List<RequestParam> paramsTable) throws Exception {
        String valueIfNotFoundInProperties = resolveVars(address);
        address = loadProperty(address, valueIfNotFoundInProperties);
        RequestSender request = createRequestByParamsTable(paramsTable);
        Response response = request.request(Method.valueOf(typeOfRequest), address);
        getResponseAndSaveToVariable(variableName, response);
    }
}
