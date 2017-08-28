package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.util.Arrays;
import java.util.Collection;

public final class Spectators {

    private Spectators() {

    }

    /**
     * Обертка над Selenide waitUntil для произвольного числа элементов
     * @see SelenideElement#waitUntil(Condition, long)
     * @param condition Selenide.Condition
     * @param timeout максимальное время ожидания в миллисекундах для перехода элементов в заданное состояние
     * @param selenideElements произвольное количество selenide-элементов
     */
    public static void waitElementsUntil(Condition condition, int timeout, SelenideElement... selenideElements) {
        Arrays.stream(selenideElements).forEach(e -> e.waitUntil(condition, timeout));
    }

    /**
     * Обертка над Selenide waitUntil для работы с колекцией элементов
     * @see SelenideElement#waitUntil(Condition, long)
     * @param condition Selenide.Condition
     * @param timeout максимальное время ожидания в миллисекундах для перехода элементов в заданное состояние
     * @param selenideElements коллекция selenide-элементов
     */
    public static void waitElementsUntil(Condition condition, int timeout, Collection<SelenideElement> selenideElements) {
        selenideElements.forEach(e -> e.waitUntil(condition, timeout));
    }

}
