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
import cucumber.api.java.en.Then;
import cucumber.api.java.ru.Тогда;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.ui.Select;

/**
 * Шаги для взаимодействия с выпадающими списками, доступные по умолчанию в каждом новом проекте
 */

@Slf4j
public class DropDownListInteractionSteps extends BaseMethods {

    /**
     * Выбор из выпадающего списка элемента с заданным текстом
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^в выпадающем списке \"([^\"]*)\" выбран элемент с текстом \"([^\"]*)\"$")
    @Then("^in drop down list named \"([^\"]*)\" element with text \"([^\"]*)\" had been selected$")
    public void selectELementFromDropDownListWithText (String listName, String expectedText) {
        final String text = getPropertyOrStringVariableOrValue(expectedText);
        SelenideElement list = akitaScenario.getCurrentPage().getElement(listName);
        list.selectOption(text);
    }

    /**
     * Выбор из выпадающего списка элемента с заданным значением
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^в выпадающем списке \"([^\"]*)\" выбран элемент со значением \"([^\"]*)\"$")
    @Then("^in drop down list named \"([^\"]*)\" element with value \"([^\"]*)\" had been selected$")
    public void selectElementFromDropDownListWithValue (String listName, String expectedValue) {
        final String value = getPropertyOrStringVariableOrValue(expectedValue);
        SelenideElement list = akitaScenario.getCurrentPage().getElement(listName);
        list.selectOptionByValue(value);
    }

    /**
     * Выбор из выпадающего списка n-го элемента
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^выбран (\\d+)-й элемент в выпадающем списке \"([^\"]*)\"$")
    @Then("^selected the (\\d+)(st|nd|rd|th) element from the drop down list named \"([^\"]*)\"$")
    public void selectElementNumberFromDropDownList (int elementNumber, String listName) {
        Select list = new Select(akitaScenario.getCurrentPage().getElement(listName));
        Integer selectedElementNumber = elementNumber - 1;
        if (selectedElementNumber < 0 || selectedElementNumber >= list.getOptions().size()) {
            throw new IndexOutOfBoundsException(
                    String.format("В списке %s нет элемента с номером %s. Количество элементов списка = %s",
                            listName, elementNumber, list.getOptions().size()));
        }
        list.selectByIndex(selectedElementNumber);
    }

    /**
     * Выбор из выпадающего списка элемента, содержащего заданный текст
     * (в приоритете: из property, из переменной сценария, значение аргумента)
     */
    @Тогда("^в выпадающем списке \"([^\"]*)\" выбран элемент содержащий текст \"([^\"]*)\"$")
    @Then("^in drop down list named \"([^\"]*)\" element containing text \"([^\"]*)\" had been selected$")
    public void selectELementFromDropDownListContainingText (String listName, String expectedText) {
        final String text = getPropertyOrStringVariableOrValue(expectedText);
        SelenideElement list = akitaScenario.getCurrentPage().getElement(listName);
        list.selectOptionContainingText(text);
    }
}