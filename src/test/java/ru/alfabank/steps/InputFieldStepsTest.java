/**
 * Copyright 2017 Alfa Laboratory
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementShouldNot;
import cucumber.api.Scenario;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InputFieldStepsTest {

    private static AkitaScenario akitaScenario;
    private static InputInteractionSteps iis;
    private static WebPageInteractionSteps wpis;

    @BeforeAll
    static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        Scenario scenario = new StubScenario();
        akitaScenario.setEnvironment(new AkitaEnvironment(scenario));
        iis = new InputInteractionSteps();
        wpis = new WebPageInteractionSteps();
        String inputFilePath = "src/test/resources/AkitaPageMock.html";
        String url = new File(inputFilePath).getAbsolutePath();
        akitaScenario.setVar("Page", "file://" + url);
        String inputFilePath2 = "src/test/resources/RedirectionPage.html";
        String url2 = new File(inputFilePath2).getAbsolutePath();
        akitaScenario.setVar("RedirectionPage", "file://" + url2);
    }

    @BeforeEach
    void prepare() {
        wpis.goToSelectedPageByLink("AkitaPageMock", akitaScenario.getVar("Page").toString());
    }

    @AfterAll
    static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    void setFieldValuePositive() {
        iis.setFieldValue("NormalField", "testSetFieldValue");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField"),
                equalTo("testSetFieldValue"));
    }

    @Test
    void setTextareaFieldValuePositive() {
        iis.setFieldValue("normalTextAreaWithText", "textValueInProps");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("normalTextAreaWithText"),
                equalTo("text"));
    }


    @Test
    void setFieldValuePositiveWithProps() {
        iis.setFieldValue("NormalField", "testValueInProps");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField"),
                equalTo("test"));
    }

    @Test
    void addValuePositive() {
        iis.addValue("TextField", "Super");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("TextField"),
                equalTo("text1 text2 text3Super"));
    }

    @Test
    void addValuePositiveWithProps() {
        iis.addValue("TextField", "itemValueInProps");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("TextField"),
                equalTo("text1 text2 text3item"));
    }

    @Test
    void cleanFieldPositive() {
        iis.cleanField("TextField");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("TextField"),
                equalTo(""));
    }

    @Test
    void cleanNotEditableField() {
        assertThrows(ElementShouldNot.class, () -> iis.cleanField("DisabledField"));
    }

    @Test
    void testSetRandomCharSequenceCyrillic() {
        iis.setRandomCharSequence("NormalField", 4, "кириллице");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField").length(),
                equalTo(4));
    }

    @Test
    void testSetRandomCharSequenceLathin() {
        iis.setRandomCharSequence("NormalField", 7, "латинице");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField").length(),
                equalTo(7));
    }

    @Test
    void testSetRandomCharSequenceAndSaveToVarCyrillic() {
        iis.setRandomCharSequenceAndSaveToVar("NormalField", 4, "кириллице", "test");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField"),
                equalTo(akitaScenario.getVar("test")));
    }

    @Test
    void testSetRandomCharSequenceAndSaveToVarLathin() {
        iis.setRandomCharSequenceAndSaveToVar("NormalField", 7, "латинице", "test");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField"),
                equalTo(akitaScenario.getVar("test")));
    }

    @Test
    void testInputRandomNumSequencePositive() {
        iis.inputRandomNumSequence("NormalField", 4);
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField").length(),
                equalTo(4));
    }

    @Test
    void testInputRandomNumSequenceNegative() {
        assertThrows(AssertionError.class, () -> {
            iis.inputRandomNumSequence("GoodButton", 4);
            assertThat(akitaScenario.getEnvironment()
                            .getPage("AkitaPageMock")
                            .getAnyElementText("GoodButton").length(),
                    equalTo(4));
        });
    }

    @Test
    void testInputAndSetRandomNumSequencePositive() {
        iis.inputAndSetRandomNumSequence("NormalField", 5, "test");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField"),
                equalTo(akitaScenario.getVar("test")));
    }

    @Test
    void testInputAndSetRandomNumSequenceOverrideVariable() {
        akitaScenario.setVar("test", "Lathin");
        akitaScenario.write(String.format("11111111111 [%s]", akitaScenario.getVar("test")));
        iis.inputAndSetRandomNumSequence("NormalField", 5, "test");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField"),
                equalTo(akitaScenario.getVar("test")));
    }

    @Test
    void pasteValuePositive() {
        iis.pasteValueToTextField("testVal", "NormalField");
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("normalField")).getAttribute("value"),
                equalTo("testVal"));
    }

    @Test
    void pasteValuePositiveWithProps() {
        iis.pasteValueToTextField("textValueInProps", "NormalField");
        assertThat(WebDriverRunner.getWebDriver().findElement(By.name("normalField")).getAttribute("value"),
                equalTo("text"));
    }

    @Test
    void testInputRandomNumSequenceWithIntAndFractPositive() {
        iis.inputRandomNumSequenceWithIntAndFract("NormalField", 10, 99, ".#");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField").length(),
                equalTo(4));
    }

    @Test
    void testInputRandomNumSequenceWithIntAndFractMorePositive() {
        iis.inputRandomNumSequenceWithIntAndFract("NormalField", -9999, -1000, "####");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField").length(),
                equalTo(5));
    }

    @Test
    void testInputRandomNumSequenceWithIntAndFractNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                iis.inputRandomNumSequenceWithIntAndFract("NormalField", -1, -9, "####"));
    }

    @Test
    void testInputRandomNumSequenceWithIntAndFractMoreNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                iis.inputRandomNumSequenceWithIntAndFract("NormalField", 10, 20, "####,"));
    }

    @Test
    void testSetRandomNumSequenceWithIntAndFractPositive() {
        iis.setRandomNumSequenceWithIntAndFract("NormalField", 100, 999, "###.###", "test");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField").length(),
                equalTo(7));
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField").length(),
                equalTo(akitaScenario.getVar("test").toString().length()));
    }

    @Test
    void testSetRandomNumSequenceWithIntAndFractMorePositive() {
        iis.setRandomNumSequenceWithIntAndFract("NormalField", -99, 99, "###,###", "test");
        assertThat(akitaScenario.getEnvironment()
                        .getPage("AkitaPageMock")
                        .getAnyElementText("NormalField").length(),
                equalTo(akitaScenario.getVar("test").toString().length()));
    }

    @Test
    void testSetRandomNumSequenceWithIntAndFractNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                iis.setRandomNumSequenceWithIntAndFract("NormalField", 9999, 1000, "####", "test"));
    }

    @Test
    void testSetRandomNumSequenceWithIntAndFractMoreNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                iis.setRandomNumSequenceWithIntAndFract("NormalField", 5, 10, "####,", "test"));
    }
}