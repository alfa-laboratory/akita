package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PageElementTest {

    private static DefaultSteps ds;
    private static DefaultPageElementSteps dpes;
    private static AkitaScenario akitaScenario;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        ds = new DefaultSteps();
        dpes = new DefaultPageElementSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
    }

    @Before
    public void prepare() {
        ds.goToSelectedPageByLinkFromPropertyFile("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void clickOnElementInBlockPositiveTest() {
        dpes.clickOnElementInBlock("SearchButton", "SearchBlock");
        assertThat(akitaScenario.getPage("SearchBlock").getElement("SearchButton").isEnabled(),
                equalTo(false));
    }
}
