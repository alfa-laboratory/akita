package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.*;
import org.openqa.selenium.Dimension;
import ru.alfabank.AlfaPageMock;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;
import static org.mockito.Mockito.*;

/**
 * Created by onotole on 08.02.17.
 */
public class DefaultStepsTest {
    private static DefaultSteps ds;
    private static AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @BeforeClass
    public static void setup() {
        Scenario scenario = new StubScenario();
        alfaScenario.setEnvironment(new AlfaEnvironment(scenario));
        ds = new DefaultSteps();
        String inputFilePath = "src/test/resources/AlfaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        alfaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        alfaScenario.setVar("RedirectionPage", "file://" + url2);
    }

    @Before
    public void prepare() {
        ds.goToSelectedPageByLinkFromProperty("AlfaPageMock", alfaScenario.getVar("Page").toString());
    }

    @AfterClass
    public static void close() { WebDriverRunner.getWebDriver().close(); }

    @Test
    public void navigateToUrl() {
        assertThat(WebDriverRunner.getWebDriver().getTitle(), equalTo("Title"));
    }

    @Test
    public void checkCurrentURLPositive() {
        ds.checkCurrentURL(alfaScenario.getVar("Page").toString());
    }

    @Test(expected = NullPointerException.class)
    public void checkCurrentURLNegative() {
        ds.checkCurrentURL(null);
    }

    @Ignore
    @Test
    public void setupWindowSizeSimple() {
        Dimension expectedDimension = new Dimension(800, 600);
        ds.setupWindowSize("800", "600");
        Dimension actualDimension = WebDriverRunner.getWebDriver().manage().window().getSize();
        assertThat(expectedDimension, equalTo(actualDimension));
    }

    @Test
    public void storeFieldValueInVariablePositive() {
        String varName = "mockId";
        ds.storeElementValueInVariable(varName, varName);
        assertThat(alfaScenario.getVar(varName), equalTo("Serious testing page"));
    }

    @Test(expected = AssertionError.class)
    public void compareTwoDigitVarsNegative() {
        String number1Name = "number1", number1Value = "1234567890";
        alfaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567894";
        alfaScenario.setVar(number2Name, number2Value);

        ds.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void compareTwoDigitVars() {
        String number1Name = "number1", number1Value = "1234567890.97531";
        alfaScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567890.97531";
        alfaScenario.setVar(number2Name, number2Value);

        ds.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void saveValueToVarPositive() {
        ds.saveValueToVar("testVar", "test");
        assertThat(alfaScenario.getVar("test"), equalTo("testValue"));
    }

    @Test
    public void clickOnElementPositive() {
        ds.clickOnElement("GoodButton");
        assertThat(alfaScenario.getPage("AlfaPageMock").getElement("GoodButton").isEnabled(),
                equalTo(false));
    }

    @Test
    public void elemIsPresentedOnPagePositive() {
        ds.elemIsPresentedOnPage("mockTagName");
    }

    @Ignore
    @Test
    public void listIsPresentedOnPageNegative() {
        ds.elemIsPresentedOnPage("mockList");
    }

    @Test
    public void loadPageSimple() {
        ds.loadPage("AlfaPageMock");
    }

    @Test
    public void compareFieldAndVariablePositive() {
        alfaScenario.setVar("test", "Serious testing page");
        ds.compareFieldAndVariable("mockTagName", "test");
    }

    @Test
    public void checkIfListContainsValueFromFieldPositive() {
        List<String> list = new ArrayList<>();
        list.add("Serious testing page");
        alfaScenario.setVar("list", list);
        ds.checkIfListContainsValueFromField("mockTagName", "list");
    }

    @Ignore
    @Test
    public void blockDisappearedSimple() {
        ds.blockDisappeared("AlfaPageMock");
    }

    @Test
    public void pushButtonOnKeyboardSimple() {
        ds.pushButtonOnKeyboard("alt");
    }

    @Test
    public void setFieldValuePositive() {
        ds.setFieldValue("NormalField", "test");
        assertThat(alfaScenario.getEnvironment()
                        .getPage("AlfaPageMock")
                        .getAnyElementText("NormalField"),
                equalTo("test"));
    }

    @Test
    public void cleanFieldPositive() {
        ds.cleanField("TextField");
        assertThat(alfaScenario.getEnvironment()
                        .getPage("AlfaPageMock")
                        .getAnyElementText("TextField"),
                equalTo(""));
    }

    @Test
    public void fieldInputIsEmptyPositive() {
        ds.fieldInputIsEmpty("NormalField");
    }

    @Ignore
    @Test
    public void checkIfListConsistsOfTableElementsPositive() {

    }

    @Ignore
    @Test
    public void checkIfSelectedListElementMatchesValuePositive() {

    }

    @Test
    public void expressionExpressionPositive() {
        ds.expressionExpression("\"test\".equals(\"test\")");
    }

    @Test
    public void urlClickAndCheckRedirectionPositive() {
        ds.urlClickAndCheckRedirection("RedirectionPage", "Link");
    }

    @Test
    public void elementIsNotVisiblePositive() {
        ds.elementIsNotVisible("HiddenDiv");
    }

    @Test
    public void checkElemContainsAtrWithValuePositive() {
        ds.checkElemContainsAtrWithValue("SUPERBUTTON", "onclick", "HIDEnSHOW()");
    }

    @Test
    public void testActualValueContainsSubstringPositive() {
        ds.testActualValueContainsSubstring("TextField", "xt");
    }

    @Test
    public void buttonIsNotActivePositive() {
        ds.buttonIsNotActive("DisabledButton");
    }

    @Test
    public void fieldIsDisablePositive() {
        ds.fieldIsDisable("DisabledField");
    }

    @Ignore
    @Test
    public void compareListFromUIAndFromVariablePositive() {

    }

    @Test
    public void openReadOnlyFormPositive() {
        ds.goToSelectedPageByLinkFromProperty("RedirectionPage",
                alfaScenario.getVar("RedirectionPage").toString());
        ds.openReadOnlyForm();
    }

    @Test
    public void addValuePositive() {
        ds.addValue("TextField", "Super");
        assertThat(alfaScenario.getEnvironment()
                        .getPage("AlfaPageMock")
                        .getAnyElementText("TextField"),
                equalTo("textSuper"));
    }

    @Test
    public void findElementPositive() {
        ds.findElement("LINK");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().getCurrentUrl(),
                equalTo(alfaScenario.getVar("RedirectionPage")));
    }

    @Test
    public void currentDatePositive() {
        ds.currentDate("NormalField", "dd.MM.yyyy");
        assertThat(alfaScenario.getEnvironment()
                        .getPage("AlfaPageMock")
                        .getAnyElementText("NormalField")
                        .matches("[0-3][0-9].[0-1][0-9].[0-2][0-9]{3}"),
                equalTo(true));
    }

    @Test
    public void refreshPageSimple() {
        ds.refreshPage();
    }

    @Test
    public void expandWindowToFullScreenSimple() {
        ds.expandWindowToFullScreen();
    }

    @Test
    public void elementHoverTest() {
        ds.elementHover("NormalField");
    }

    @Test
    public void clickableFieldTest() {
        ds.clickableField("SUPERBUTTON");
    }

    @Test
    public void scrollDownSimple() {
        ds.scrollDown();
    }
}