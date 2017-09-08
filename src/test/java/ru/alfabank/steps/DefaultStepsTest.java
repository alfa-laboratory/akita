package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementShouldNot;
import cucumber.api.Scenario;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.ScopedVariables;
import ru.alfabank.alfatest.cucumber.api.TestEnvironment;
import ru.alfabank.alfatest.cucumber.api.TestScenario;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Created by onotole on 08.02.17.
 */
public class DefaultStepsTest {
    private static DefaultSteps ds;
    private static TestScenario testScenario;

    @BeforeClass
    public static void setup() {
        testScenario = TestScenario.getInstance();
        Scenario scenario = new StubScenario();
        testScenario.setEnvironment(new TestEnvironment(scenario));
        ds = new DefaultSteps();
        String inputFilePath = "src/test/resources/TestPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        testScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        testScenario.setVar("RedirectionPage", "file://" + url2);
    }

    @Before
    public void prepare() {
        ds.goToSelectedPageByLinkFromProperty("TestPageMock", testScenario.getVar("Page").toString());
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
    public void checkCurrentURLPositive() {
        ds.checkCurrentURL(testScenario.getVar("Page").toString());
    }

    @Test(expected = NullPointerException.class)
    public void checkCurrentURLNegative() {
        ds.checkCurrentURL(null);
    }

    @Ignore
    @Test
    public void setWindowSizeSimple() {
        Dimension expectedDimension = new Dimension(800, 600);
        ds.setWindowSize("800", "600");
        Dimension actualDimension = WebDriverRunner.getWebDriver().manage().window().getSize();
        assertThat(expectedDimension, equalTo(actualDimension));
    }

    @Test
    public void storeFieldValueInVariablePositive() {
        String varName = "mockId";
        ds.storeElementValueInVariable(varName, varName);
        assertThat(testScenario.getVar(varName), equalTo("Serious testing page"));
    }

    @Test(expected = AssertionError.class)
    public void compareTwoDigitVarsNegative() {
        String number1Name = "number1", number1Value = "1234567890";
        testScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567894";
        testScenario.setVar(number2Name, number2Value);

        ds.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void compareTwoDigitVars() {
        String number1Name = "number1", number1Value = "1234567890.97531";
        testScenario.setVar(number1Name, number1Value);

        String number2Name = "number2", number2Value = "1234567890.97531";
        testScenario.setVar(number2Name, number2Value);

        ds.compareTwoVariables(number1Name, number2Name);
    }

    @Test
    public void saveValueToVarPositive() {
        ds.saveValueToVar("testVar", "test");
        assertThat(testScenario.getVar("test"), equalTo("testValue"));
    }

    @Test
    public void clickOnElementPositive() {
        ds.clickOnElement("GoodButton");
        assertThat(testScenario.getPage("TestPageMock").getElement("GoodButton").isEnabled(),
                equalTo(false));
    }

    @Test
    public void elemIsPresentedOnPagePositive() {
        ds.elemIsPresentedOnPage("mockTagName");
    }

    @Test
    public void listIsPresentedOnPageTest() {
        ds.listIsPresentedOnPage("List");
    }

    @Test
    public void loadPageSimple() {
        ds.loadPage("TestPageMock");
    }

    @Test
    public void compareFieldAndVariablePositive() {
        testScenario.setVar("test", "Serious testing page");
        ds.compareFieldAndVariable("mockTagName", "test");
    }

    @Test
    public void checkIfListContainsValueFromFieldPositive() {
        List<String> list = new ArrayList<>();
        list.add("Serious testing page");
        testScenario.setVar("list", list);
        ds.checkIfListContainsValueFromField("mockTagName", "list");
    }

    @Test(expected = ElementShouldNot.class)
    public void blockDisappearedSimple() {
        ds.blockDisappeared("TestPageMock");
    }

    @Test
    public void pushButtonOnKeyboardSimple() {
        ds.pushButtonOnKeyboard("alt");
    }

    @Test
    public void setFieldValuePositive() {
        ds.setFieldValue("NormalField", "test");
        assertThat(testScenario.getEnvironment()
                        .getPage("TestPageMock")
                        .getAnyElementText("NormalField"),
                equalTo("test"));
    }

    @Test
    public void cleanFieldPositive() {
        ds.cleanField("TextField");
        assertThat(testScenario.getEnvironment()
                        .getPage("TestPageMock")
                        .getAnyElementText("TextField"),
                equalTo(""));
    }

    @Test
    public void fieldInputIsEmptyPositive() {
        ds.fieldInputIsEmpty("NormalField");
    }

    @Test
    public void checkIfListConsistsOfTableElementsTest() {
        ArrayList<String> types = new ArrayList<>();
        types.add("One");
        types.add("Two");
        types.add("Three");
        ds.checkIfListConsistsOfTableElements("List", types);
    }

    @Test
    public void checkIfSelectedListElementMatchesValueTest() {
        ds.checkIfSelectedListElementMatchesValue("List", "One");
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

    @Test
    public void compareListFromUIAndFromVariableTest() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("One");
        arrayList.add("Two");
        arrayList.add("Three");
        testScenario.setVar("qwerty", arrayList);
        ds.compareListFromUIAndFromVariable("List", "qwerty");
    }

    @Test
    public void openReadOnlyFormPositive() {
        ds.goToSelectedPageByLinkFromProperty("RedirectionPage",
                testScenario.getVar("RedirectionPage").toString());
        ds.openReadOnlyForm();
    }

    @Test
    public void addValuePositive() {
        ds.addValue("TextField", "Super");
        assertThat(testScenario.getEnvironment()
                        .getPage("TestPageMock")
                        .getAnyElementText("TextField"),
                equalTo("textSuper"));
    }

    @Test
    public void findElementPositive() {
        ds.findElement("LINK");
        sleep(500);
        assertThat(WebDriverRunner.getWebDriver().getCurrentUrl(),
                equalTo(testScenario.getVar("RedirectionPage")));
    }

    @Test
    public void currentDatePositive() {
        ds.currentDate("NormalField", "dd.MM.yyyy");
        assertThat(testScenario.getEnvironment()
                        .getPage("TestPageMock")
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

    @Test
    public void loginByUserDataPositive() {
        ds.loginByUserData("testUser");
    }

    @Test
    public void pasteValuePositive() {
        ds.pasteValueToTextField("testVal", "NormalField");
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("normalField")).getAttribute("value"),
                equalTo("testVal"));
    }

    @Test
    public void elementDisapperaredAndAppearedComplex() {
        ds.testElementAppeared("ul", 1);
        ds.clickOnElement("SUPERBUTTON");
        ds.elemDisappered("ul");
    }

    @Test
    public void goToUrl() {
        ds.goToUrl((String) testScenario.getVar("RedirectionPage"));
    }

    @Test
    public void compareValInFieldAndFromStepTest() {
        ds.compareValInFieldAndFromStep("ul", "Serious testing page");
    }

    @Test
    public void setVariableTest() {
        ds.setVariable("ul", "Serious testing page");
        assertThat(ds.getVar("ul"), equalTo("Serious testing page"));
    }

    @Test
    public void getVarsTest() {
        ds.setVar("1", "1");
        ds.setVar("2", "2");
        ScopedVariables scopedVariables = ds.getVars();
        assertThat((String)scopedVariables.get("1") + (String)scopedVariables.get("2"),
                equalTo("12"));
    }
}