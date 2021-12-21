
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
    public void selectElementFromDropDownListWithText(String listName, String expectedText) {
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
    public void selectElementFromDropDownListWithValue(String listName, String expectedValue) {
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
    public void selectElementNumberFromDropDownList(int elementNumber, String listName) {
        Select list = new Select(akitaScenario.getCurrentPage().getElement(listName));
        int selectedElementNumber = elementNumber - 1;
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
    public void selectElementFromDropDownListContainingText(String listName, String expectedText) {
        final String text = getPropertyOrStringVariableOrValue(expectedText);
        SelenideElement list = akitaScenario.getCurrentPage().getElement(listName);
        list.selectOptionContainingText(text);
    }
}