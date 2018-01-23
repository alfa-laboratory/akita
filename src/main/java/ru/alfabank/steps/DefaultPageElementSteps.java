package ru.alfabank.steps;

import cucumber.api.java.ru.И;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

/**
 * Шаги для работы с блоками со страницы, доступные по умолчанию в каждом новом проекте
 */
@Slf4j
public class DefaultPageElementSteps {

    private AkitaScenario akitaScenario = AkitaScenario.getInstance();

    /**
     * На странице происходит клик по заданному элементу в блоке
     */
    @И("^выполнено нажатие на (?:кнопку|поле) \"([^\"]*)\" в блоке \"([^\"]*)\"$")
    public void clickOnElementInBlock(String elementName, String blockName) {
        akitaScenario.getCurrentPage().getBlock(blockName).getElement(elementName).click();
    }
}
