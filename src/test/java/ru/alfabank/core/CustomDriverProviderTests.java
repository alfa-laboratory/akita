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
package ru.alfabank.core;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.alfabank.tests.core.drivers.CustomDriverProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CustomDriverProviderTests {

    @Test
    void createChromeDriverTest() {
        System.setProperty("browser", "chrome");
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.chrome.ChromeDriver"));
        currentDriver.quit();
    }

    @Test
    @Disabled
    void createFirefoxDriverTest() {
        System.setProperty("browser", "firefox");
        CustomDriverProvider customDriverProvider = new CustomDriverProvider();
        WebDriver currentDriver;
        currentDriver = customDriverProvider.createDriver(new DesiredCapabilities());
        assertThat(currentDriver.getClass().getName(), is("org.openqa.selenium.firefox.FirefoxDriver"));
        currentDriver.quit();
    }
}