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

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadPropertyInt;


/**
 * Шаги для тестирования взаимодействия с элементами страницы, доступные по умолчанию в каждом новом проекте.
 */

@Slf4j
public class ElementInteractionSteps {

    private AkitaScenario akitaScenario = AkitaScenario.getInstance();

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);


    /**
     * На странице происходит клик по заданному элементу
     */
    @И("^выполнено нажатие на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        akitaScenario.getCurrentPage().getElement(elementName).click();
    }



    /**
     * Выполняется наведение курсора на элемент
     */
    @Когда("^выполнен ховер на (?:поле|элемент) \"([^\"]*)\"$")
    public void elementHover(String elementName) {
        SelenideElement field = akitaScenario.getCurrentPage().getElement(elementName);
        field.hover();
    }

    /**
     * Проверка, что элемент недоступен для нажатия и редактирования
     */
    @Тогда("^(?:ссылка|кнопка|поле|элемент) \"([^\"]*)\" (?:недоступно|недоступна|недоступен) для (?:нажатия|редактирования)$")
    public void elementIsDisabled(String elementName) {
        SelenideElement element = akitaScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] кликабелен", elementName), element.is(Condition.disabled));
    }

    /**
     * Скроллит страницу вниз до появления элемента с текстом каждую секунду.
     * Если достигнут футер страницы и элемент не найден - выбрасывается exception.
     */
    @И("^страница прокручена до появления элемента с текстом \"([^\"]*)\"$")
    public void scrollWhileElemWithTextNotFoundOnPage(String expectedValue) {
        SelenideElement el = null;
        do {
            el = $(By.xpath(getTranslateNormalizeSpaceText(expectedValue)));
            if (el.exists()) {
                break;
            }
            executeJavaScript("return window.scrollBy(0, 250);");
            sleep(1000);
        } while (!atBottom());
        assertThat("Элемент с текстом " + expectedValue + " не найден", el.isDisplayed());
    }


    /**
     * Нажатие на элемент по его тексту (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @И("^выполнено нажатие на элемент с текстом \"(.*)\"$")
    public void findElement(String text) {
        $(By.xpath(getTranslateNormalizeSpaceText(akitaScenario.getPropertyOrStringVariableOrValue(text)))).click();
    }

    /**
     * Возвращает локатор для поиска по нормализованному(без учета регистра) тексту
     */
    public String getTranslateNormalizeSpaceText (String expectedText) {
        StringBuilder text = new StringBuilder();
        text.append("//*[contains(translate(normalize-space(text()), ");
        text.append("'ABCDEFGHIJKLMNOPQRSTUVWXYZАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ', ");
        text.append("'abcdefghijklmnopqrstuvwxyzабвгдеёжзийклмнопрстуфхчшщъыьэюя'), '");
        text.append(expectedText.toLowerCase());
        text.append("')]");
        return text.toString();
    }

}
