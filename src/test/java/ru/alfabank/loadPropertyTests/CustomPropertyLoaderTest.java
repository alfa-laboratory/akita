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
package ru.alfabank.loadPropertyTests;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

public class CustomPropertyLoaderTest {
    private static AkitaScenario akitaScenario = AkitaScenario.getInstance();

    @Before
    public void prepare() {
        akitaScenario.setEnvironment(new AkitaEnvironment());
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void customPropertyFile() {
        System.setProperty("profile", "customProperties");
        System.out.println(System.getProperty("profile"));
        assertThat(loadProperty("testVar"), equalTo("customPropertiesTestValue"));
    }
}
