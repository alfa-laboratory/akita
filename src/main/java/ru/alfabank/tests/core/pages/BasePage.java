package ru.alfabank.tests.core.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
abstract public class BasePage extends ElementsContainer {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Name {
        String value();
    }

    public BasePage load() {
        setMapOfElements(this.getClass());
        isOpened();
        return this;
    }

    public void disappeared() {
        setMapOfElements(this.getClass());
        isDissapered();
    }

    protected void isOpened() {
        waitElementsUntil(Condition.appear, 8000, getAllSelenideElements());
    }

    protected void isDissapered() {
        waitElementsUntil(Condition.disappears, 5000, getAllSelenideElements());
    }

    public Map<String, Object> elements;

    protected void setMapOfElements(Class<? extends BasePage> pageClass) {
        elements = Arrays.stream(pageClass.getDeclaredFields())
                .filter(f -> f.getDeclaredAnnotation(Name.class) != null)
                .collect(Collectors.toMap(f -> f.getDeclaredAnnotation(Name.class).value(), this::getFieldValue));
    }

    private Object getFieldValue(Field f) {
        f.setAccessible(true);
        try {
            return f.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void pageWithScriptsLoaded() {
        WebDriverWait waiter = new WebDriverWait(WebDriverRunner.getWebDriver(), 60);
        ExpectedCondition<Boolean> expectation = driver ->
                "complete".equals(getJavaScriptExecutor().executeScript("return document.readyState"));
        try {
            waiter.until(expectation);
        } catch(TimeoutException ignored) {

        }
    }

    public static SelenideElement getSelenideElement(Object object) {
        if (!(object instanceof List)) {
            return (SelenideElement) object;
        } else {
            throw new NullPointerException();
        }
    }

    @FindBy(xpath = "//div[@class='app-header__company']")
    protected SelenideElement linkOrganization;

    @FindBy(xpath = "//div[@class='popup__content']")
    protected SelenideElement listOfOrganizations;

    public static SelenideElement getButtonByName(List<SelenideElement> listButtons, String nameOfButton) {
        List<String> names = new ArrayList<>();
        for (SelenideElement button : listButtons) {
            names.add(button.getText());
        }
        return listButtons.get(names.indexOf(nameOfButton));
    }

    public SelenideElement getErrorFromField(String error) {
        return $(By.xpath("//div[contains(text(), '" + error + "')]"));
    }

    public void clear(String fieldName) {
        ((SelenideElement) elements.get(fieldName)).clear();
    }

    public void sendKeys(String fieldName, String text) {
        ((SelenideElement) elements.get(fieldName)).sendKeys(text);
    }

    public void click(String buttonName) {
        ((SelenideElement) elements.get(buttonName)).click();
    }

    public void waitUntilAppears() {
        elements.entrySet().forEach(elem ->{
                ((SelenideElement)(elem.getValue())).waitUntil(Condition.appears, 10000);});
    }

    public String getText(String fieldName) {
        String expectedString = ((SelenideElement) elements.get(fieldName)).innerText();
        return expectedString;
    }

    public void commandPaste(String fieldName) {
        SelenideElement element = (SelenideElement) elements.get(fieldName);
        if ("safari".equals(System.getProperty("browser"))) {
            element.sendKeys(Keys.chord(Keys.COMMAND, "v"));
        } else {
            element.sendKeys(Keys.chord(Keys.CONTROL, "v"));
        }
    }

    public String getValue(String fieldName) {
        return ((SelenideElement) elements.get(fieldName)).getValue();
    }

    protected void waitElementsUntil(Condition ec, int timeout, SelenideElement ... els) {
        Arrays.stream(els).forEach(e -> e.waitUntil(ec, timeout));
    }

    protected void waitElementsUntil(Condition ec, int timeout, Collection<SelenideElement> els) {
        waitElementsUntil(ec, timeout, els.toArray(new SelenideElement[els.size()]));
    }

    @SuppressWarnings("unchecked")
    private ArrayList<SelenideElement> getAllSelenideElements() {
        BiConsumer<ArrayList<SelenideElement>, Object> addElement = (list, el) -> {
            if (el instanceof SelenideElement) {
                list.add((SelenideElement)el);
            } else if (el instanceof Collection) {
                Collection<?> collection = ((Collection<?>) el);
                collection.stream()
                        .filter(v -> !(v instanceof SelenideElement))
                        .findAny()
                        .ifPresent(v -> {
                            throw new IllegalStateException("Illegal field type: " + v.getClass());
                        });
                list.addAll((Collection<SelenideElement>)collection);
            }
        };
        return elements.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(ArrayList::new, addElement, ArrayList::addAll);
    }

    private JavascriptExecutor getJavaScriptExecutor() {
        return (JavascriptExecutor) WebDriverRunner.getWebDriver();
    }
}
