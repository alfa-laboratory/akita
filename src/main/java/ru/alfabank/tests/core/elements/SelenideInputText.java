package ru.alfabank.tests.core.elements;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.Map;

/**
 * Created by U_M0UKA on 18.01.2017.
 */
public abstract class SelenideInputText implements SelenideElement {
    public SelenideElement getErrorFromField(String error) {
        return $(By.xpath("//div[contains(text(), '" + error + "')]"));
    }

    public void clear(String fieldName, Map<String, Object> elements) {
        ((SelenideElement) elements.get(fieldName)).clear();
    }

    public void sendKeys(String fieldName, String text, Map<String, Object> elements) {
        ((SelenideElement) elements.get(fieldName)).sendKeys(text);
    }

    public String getText(String fieldName,Map<String, Object> elements) {
        return ((SelenideElement) elements.get(fieldName)).innerText();
    }

    public void setValue(String fieldName, String value, Map<String, Object> elements) {
        ((SelenideElement) elements.get(fieldName)).setValue(value);
    }

    public void commandPaste(String fieldName, Map<String, Object> elements) {
        SelenideElement element = (SelenideElement) elements.get(fieldName);
        if ("safari".equals(System.getProperty("browser"))) {
            element.sendKeys(Keys.chord(Keys.COMMAND, "v"));
        } else {
            element.sendKeys(Keys.chord(Keys.CONTROL, "v"));
        }
    }
    public String getValue(String fieldName, Map<String, Object> elements) {
        return ((SelenideElement) elements.get(fieldName)).getValue();
    }
}
