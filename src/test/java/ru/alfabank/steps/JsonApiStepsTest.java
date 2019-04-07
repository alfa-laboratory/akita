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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import cucumber.api.DataTable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.alfabank.util.DataTableUtils.dataTableFromLists;

public class JsonApiStepsTest {
    private static ApiSteps api;
    private static AkitaScenario akitaScenario;

    @BeforeAll
    static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        api = new ApiSteps();
        akitaScenario.setEnvironment(new AkitaEnvironment(new StubScenario()));
    }

    @AfterAll
    static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    void shouldCheckValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object2.number", "0.003"));
        List<String> row2 = new ArrayList<>(Arrays.asList("$.object2.string", "\"stringValue\""));
        List<String> row3 = new ArrayList<>(Arrays.asList("$.object2.boolean", "true"));
        List<String> row4 = new ArrayList<>(Arrays.asList("$.object2.nullName", "null"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        allLists.add(row2);
        allLists.add(row3);
        allLists.add(row4);
        DataTable dataTable = dataTableFromLists(allLists);

        api.checkValuesInJsonAsString("strJson", dataTable);
    }

    @Test
    void shouldCheckObjectValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object1", "   { \"innerObject\":\n {\"str\": \"qwer\"}, \"array\" : [\"stringInArray\",   0.003, true, false, null] }"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);
        api.checkValuesInJsonAsString("strJson", dataTable);
    }

    @Test
    void shouldCheckArray1ValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$..number", "[0.003, -3579.09]"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);
        api.checkValuesInJsonAsString("strJson", dataTable);
    }

    @Test
    void shouldCheckArray2ValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object1.array", "[\"stringInArray\",0.003,true,false,null]"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        api.checkValuesInJsonAsString("strJson", dataTable);
    }

    @Test
    void shouldThrowRuntimeExceptionIfValuesNotMatchWhenCheckValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$..number", "[0.003, -3579.09, 4]"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        assertThrows(RuntimeException.class, () ->
                api.checkValuesInJsonAsString("strJson", dataTable));
    }

    @Test
    void shouldThrowRuntimeExceptionIfPathNotFoundWhenCheckValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object1.farebea", "0.003"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        assertThrows(RuntimeException.class, () ->
                api.checkValuesInJsonAsString("strJson", dataTable));
    }

    @Test
    void shouldGetValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object2.number", "numberValue"));
        List<String> row2 = new ArrayList<>(Arrays.asList("$.object2.string", "stringValue"));
        List<String> row3 = new ArrayList<>(Arrays.asList("$.object2.boolean", "booleanValue"));
        List<String> row4 = new ArrayList<>(Arrays.asList("$.object2.nullName", "nullValue"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        allLists.add(row2);
        allLists.add(row3);
        allLists.add(row4);
        DataTable dataTable = dataTableFromLists(allLists);

        api.getValuesFromJsonAsString("strJson", dataTable);

        assertEquals(createJsonElementAndReturnString("0.003"), akitaScenario.getVar("numberValue"));
        assertEquals(createJsonElementAndReturnString("stringValue"), akitaScenario.getVar("stringValue"));
        assertEquals(createJsonElementAndReturnString("true"), akitaScenario.getVar("booleanValue"));
        assertEquals(createJsonElementAndReturnString("null"), akitaScenario.getVar("nullValue"));
    }

    @Test
    void shouldGetArray1ValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$..number", "numbers"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        api.getValuesFromJsonAsString("strJson", dataTable);

        assertEquals(createJsonElementAndReturnString("[0.003,\n -3579.09]"), akitaScenario.getVar("numbers"));
    }

    @Test
    void shouldGetArray2ValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object1.array", "array"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        api.getValuesFromJsonAsString("strJson", dataTable);

        assertEquals(createJsonElementAndReturnString("[\"stringInArray\",0.003,true,   false,null]"), akitaScenario.getVar("array"));
    }

    @Test
    void shouldThrowRuntimeExceptionIfPathNotFoundWhenGetValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object3.dsfbfsb", "number1"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        assertThrows(RuntimeException.class, () ->
                api.getValuesFromJsonAsString("strJson", dataTable));
    }

    private String createJsonElementAndReturnString(String element) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(element);
        return jsonElement.toString();
    }
}