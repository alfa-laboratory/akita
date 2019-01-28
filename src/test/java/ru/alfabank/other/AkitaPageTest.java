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
import cucumber.api.Scenario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaPage;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.steps.WebPageInteractionSteps;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.alfabank.alfatest.cucumber.api.AkitaPage.getButtonFromListByName;

public class AkitaPageTest {
    private static AkitaPageMock akitaPageMock;
    private static AkitaPage page;
    private static WebPageInteractionSteps wpis;

    @BeforeAll
    public static void setup() {
        wpis = new WebPageInteractionSteps();
        akitaPageMock = new AkitaPageMock();
        AkitaScenario akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        page = akitaScenario.getEnvironment().getPage("AkitaPageMock");
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterAll
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void getBlockPositive() {
        assertThat(page.getBlock("SearchBlock"), is(notNullValue()));
    }

    @Test
    public void getBlockElementsPositive() {
        List<SelenideElement> selenideElements = page.getBlockElements("SearchBlock");
        assertThat(selenideElements, is(notNullValue()));
    }

    @Test
    public void getBlockElementPositive() {
        assertThat(page.getBlockElement("SearchBlock", "SearchButton"), is(notNullValue()));
    }

    @Test
    public void getBlockNegative() {
        assertThrows(IllegalArgumentException.class, () -> page.getBlock("Not exact Block"));
    }

    @Test
    public void getElementNegative() {
        assertThrows(NullPointerException.class, () -> akitaPageMock.getElement("test"));
    }

    @Test
    public void getElementsListNegative() {
        assertThrows(NullPointerException.class, () -> akitaPageMock.getElementsList("test"));
    }

    @Test
    public void getAnyElementTextNegative() {
        assertThrows(NullPointerException.class, () -> akitaPageMock.getAnyElementText("test"));
    }

    @Test
    public void getAnyElementsListTextsNegative() {
        assertThrows(NullPointerException.class, () ->akitaPageMock.getAnyElementsListTexts("test"));
    }

    @Test
    public void appearedPositive() {
        assertEquals(akitaPageMock, akitaPageMock.appeared());
    }

    @Test
    public void disappearedNegative() {
        assertEquals(akitaPageMock, akitaPageMock.disappeared());
    }

    @Test
    public void waitElementsUntilNegative() {
        assertThrows(NullPointerException.class, () -> akitaPageMock.waitElementsUntil(Condition.appear, 1, "test"));
    }

    @Test
    public void getButtonFromListByNameNegative() {
        assertThrows(NullPointerException.class, () -> {
            SelenideElement selenideElementMock = akitaPageMock.getMockCss();
            List<SelenideElement> list = new LinkedList<>();
            list.add(selenideElementMock);
            getButtonFromListByName(list, "test");
        });
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
        assertThat(page.getAnyElementText("TextField"), equalTo("text1 text2 text3"));
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

    @Test
    @Disabled
    public void getButtonFromListByNamePositive() {
        SelenideElement selenideElement = akitaPageMock.getGoodButton();
        List<SelenideElement> list = new LinkedList<>();
        list.add(selenideElement);
        assertThat(getButtonFromListByName(list, "GoodButton"), is(notNullValue()));
    }
}
