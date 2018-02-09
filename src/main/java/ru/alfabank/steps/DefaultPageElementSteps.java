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
