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
import cucumber.api.Scenario;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaPage;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.steps.DefaultSteps;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.alfabank.alfatest.cucumber.api.AkitaPage.getButtonFromListByName;

public class AkitaPageTest {
    private static AkitaPageMock alfaPageMock;
    private static AkitaPage page;

    @BeforeClass
    public static void setup() {
        alfaPageMock = new AkitaPageMock();
        AkitaScenario akitaScenario = AkitaScenario.getInstance();
        DefaultSteps ds = new DefaultSteps();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        page = akitaScenario.getEnvironment().getPage("AkitaPageMock");
        ds.goToSelectedPageByLinkFromPropertyFile("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @Test
    public void getBlockPositive() {
        assertThat(page.getBlock("SearchBlock"), is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBlockNegative() {
        page.getBlock("Not exact Block");
    }

    @Test(expected = NullPointerException.class)
    public void getElementNegative() {
        alfaPageMock.getElement("test");
    }

    @Test(expected = NullPointerException.class)
    public void getElementsListNegative() {
        alfaPageMock.getElementsList("test");
    }

    @Test(expected = NullPointerException.class)
    public void getAnyElementTextNegative() {
        alfaPageMock.getAnyElementText("test");
    }

    @Test(expected = NullPointerException.class)
    public void getAnyElementsListTextsNegative() {
        alfaPageMock.getAnyElementsListTexts("test");
    }

    @Test
    public void appearedPositive() {
        assertThat(alfaPageMock.appeared(), equalTo(alfaPageMock));
    }

    @Test
    public void disappearedNegative() {
        assertThat(alfaPageMock.disappeared(), equalTo(alfaPageMock));
    }

    @Test(expected = NullPointerException.class)
    public void waitElementsUntilNegative() {
        alfaPageMock.waitElementsUntil(Condition.appear, 1, "test");
    }

    @Test(expected = NullPointerException.class)
    public void getButtonFromListByNameNegative() {
        SelenideElement selenideElementMock = alfaPageMock.getMockCss();
        List<SelenideElement> list = new LinkedList<>();
        list.add(selenideElementMock);
        getButtonFromListByName(list, "test");
    }

    @Test
    public void getElementPositive() {
        assertThat(page.getElement("GoodButton"), is(notNullValue()));
    }

    @Test
    public void getElementsListPositive() {
        assertThat(page.getElementsList("List"), is(notNullValue()));
    }

    @Test
    public void getAnyElementTextPositive() {
        assertThat(page.getAnyElementText("TextField"), equalTo("text"));
    }

    @Test
    public void getAnyElementsListTextsPositive() {
        assertThat(page.getAnyElementsListTexts("List").toString(),
                equalTo("[Three, One, Two]"));
    }

    @Test
    public void waitElementsUntilPositive() {
        page.waitElementsUntil(Condition.disappear, 1, "HiddenDiv");
    }

    @Ignore
    @Test
    public void getButtonFromListByNamePositive() {
        SelenideElement selenideElement = alfaPageMock.getGoodButton();
        List<SelenideElement> list = new LinkedList<>();
        list.add(selenideElement);
        assertThat(getButtonFromListByName(list, "GoodButton"), is(notNullValue()));
    }
}
