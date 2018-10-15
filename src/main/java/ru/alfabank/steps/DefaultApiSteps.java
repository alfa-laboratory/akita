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

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import cucumber.api.DataTable;
import cucumber.api.java.ru.И;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.tests.core.rest.RequestParam;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveJsonVars;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;
import static ru.alfabank.tests.core.helpers.PropertyLoader.*;
import java.util.ArrayList;
import java.util.List;

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
     * В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сравниваются с ожидаемыми значением во втором и последующих столбцах таблицы.
     * Шаг работает со строками, числами, с литералами true, false и null. А также с массивами, состоящими из этих элементов.
     * При сравнении массива значений в таблице в первом столбце указывается jsonpath к массиву,
     * а в последующих столбцах значения содержащиеся в массиве.
     */
    @И("^в json строке из переменной \"([^\"]*)\" значения найденные по jsonpath в таблице равны значениям$")
    public void checkValuesInJsonAsString(String jsonVar, DataTable dataTable) {
        String json = (String) akitaScenario.getVar(jsonVar);
        ReadContext ctx = JsonPath.parse(json);
        boolean error = false;
        for (List<String> row : dataTable.raw()){
            String jsonPath = row.get(0);
            //Если jsonpath содержит одно значение
            if (row.size() == 2) {
                String expectedValue = row.get(1);
                String actualValue;
                try {
                    actualValue = ctx.read(jsonPath, String.class);
                    actualValue = actualValue != null ? actualValue : "null";
                } catch (PathNotFoundException e) {
                    akitaScenario.write("В json'e не найдено значение по указанному jsonPath " + jsonPath);
                    error = true;
                    continue;
                }
                if (!expectedValue.equals(actualValue)) {
                    akitaScenario.write("JsonPath - \"" + jsonPath + "\", ожидаемое значение - \"" + expectedValue + "\",  фактическое значение элемента - \"" + actualValue + "\"");
                    error = true;
                }
            }
            //Если jsonpath содержит массив значений
            else if (row.size() > 2) {
                List<Object> actualListValueObject;
                List<String> actualListValue = new ArrayList<>();
                List<String> expectedListValue = new ArrayList<>(row);
                expectedListValue.remove(0);  //Удаляем из списка jsopath, остаются только искомые значения
                try {
                    actualListValueObject = ctx.read(jsonPath);
                    actualListValueObject.forEach(object -> actualListValue.add(object != null ? object.toString() : "null"));
                } catch (PathNotFoundException e) {
                    System.out.println("В json'e не найдено значение по указанному jsonPath " + jsonPath);
                    error = true;
                    continue;
                }
                if (actualListValue.size() != expectedListValue.size()) {
                    error = true;
                    akitaScenario.write("Разное количество элементов");
                    continue;
                }
                if (actualListValue.stream().filter(value -> !expectedListValue.contains(value)).count() != 0) {
                    akitaScenario.write("JsonPath - \"" + jsonPath + "\", ожидаемое значение - \"" + expectedListValue + "\",  фактическое значение элемента - \"" + actualListValue + "\"");
                    error = true;
                }
            }
            else {
                akitaScenario.write("В таблице должно быть больше одного столбца");
                error = true;
            }
        }
        if (error)
            throw new RuntimeException("Ожидаемые и фактические значения в json не совпадают");
    }

    /**
     * В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Названия переменных указываются во втором и последующих столбцах таблицы.
     * Шаг работает со строками, числами, с литералами true, false и null. А также с массивами, состоящими из этих элементов.
     * При сравнении массива значений в таблице в первом столбце указывается jsonpath к массиву,
     * а в последующих столбцах названия переменных в которые сохраняются значения из массива.
     */
    @И("^в json строке из переменной \"([^\"]*)\" значения найденные по jsonpath в таблице сохранены в переменные$")
    public void getValuesFromJsonAsString(String jsonVar, DataTable dataTable) {
        String json = (String) akitaScenario.getVar(jsonVar);
        ReadContext ctx = JsonPath.parse(json);
        boolean error = false;
        //Если jsonpath содержит одно значение
        for (List<String> row : dataTable.raw()){
            String jsonPath = row.get(0);
            if (row.size() == 2) {
                String varName = row.get(1);
                String value;
                try {
                    value = ctx.read(jsonPath, String.class);
                    value = value != null ? value : "null";
                } catch (PathNotFoundException e) {
                    akitaScenario.write("В json'e не найдено значение по указанному jsonPath " + jsonPath);
                    error = true;
                    continue;
                }
                akitaScenario.setVar(varName, value);
            }
            //Если jsonpath содержит массив значений
            else if (row.size() > 2) {
                List<Object> listValueObject;
                List<String> listValue = new ArrayList<>();
                List<String> listVarName = new ArrayList<>(row);
                listVarName.remove(0); //Удаляем из списка jsopath, остаются только названия переменных
                try {
                    listValueObject = ctx.read(jsonPath);
                    listValueObject.forEach(object -> listValue.add(object != null ? object.toString() : "null"));
                } catch (PathNotFoundException e) {
                    akitaScenario.write("В json'e не найдено значение по указанному jsonPath " + jsonPath);
                    error = true;
                    continue;
                }
                if (listValue.size() != listVarName.size()) {
                    error = true;
                    akitaScenario.write("JsonPath - \"" + jsonPath + "\", количество элементов в массиве - \"" + listValue.size() + "\",  количество наименований переменных из таблицы - \"" + listVarName.size() + "\"");
                    continue;
                }
                for (int i = 0; i <= listValue.size() - 1; i++) {
                    akitaScenario.setVar(listVarName.get(i), listValue.get(i));
                }
            }
            else {
                akitaScenario.write("В таблице должно быть больше одного столбца");
                error = true;
            }
        }
        if (error)
            throw new RuntimeException("Ожидаемые и фактические значения в json не совпадают");
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
            String value = resolveJsonVars(requestParam.getValue());
            String name = requestParam.getName();
            switch (requestParam.getType()) {
                case PARAMETER:
                    request.param(name, value);
                    break;
                case HEADER:
                    request.header(name, value);
                    break;
                case BODY:
                    request.body(loadValueFromFileOrPropertyOrVariableOrDefault(value));
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
     * @param method      тип http запроса
     * @param address     url, на который будет направлен запроc
     * @param paramsTable список параметров для http запроса
     */
    public Response sendRequest(String method, String address,
                                List<RequestParam> paramsTable) {
        address = loadProperty(address, resolveVars(address));
        RequestSender request = createRequest(paramsTable);
        return request.request(Method.valueOf(method), address);
    }
}