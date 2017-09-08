package ru.alfabank.other;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.*;
import ru.alfabank.TestPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.TestEnvironment;
import ru.alfabank.alfatest.cucumber.api.TestPage;
import ru.alfabank.alfatest.cucumber.api.TestScenario;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by onotole on 08.02.17.
 */
public class TestScenarioTest {
    private static TestScenario testScenario;

    @BeforeClass
    public static void init() {
        testScenario = TestScenario.getInstance();
    }

    @AfterClass
    public static void close() { WebDriverRunner.closeWebDriver(); }

    @Before
    public void prepare() {
        Scenario scenario = new StubScenario();
        TestPage testPageMock = mock(TestPage.class);
        testScenario.setEnvironment(new TestEnvironment(scenario));
        testScenario.getPages().put("Title", testPageMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSetVarNegative1() {
        String notExistingVar = "randomName";
        testScenario.getVar(notExistingVar);
    }

    @Test
    public void testGetSetVar() {
        String varName = "varName";
        String varValue = "1234567891011";
        testScenario.setVar(varName, varValue);
        assertThat("Вернулось правильное значение сохраненной переменной",
                testScenario.getVar(varName).toString(), equalTo(varValue));
    }

    @Test
    public void putGetPagesPositive() {
        assertThat(testScenario.getPage("Title"), is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putGetPagesNegative() {
        TestPageMock alfaPageMock = null;
        testScenario.getPages().put("Mock", alfaPageMock);
    }

    @Test
    public void getEnvironmentPositive() {
        assertThat(testScenario.getEnvironment(), is(notNullValue()));
    }

    @Test
    public void getEnvironmentNegative() {
        testScenario.setEnvironment(null);
        assertThat(testScenario.getEnvironment(), is(nullValue()) );
    }

    @Test
    public void getCurrentPagePositive() {
        testScenario.setCurrentPage(testScenario.getPage("Title"));
        assertThat(testScenario.getCurrentPage(), is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCurrentPageNegative() {
        testScenario.setCurrentPage(null);
    }

}
