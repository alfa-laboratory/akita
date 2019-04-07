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

import cucumber.api.Scenario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PageBlockStepsTest {

    private static AkitaScenario akitaScenario;
    private static WebPageInteractionSteps wpis;
    private static PageBlockSteps pbs;

    @BeforeAll
    static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        wpis = new WebPageInteractionSteps();
        pbs = new PageBlockSteps();
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
    void clickOnElementInBlockPositiveTest() {
        pbs.clickOnElementInBlock("SearchButton", "SearchBlock");
        assertFalse(akitaScenario.getPage("SearchBlock").getElement("SearchButton").isEnabled());
    }

    @Test
    void getElementsListInBlockPositiveTest() {
        pbs.getElementsList("AkitaTable", "Rows", "ListTable");
        assertNotNull(akitaScenario.getVar("ListTable"));
    }

    @Test
    void getListElementsTextInBlockPositiveTest() {
        pbs.getListElementsText("AkitaTable", "Rows", "ListTable");
        assertNotNull(akitaScenario.getVar("ListTable"));
    }
}