package ru.alfabank.steps.defaultSteps;

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

import static com.codeborne.selenide.Selenide.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ElementStepsTest {

    private static ElementSteps es;
    private static AkitaScenario akitaScenario;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        es = new ElementSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }


    @Before
    public void prepare() {
        WebPageSteps wps = new WebPageSteps();
        wps.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void navigateToUrl() {
        assertThat(WebDriverRunner.getWebDriver().getTitle(), equalTo("Title"));
    }

    @Test
    public void storeFieldValueInVariablePositive() {
        String varName = "mockId";
        es.storeElementValueInVariable(varName, varName);
        assertThat(akitaScenario.getVar(varName), equalTo("Serious testing page"));
    }

    @Test
    public void clickOnElementPositive() {
        es.clickOnElement("GoodButton");
        assertThat(akitaScenario.getPage("AkitaPageMock").getElement("GoodButton").isEnabled(),
            equalTo(false));
    }

    @Test
    public void elemIsPresentedOnPagePositive() {
        es.elemIsPresentedOnPage("mockTagName");
    }

    @Test
    public void compareFieldAndVariablePositive() {
        akitaScenario.setVar("test", "Serious testing page");
        es.compareFieldAndVariable("mockTagName", "test");
    }

    @Test
    public void elementIsNotVisiblePositive() {
        es.elementIsNotVisible("HiddenDiv");
    }

    @Test
    public void checkElemContainsAtrWithValuePositive() {
        es.checkElemContainsAtrWithValue("SUPERBUTTON", "onclick", "HIDEnSHOW()");
    }

    @Test
    public void testFieldContainsInnerTextPositive() {
        es.testFieldContainsInnerText("innerTextP", "inner text");
    }

    @Test
    public void testActualValueContainsSubstringPositive() {
        es.testActualValueContainsSubstring("TextField", "xt");
    }

    @Test
    public void testActualValueContainsSubstringPositiveWithProps() {
        es.testActualValueContainsSubstring("TextField", "textValueInProps");
    }

    @Test
    public void buttonIsNotActivePositive() {
        es.buttonIsNotActive("DisabledButton");
    }

    @Test
    public void fieldIsDisablePositive() {
        es.fieldIsDisable("DisabledField");
    }

    @Test
    public void findElementPositive() {
        es.findElement("LINK");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().getCurrentUrl(),
            equalTo(akitaScenario.getVar("RedirectionPage")));
    }

    @Test
    public void elementHoverTest() {
        es.elementHover("NormalField");
    }

    @Test
    public void clickableFieldTest() {
        es.clickableField("SUPERBUTTON");
    }

    @Test
    public void testButtonIsActiveAnotherPositive() {
        es.clickableField("Link");
    }

    @Test(expected = AssertionError.class)
    public void testButtonIsActiveNegative() {
        es.clickableField("Кнопка Подписать и отправить");
    }

    @Test
    public void elementDisapperaredAndAppearedComplex() {
        es.testElementAppeared("ul", 1);
        es.clickOnElement("SUPERBUTTON");
        es.elemDisappered("ul");
    }

    @Test
    public void compareValInFieldAndFromStepTest() {
        es.compareValInFieldAndFromStep("ul", "Serious testing page");
    }

    @Test
    public void compareValInFieldAndFromStepTestWithProps() {
        es.compareValInFieldAndFromStep("ul", "testingPageTextProps");
    }

    @Test
    public void testCheckElemClassContainsExpectedValuePositive() {
        es.checkElemClassContainsExpectedValue("Кнопка Подписать и отправить", "disabled");
    }

    @Test(expected = AssertionError.class)
    public void testCheckElemClassContainsExpectedValueNegative() {
        es.checkElemClassContainsExpectedValue("Кнопка Подписать и отправить", "enabled");
    }

    @Test
    public void testCheckElemClassNotContainsExpectedValuePositive() {
        es.checkElemClassNotContainsExpectedValue("Кнопка Подписать и отправить", "enabled");
    }

    @Test(expected = AssertionError.class)
    public void testCheckElemClassNotContainsExpectedValueNegative() {
        es.checkElemClassNotContainsExpectedValue("Кнопка Подписать и отправить", "disabled");
    }

}
