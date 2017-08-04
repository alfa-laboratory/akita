package ru.alfabank.other;

import cucumber.api.Scenario;
import org.junit.Before;
import org.junit.Test;
import ru.alfabank.AlfaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.ScopedVariables;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaPage;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

/**
 * Created by alexander on 02.08.17.
 */
public class AlfaEnvironmentTest {
    private static AlfaEnvironment env = new AlfaEnvironment();

    @Test
    public void initPagesTest() {
        assertThat(env.getPage("AlfaPageMock"), is(notNullValue()));
    }

    @Test
    public void getVarsTest() {
        assertThat(env.getVars(), is(not(nullValue())) );
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
        assertThat(env.getPages(), is(not(nullValue())) );
    }

    @Test
    public void getPage() {
        AlfaPageMock alfaPageMockInstance = new AlfaPageMock();
        env.getPages().put("newAwesomePage", alfaPageMockInstance);
        assertThat(env.getPage("newAwesomePage"), is(alfaPageMockInstance));
    }
}
