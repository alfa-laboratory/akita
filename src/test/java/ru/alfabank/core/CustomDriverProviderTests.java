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
package ru.alfabank.core;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.alfabank.tests.core.drivers.CustomDriverProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CustomDriverProviderTests {

    @Test
    public void createChromeDriverTest() {
        System.setProperty("browser", "chrome");
        System.setProperty("width", "666");
        System.setProperty("height", "666");
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.chrome.ChromeDriver"));
        assertThat(currentDriver.manage().window().getSize().getWidth(), is(666));
        assertThat(currentDriver.manage().window().getSize().getHeight(), is(666));
        currentDriver.quit();
    }

    @Ignore
    @Test
    public void createFirefoxDriverTest() {
        System.setProperty("browser", "firefox");
        System.setProperty("width", "1000");
        System.setProperty("height", "1000");
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.firefox.FirefoxDriver"));
        assertThat(currentDriver.manage().window().getSize().getWidth(), is(1000));
        assertThat(currentDriver.manage().window().getSize().getHeight(), is(1000));
        currentDriver.quit();
    }

    @Ignore
    @Test
    public void createOperaDriverTest() {
        System.setProperty("browser", "opera");
        System.setProperty("width", "500");
        System.setProperty("height", "500");
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.opera.OperaDriver"));
        assertThat(currentDriver.manage().window().getSize().getWidth(), is(500));
        assertThat(currentDriver.manage().window().getSize().getHeight(), is(500));
        currentDriver.quit();
    }

    @Ignore
    @Test
    public void createEdgeDriverTest() {
        System.setProperty("browser", "Edge");
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.edge.EdgeDriver"));
        currentDriver.quit();
    }

    @Ignore
    @Test
    public void createIEDriverTest() {
        System.setProperty("browser", "internet explorer");
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.ie.InternetExplorerDriver"));
        currentDriver.quit();
    }

    @Test
    public void createMobileDriverTest() {
        System.setProperty("browser", "mobile");
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.chrome.ChromeDriver"));
        currentDriver.quit();
    }

    @Test
    public void createNonexistentDriverTest() {
        System.setProperty("browser", "nonexistent");
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.chrome.ChromeDriver"));
        assertThat(currentDriver.manage().window().getSize().getWidth(), is(1920));
        assertThat(currentDriver.manage().window().getSize().getHeight(), is(1080));
        currentDriver.quit();
    }

    @Test
    public void createEmptyDriverTest() {
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.chrome.ChromeDriver"));
        assertThat(currentDriver.manage().window().getSize().getWidth(), is(1920));
        assertThat(currentDriver.manage().window().getSize().getHeight(), is(1080));
        currentDriver.quit();
    }
}