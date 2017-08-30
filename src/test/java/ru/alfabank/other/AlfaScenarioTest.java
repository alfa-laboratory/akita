package ru.alfabank.other;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import groovy.util.logging.Slf4j;
import org.junit.*;
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
    private static AlfaScenario alfaScenario;

    @BeforeClass
    public static void init() {
        alfaScenario = AlfaScenario.getInstance();
    }

    @AfterClass
    public static void close() { WebDriverRunner.closeWebDriver(); }

    @Before
    public void prepare() {
        Scenario scenario = new StubScenario();
        AlfaPage alfaPageMock = mock(AlfaPage.class);
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
        alfaScenario.getPages().put("Title", alfaPageMock);
    }

    @Test(expected = NullPointerException.class)
    public void testGetSetVarNegative1() {
        String notExistingVar = "randomName";
        alfaScenario.getVar(notExistingVar);
    }

    @Test
    public void testGetSetVar() {
        String varName = "varName";
        String varValue = "1234567891011";
        alfaScenario.setVar(varName, varValue);
        assertThat("Вернулось правильное значение сохраненной переменной",
                alfaScenario.getVar(varName).toString(), equalTo(varValue));
    }

    @Test
    public void putGetPagesPositive() {
        assertThat(alfaScenario.getPage("Title"), is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putGetPagesNegative() {
        AlfaPageMock alfaPageMock = null;
        alfaScenario.getPages().put("Mock", alfaPageMock);
    }

    @Test
    public void getEnvironmentPositive() {
        assertThat(alfaScenario.getEnvironment(), is(notNullValue()));
    }

    @Test
    public void getEnvironmentNegative() {
        alfaScenario.setEnvironment(null);
        assertThat(alfaScenario.getEnvironment(), is(nullValue()) );
    }

    @Test
    public void getCurrentPagePositive() {
        alfaScenario.setCurrentPage(alfaScenario.getPage("Title"));
        assertThat(alfaScenario.getCurrentPage(), is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCurrentPageNegative() {
        alfaScenario.setCurrentPage(null);
    }

}
