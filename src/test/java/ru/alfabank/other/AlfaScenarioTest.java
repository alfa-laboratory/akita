package ru.alfabank.other;

import cucumber.api.Scenario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.steps.DefaultSteps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by onotole on 08.02.17.
 */
class AlfaScenarioTest {
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
    void testGetSetVarNegative1() {
        Throwable exception = assertThrows(AssertionError.class, () -> alfaScenario.getVar("randomName"));
        assertThat("Вернулось правильное исключение", exception.getClass(), equalTo(AssertionError.class));
    }

    @Test
    void testGetSetVar() {
        String varName = "varName";
        String varValue = "1234567891011";
        alfaScenario.setVar(varName, varValue);
        assertThat("Вернулось правильное значение сохраненной переменной", alfaScenario.getVar(varName).toString(),
                equalTo(varValue));
    }


}
