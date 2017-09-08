package ru.alfabank.other;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.TestPageMock;
import ru.alfabank.alfatest.cucumber.api.TestEnvironment;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Created by alexander on 02.08.17.
 */
public class TestEnvironmentTest {
    private static TestEnvironment env;

    @BeforeClass
    public static void prepare() {
        env = new TestEnvironment();
    }

    @Test
    public void initPagesTest() {
        assertThat(env.getPage("TestPageMock"), is(notNullValue()));
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
        TestPageMock alfaPageMockInstance = new TestPageMock();
        env.getPages().put("newAwesomePage", alfaPageMockInstance);
        assertThat(env.getPage("newAwesomePage"), is(alfaPageMockInstance));
    }
}
