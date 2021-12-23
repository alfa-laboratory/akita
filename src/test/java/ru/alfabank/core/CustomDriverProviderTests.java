/*
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
package ru.alfabank.core;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.alfabank.tests.core.drivers.CustomDriverProvider;

import static com.codeborne.selenide.Browsers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CustomDriverProviderTests {

    @Test
    void createChromeDriverTest() {
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        DesiredCapabilities ds = new DesiredCapabilities();
        ds.setBrowserName(CHROME);
        System.setProperty(CustomDriverProvider.BROWSER, CHROME);
        currentDriver = customDriverProvider.createDriver(ds);
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.chrome.ChromeDriver"));
        currentDriver.quit();
    }

    @Test
    void createFirefoxDriverTest() {
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        DesiredCapabilities ds = new DesiredCapabilities();
        ds.setBrowserName(FIREFOX);
        System.setProperty(CustomDriverProvider.BROWSER, FIREFOX);
        currentDriver = customDriverProvider.createDriver(ds);
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.firefox.FirefoxDriver"));
        currentDriver.quit();
    }

    @Test
    @Disabled
    @Deprecated
    /*
    Use edge
     */
    void createInternetExplorerDriverTest() {
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        DesiredCapabilities ds = new DesiredCapabilities();
        ds.setBrowserName(INTERNET_EXPLORER);
        System.setProperty(CustomDriverProvider.BROWSER, INTERNET_EXPLORER);
        currentDriver = customDriverProvider.createDriver(ds);
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.ie.InternetExplorerDriver"));
        currentDriver.quit();
    }

    @Test
    void createEdgeDriverTest() {
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        DesiredCapabilities ds = new DesiredCapabilities();
        ds.setBrowserName(EDGE);
        System.setProperty(CustomDriverProvider.BROWSER, EDGE);
        currentDriver = customDriverProvider.createDriver(ds);
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.edge.EdgeDriver"));
        currentDriver.quit();
    }

}