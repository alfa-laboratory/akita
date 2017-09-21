package ru.alfabank.other;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;

import static org.hamcrest.MatcherAssert.*;
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
