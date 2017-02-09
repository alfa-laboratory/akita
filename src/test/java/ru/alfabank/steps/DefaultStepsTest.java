package ru.alfabank.steps;

import cucumber.api.Scenario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Created by onotole on 08.02.17.
 */
class DefaultStepsTest {
    private static DefaultSteps ds;
    private static AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @BeforeAll
    static void setup() {
        ds = new DefaultSteps();
    }

    @BeforeEach
    void prepare() {
        Scenario scenario = new StubScenario();
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
    }

    @Test
    void saveValueToVariable() {
        String varName = "testVar";
        String varValue = loadProperty(varName);

        ds.saveValueToVariable(varName, varName);
        assertThat("вытащилось правильной  значение глобальной переменной",
                alfaScenario.getVar(varName), equalTo(varValue));
    }

    @Test
    void compareTwoDigitVarsNegative() {
        String number1Name = "number1";
        String number2Name = "number2";
        String number1Value = "1234567890";
        String number2Value = "1234567894";
        alfaScenario.setVar(number1Name, number1Value);
        alfaScenario.setVar(number2Name, number2Value);
        Throwable exception =
                assertThrows(AssertionError.class,
                        () -> ds.compareTwoDigitVars(number1Name, number2Name));
        assertThat("Вылетело ожидаемое исключение", exception.getClass(),
                equalTo(AssertionError.class));
    }

    @Test
    void compareTwoDigitVars() {
        String number1Name = "number1";
        String number2Name = "number2";
        String number1Value = "1234567890";
        String number2Value = "1234567890";
        alfaScenario.setVar(number1Name, number1Value);
        alfaScenario.setVar(number2Name, number2Value);
        ds.compareTwoDigitVars(number1Name, number2Name);
    }

}