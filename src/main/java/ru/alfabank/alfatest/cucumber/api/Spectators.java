package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by ruslanmikhalev on 26/01/17.
 */
public final class Spectators {

    private Spectators() {

    }

    public static void waitElementsUntil(Condition selenideCondition, int timeout, SelenideElement... els) {
        Arrays.stream(els).forEach(e -> e.waitUntil(selenideCondition, timeout));
    }

    public static void waitElementsUntil(Condition selenideCondition, int timeout, Collection<SelenideElement> els) {
        els.forEach(e -> e.waitUntil(selenideCondition, timeout));
    }

}
