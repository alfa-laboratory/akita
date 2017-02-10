package ru.alfabank.other;

import cucumber.api.Scenario;
import org.junit.Before;
import org.junit.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.steps.DefaultSteps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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

    @Test(expected = AssertionError.class)
    public void testGetSetVarNegative1() {
        alfaScenario.getVar("randomName");
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
