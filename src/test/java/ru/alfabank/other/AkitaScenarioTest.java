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
package ru.alfabank.other;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.*;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaPage;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.steps.DefaultSteps;

import java.io.File;

import static com.codeborne.selenide.Selenide.open;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class AkitaScenarioTest {
    private static AkitaScenario akitaScenario;

    @BeforeClass
    public static void init() {
        akitaScenario = AkitaScenario.getInstance();
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Before
    public void prepare() {
        Scenario scenario = new StubScenario();
        AkitaPage akitaPageMock = mock(AkitaPage.class);
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSetVarNegative1() {
        String notExistingVar = "randomName";
        akitaScenario.getVar(notExistingVar);
    }

    @Test
    public void testGetSetVar() {
        String varName = "varName";
        String varValue = "1234567891011";
        akitaScenario.setVar(varName, varValue);
        assertThat("Вернулось правильное значение сохраненной переменной",
                akitaScenario.getVar(varName).toString(), equalTo(varValue));
    }

    @Test
    public void putGetPagesPositive() {
        AkitaScenario akitaScenario = AkitaScenario.getInstance();
        DefaultSteps ds = new DefaultSteps();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        ds.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
        akitaScenario.setCurrentPage(akitaScenario.getPage("AkitaPageMock"));
        assertThat(akitaScenario.getPage("AkitaPageMock"), is(notNullValue()));
        close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void putGetPagesNegative() {
        AkitaPageMock alfaPageMock = null;
        akitaScenario.getPages().put("Mock", alfaPageMock);
    }

    @Test
    public void getEnvironmentPositive() {
        assertThat(akitaScenario.getEnvironment(), is(notNullValue()));
    }

    @Test
    public void getEnvironmentNegative() {
        akitaScenario.setEnvironment(null);
        assertThat(akitaScenario.getEnvironment(), is(nullValue()));
    }

    @Test
    public void getCurrentPagePositive() {
        AkitaScenario akitaScenario = AkitaScenario.getInstance();
        DefaultSteps ds = new DefaultSteps();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        ds.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
        akitaScenario.setCurrentPage(akitaScenario.getPage("AkitaPageMock"));
        assertThat(akitaScenario.getCurrentPage(), is(notNullValue()));
        close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCurrentPageNegative() {
        akitaScenario.setCurrentPage(null);
    }

    @Test
    public void shouldReturnScenarioTest() {
        Scenario actualScenario = akitaScenario.getScenario();
        Assert.assertEquals("My scenario", actualScenario.getName());
    }
}
