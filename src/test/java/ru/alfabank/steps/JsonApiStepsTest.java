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

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.DataTable;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import static ru.alfabank.util.DataTableUtils.dataTableFromLists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonApiStepsTest {
    private static DefaultApiSteps api;
    private static AkitaScenario akitaScenario;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        api = new DefaultApiSteps();
        akitaScenario.setEnvironment(new AkitaEnvironment(new StubScenario()));
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void shouldCheckValuesInJsonAsString() throws Exception {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object2.number", "0.003"));
        List<String> row2 = new ArrayList<>(Arrays.asList("$.object2.string", "stringValue"));
        List<String> row3 = new ArrayList<>(Arrays.asList("$.object2.boolean", "true"));
        List<String> row4 = new ArrayList<>(Arrays.asList("$.object2.nullName", "null"));
        List<List<String>> allLists= new ArrayList<>();
        allLists.add(row1);
        allLists.add(row2);
        allLists.add(row3);
        allLists.add(row4);
        DataTable dataTable = dataTableFromLists(allLists);

        api.checkValuesInJsonAsString("strJson", dataTable);
    }

    @Test
    public void shouldCheckArray1ValuesInJsonAsString() throws Exception {
        List<String> row1 = new ArrayList<>(Arrays.asList("$..number", "0.003", "-3579.09"));
        List<List<String>> allLists= new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);
        api.checkValuesInJsonAsString("strJson", dataTable);
    }

    @Test
    public void shouldCheckArray2ValuesInJsonAsString() throws Exception {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object1.array", "stringInArray", "0.003", "true", "false", "null"));
        List<List<String>> allLists= new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        api.checkValuesInJsonAsString("strJson", dataTable);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionIfValuesNotMatchWhenCheckValuesInJsonAsString() throws Exception {
        List<String> row1 = new ArrayList<>(Arrays.asList("$..number", "0.003", "12345"));
        List<List<String>> allLists= new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        api.checkValuesInJsonAsString("strJson", dataTable);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionIfPathNotFoundWhenCheckValuesInJsonAsString() {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object1.farebea", "0.003"));
        List<List<String>> allLists= new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        api.checkValuesInJsonAsString("strJson", dataTable);
    }

    @Test
    public void shouldGetValuesInJsonAsString() throws Exception {
        //
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object2.number", "numberValue"));
        List<String> row2 = new ArrayList<>(Arrays.asList("$.object2.string", "stringValue"));
        List<String> row3 = new ArrayList<>(Arrays.asList("$.object2.boolean", "booleanValue"));
        List<String> row4 = new ArrayList<>(Arrays.asList("$.object2.nullName", "nullValue"));
        List<List<String>> allLists= new ArrayList<>();
        allLists.add(row1);
        allLists.add(row2);
        allLists.add(row3);
        allLists.add(row4);
        DataTable dataTable = dataTableFromLists(allLists);

        api.getValuesFromJsonAsString("strJson", dataTable);

        Assert.assertEquals("0.003", (String) akitaScenario.getVar("numberValue"));
        Assert.assertEquals("stringValue", (String) akitaScenario.getVar("stringValue"));
        Assert.assertEquals("true", (String) akitaScenario.getVar("booleanValue"));
        Assert.assertEquals("null", (String) akitaScenario.getVar("nullValue"));
    }

    @Test
    public void shouldGetArrayValuesInJsonAsString() throws Exception {
        List<String> row1 = new ArrayList<>(Arrays.asList("$..number", "number1", "number2"));
        List<List<String>> allLists= new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        api.getValuesFromJsonAsString("strJson", dataTable);

        Assert.assertEquals("0.003", (String) akitaScenario.getVar("number1"));
        Assert.assertEquals("-3579.09", (String) akitaScenario.getVar("number2"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionIfPathNotFoundWhenGetValuesInJsonAsString() throws Exception {
        List<String> row1 = new ArrayList<>(Arrays.asList("$.object3.dsfbfsb", "number1"));
        List<List<String>> allLists= new ArrayList<>();
        allLists.add(row1);
        DataTable dataTable = dataTableFromLists(allLists);

        api.getValuesFromJsonAsString("strJson", dataTable);
    }

}