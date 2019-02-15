package ru.alfabank.steps;


import com.codeborne.selenide.SelenideElement;
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
    @Тогда("^в выпадающем списке \"([^\"]*)\" выбран элемент с внутренним текстом \"([^\"]*)\"$")
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
    public void selectELementFromDropDownListContainingText (String listName, String expectedText) {
        final String text = getPropertyOrStringVariableOrValue(expectedText);
        SelenideElement list = akitaScenario.getCurrentPage().getElement(listName);

        list.selectOptionContainingText(text);

    }

}
