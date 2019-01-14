package ru.alfabank.steps;

import cucumber.api.Scenario;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class PageBlockStepsTest {

    private static AkitaScenario akitaScenario;
    private static WebPageInteractionSteps wpis;
    private static PageBlockSteps pbs;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        wpis = new WebPageInteractionSteps();
        pbs = new PageBlockSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }

    @Before
    public void prepare() {
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @Test
    public void clickOnElementInBlockPositiveTest() {
        pbs.clickOnElementInBlock("SearchButton", "SearchBlock");
        assertFalse(akitaScenario.getPage("SearchBlock").getElement("SearchButton").isEnabled());
    }

    @Test
    public void getElementsListInBlockPositiveTest() {
        pbs.getElementsList("AkitaTable", "Rows", "ListTable");
        assertNotNull(akitaScenario.getVar("ListTable"));
    }

    @Test
    public void getListElementsTextInBlockPositiveTest() {
        pbs.getListElementsText("AkitaTable", "Rows", "ListTable");
        assertNotNull(akitaScenario.getVar("ListTable"));
    }

}
