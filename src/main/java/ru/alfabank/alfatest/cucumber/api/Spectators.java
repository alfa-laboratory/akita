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
package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.Arrays;
import java.util.Collection;

public final class Spectators {

    private Spectators() {

    }

    /**
     * Обертка над Selenide waitUntil для произвольного числа элементов
     *
     * @param selenideCondition Selenide.Condition
     * @param timeout           максимальное время ожидания в миллисекундах для перехода элементов в заданное состояние
     * @param selenideElements  произвольное количество selenide-элементов
     * @see SelenideElement#waitUntil(Condition, long)
     */
    public static void waitElementsUntil(Condition selenideCondition, int timeout, SelenideElement... selenideElements) {
        Arrays.stream(selenideElements).forEach(e -> e.waitUntil(selenideCondition, timeout));
    }

    /**
     * Перегрузка метода для работы с ElementsCollection и использования стандартных методов обработки списков
     *
     * @param selenideCondition Selenide.Condition
     * @param timeout           максимальное время ожидания в миллисекундах для перехода элементов в заданное состояние
     * @param selenideElements  ElementsCollection
     */
    public static void waitElementsUntil(Condition selenideCondition, int timeout, ElementsCollection selenideElements) {
        selenideElements.shouldBe(conditionToConditionCollection(selenideCondition), timeout);
    }

    /**
     * Обертка над Selenide waitUntil для работы с колекцией элементов
     *
     * @param selenideCondition Selenide.Condition
     * @param timeout           максимальное время ожидания в миллисекундах для перехода элементов в заданное состояние
     * @param selenideElements  коллекция selenide-элементов
     * @see SelenideElement#waitUntil(Condition, long)
     */
    public static void waitElementsUntil(Condition selenideCondition, int timeout, Collection<SelenideElement> selenideElements) {
        selenideElements.forEach(e -> e.waitUntil(selenideCondition, timeout));
    }

    private static CollectionCondition conditionToConditionCollection(Condition selenideCondition) {
        if (selenideCondition.equals(Condition.visible)) {
            return CollectionCondition.sizeGreaterThan(0);
        }
        return null;
    }

}
