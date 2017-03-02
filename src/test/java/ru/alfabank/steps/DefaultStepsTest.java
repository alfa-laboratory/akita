package ru.alfabank.steps;

import cucumber.api.Scenario;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Created by onotole on 08.02.17.
 */
public class DefaultStepsTest {
    private static DefaultSteps ds;
    private static AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @BeforeClass
    public static void setup() {
        ds = new DefaultSteps();
    }

    @Before
    public void prepare() {
        Scenario scenario = new StubScenario();
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
    }

    @Test
    public void saveValueToVariable() {
        String varName = "testVar", varValue = loadProperty(varName);
        ds.saveValueToVariable(varName, varName);

        assertThat(alfaScenario.getVar(varName), equalTo(varValue));
    }

    @Test(expected = AssertionError.class)
    public void compareTwoDigitVarsNegative() {
        String number1Name = "number1", number1Value = "1234567890";
        alfaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567894";
        alfaScenario.setVar(number2Name, number2Value);

        ds.compareTwoDigitVars(number1Name, number2Name);
    }

    @Test
    public void compareTwoDigitVars() {
        String number1Name = "number1", number1Value = "1234567890";
        alfaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567890";
        alfaScenario.setVar(number2Name, number2Value);

        ds.compareTwoDigitVars(number1Name, number2Name);
    }

}