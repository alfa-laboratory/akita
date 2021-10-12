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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.tests.core.rest.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadValueFromFileOrPropertyOrVariableOrDefault;

/**
 * Шаги для тестирования API, доступные по умолчанию в каждом новом проекте
 */

@Slf4j
public class ApiSteps extends BaseMethods {

    private final AkitaScenario akitaScenario = AkitaScenario.getInstance();

    /**
     * Посылается http запрос по заданному урлу без параметров и BODY.
     * Результат сохраняется в заданную переменную
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\". Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    @And("^(GET|POST|PUT|DELETE) request to URL \"([^\"]*)\" has been executed. Response has been saved to the variable named \"([^\"]*)\"$")
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
    @And("^(GET|POST|PUT|DELETE) request to URL \"([^\"]*)\" with headers and parametres from the table has been executed. Response has been saved to the variable named \"([^\"]*)\"$")
    public void sendHttpRequestSaveResponse(String method, String address, String variableName, List<RequestParam> paramsTable) throws Exception {
        Response response = sendRequest(method, address, paramsTable);
        getBodyAndSaveToVariable(variableName, response);
    }

    /**
     * Посылается http запрос по заданному урлу без параметров и BODY.
     * Проверяется, что код ответа соответствует ожиданиям.
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\". Ожидается код ответа: (\\d+)$")
    @And("^(GET|POST|PUT|DELETE) request to URL \"([^\"]*)\" has been executed. Expected response code: (\\d+)$")
    public void checkResponseCodeWithoutParams(String method, String address, int expectedStatusCode) throws Exception {
        Response response = sendRequest(method, address, new ArrayList<>());
        assertTrue(checkStatusCode(response, expectedStatusCode));
    }

    /**
     * Посылается http запрос по заданному урлу с заданными параметрами.
     * Проверяется, что код ответа соответствует ожиданиям.
     * URL можно задать как напрямую в шаге, так и указав в application.properties
     * Content-Type при необходимости должен быть указан в качестве header.
     */
    @И("^выполнен (GET|POST|PUT|DELETE) запрос на URL \"([^\"]*)\" с headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
    @And("^(GET|POST|PUT|DELETE) request to URL \"([^\"]*)\" with headers and parametres from the table has been executed. Expected response code: (\\d+)$")
    public void checkResponseCode(String method, String address, int expectedStatusCode, List<RequestParam> paramsTable) throws Exception {
        Response response = sendRequest(method, address, paramsTable);
        assertTrue(checkStatusCode(response, expectedStatusCode));
    }

    /**
     * В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сравниваются с ожидаемым значением во втором столбце таблицы.
     * Шаг работает со всеми типами json элементов: объекты, массивы, строки, числа, литералы true, false и null.
     */
    @Тогда("^в json (?:строке|файле) \"([^\"]*)\" значения, найденные по jsonpath, равны значениям из таблицы$")
    @Then("^values from json (?:string|file) named \"([^\"]*)\" found via jsonpath are equal to the values from the table$")
    public void checkValuesInJsonAsString(String jsonVar, DataTable dataTable) {
        String strJson = loadValueFromFileOrPropertyOrVariableOrDefault(jsonVar);
        Gson gsonObject = new Gson();
        JsonParser parser = new JsonParser();
        ReadContext ctx = JsonPath.parse(strJson, createJsonPathConfiguration());
        boolean error = false;
        for (List<String> row : dataTable.raw()) {
            String jsonPath = row.get(0);
            JsonElement actualJsonElement;
            try {
                actualJsonElement = gsonObject.toJsonTree(ctx.read(jsonPath));
            } catch (PathNotFoundException e) {
                error = true;
                continue;
            }
            JsonElement expectedJsonElement = parser.parse(row.get(1));
            if (!actualJsonElement.equals(expectedJsonElement)) {
                error = true;
            }
            akitaScenario.write("JsonPath: " + jsonPath + ", ожидаемое значение: " + expectedJsonElement + ", фактическое значение: " + actualJsonElement);
        }
        if (error)
            throw new RuntimeException("Ожидаемые и фактические значения в json не совпадают");
    }

    /**
     * В json строке, сохраннённой в переменной, происходит поиск значений по jsonpath из первого столбца таблицы.
     * Полученные значения сохраняются в переменных. Название переменной указывается во втором столбце таблицы.
     * Шаг работает со всеми типами json элементов: объекты, массивы, строки, числа, литералы true, false и null.
     */
    @Тогда("^значения из json (?:строки|файла) \"([^\"]*)\", найденные по jsonpath из таблицы, сохранены в переменные$")
    @Then("^values from json (?:string|file) named \"([^\"]*)\" has been found via jsonpaths from the table and saved to the variables$")
    public void getValuesFromJsonAsString(String jsonVar, DataTable dataTable) {
        String strJson = loadValueFromFileOrPropertyOrVariableOrDefault(jsonVar);
        Gson gsonObject = new Gson();
        ReadContext ctx = JsonPath.parse(strJson, createJsonPathConfiguration());
        boolean error = false;
        for (List<String> row : dataTable.raw()) {
            String jsonPath = row.get(0);
            String varName = row.get(1);
            JsonElement jsonElement;
            try {
                jsonElement = gsonObject.toJsonTree(ctx.read(jsonPath));
            } catch (PathNotFoundException e) {
                error = true;
                continue;
            }
            akitaScenario.setVar(varName, jsonElement.toString());
            akitaScenario.write("JsonPath: " + jsonPath + ", значение: " + jsonElement + ", записано в переменную: " + varName);
        }
        if (error)
            throw new RuntimeException("В json не найдено значение по заданному jsonpath");
    }
}