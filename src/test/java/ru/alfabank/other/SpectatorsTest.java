package ru.alfabank.other;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementShould;
import cucumber.api.Scenario;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.AlfaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.steps.DefaultSteps;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by alexander on 02.08.17.
 */
public class SpectatorsTest {
    private static AlfaScenario alfaScenario;
    private static AlfaPageMock page;

    @BeforeClass
    public static void setup() {
        alfaScenario = AlfaScenario.getInstance();
        Scenario scenario = new StubScenario();
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
        DefaultSteps ds = new DefaultSteps();
        page = (AlfaPageMock)alfaScenario.getPage("AlfaPageMock");
        String inputFilePath = "src/test/resources/AlfaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        alfaScenario.setVar("Page", "file://" + url);
        ds.goToSelectedPageByLinkFromProperty("AlfaPageMock", alfaScenario.getVar("Page").toString());
    }

    @AfterClass
    public static void close() { WebDriverRunner.closeWebDriver(); }

    @Test
    public void waitElementsUntilPositiveElements() {
        SelenideElement mockId = page.mockId;
        SelenideElement mockCss = page.mockCss;
        alfaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                1000, mockCss, mockId);
    }

    @Test
    public void waitElementsUntilPositiveList() {
        SelenideElement mockId = page.mockId;
        SelenideElement mockCss = page.mockCss;
        LinkedList<SelenideElement> list = new LinkedList<>();
        list.add(mockCss);
        list.add(mockId);
        alfaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                1000, list);
    }

    @Test(expected = NullPointerException.class)
    public void waitElementsUntilNull() {
        SelenideElement nullElement = null;
        alfaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                1000, nullElement);
    }

    @Test(expected = ElementShould.class)
    public void waitElementsUntilNegative() {
        SelenideElement mockId = page.mockId;
        SelenideElement mockCss = page.mockCss;
        alfaScenario.getCurrentPage().waitElementsUntil(Condition.disappear,
                1000, mockCss, mockId);
    }

    @Test
    public void waitElementsUntilEmptyList() {
        LinkedList<SelenideElement> list = new LinkedList<>();
        alfaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                1000, list);
    }

    @Test(expected = NullPointerException.class)
    public void waitElementsUntilListNull() {
        SelenideElement nullElement1 = null;
        SelenideElement nullElement2 = null;
        LinkedList<SelenideElement> list = new LinkedList<>();
        list.add(nullElement1);
        list.add(nullElement2);
        alfaScenario.getCurrentPage().waitElementsUntil(Condition.appear,
                1000, list);
    }
}
