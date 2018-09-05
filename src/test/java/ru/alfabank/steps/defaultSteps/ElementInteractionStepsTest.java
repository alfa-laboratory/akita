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
package ru.alfabank.steps.defaultSteps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static com.codeborne.selenide.Selenide.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ElementInteractionStepsTest {

    private static ElementInteractionSteps eis;
    private static AkitaScenario akitaScenario;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        eis = new ElementInteractionSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }


    @Before
    public void prepare() {
        WebPageSteps wps = new WebPageSteps();
        wps.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void navigateToUrl() {
        assertThat(WebDriverRunner.getWebDriver().getTitle(), equalTo("Title"));
    }

    @Test
    public void clickOnElementPositive() {
        eis.clickOnElement("GoodButton");
        assertThat(akitaScenario.getPage("AkitaPageMock").getElement("GoodButton").isEnabled(),
            equalTo(false));
    }

    @Test
    public void buttonIsNotActivePositive() {
        eis.elementIsDisabled("DisabledButton");
    }

    @Test
    public void findElementPositive() {
        eis.findElement("LINK");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().getCurrentUrl(),
            equalTo(akitaScenario.getVar("RedirectionPage")));
    }

    @Test
    public void findElementMixedLanguagePositive() {
        eis.findElement("EnGliSh? РуСсКий.");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("mixedButton"))
            .isEnabled(), is(false));
    }

    @Test
    public void findElementMixedLanguagePartialRuPositive() {
        eis.findElement("РуСсКий.");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("mixedButton"))
            .isEnabled(), is(false));
    }

    @Test
    public void findElementMixedLanguagePartialEnPositive() {
        eis.findElement("EnGliSh");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("mixedButton"))
            .isEnabled(), is(false));
    }

}
