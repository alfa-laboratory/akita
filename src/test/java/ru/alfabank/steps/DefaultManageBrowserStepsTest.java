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

import org.junit.*;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import org.openqa.selenium.WebDriver.Options;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;


public class DefaultManageBrowserStepsTest {

    private DefaultManageBrowserSteps dmbs;
    private WebDriver webDriver;
    private AkitaScenario akitaScenario;

    @Before
    public void setup() {

        akitaScenario = mock(AkitaScenario.class);
        webDriver = mock(WebDriver.class);
        dmbs = new DefaultManageBrowserSteps(webDriver, akitaScenario);
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
        verify(akitaScenario, times(1)).setVar("varName", cookie);
    }

    @Test
    public void saveAllCookiesTest(){
        Set set = new HashSet();
        when(webDriver.manage().getCookies()).thenReturn(set);
        dmbs.saveAllCookies("var2");
        verify(akitaScenario, times(1)).setVar("var2", set);
    }

    @Test
    public void replaceCookieTest() {
        dmbs.replaceCookie("testName", "12qwe");
        verify(webDriver.manage(), times(1)).addCookie(new Cookie("testName", "12qwe"));
    }
}
