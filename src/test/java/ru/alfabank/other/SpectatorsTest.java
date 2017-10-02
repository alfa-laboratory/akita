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

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementShould;
import cucumber.api.Scenario;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.steps.DefaultSteps;

import java.io.File;
import java.util.LinkedList;

public class SpectatorsTest {
    private static AkitaScenario akitaScenario;
    private static AkitaPageMock page;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        DefaultSteps ds = new DefaultSteps();
        page = (AkitaPageMock) akitaScenario.getPage("AkitaPageMock");
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        ds.goToSelectedPageByLinkFromPropertyFile("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void waitElementsUntilPositiveElements() {
        SelenideElement mockId = page.mockId;
        SelenideElement mockCss = page.mockCss;
        akitaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                3000, mockCss, mockId);
    }

    @Test
    public void waitElementsUntilPositiveList() {
        SelenideElement mockId = page.mockId;
        SelenideElement mockCss = page.mockCss;
        LinkedList<SelenideElement> list = new LinkedList<>();
        list.add(mockCss);
        list.add(mockId);
        akitaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                3000, list);
    }

    @Test(expected = NullPointerException.class)
    public void waitElementsUntilNull() {
        SelenideElement nullElement = null;
        akitaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                3000, nullElement);
    }

    @Test(expected = ElementShould.class)
    public void waitElementsUntilNegative() {
        SelenideElement mockId = page.mockId;
        SelenideElement mockCss = page.mockCss;
        akitaScenario.getCurrentPage().waitElementsUntil(Condition.disappear,
                3000, mockCss, mockId);
    }

    @Test
    public void waitElementsUntilEmptyList() {
        LinkedList<SelenideElement> list = new LinkedList<>();
        akitaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                3000, list);
    }

    @Test(expected = NullPointerException.class)
    public void waitElementsUntilListNull() {
        SelenideElement nullElement1 = null;
        SelenideElement nullElement2 = null;
        LinkedList<SelenideElement> list = new LinkedList<>();
        list.add(nullElement1);
        list.add(nullElement2);
        akitaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                3000, list);
    }
}
