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

import cucumber.api.Scenario;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaPage;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.alfatest.cucumber.api.Pages;
import ru.alfabank.steps.DefaultSteps;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PagesTest {
    private static Pages pages;
    private static AkitaPageMock akitaPageMock;

    @BeforeClass
    public static void init() {
        pages = new Pages();
        akitaPageMock = new AkitaPageMock();

        AkitaScenario akitaScenario = AkitaScenario.getInstance();
        DefaultSteps ds = new DefaultSteps();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        ds.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @Test
    public void getSetCurrentPagePositive() {
        pages.setCurrentPage(akitaPageMock);
        assertThat(pages.getCurrentPage(), equalTo(akitaPageMock));
    }

    @Test(expected = IllegalStateException.class)
    public void setCurrentPageNegative() {
        pages.setCurrentPage(null);
        pages.getCurrentPage();
    }

    @Test
    public void getPutPositive() {
        pages.put("Test", akitaPageMock);
        assertThat(pages.get("Test"), equalTo(akitaPageMock));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNegative() {
        AkitaPage nullPage = null;
        pages.put("Test", nullPage);
    }

    @Test
    public void getNegative() {
        assertThat(pages.get("WRONG_KEY"), is(nullValue()));
    }
}
