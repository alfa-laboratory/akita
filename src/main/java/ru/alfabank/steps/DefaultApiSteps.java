/**
 * Copyright 2017 Alfa Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.steps;

import cucumber.api.java.ru.И;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.tests.core.rest.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    private AkitaScenario akitaScenario = AkitaScenario.getInstance();

    /**
     * Посылается http запрос по заданному урлу без параметров и BODY.
     * Результат сохраняется в заданную переменную
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\". Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestWithoutParams(String method, String address, String variableName) throws Exception {
        Response response = sendRequest(method, address, new ArrayList<>());
        getBodyAndSaveToVariable(variableName, response);
    }

    /**
     * Посылается http запрос по заданному урлу с заданными параметрами.
     * И в URL, и в значениях в таблице можно использовать переменные и из application.properties, и из хранилища переменных
     * из AlfaScenario. Для этого достаточно заключить переменные в фигурные скобки, например: http://{hostname}?user={username}.
     * Content-Type при необходимости должен быть указан в качестве header.
     * Результат сохраняется в заданную переменную
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendHttpRequestSaveResponse(String method, String address, String variableName, List<RequestParam> paramsTable) throws Exception {
        Response response = sendRequest(method, address, paramsTable);
        getBodyAndSaveToVariable(variableName, response);
    }

    /**
     * Посылается http запрос по заданному урлу с заданными параметрами.
     * Проверяется, что код ответа соответствует ожиданиям.
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     * Content-Type при необходимости должен быть указан в качестве header.
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
    public void checkResponseCode(String method, String address, int expectedStatusCode, List<RequestParam> paramsTable) throws Exception {
        Response response = sendRequest(method, address, paramsTable);
        assertTrue(checkStatusCode(response, expectedStatusCode));
    }

    /**
     * Создание запроса
     *
     * @param paramsTable массив с параметрами
     * @return сформированный запрос
     */
    private RequestSender createRequest(List<RequestParam> paramsTable) {
        String body = null;
        RequestSpecification request = given();
        for (RequestParam requestParam : paramsTable) {
            String value = resolveVars(requestParam.getValue());
            String name = requestParam.getName();
            switch (requestParam.getType()) {
                case PARAMETER:
                    request.param(name, value);
                    break;
                case HEADER:
                    request.header(name, value);
                    break;
                case BODY:
                    String folder = getPropertyOrValue("requestBodies");
                    Path path = Paths.get("src", "main", "java", folder, value);
                    try {
                        body = new String(Files.readAllBytes(path), "UTF-8");
                    } catch (IOException e) {
                        body = value;
                    }
                    request.body(body);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Некорректно задан тип %s для параметра запроса %s ", requestParam.getType(), name));
            }
        }
        if (body != null) {
            akitaScenario.write("Тело запроса:\n" + body);
        }
        return request;
    }

    /**
     * Получает body из ответа и сохраняет в переменную
     *
     * @param variableName имя переменной, в которую будет сохранен ответ
     * @param response     ответ от http запроса
     */
    private void getBodyAndSaveToVariable(String variableName, Response response) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            akitaScenario.setVar(variableName, response.getBody().asString());
            if (log.isDebugEnabled()) akitaScenario.write("Тело ответа : \n" + response.getBody().asString());
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
        Pattern p = Pattern.compile("\\{([^{}]+)\\}");
        Matcher m = p.matcher(inputString);
        String newString = "";
        while (m.find()) {
            String varName = m.group(1);
            String value = loadProperty(varName, (String) AkitaScenario.getInstance().tryGetVar(varName));
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
     * Сравнение кода http ответа с ожидаемым
     *
     * @param response           ответ от сервиса
     * @param expectedStatusCode ожидаемый http статус код
     * @return возвращает true или false в зависимости от ожидаемого и полученного http кодов
     */
    public boolean checkStatusCode(Response response, int expectedStatusCode) {
        int statusCode = response.getStatusCode();
        if (statusCode != expectedStatusCode) {
            akitaScenario.write("Получен неверный статус код ответа " + statusCode + ". Ожидаемый статус код " + expectedStatusCode);
        }
        return statusCode == expectedStatusCode;
    }

    /**
     * Отправка http запроса
     *
     * @param method        тип http запроса
     * @param address       url, на который будет направлен запроc
     * @param paramsTable   список параметров для http запроса
     */
    private Response sendRequest(String method, String address,
                                     List<RequestParam> paramsTable) {
        address = loadProperty(address, resolveVars(address));
        RequestSender request = createRequest(paramsTable);
        return request.request(Method.valueOf(method), address);
    }
}