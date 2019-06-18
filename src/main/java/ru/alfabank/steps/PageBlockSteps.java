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
    @And("^(?:button|field) \"([^\"]*)\" in \"([^\"]*)\" block has been pressed$")
    public void clickOnElementInBlock(String elementName, String blockName) {
        akitaScenario.getCurrentPage().getBlock(blockName).getElement(elementName).click();
    }

    /**
     * Поиск списка в блоке и сохранение всех значений выбранного списка в переменную
     */
    @И("^в блоке \"([^\"]*)\" найден список элементов \"([^\"]*)\" и сохранен текст в переменную \"([^\"]*)\"$")
    @And("^in block named \"([^\"]*)\" the elements list named \"([^\"]*)\" has been found and its text has been saved to the \"([^\"]*)\" variable$")
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
    @And("^in block named \"([^\"]*)\" the elements list named \"([^\"]*)\" has been found and saved to the \"([^\"]*)\" variable$")
    public void getElementsList(String blockName, String listName, String varName) {
        akitaScenario.setVar(varName, akitaScenario.getCurrentPage().getBlock(blockName).getElementsList(listName));
    }
}