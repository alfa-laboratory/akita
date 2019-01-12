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

package ru.alfabank.steps;

import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadPropertyInt;

/**
 * Шаги для работы с вэб-страницей, переменными и property-файлами, доступные по умолчанию в каждом новом проекте
 */


@Slf4j
public class WebPageVerificationSteps extends BaseMethods {

    private AkitaScenario akitaScenario = AkitaScenario.getInstance();

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);

    /**
     * Проверка, что текущий URL совпадает с ожидаемым
     * (берется из property / переменной, если такая переменная не найдена,
     * то берется переданное значение)
     */
    @Тогда("^текущий URL равен \"([^\"]*)\"$")
    public void checkCurrentURL(String url) {
        String currentUrl = url();
        String expectedUrl = resolveVars(getPropertyOrStringVariableOrValue(url));
        assertThat("Текущий URL не совпадает с ожидаемым", currentUrl, is(expectedUrl));
    }

    /**
     * Проверка, что текущий URL не совпадает с ожидаемым
     * (берется из property / переменной, если такая переменная не найдена,
     * то берется переданное значение)
     */
    @Тогда("^текущий URL не равен \"([^\"]*)\"$")
    public void checkCurrentURLIsNotEquals(String url) {
        String currentUrl = url();
        String expectedUrl = resolveVars(getPropertyOrStringVariableOrValue(url));
        assertThat("Текущий URL совпадает с ожидаемым", currentUrl, Matchers.not(expectedUrl));
    }

    /**
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional появились на странице
     * в течение WAITING_APPEAR_TIMEOUT, которое равно значению свойства "waitingAppearTimeout"
     * из application.properties. Если свойство не найдено, время таймаута равно 8 секундам
     */
    @Тогда("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadingPage(String nameOfPage) {
        super.loadPage(nameOfPage);
    }

    /**
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional, не появились на странице
     */
    @Тогда("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" не (?:загрузилась|загрузился)$")
    public void loadPageFailed(String nameOfPage) {
        akitaScenario.setCurrentPage(akitaScenario.getPage(nameOfPage));
        if (isIE()) {
            akitaScenario.getCurrentPage().ieDisappeared();
        } else akitaScenario.getCurrentPage().disappeared();
    }

    /**
     * Проверка равенства двух переменных из хранилища
     */
    @Тогда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
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
    public void checkingTwoVariablesAreNotEquals(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = akitaScenario.getVar(firstVariableName).toString();
        String secondValueToCompare = akitaScenario.getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] совпадают", firstVariableName, secondVariableName),
                firstValueToCompare, Matchers.not(equalTo(secondValueToCompare)));
    }

    /**
     * Проверка того, что блок исчез/стал невидимым
     */
    @Тогда("^(?:страница|блок|форма) \"([^\"]*)\" (?:скрыт|скрыта)")
    public void blockDisappeared(String nameOfPage) {
        if (isIE()) {
            akitaScenario.getPage(nameOfPage).ieDisappeared();
        } else akitaScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * Проверка выражения на истинность
     * выражение из property, из переменной сценария или значение аргумента
     * Например, string1.equals(string2)
     * OR string.equals("string")
     * Любое Java-выражение, возвращающие boolean
     */
    @Тогда("^верно, что \"([^\"]*)\"$")
    public void expressionExpression(String expression) {
        akitaScenario.getVars().evaluate("assert(" + expression + ")");
    }

    /**
     * Проверка, что на странице не отображаются редактируемые элементы, такие как:
     * -input
     * -textarea
     */
    @Тогда("^открыта read-only форма$")
    public void openReadOnlyForm() {
        int inputsCount = getDisplayedElementsByCss("input").size();
        assertTrue("Форма не read-only. Количество input-полей: " + inputsCount, inputsCount == 0);
        int textareasCount = getDisplayedElementsByCss("textarea").size();
        assertTrue("Форма не read-only. Количество элементов textarea: " + textareasCount, textareasCount == 0);
    }

    private List<SelenideElement> getDisplayedElementsByCss(String cssSelector) {
        return $$(cssSelector).stream()
                .filter(SelenideElement::isDisplayed)
                .collect(Collectors.toList());
    }

    /**
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     */
    @Тогда("^файл \"(.*)\" загрузился в папку /Downloads$")
    public void testFileDownloaded(String fileName) {
        File downloads = getDownloadsDir();
        File[] expectedFiles = downloads.listFiles((files, file) -> file.contains(fileName));
        assertNotNull("Ошибка поиска файла", expectedFiles);
        assertFalse("Файл не загрузился", expectedFiles.length == 0);
        assertTrue(String.format("В папке присутствуют более одного файла с одинаковым названием, содержащим текст [%s]", fileName),
                expectedFiles.length == 1);
        deleteFiles(expectedFiles);
    }

    /**
    * Проверка совпадения значения из переменной и значения из property
    */
    @Тогда("^значения из переменной \"([^\"]*)\" и из property файла \"([^\"]*)\" совпадают$")
    public void checkIfValueFromVariableEqualPropertyVariable(String envVarible, String propertyVariable) {
        assertThat("Переменные " + envVarible + " и " + propertyVariable + " не совпадают",
                (String) akitaScenario.getVar(envVarible), equalToIgnoringCase(loadProperty(propertyVariable)));
    }
}
