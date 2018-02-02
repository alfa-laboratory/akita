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
import org.junit.*;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import org.openqa.selenium.WebDriver.Options;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class DefaultManageBrowserStepsTest {

    private DefaultManageBrowserSteps dmbs;
    private WebDriver webDriver;
    private AkitaScenario akitaScenario;

    @Before
    public void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        webDriver = mock(WebDriver.class);
        WebDriverRunner.setWebDriver(webDriver);
        dmbs = new DefaultManageBrowserSteps();
        when(webDriver.manage()).thenReturn(mock(Options.class));
    }

    @Test
    public void deleteCookiesTest() {
        dmbs.deleteCookies();
        verify(webDriver.manage(), times(1)).deleteAllCookies();
    }

    @Test
    public void saveCookieToVarTest() {
        Cookie cookie = new Cookie("cookieName", "123");
        when(webDriver.manage().getCookieNamed("cookieName")).thenReturn(cookie);
        dmbs.saveCookieToVar("cookieName", "varName");
        assertThat(akitaScenario.getVar("varName"), equalTo(cookie));
    }

    @Test
    public void saveAllCookiesTest(){
        Set set = new HashSet();
        when(webDriver.manage().getCookies()).thenReturn(set);
        dmbs.saveAllCookies("var2");
        assertThat(akitaScenario.getVar("var2"), equalTo(set));
    }

    @Test
    public void replaceCookieTest() {
        dmbs.replaceCookie("testName", "12qwe");
        verify(webDriver.manage(), times(1)).addCookie(new Cookie("testName", "12qwe"));
    }
}
