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
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;

/**
 * Шаги, содержащие проверки состояний вэб-страницы, доступные по умолчанию в каждом новом проекте
 */

@Slf4j
public class WebPageVerificationSteps extends BaseMethods {

    /**
     * Проверка, что текущий URL совпадает с ожидаемым
     * (берется из property / переменной, если такая переменная не найдена,
     * то берется переданное значение)
     */
    @Тогда("^текущий URL равен \"([^\"]*)\"$")
    @Then("^current URL is equal to \"([^\"]*)\"$")
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
    @Then("^current URL is not equal to \"([^\"]*)\"$")
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
    @Then("^(?:page|block|form|tab) \"([^\"]*)\" has been loaded$")
    public void loadingPage(String nameOfPage) {
        super.loadPage(nameOfPage);
    }

    /**
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional, не появились на странице
     */
    @Тогда("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" не (?:загрузилась|загрузился)$")
    @Then("^(?:page|block|form|tab) \"([^\"]*)\" has not been loaded$")
    public void loadPageFailed(String nameOfPage) {
        akitaScenario.setCurrentPage(akitaScenario.getPage(nameOfPage));
        if (isIE()) {
            akitaScenario.getCurrentPage().ieDisappeared();
        } else akitaScenario.getCurrentPage().disappeared();
    }

    /**
     * Проверка того, что блок исчез/стал невидимым
     */
    @Тогда("^(?:страница|блок|форма) \"([^\"]*)\" (?:скрыт|скрыта)")
    @Then("^(?:page|block|form) \"([^\"]*)\" is hidden$")
    public void blockDisappeared(String nameOfPage) {
        if (isIE()) {
            akitaScenario.getPage(nameOfPage).ieDisappeared();
        } else akitaScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * Проверка, что на странице не отображаются редактируемые элементы, такие как:
     * -input
     * -textarea
     */
    @Тогда("^открыта read-only форма$")
    @Then("^read-only form has been opened$")
    public void openReadOnlyForm() {
        int inputsCount = getDisplayedElementsByCss("input").size();
        assertTrue(inputsCount == 0, "Форма не read-only. Количество input-полей: " + inputsCount);
        int textareasCount = getDisplayedElementsByCss("textarea").size();
        assertTrue(textareasCount == 0, "Форма не read-only. Количество элементов textarea: " + textareasCount);
    }

    private List<SelenideElement> getDisplayedElementsByCss(String cssSelector) {
        return $$(cssSelector).stream()
                .filter(SelenideElement::isDisplayed)
                .collect(Collectors.toList());
    }

    /*
     * Проверка, что значение в ссылке страницы содержит текст, указанный в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^ссылка страницы содержит текст \"([^\"]*)\"$")
    @And("^link contains text \"([^\"]*)\"$")
    public void linkShouldHaveText(String text) {
        String currentUrl = url();
        assertThat(currentUrl, containsStringIgnoringCase(getPropertyOrStringVariableOrValue(text)));
    }

    /*
     * Производится сравнение заголовка страницы со значением, указанным в шаге
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^заголовок страницы равен \"([^\"]*)\"$")
    @Then("^page's header is equal to \"([^\"]*)\"$")
    public void checkTitlePage(String pageTitleName) {
        checkPageTitle(getTranslateNormalizeSpaceText(getPropertyOrStringVariableOrValue(pageTitleName)));
    }
}