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

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.alfabank.util.DataTableUtils.dataTableFromLists;

public class RoundUpStepsTest {

    private static AkitaScenario akitaScenario;
    private static WebPageInteractionSteps wpis;
    private static RoundUpSteps rus;
    private static ElementsVerificationSteps elis;

    @BeforeAll
    static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        wpis = new WebPageInteractionSteps();
        elis = new ElementsVerificationSteps();
        rus = new RoundUpSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }

    @BeforeEach
    void prepare() {
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @Test
    void compareTwoDigitVarsNegative() {
        String number1Name = "number1", number1Value = "1234567890";
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567894";
        akitaScenario.setVar(number2Name, number2Value);

        assertThrows(AssertionError.class, () ->
                rus.compareTwoVariables(number1Name, number2Name));
    }

    @Test
    void compareTwoDigitVars() {
        String number1Name = "number1", number1Value = "1234567890.97531";
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567890.97531";
        akitaScenario.setVar(number2Name, number2Value);

        rus.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    void testCompareTwoDigitVarsAnotherNegative() {
        String number1Name = "number1", number1Value = null;
        akitaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = null;
        akitaScenario.setVar(number2Name, number2Value);

        assertThrows(IllegalArgumentException.class, () ->
                rus.compareTwoVariables(number1Name, number2Name));
    }

    @Test
    void testCheckingTwoVariablesAreNotEqualsPositive() {
        String variable1Name = "number1";
        int variable1Value = 666;
        akitaScenario.setVar(variable1Name, variable1Value);

        String variable2Name = "number2";
        int variable2Value = 123;
        akitaScenario.setVar(variable2Name, variable2Value);
        rus.checkingTwoVariablesAreNotEquals(variable1Name, variable2Name);
    }

    @Test
    void testCheckingTwoVariablesAreNotEqualsNegative() {
        String variable1Name = "number1";
        int variable1Value = 666;
        akitaScenario.setVar(variable1Name, variable1Value);

        String variable2Name = "number2";
        int variable2Value = 666;
        akitaScenario.setVar(variable2Name, variable2Value);
        assertThrows(AssertionError.class, () ->
                rus.checkingTwoVariablesAreNotEquals(variable1Name, variable2Name));
    }

    @Test
    void testCheckingTwoVariablesAreNotEqualsAnotherNegative() {
        String variable1Name = "number1", variable1Value = null;
        akitaScenario.setVar(variable1Name, variable1Value);

        String variable2Name = "number2", variable2Value = null;
        akitaScenario.setVar(variable2Name, variable2Value);

        assertThrows(IllegalArgumentException.class, () ->
                rus.checkingTwoVariablesAreNotEquals(variable1Name, variable2Name));
    }

    @Test
    void saveValueToVarPositive() {
        rus.saveValueToVar("testVar", "test");
        assertThat(akitaScenario.getVar("test"), equalTo("customTestValue"));
    }

    @Test
    void testCheckIfValueFromVariableEqualPropertyVariablePositive() {
        akitaScenario.setVar("timeout", "1000");
        rus.checkIfValueFromVariableEqualPropertyVariable("timeout", "waitingAppearTimeout");
    }

    @Test
    void testCheckIfValueFromVariableEqualPropertyVariableNegative() {
        akitaScenario.setVar("timeout", "500");
        assertThrows(AssertionError.class, () ->
                rus.checkIfValueFromVariableEqualPropertyVariable("timeout", "waitingAppearTimeout"));
    }

    @Test
    void testfillTemplate() {
        String templateName = "strTemplate";
        String varName = "varName";
        List<String> row1 = new ArrayList<>(Arrays.asList("_name_", "Jack"));
        List<String> row2 = new ArrayList<>(Arrays.asList("_age_", "35"));
        List<List<String>> allLists = new ArrayList<>();
        allLists.add(row1);
        allLists.add(row2);
        DataTable dataTable = dataTableFromLists(allLists);

        rus.fillTemplate(templateName, varName, dataTable);
        assertEquals("{\"name\": \"Jack\", \"age\": 35}", akitaScenario.getVar(varName));
    }

    @Test
    void pushButtonOnKeyboardSimple() {
        rus.pushButtonOnKeyboard("alt");
    }

    @Test
    void setVariableTest() {
        rus.setVariable("ul", "Serious testing page");
        assertThat(akitaScenario.getVar("ul"), equalTo("Serious testing page"));
    }

    @Test
    void testTestScript() {
        rus.executeJsScript("HIDEnSHOW()");
        elis.elementIsNotVisible("ul");
    }

    @Test
    void expressionExpressionPositive() {
        rus.expressionExpression("\"test\".equals(\"test\")");
    }
}