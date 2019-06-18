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
package ru.alfabank.other;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.ScopedVariables;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaPage;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.steps.WebPageInteractionSteps;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class AkitaScenarioTest {
    private static AkitaScenario akitaScenario;

    @BeforeAll
    static void init() {
        akitaScenario = AkitaScenario.getInstance();
    }

    @AfterAll
    static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @BeforeEach
    void prepare() {
        Scenario scenario = new StubScenario();
        AkitaPage akitaPageMock = mock(AkitaPage.class);
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
    }

    @Test
    void testGetSetVarNegative1() {
        String notExistingVar = "randomName";
        assertThrows(IllegalArgumentException.class, () ->
                akitaScenario.getVar(notExistingVar));
    }

    @Test
    void testGetSetVar() {
        String varName = "varName";
        String varValue = "1234567891011";
        akitaScenario.setVar(varName, varValue);
        assertThat("Вернулось правильное значение сохраненной переменной",
                akitaScenario.getVar(varName).toString(), equalTo(varValue));
    }

    @Test
    void putGetPagesPositive() {
        AkitaScenario akitaScenario = AkitaScenario.getInstance();
        WebPageInteractionSteps wpis = new WebPageInteractionSteps();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
        akitaScenario.setCurrentPage(akitaScenario.getPage("AkitaPageMock"));
        assertThat(akitaScenario.getPage("AkitaPageMock"), is(notNullValue()));
        close();
    }

    @Test
    void putGetPagesNegative() {
        AkitaPageMock alfaPageMock = null;
        assertThrows(IllegalArgumentException.class, () ->
                akitaScenario.getPages().put("Mock", alfaPageMock));
    }

    @Test
    void getEnvironmentPositive() {
        assertThat(akitaScenario.getEnvironment(), is(notNullValue()));
    }

    @Test
    void getEnvironmentNegative() {
        akitaScenario.setEnvironment(null);
        assertThat(akitaScenario.getEnvironment(), is(nullValue()));
    }

    @Test
    void getCurrentPagePositive() {
        AkitaScenario akitaScenario = AkitaScenario.getInstance();
        WebPageInteractionSteps wpis = new WebPageInteractionSteps();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
        akitaScenario.setCurrentPage(akitaScenario.getPage("AkitaPageMock"));
        assertThat(akitaScenario.getCurrentPage(), is(notNullValue()));
        close();
    }

    @Test
    void setCurrentPageNegative() {
        assertThrows(IllegalArgumentException.class, () -> akitaScenario.setCurrentPage(null));
    }

    @Test
    void shouldReturnScenarioTest() {
        Scenario actualScenario = akitaScenario.getScenario();
        assertEquals("My scenario", actualScenario.getName());
    }

    @Test
    void getVarsTest() {
        akitaScenario.setVar("1", "1");
        akitaScenario.setVar("2", "2");
        ScopedVariables scopedVariables = akitaScenario.getVars();
        assertThat((String) scopedVariables.get("1") + (String) scopedVariables.get("2"),
                equalTo("12"));
    }

}
