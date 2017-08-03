package ru.alfabank.other;

import org.junit.Test;
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by alexander on 02.08.17.
 */
public class ScopedVariablesTest {
    private static ScopedVariables variables = new ScopedVariables();

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
