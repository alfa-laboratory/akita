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

import cucumber.api.java.en.Then;
import cucumber.api.java.ru.Тогда;

import java.util.ArrayList;
import java.util.List;

/**
 * Шаги для тестирования верстки (Galen Framework)
 */

public class LayoutSteps extends BaseMethods {

    /**
     * Шаг проверяет, что текущая страница соответствует описанным в .spec файле требованиям
     * @param spec - Название galen спецификации .spec, где описан ожидаемый дизайн страницы
     *             По умолчанию ожидается, что .spec файлы находятся по пути /src/test/resources/specs.
     *             Этот путь можно переопределить, задав системную переменную specsDir
     */

    @Тогда("(?:страница соответствует|соответствует|блок соответствует) ожидаемой спецификации \"([^\"]*)\"")
    @Then("^(?:page|block) matches the expected Galen-specification \"([^\"]*)\"$")
    public void compareCurrentPageWithBase(String spec) {
        checkLayoutAccordingToSpec(spec, null);
    }

    /**
     *
     * Шаг проверяет, что текущая страница соответствует описанным в .spec файле требованиям
     * @param spec - Название galen спецификации .spec, где описан ожидаемый дизайн страницы
     *             По умолчанию ожидается, что .spec файлы находятся по пути /src/test/resources/specs.
     *             Этот путь можно переопределить, задав системную переменную specsDir
     * @param tag - название тэга в galen спецификации (например @on desktop),
     *           для которого описан дизайн конкретных элементов.
     */
    @Тогда("(?:страница соответствует|соответствует|блок соответствует) спецификации \"([^\"]*)\" для экрана \"(\\D+)\"")
    @Then("^(?:page|block) matches the Galen-specification \"([^\"]*)\" for \"(\\D+)\"$")
    public void compareCurrentPageWithBase(String spec, String tag) {
        List<String> tags = new ArrayList<>();
        tags.add(tag);
        checkLayoutAccordingToSpec(spec, tags);
    }
}