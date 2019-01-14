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
package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ManageBrowserStepsTest {

    private static ManageBrowserSteps dmbs;
    private static AkitaScenario akitaScenario;
    public static WebPageInteractionSteps wpis;

    @BeforeClass
    public static void setup() {
        dmbs = new ManageBrowserSteps();
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        wpis = new WebPageInteractionSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }

    @Before
    public void prepare() {
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void testCloseCurrentTab() {
        executeJavaScript("window.open(\"RedirectionPage.html\")");
        dmbs.switchToTheTabWithTitle("Page with redirection");
        dmbs.closeCurrentTab();
        dmbs.switchToTheTabWithTitle("Title");
    }

    @Test
    public void testCheckPageTitleSuccess() {
        dmbs.checkPageTitle("Title");
    }

    @Test(expected = AssertionError.class)
    public void testCheckPageTitleFailure() {
        dmbs.checkPageTitle("NoTitle");
    }

    @Test
    public void testCheckPageTitlePositive() {
        dmbs.checkPageTitle("titleFromProperty");
    }

    @Test
    public void savePageTitleToVariablePositive() {
        dmbs.savePageTitleToVariable("TitleVariable");
        MatcherAssert.assertThat(akitaScenario.getVar("TitleVariable"), Matchers.equalTo("Title"));
    }

    @Test
    public void testSwitchToTheTabWithTitle() {
        executeJavaScript("window.open(\"RedirectionPage.html\")");
        dmbs.switchToTheTabWithTitle("Page with redirection");
        dmbs.checkPageTitle("Page with redirection");
        dmbs.switchToTheTabWithTitle("Title");
        dmbs.checkPageTitle("Title");
    }

    @Test
    public void testSwitchToTheNextTab() {
        executeJavaScript("window.open(\"RedirectionPage.html\")");
        dmbs.switchToTheNextTab();
        Assert.assertThat(getWebDriver().getTitle(), IsEqual.equalTo("Page with redirection"));
        dmbs.switchToTheNextTab();
        Assert.assertThat(getWebDriver().getTitle(), IsEqual.equalTo("Title"));
    }

    @Ignore
    @Test
    public void setWindowSizeSimple() {
        Dimension expectedDimension = new Dimension(800, 600);
        dmbs.setBrowserWindowSize(800, 600);
        Dimension actualDimension = WebDriverRunner.getWebDriver().manage().window().getSize();
        assertThat(expectedDimension, equalTo(actualDimension));
    }

}
