package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.util.Arrays;
import java.util.Collection;

public final class Spectators {

    private Spectators() {

    }

    /**
     * Обертка над Selenide.waitUntil для произвольного числа элементов
     *
     * @param ec Selenide.Condition
     * @param timeout максимальное время ожидания для перехода элементов в заданное состояние
     * @param els произвольное количество selenide-элементов
     */
    public static void waitElementsUntil(Condition ec, int timeout, SelenideElement... els) {
        Arrays.stream(els).forEach(e -> e.waitUntil(ec, timeout));
    }

    /**
     * Обертка над Selenide.waitUntil для работы с колекцией элементов
     *
     * @param els коллекция selenide-элементов
     */
    public static void waitElementsUntil(Condition ec, int timeout, Collection<SelenideElement> els) {
        els.forEach(e -> e.waitUntil(ec, timeout));
    }

}
