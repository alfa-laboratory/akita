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
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by alexander on 02.08.17.
 */
public class ScopedVariablesTest {
    private static ScopedVariables variables;

    @BeforeClass
    public static void init() {
        variables = new ScopedVariables();
    }

    @Test
    public void evaluatePositive() {
        assertThat(variables.evaluate("\"test\".equals(\"test\")"), equalTo(true));
    }

    @Test
    public void evaluateNegative() {
        assertThat(variables.evaluate("\"test1\".equals(\"test\")"), equalTo(false));
    }

    @Test
    public void putGetPositive() {
        variables.put("Test", "text");
        assertThat(variables.get("Test"), equalTo("text"));
    }

    @Test
    public void putGetNull() {
        Object nullObject = nullValue();
        variables.put("Test", nullObject);
        assertThat(variables.get("Test"), equalTo(nullObject));
    }

    @Test
    public void getNegative() {
        assertThat(variables.get("asdfg"), equalTo(null));
    }

    @Test
    public void clearPositive() {
        variables.put("test", "text");
        variables.clear();
        assertThat(variables.get("test"), equalTo(null));
    }


    @Test
    public void removePositive() {
        variables.put("test", "text");
        variables.remove("test");
        assertThat(variables.get("test"), equalTo(null));
    }

    @Test
    public void removeNegative() {
        assertThat(variables.remove("WRONG_KEY"), equalTo(null));
    }
}
