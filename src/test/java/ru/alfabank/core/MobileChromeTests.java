/**
 * Copyright 2017 Alfa Laboratory
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.core;

import org.junit.AfterClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.alfabank.tests.core.drivers.MobileChrome;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;

public class MobileChromeTests {
    private static MobileChrome mobileChrome = new MobileChrome();
    private static WebDriver mobileDriver;

    @Test
    public void createDriverTest() {
        mobileDriver = mobileChrome.createDriver(new DesiredCapabilities());
        assertThat(mobileDriver, isA(WebDriver.class));
    }

    @AfterClass
    public static void close() {
        mobileDriver.close();
    }
}
