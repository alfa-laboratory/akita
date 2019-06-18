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

import cucumber.api.Scenario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.steps.WebPageInteractionSteps;

import java.io.File;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AkitaEnvironmentTest {
    private static AkitaEnvironment env;
    private static WebPageInteractionSteps wpis;

    @BeforeAll
    static void prepare() {

        env = new AkitaEnvironment();
        AkitaScenario akitaScenario = AkitaScenario.getInstance();
        wpis = new WebPageInteractionSteps();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @Test
    void initPagesTest() {
        getWebDriver().navigate();
        assertThat(env.getPage("AkitaPageMock"), is(notNullValue()));
    }

    @Test
    void getVarsTest() {
        assertThat(env.getVars(), is(notNullValue()));
    }

    @Test
    void getSetVarPositive() {
        String testString = "TestString1";
        env.setVar("Test1", testString);
        assertThat(env.getVar("Test1"), equalTo(testString));
    }

    @Test
    void getSetVarNegative() {
        assertThat(env.getVar("Test"), is(nullValue()));
    }

    @Test
    void getPagesTest() {
        assertThat(env.getPages(), is(notNullValue()));
    }

    @Test
    void getPage() {
        AkitaPageMock alfaPageMockInstance = new AkitaPageMock();
        env.getPages().put("newAwesomePage", alfaPageMockInstance);
        assertThat(env.getPage("newAwesomePage"), is(alfaPageMockInstance));
    }
}
