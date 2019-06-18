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

import com.codeborne.selenide.Selenide;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static ru.alfabank.tests.core.helpers.PropertyLoader.*;

/**
 * Шаги для тестирования взаимодействия с внешним окружением (устройствами, файлами, переменными окружения, property и т.д.)
 */

@Slf4j
public class RoundUpSteps extends BaseMethods {

    /**
     * Значение заданной переменной из application.properties сохраняется в переменную в akitaScenario
     * для дальнейшего использования
     */
    @И("^сохранено значение \"([^\"]*)\" из property файла в переменную \"([^\"]*)\"$")
    @And("^value of \"([^\"]*)\" from property-file has been saved to the variable \"([^\"]*)\"$")
    public void saveValueToVar(String propertyVariableName, String variableName) {
        propertyVariableName = loadProperty(propertyVariableName);
        akitaScenario.setVar(variableName, propertyVariableName);
        akitaScenario.write("Значение сохраненной переменной " + propertyVariableName);
    }

    /**
     * Устанавливается значение переменной в хранилище переменных. Один из кейсов: установка login пользователя
     */
    @И("^установлено значение переменной \"([^\"]*)\" равным \"(.*)\"$")
    @And("^value of the variable \"([^\"]*)\" has been set to \"(.*)\"$")
    public void setVariable(String variableName, String value) {
        value = getPropertyOrValue(value);
        akitaScenario.setVar(variableName, value);
    }

    /**
     * Ожидание в течение заданного количества секунд
     */
    @Когда("^выполнено ожидание в течение (\\d+) (?:секунд|секунды)")
    @When("^waiting for (\\d+) (?:second|seconds)$")
    public void waitForSeconds(long seconds) {
        sleep(1000 * seconds);
    }

    /**
     * Эмулирует нажатие клавиш на клавиатуре
     */
    @И("^выполнено нажатие на клавиатуре \"([^\"]*)\"$")
    @And("^pressed \"([^\"]*)\" key $")
    public void pushButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        switchTo().activeElement().sendKeys(key);
    }

    /**
     * Эмулирует нажатие сочетания клавиш на клавиатуре.
     * Допустим, чтобы эмулировать нажатие на Ctrl+A, в таблице должны быть следующие значения
     * | CONTROL |
     * | a       |
     *
     * @param keyNames название клавиши
     */
    @И("^выполнено нажатие на сочетание клавиш из таблицы$")
    @And("^pressed keyboard shortcut from the table$")
    public void pressKeyCombination(List<String> keyNames) {
        Iterable<CharSequence> listKeys = keyNames.stream()
                .map(this::getKeyOrCharacter)
                .collect(Collectors.toList());
        String combination = Keys.chord(listKeys);
        switchTo().activeElement().sendKeys(combination);
    }

    private CharSequence getKeyOrCharacter(String key) {
        try {
            return Keys.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return key;
        }
    }

     /**
     * Выполняется запуск js-скрипта с указанием в js.executeScript его логики
     * Скрипт можно передать как аргумент метода или значение из application.properties
     */
    @Когда("^выполнен js-скрипт \"([^\"]*)\"")
    @When("^executed js-script \"([^\"]*)\"$")
    public void executeJsScript(String scriptName) {
        String content = loadValueFromFileOrPropertyOrVariableOrDefault(scriptName);
        Selenide.executeJavaScript(content);
    }

    /**
     * Метод осуществляет снятие скриншота и прикрепление его к cucumber отчету.
     *
     */
    @И("^снят скриншот текущей страницы$")
    @And("^screenshot of the current page has been taken$")
    public void takeScreenshot() {
        final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
        AkitaScenario.getInstance().getScenario().embed(screenshot, "image/png");
    }

    /**
     * Выполняется чтение файла с шаблоном и заполнение его значениями из таблицы
     */
    @И("^шаблон \"([^\"]*)\" заполнен данными из таблицы и сохранён в переменную \"([^\"]*)\"$")
    @And("^template named \"([^\"]*)\" has been filled with data from the table and saved to the variable \"([^\"]*)\"$")
    public void fillTemplate(String templateName, String varName, DataTable table) {
        String template = loadValueFromFileOrPropertyOrVariableOrDefault(templateName);
        boolean error = false;
        for (List<String> list : table.raw()) {
            String regexp = list.get(0);
            String replacement = list.get(1);
            if (template.contains(regexp)) {
                template = template.replaceAll(regexp, replacement);
            } else {
                akitaScenario.write("В шаблоне не найден элемент " + regexp);
                error = true;
            }
        }
        if (error)
            throw new RuntimeException("В шаблоне не найдены требуемые регулярные выражения");
        akitaScenario.setVar(varName, template);
    }

    /**
     * Проверка равенства двух переменных из хранилища
     */
    @Тогда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    @Then("^variable named \"([^\"]*)\" is equal to variable named \"([^\"]*)\"$")
    public void compareTwoVariables(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = akitaScenario.getVar(firstVariableName).toString();
        String secondValueToCompare = akitaScenario.getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] не совпадают", firstVariableName, secondVariableName),
                firstValueToCompare, equalTo(secondValueToCompare));
    }

    /**
     * Проверка неравенства двух переменных из хранилища
     */
    @Тогда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" не совпадают$")
    @Then("^variable named \"([^\"]*)\" is not equal to variable named \"([^\"]*)\"$")
    public void checkingTwoVariablesAreNotEquals(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = akitaScenario.getVar(firstVariableName).toString();
        String secondValueToCompare = akitaScenario.getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] совпадают", firstVariableName, secondVariableName),
                firstValueToCompare, Matchers.not(equalTo(secondValueToCompare)));
    }

    /**
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     */
    @Тогда("^файл \"(.*)\" загрузился в папку /Downloads$")
    @Then("^file \"(.*)\" has been downloaded to the /Downloads folder$")
    public void testFileDownloaded(String fileName) {
        File downloads = getDownloadsDir();
        File[] expectedFiles = downloads.listFiles((files, file) -> file.contains(fileName));
        assertNotNull(expectedFiles, "Ошибка поиска файла");
        assertFalse( expectedFiles.length == 0, "Файл не загрузился");
        assertTrue(expectedFiles.length == 1,
                String.format("В папке присутствуют более одного файла с одинаковым названием, содержащим текст [%s]", fileName));
        deleteFiles(expectedFiles);
    }

    /**
     * Проверка совпадения значения из переменной и значения из property
     */
    @Тогда("^значения из переменной \"([^\"]*)\" и из property файла \"([^\"]*)\" совпадают$")
    @Then("^values of \"([^\"]*)\" variable and \"([^\"]*)\" key from property file are equal$")
    public void checkIfValueFromVariableEqualPropertyVariable(String envVarible, String propertyVariable) {
        assertThat("Переменные " + envVarible + " и " + propertyVariable + " не совпадают",
                (String) akitaScenario.getVar(envVarible), equalToIgnoringCase(loadProperty(propertyVariable)));
    }

    /**
     * Проверка выражения на истинность
     * выражение из property, из переменной сценария или значение аргумента
     * Например, string1.equals(string2)
     * OR string.equals("string")
     * Любое Java-выражение, возвращающие boolean
     */
    @Тогда("^верно, что \"([^\"]*)\"$")
    @Then("^\"([^\"]*)\" is true$")
    public void expressionExpression(String expression) {
        akitaScenario.getVars().evaluate("assert(" + expression + ")");
    }
}