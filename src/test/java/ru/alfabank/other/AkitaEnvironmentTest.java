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

import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AkitaEnvironmentTest {
    private static AkitaEnvironment env;

    @BeforeClass
    public static void prepare() {
        env = new AkitaEnvironment();
    }

    @Test
    public void initPagesTest() {
        assertThat(env.getPage("AkitaPageMock"), is(notNullValue()));
    }

    @Test
    public void getVarsTest() {
        assertThat(env.getVars(), is(notNullValue()));
    }

    @Test
    public void getSetVarPositive() {
        String testString = "TestString1";
        env.setVar("Test1", testString);
        assertThat(env.getVar("Test1"), equalTo(testString));
    }

    @Test
    public void getSetVarNegative() {
        assertThat(env.getVar("Test"), is(nullValue()));
    }

    @Test
    public void getPagesTest() {
        assertThat(env.getPages(), is(notNullValue()));
    }

    @Test
    public void getPage() {
        AkitaPageMock alfaPageMockInstance = new AkitaPageMock();
        env.getPages().put("newAwesomePage", alfaPageMockInstance);
        assertThat(env.getPage("newAwesomePage"), is(alfaPageMockInstance));
    }
}
