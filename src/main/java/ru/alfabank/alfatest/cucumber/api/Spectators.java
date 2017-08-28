package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.util.Arrays;
import java.util.Collection;

public final class Spectators {

    private Spectators() {

    }

    /**
     * Обертка над  @see SelenideElement#waitUntil(Condition, long)  для произвольного числа элементов
     * @param condition Selenide.Condition
     * @param timeout максимальное время ожидания для перехода элементов в заданное состояние
     * @param selenideElements произвольное количество selenide-элементов
     */
    public static void waitElementsUntil(Condition condition, int timeout, SelenideElement... selenideElements) {
        Arrays.stream(selenideElements).forEach(e -> e.waitUntil(condition, timeout));
    }

    /**
     * Обертка над @see SelenideElement#waitUntil(Condition, long) для работы с колекцией элементов
     * @param condition Selenide.Condition
     * @param timeout максимальное время ожидания для перехода элементов в заданное состояние
     * @param selenideElements коллекция selenide-элементов
     */
    public static void waitElementsUntil(Condition condition, int timeout, Collection<SelenideElement> selenideElements) {
        selenideElements.forEach(e -> e.waitUntil(condition, timeout));
    }

}
