package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.utils.Reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

@Slf4j
public abstract class AlfaPage extends ElementsContainer {
    private static final String WAITING_APPEAR_TIMEOUT = "8000";

    public SelenideElement getElement(String name) {
        Object value = namedElements.get(name);
        if (value == null) {
            log.error("Элемент " + name + " на странице не найден");
            return null;
        }
        return (SelenideElement) value;
    }

    @SuppressWarnings("unchecked")
    public List<SelenideElement> getElementsList(String name) {
        Object value = namedElements.get(name);
        if (!(value instanceof List)) {
            log.error("Элемент-список " + name + " на странице не найден");
            return Collections.emptyList();
        }

        Stream<Object> s = ((List) value).stream();
        return s.map(AlfaPage::castToSelenideElement).collect(Collectors.toList());
    }

    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Name {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Optional {
    }

    public List<SelenideElement> getPrimaryElements() {
        if (primaryElements == null) primaryElements = readWithWrappedElements();
        return new ArrayList<>(primaryElements);
    }

    public final AlfaPage appeared() {
        isAppeared();
        return this;
    }

    public final AlfaPage disappeared() {
        isDisappeared();
        return this;
    }

    protected void isAppeared() {
        String timeout;
        try {
            Object property = loadProperty("waitingAppearTimeout");
            timeout = property.toString();
        } catch (IllegalArgumentException e) {
            timeout = WAITING_APPEAR_TIMEOUT;
        }
        String finalTimeout = timeout;
        getPrimaryElements().parallelStream().forEach(elem ->
                elem.waitUntil(Condition.appear, Integer.valueOf(finalTimeout)));
    }

    protected void isDisappeared() {
        Selenide.sleep(4000);
        getPrimaryElements().parallelStream().forEach(
                elem -> elem.shouldBe(Condition.not(Condition.exist))
        );
    }

    public void waitElementsUntil(Condition condition, int timeout, SelenideElement... elements) {
        Spectators.waitElementsUntil(condition, timeout, elements);
    }

    public void waitElementsUntil(Condition condition, int timeout, List<SelenideElement> elements) {
        Spectators.waitElementsUntil(condition, timeout, elements);
    }

    public void waitElementsUntil(Condition condition, int timeout, String... elementNames) {
        List<SelenideElement> elements = Arrays.stream(elementNames)
                .map(name -> namedElements.get(name))
                .flatMap(v -> v instanceof List ? ((List<?>) v).stream() : Stream.of(v))
                .map(AlfaPage::castToSelenideElement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Spectators.waitElementsUntil(condition, timeout, elements);
    }

    public static SelenideElement getButtonFromListByName(List<SelenideElement> listButtons, String nameOfButton) {
        List<String> names = new ArrayList<>();
        for (SelenideElement button : listButtons) {
            names.add(button.getText());
        }
        return listButtons.get(names.indexOf(nameOfButton));
    }


    private static SelenideElement castToSelenideElement(Object o) {
        if (o instanceof SelenideElement) {
            return (SelenideElement) o;
        }
        return null;
    }

    private Map<String, Object> namedElements;
    private List<SelenideElement> primaryElements;

    @Override
    public void setSelf(SelenideElement self) {
        super.setSelf(self);
        initialize();
    }

    public AlfaPage initialize() {
        namedElements = readNamedElements();
        primaryElements = readWithWrappedElements();
        return this;
    }

    private Map<String, Object> readNamedElements() {
        checkNamedAnnotations();
        return Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getDeclaredAnnotation(Name.class) != null)
                .peek(f -> {
                    if(!SelenideElement.class.isAssignableFrom(f.getType()))
                        throw new IllegalStateException(
                                format("Field with @Name annotation must be SelenideElement, but %s found", f.getType())
                        );
                })
                .collect(Collectors.toMap(f -> f.getDeclaredAnnotation(Name.class).value(), this::extractFieldValueViaReflection));
    }

    private void checkNamedAnnotations() {
        List<String> list = Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getDeclaredAnnotation(Name.class) != null)
                .map(f -> f.getDeclaredAnnotation(Name.class).value())
                .collect(Collectors.toList());
        if (list.size() != new HashSet<>(list).size()) {
            throw new IllegalStateException("Found two annotation with same value in class " + this.getClass());
        }
    }

    private List<SelenideElement> readWithWrappedElements() {
        return Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getDeclaredAnnotation(Optional.class) == null)
                .map(this::extractFieldValueViaReflection)
                .flatMap(v -> v instanceof List ? ((List<?>) v).stream() : Stream.of(v))
                .map(AlfaPage::castToSelenideElement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Object extractFieldValueViaReflection(Field f) {
        return Reflection.extractFieldValue(f, this);
    }
}
