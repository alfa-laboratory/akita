package ru.alfabank.other;

import cucumber.api.Scenario;
import groovy.util.logging.Slf4j;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.alfabank.AlfaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaPage;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by onotole on 08.02.17.
 */
public class AlfaScenarioTest {
    private static AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @Before
    public void prepare() {
        Scenario scenario = new StubScenario();
        AlfaPage alfaPageMock = mock(AlfaPage.class);
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
        alfaScenario.getPages().put("Title", alfaPageMock);
    }

    @Rule
    public ExpectedException expectedEx;

    @Test(expected = NullPointerException.class)
    public void testGetSetVarNegative1() {
        String notExistingVar = "randomName";
        expectedEx.expectMessage("Переменная " + notExistingVar + " не найдена");
        assertThat(alfaScenario.getVar(notExistingVar), nullValue());
    }

    @Test
    public void testGetSetVar() {
        String varName = "varName";
        String varValue = "1234567891011";
        alfaScenario.setVar(varName, varValue);
        assertThat("Вернулось правильное значение сохраненной переменной", alfaScenario.getVar(varName).toString(),
                equalTo(varValue));
    }

    @Test
    public void putGetPagesPositive() {
        assertThat(alfaScenario.getPage("Title"), is(not(nullValue())) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void putGetPagesNegative() {
        AlfaPageMock alfaPageMock = null;
        alfaScenario.getPages().put("Mock", alfaPageMock);
    }

    @Test
    public void getEnvironmentPositive() {
        assertThat(alfaScenario.getEnvironment(), is(not(nullValue())) );
    }

    @Test
    public void getEnvironmentNegative() {
        alfaScenario.setEnvironment(null);
        assertThat(alfaScenario.getEnvironment(), is(nullValue()) );
    }

    @Test
    public void getCurrentPagePositive() {
        alfaScenario.setCurrentPage(alfaScenario.getPage("Title"));
        assertThat(alfaScenario.getCurrentPage(), is(not(nullValue())) );
    }

    @Test(expected = AssertionError.class)
    public void setCurrentPageNegative() {
        alfaScenario.setCurrentPage(null);
    }

}
