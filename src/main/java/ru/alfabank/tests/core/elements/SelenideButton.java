package ru.alfabank.tests.core.elements;

import com.codeborne.selenide.SelenideElement;

import java.util.Map;

/**
 * Created by U_M0UKA on 18.01.2017.
 */
public abstract class SelenideButton implements SelenideElement{

    public void click(String buttonName, Map<String, Object> elements) {
        ((SelenideElement) elements.get(buttonName)).click();
    }

}
