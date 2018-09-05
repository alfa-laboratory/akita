/**
 * Copyright 2017 Alfa Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.steps.defaultSteps;

import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Пусть;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.Keys;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;


/**
 * Шаги для тестирования полей ввода, доступные по умолчанию в каждом новом проекте.
 */

@Slf4j
public class InputFieldSteps {

    private AkitaScenario akitaScenario = AkitaScenario.getInstance();


    /**
     * Устанавливается значение (в приоритете: из property, из переменной сценария, значение аргумента) в заданное поле.
     * Перед использованием поле нужно очистить
     */
    @Когда("^в поле \"([^\"]*)\" введено значение \"(.*)\"$")
    public void setFieldValue(String elementName, String value) {
        value = akitaScenario.getPropertyOrStringVariableOrValue(value);
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(elementName);
        cleanField(elementName);
        valueInput.setValue(value);
    }

    /**
     * Очищается заданное поле
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String nameOfField) {
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(nameOfField);
        do {
            valueInput.doubleClick().sendKeys(Keys.DELETE);
        } while (valueInput.getValue().length() != 0);
    }

    /**
     * Проверка, что поле для ввода пусто
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String fieldName) {
        assertThat(String.format("Поле [%s] не пусто", fieldName),
            akitaScenario.getCurrentPage().getAnyElementText(fieldName),
            isEmptyOrNullString());
    }

    /**
     * Шаг авторизации.
     * Для того, чтобы шаг работал, на текущей странице должны быть указаны элементы
     * со значениями аннотации @Name:
     * "Логин" - для поля ввода логина,
     * "Пароль" - для поля ввода пароля и
     * "Войти" - для кнопки входа.
     * Также должны быть указаны логин и пароль в файле application.properties.
     * Например для шага: "Пусть пользователь user ввел логин и пароль"
     * логин и пароль должны быть указаны со следующими ключами:
     * user.login - для логина и
     * user.password - для пароля
     */
    @Пусть("^пользователь \"([^\"]*)\" ввел логин и пароль$")
    public void loginByUserData(String userCode) {
        String login = loadProperty(userCode + ".login");
        String password = loadProperty(userCode + ".password");
        cleanField("Логин");
        akitaScenario.getCurrentPage().getElement("Логин").sendKeys(login);
        cleanField("Пароль");
        akitaScenario.getCurrentPage().getElement("Пароль").sendKeys(password);
        akitaScenario.getCurrentPage().getElement("Войти").click();
    }

    /**
     * Добавление строки (в приоритете: из property, из переменной сценария, значение аргумента) в поле к уже заполненой строке
     */
    @Когда("^в элемент \"([^\"]*)\" дописывается значение \"(.*)\"$")
    public void addValue(String elementName, String value) {
        value = akitaScenario.getPropertyOrStringVariableOrValue(value);
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
    public void pasteValueToTextField(String value, String fieldName) {
        value = akitaScenario.getPropertyOrStringVariableOrValue(value);
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
     * Производится проверка количества символов в поле со значением, указанным в шаге
     */
    @Тогда("^в поле \"([^\"]*)\" содержится (\\d+) символов$")
    public void checkFieldSymbolsCount(String element, int num) {
        int length = akitaScenario.getCurrentPage().getAnyElementText(element).length();
        assertEquals(String.format("Неверное количество символов. Ожидаемый результат: %s, текущий результат: %s", num, length), num, length);
    }


    /**
     * Возвращает последовательность случайных символов переданных алфавита и длины
     * Принимает на вход варианты языков 'ru' и 'en'
     * Для других входных параметров возвращает латинские символы (en)
     */
    public String getRandCharSequence(int length, String lang) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char symbol = charGenerator(lang);
            builder.append(symbol);
        }
        return builder.toString();
    }

    /**
     * Возвращает случайный символ переданного алфавита
     */
    private char charGenerator(String lang) {
        Random random = new Random();
        if (lang.equals("ru")) {
            return (char) (1072 + random.nextInt(32));
        } else {
            return (char) (97 + random.nextInt(26));
        }
    }

}
