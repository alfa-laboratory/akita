package ru.alfabank.steps;

import com.codeborne.selenide.SelenideElement;
import cucumber.api.java.ru.И;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

/**
 * Шаги для тестирования отдельных блоков страницы
 */

@Slf4j
public class PageBlockSteps extends BaseMethods{

    /**
     * На странице происходит клик по заданному элементу в блоке
     */
    @И("^выполнено нажатие на (?:кнопку|поле) \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void clickOnElementInBlock(String elementName, String blockName) {
        akitaScenario.getCurrentPage().getBlock(blockName).getElement(elementName).click();
    }
    /**
     * Поиск списка в блоке и сохранение всех значений выбранного списка в переменную
     */
    @И("^в блоке \"([^\"]*)\" найден список элементов\"([^\"]*)\" и сохранен текст в переменную \"([^\"]*)\"$")
    public void getListElementsText(String blockName, String listName, String varName) {
        akitaScenario.setVar(varName,
                akitaScenario.getCurrentPage()
                        .getBlock(blockName)
                        .getElementsList(listName)
                        .stream()
                        .map(SelenideElement::getText)
                        .collect(Collectors.toList()));
    }

    /**
     * Поиск списка в блоке и сохранение всех элементов выбранного списка в переменную
     */
    @И("^в блоке \"([^\"]*)\" найден список элементов\"([^\"]*)\" и сохранен в переменную \"([^\"]*)\"$")
    public void getElementsList(String blockName, String listName, String varName) {
        akitaScenario.setVar(varName, akitaScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
    }

}
