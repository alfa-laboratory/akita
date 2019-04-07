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

import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.en.When;
import cucumber.api.java.ru.Когда;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Шаги для взаимодействия с полями ввода, доступные по умолчанию в каждом новом проекте
 */

@Slf4j
public class InputInteractionSteps extends BaseMethods {


    /**
     * Устанавливается значение (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле нужно очистить
     */
    @Когда("^в поле \"([^\"]*)\" введено значение \"(.*)\"$")
    @When("^into the field named \"([^\"]*)\" has been typed value \"(.*)\"$")
    public void setFieldValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        valueInput.setValue(value);
    }

    /**
     * Очищается заданное поле
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    @When("^cleared field named \"([^\"]*)\"$")
    public void cleaningField(String nameOfField) {
        super.cleanField(nameOfField);
    }

    /**
     * Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке
     */
    @Когда("^в элемент \"([^\"]*)\" дописывается значение \"(.*)\"$")
    @When("^element named \"([^\"]*)\" has been suplemented with value of \"(.*)\"$")
    public void addValue(String elementName, String value) {
        value = getPropertyOrStringVariableOrValue(value);
        SelenideElement field = akitaScenario.getCurrentPage().getElement(elementName);
        String oldValue = field.getValue();
        if (oldValue.isEmpty()) {
            oldValue = field.getText();
        }
        field.setValue("");
        field.setValue(oldValue + value);
    }

    /**
     * Ввод в поле текущей даты в заданном формате
     * При неверном формате, используется dd.MM.yyyy
     */
    @Когда("^элемент \"([^\"]*)\" заполняется текущей датой в формате \"([^\"]*)\"$")
    @When("^element named \"([^\"]*)\" has been filled with current date in format \"([^\"]*)\"$")
    public void currentDate(String fieldName, String dateFormat) {
        long date = System.currentTimeMillis();
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(dateFormat).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
            log.error("Неверный формат даты. Будет использоваться значание по умолчанию в формате dd.MM.yyyy");
        }
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(fieldName);
        valueInput.setValue("");
        valueInput.setValue(currentStringDate);
        akitaScenario.write("Текущая дата " + currentStringDate);
    }

    /**
     * Ввод в поле указанного текста (в приоритете: из property, из переменной сценария, значение аргумента),
     * используя буфер обмена и клавиши SHIFT + INSERT
     */
    @Когда("^вставлено значение \"([^\"]*)\" в элемент \"([^\"]*)\" с помощью горячих клавиш$")
    @When("^the value of \"([^\"]*)\" has been pasted to element named \"([^\"]*)\" using hotkeys$")
    public void pasteValueToTextField(String value, String fieldName) {
        value = getPropertyOrStringVariableOrValue(value);
        ClipboardOwner clipboardOwner = (clipboard, contents) -> {
        };
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(value);
        clipboard.setContents(stringSelection, clipboardOwner);
        akitaScenario.getCurrentPage().getElement(fieldName).sendKeys(Keys.chord(Keys.SHIFT, Keys.INSERT));
    }

    /**
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины
     */
    @Когда("^в поле \"([^\"]*)\" введено (\\d+) случайных символов на (кириллице|латинице)$")
    @When("^into the field named \"([^\"]*)\" has been entered (\\d+) random (?:latin|cyrillic) symbol(|s)$")
    public void setRandomCharSequence(String elementName, int seqLength, String lang) {
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);

        if (lang.equals("кириллице")) lang = "ru";
        else lang = "en";
        String charSeq = getRandCharSequence(seqLength, lang);
        valueInput.setValue(charSeq);
        akitaScenario.write("Строка случайных символов равна :" + charSeq);
    }

    /**
     * Ввод в поле случайной последовательности латинских или кириллических букв задаваемой длины и сохранение этого значения в переменную
     */
    @Когда("^в поле \"([^\"]*)\" введено (\\d+) случайных символов на (кириллице|латинице) и сохранено в переменную \"([^\"]*)\"$")
    @When("^into the field named \"([^\"]*)\" has been entered (\\d+) random (?:latin|cyrillic) symbol(|s) and saved to variable named \"([^\"]*)\"$")
    public void setRandomCharSequenceAndSaveToVar(String elementName, int seqLength, String lang, String varName) {
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);

        if (lang.equals("кириллице")) lang = "ru";
        else lang = "en";
        String charSeq = getRandCharSequence(seqLength, lang);
        valueInput.setValue(charSeq);
        akitaScenario.setVar(varName, charSeq);
        akitaScenario.write("Строка случайных символов равна :" + charSeq);
    }

    /**
     * Ввод в поле случайной последовательности цифр задаваемой длины
     */
    @Когда("^в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры)$")
    @When("^into the field named \"([^\"]*)\" has been entered (\\d+) random digit(|s)$")
    public void inputRandomNumSequence(String elementName, int seqLength) {
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        valueInput.setValue(numSeq);
        akitaScenario.write(String.format("В поле [%s] введено значение [%s]", elementName, numSeq));
    }

    /**
     * Ввод в поле случайной последовательности цифр задаваемой длины и сохранение этого значения в переменную
     */
    @Когда("^в поле \"([^\"]*)\" введено случайное число из (\\d+) (?:цифр|цифры) и сохранено в переменную \"([^\"]*)\"$")
    @When("^into the field named \"([^\"]*)\" has been entered (\\d+) random digit(|s) and saved to variable named \"([^\"]*)\"$")
    public void inputAndSetRandomNumSequence(String elementName, int seqLength, String varName) {
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        String numSeq = RandomStringUtils.randomNumeric(seqLength);
        valueInput.setValue(numSeq);
        akitaScenario.setVar(varName, numSeq);
        akitaScenario.write(String.format("В поле [%s] введено значение [%s] и сохранено в переменную [%s]",
                elementName, numSeq, varName));
    }

    /**
     * Ввод в поле случайного дробного числа в заданном диапазоне и формате с последующим сохранением этого значения в переменную
     * Пример формата ввода: ###.##
     */
    @Когда("^в поле \"([^\"]*)\" введено случайное дробное число от (\\d+) до (\\d+) в формате \"([^\"]*)\" и сохранено в переменную \"([^\"]*)\"$")
    @When("^into the field named \"([^\"]*)\" has been entered random fractional number from (\\d+) to (\\d+) in format \"([^\"]*)\" and saved to variable named \"([^\"]*)\"$")
    public void setRandomNumSequenceWithIntAndFract(String fieldName, double valueFrom, double valueTo, String outputFormat, String saveToVariableName) {
        outputFormat = outputFormat.replaceAll("#", "0");
        double finalValue = ThreadLocalRandom.current().nextDouble(valueFrom, valueTo);
        setFieldValue(fieldName, new DecimalFormat(outputFormat).format(finalValue));
        akitaScenario.setVar(saveToVariableName, new DecimalFormat(outputFormat).format(finalValue));
        akitaScenario.write(String.format("В поле [%s] введено значение [%s] и сохранено в переменную [%s]",
                fieldName, new DecimalFormat(outputFormat).format(finalValue), saveToVariableName));
    }

    /**
     * Ввод в поле случайного дробного числа в заданном диапазоне и формате
     * Пример формата ввода: ###.##
     */
    @Когда("^в поле \"([^\"]*)\" введено случайное дробное число от (\\d+) до (\\d+) в формате \"([^\"]*)\"$")
    @When("^into the field named \"([^\"]*)\" has been entered random fractional number from (\\d+) to (\\d+) in format \"([^\"]*)\"$")
    public void inputRandomNumSequenceWithIntAndFract(String fieldName, double valueFrom, double valueTo, String outputFormat) {
        double finalValue = ThreadLocalRandom.current().nextDouble(valueFrom, valueTo);
        outputFormat = outputFormat.replaceAll("#", "0");
        setFieldValue(fieldName, new DecimalFormat(outputFormat).format(finalValue));
        akitaScenario.write(String.format("В поле [%s] введено значение [%s]", fieldName, new DecimalFormat(outputFormat).format(finalValue)));
    }
}