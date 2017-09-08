package ru.alfabank.other;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.Scenario;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.alfabank.TestPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.TestEnvironment;
import ru.alfabank.alfatest.cucumber.api.TestPage;
import ru.alfabank.alfatest.cucumber.api.TestScenario;
import ru.alfabank.steps.DefaultSteps;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static ru.alfabank.alfatest.cucumber.api.TestPage.getButtonFromListByName;

/**
 * Created by alexander on 02.08.17.
 */
public class TestPageTest {
    private static TestPageMock alfaPageMock;
    private static TestPage page;

    @BeforeClass
    public static void setup() {
        alfaPageMock = new TestPageMock();
        TestScenario testScenario = TestScenario.getInstance();
        DefaultSteps ds = new DefaultSteps();
        Scenario scenario = new StubScenario();
        testScenario.setEnvironment(new TestEnvironment(scenario));
        String inputFilePath = "src/test/resources/TestPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        testScenario.setVar("Page", "file://" + url);
        page = testScenario.getEnvironment().getPage("TestPageMock");
        ds.goToSelectedPageByLinkFromProperty("TestPageMock", testScenario.getVar("Page").toString());
    }

    @Test(expected = NullPointerException.class)
    public void getElementNegative() {
        alfaPageMock.getElement("test");
    }

    @Test(expected = NullPointerException.class)
    public void getElementsListNegative() {
        alfaPageMock.getElementsList("test");
    }

    @Test(expected = NullPointerException.class)
    public void getAnyElementTextNegative() {
        alfaPageMock.getAnyElementText("test");
    }

    @Test(expected = NullPointerException.class)
    public void getAnyElementsListTextsNegative() {
        alfaPageMock.getAnyElementsListTexts("test");
    }

    @Test
    public void appearedPositive() {
        assertThat(alfaPageMock.appeared(), equalTo(alfaPageMock));
    }

    @Test
    public void disappearedNegative() {
        assertThat(alfaPageMock.disappeared(), equalTo(alfaPageMock));
    }

    @Test(expected = NullPointerException.class)
    public void waitElementsUntilNegative() {
        alfaPageMock.waitElementsUntil(Condition.appear, 1,"test");
    }

    @Test(expected = NullPointerException.class)
    public void getButtonFromListByNameNegative() {
        SelenideElement selenideElementMock = alfaPageMock.getMockCss();
        List<SelenideElement> list = new LinkedList<>();
        list.add(selenideElementMock);
        getButtonFromListByName(list, "test");
    }

    @Test
    public void getElementPositive() {
        assertThat(page.getElement("GoodButton"), is(notNullValue()));
    }

    @Test
    public void getElementsListPositive() {
        assertThat(page.getElementsList("List"), is(notNullValue()));
    }

    @Test
    public void getAnyElementTextPositive() {
        assertThat(page.getAnyElementText("TextField"), equalTo("text"));
    }

    @Test
    public void getAnyElementsListTextsPositive() {
        assertThat(page.getAnyElementsListTexts("List").toString(),
                equalTo("[Three, One, Two]"));
    }

    @Test
    public void waitElementsUntilPositive() {
        page.waitElementsUntil(Condition.disappear, 1,"HiddenDiv");
    }

    @Ignore
    @Test
    public void getButtonFromListByNamePositive() {
        SelenideElement selenideElement = alfaPageMock.getGoodButton();
        List<SelenideElement> list = new LinkedList<>();
        list.add(selenideElement);
        assertThat(getButtonFromListByName(list, "GoodButton"), is(notNullValue()));
    }
}
