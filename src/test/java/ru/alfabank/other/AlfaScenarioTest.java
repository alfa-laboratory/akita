package ru.alfabank.other;

import cucumber.api.Scenario;
import groovy.util.logging.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by onotole on 08.02.17.
 */
public class AlfaScenarioTest {
    private static AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @Before
    public void prepare() {
        Scenario scenario = new StubScenario();
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
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


}
