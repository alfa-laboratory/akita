package ru.alfabank.steps.defaultSteps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.Scenario;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class InputFieldStepsTest {

    private static InputFieldSteps ifs;
    private static AkitaScenario akitaScenario;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        ifs = new InputFieldSteps();
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
    public void setFieldValuePositive() {
        ifs.setFieldValue("NormalField", "testSetFieldValue");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("NormalField"),
            equalTo("testSetFieldValue"));
    }

    @Test
    public void setFieldValuePositiveWithProps() {
        ifs.setFieldValue("NormalField", "testValueInProps");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("NormalField"),
            equalTo("test"));
    }

    @Test
    public void cleanFieldPositive() {
        ifs.cleanField("TextField");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("TextField"),
            equalTo(""));
    }

    @Test
    public void fieldInputIsEmptyPositive() {
        ifs.fieldInputIsEmpty("NormalField");
    }

    @Test
    public void addValuePositive() {
        ifs.addValue("TextField", "Super");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("TextField"),
            equalTo("text1 text2 text3Super"));
    }

    @Test
    public void addValuePositiveWithProps() {
        ifs.addValue("TextField", "itemValueInProps");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("TextField"),
            equalTo("text1 text2 text3item"));
    }

    @Test
    public void currentDatePositive() {
        ifs.currentDate("NormalField", "dd.MM.yyyy");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("NormalField")
                .matches("[0-3][0-9].[0-1][0-9].[0-2][0-9]{3}"),
            equalTo(true));
    }

    @Test
    public void loginByUserDataPositive() {
        ifs.loginByUserData("testUser");
    }

    @Test
    public void pasteValuePositive() {
        ifs.pasteValueToTextField("testVal", "NormalField");
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("normalField")).getAttribute("value"),
            equalTo("testVal"));
    }

    @Test
    public void pasteValuePositiveWithProps() {
        ifs.pasteValueToTextField("textValueInProps", "NormalField");
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("normalField")).getAttribute("value"),
            equalTo("text"));
    }

    @Test
    public void testSetRandomCharSequenceCyrillic() {
        ifs.setRandomCharSequence("NormalField", 4, "кириллице");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("NormalField").length(),
            equalTo(4));
    }

    @Test
    public void testSetRandomCharSequenceLathin() {
        ifs.setRandomCharSequence("NormalField", 7, "латинице");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("NormalField").length(),
            equalTo(7));
    }

    @Test
    public void testInputRandomNumSequencePositive() {
        ifs.inputRandomNumSequence("NormalField",4);
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("NormalField").length(),
            equalTo(4));
    }

    @Test(expected = AssertionError.class)
    public  void testInputRandomNumSequenceNegative() {
        ifs.inputRandomNumSequence("GoodButton", 4);
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("GoodButton").length(),
            equalTo(4));
    }

    @Test
    public void testInputAndSetRandomNumSequencePositive() {
        ifs.inputAndSetRandomNumSequence("NormalField", 5, "test");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("NormalField"),
            equalTo(akitaScenario.getVar("test")));
    }

    @Test
    public void testInputAndSetRandomNumSequenceOverrideVariable() {
        akitaScenario.setVar("test", "Lathin");
        akitaScenario.write(String.format("11111111111 [%s]", akitaScenario.getVar("test")));
        ifs.inputAndSetRandomNumSequence("NormalField", 5, "test");
        assertThat(akitaScenario.getEnvironment()
                .getPage("AkitaPageMock")
                .getAnyElementText("NormalField"),
            equalTo(akitaScenario.getVar("test")));
    }

    @Test
    public void testCheckFieldSize() {
        ifs.checkFieldSymbolsCount("ul", 20);
    }

}
