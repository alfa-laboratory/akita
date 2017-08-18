package ru.alfabank.loadPropertyTests;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import static org.junit.Assert.assertThat;
import static ru.alfabank.steps.DefaultApiSteps.getURLwithPathParamsCalculated;

/**
 * Created by rum0tbl on 02.06.17.
 */
public class PropertyLoaderTests {
    private static AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @Before
    public void prepare() {
        alfaScenario.setEnvironment(new AlfaEnvironment());
    }

    @Test
    public void someValuesFromMap() {
        alfaScenario.setVar("first", "pervoe");
        alfaScenario.setVar("second", "ne_rabotaet");
        String expected = "pervoe ne_rabotaet";
        String actual = getURLwithPathParamsCalculated("{first} {second}");
        assertThat("Итоговый URL не равен 'pervoe ne_rabotaet'", actual, Matchers.equalTo(expected));
    }

    @Test
    public void getValueFromPropertyFile() {
        alfaScenario.setVar("first", "alfalab");
        alfaScenario.setVar("second", "/ru/credit");
        String actual = getURLwithPathParamsCalculated("{varFromPropertyFile1}");
        assertThat("Итоговый URL не равен 'caramba'", actual, Matchers.equalTo("caramba"));
    }

    @Test
    public void getSomeValuesFromPropertyFile() {
        alfaScenario.setVar("first", "alfalab");
        alfaScenario.setVar("second", "/ru/credit");
        String actual = getURLwithPathParamsCalculated("{varFromPropertyFile1}/{varFromPropertyFile2}");
        assertThat("Итоговый URL не равен 'caramba/kumkvat'", actual, Matchers.equalTo("caramba/kumkvat"));
    }

    @Test
    public void getSomeValuesFromPropAndMap() {
        alfaScenario.setVar("first", "alfalab");
        alfaScenario.setVar("second", "/ru/credit");
        String actual = getURLwithPathParamsCalculated("{varFromPropertyFile1}/{first}");
        assertThat("Итоговый URL не равен 'caramba/alfalab'", actual, Matchers.equalTo("caramba/alfalab"));
    }

    @Test
    public void getSomeValuesFromPropAndMapAndSpec() {
        alfaScenario.setVar("first", "alfalab");
        alfaScenario.setVar("second", "/ru/credit");
        String actual = getURLwithPathParamsCalculated("/{second}/{varFromPropertyFile1}/{first}/");
        assertThat("Итоговый URL не равен '//ru/credit/caramba/alfalab/'", actual, Matchers.equalTo("//ru/credit/caramba/alfalab/"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNotExistingValue() {
        getURLwithPathParamsCalculated("{RandomTestVariable3321}");
    }
}
