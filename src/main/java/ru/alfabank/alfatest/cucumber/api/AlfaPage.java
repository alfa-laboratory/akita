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

/**
 * Класс-аннотация для реализации паттерна PageObject
 */
@Slf4j
public abstract class AlfaPage extends ElementsContainer {
    /**
     * Стандартный таймаут ожидания элементов
     */
    private static final String WAITING_APPEAR_TIMEOUT = "8000";

    /**
     * Метод для получения элемента со страницы
     */
    public SelenideElement getElement(String name) {
        Object value = namedElements.get(name);
        if (value == null) throw new IllegalStateException("Элемент " + name + " на странице не найден.\n" +
                "Проверьте поля в описании страницы");
        return (SelenideElement) value;
    }

    /**
     * Метод для получения элемента-списка со страницы
     */
    @SuppressWarnings("unchecked")
    public List<SelenideElement> getElementsList(String name) {
        Object value = namedElements.get(name);
        if (!(value instanceof List))
            throw new IllegalStateException("Элемент-список " + name + " на странице не найден.\n" +
                    "Проверьте поля в описании страницы.");
        Stream<Object> s = ((List) value).stream();
        return s.map(AlfaPage::castToSelenideElement).collect(Collectors.toList());
    }

    /**
     * Метод для получения текста элемента, как редактируемого поля, так и статичного элемента
     */
    public String getAnyElementText(String name) {
        SelenideElement element = getElement(name);
        if (element.getTagName().equals("input")) {
            return element.getValue();
        }
        else {
            return element.innerText();
        }
    }

    /**
     * Метод для получения текстов всех элементов, содержащихся в элементе-списке,
     * состоящего как из редактируемых полей, так и статичных элементов
     */
    public List<String> getAnyElementsListTexts(String name) {
        List<SelenideElement> elementsList = getElementsList(name);
        return elementsList.stream()
                .map(element -> element.getTagName().equals("input") ? element.getValue()
                        : element.innerText()
                )
                .collect(Collectors.toList());
    }

    /**
     * Аннотация для элементов страницы, служащая для их индентификации в cucumber-сценариях
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Name {
        String value();
    }

    /**
     * Аннотация для элементов страницы,
     * служащая для отключения проверки появления элемента после загрухки страницы
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Optional {
    }

    /**
     * Метод для получения всех элементов страницы, не помеченных аннотацией "Optional"
     */
    public List<SelenideElement> getPrimaryElements() {
        if (primaryElements == null) primaryElements = readWithWrappedElements();
        return new ArrayList<>(primaryElements);
    }

    /**
     * Не уверен, что понимаю предназначение следующих двух методов
     */
    public final AlfaPage appeared() {
        isAppeared();
        return this;
    }

    public final AlfaPage disappeared() {
        isDisappeared();
        return this;
    }

    /**
     * Метод проверки появления всех элементов страницы, не помеченных аннотацией "Optional"
     */
    protected void isAppeared() {
        String timeout = loadProperty("waitingAppearTimeout", WAITING_APPEAR_TIMEOUT);
        getPrimaryElements().parallelStream().forEach(elem ->
            elem.waitUntil(Condition.appear, Integer.valueOf(timeout)));
    }

    /**
     * Метод для проверки, что все элементы страницы, не помеченных аннотацией "Optional", исчезли
     */
    protected void isDisappeared() {
        String timeout = loadProperty("waitingAppearTimeout", WAITING_APPEAR_TIMEOUT);
        getPrimaryElements().parallelStream().forEach(elem ->
            elem.waitWhile(Condition.exist, Integer.valueOf(timeout)));
    }

    /**
     * Надстройка над Selenide.waitUntil для произвольного количества элементов
     *
     * @param condition Selenide.Condition
     * @param timeout максимальное время ожидания для перехода элементов в заданное состояние
     * @param elements произвольное количество selenide-элементов
     */
    public void waitElementsUntil(Condition condition, int timeout, SelenideElement... elements) {
        Spectators.waitElementsUntil(condition, timeout, elements);
    }

    /**
     * Надстройка над Selenide.waitUntil для работы со списком элементов
     *
     * @param elements список selenide-элементов
     */
    public void waitElementsUntil(Condition condition, int timeout, List<SelenideElement> elements) {
        Spectators.waitElementsUntil(condition, timeout, elements);
    }

    /**
     * Метод для проверки, что все переданные элементы в течении заданного периода времени
     * перешли в состояние Selenide.Condition
     *
     * @param elementNames произвольное количество строковых переменных с именами элементов
     */
    public void waitElementsUntil(Condition condition, int timeout, String... elementNames) {
        List<SelenideElement> elements = Arrays.stream(elementNames)
                .map(name -> namedElements.get(name))
                .flatMap(v -> v instanceof List ? ((List<?>) v).stream() : Stream.of(v))
                .map(AlfaPage::castToSelenideElement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Spectators.waitElementsUntil(condition, timeout, elements);
    }

    /**
     * (Название метода крайне странное, так как проверки на то, что переданные элементы являются кнопками нет)
     * Метод для поиска элемента по имени внутри списка элементов
     */
    public static SelenideElement getButtonFromListByName(List<SelenideElement> listButtons, String nameOfButton) {
        List<String> names = new ArrayList<>();
        for (SelenideElement button : listButtons) {
            names.add(button.getText());
        }
        return listButtons.get(names.indexOf(nameOfButton));
    }

    /**
     * Безопасное приведение объекта к типу SelenideElement
     */
    private static SelenideElement castToSelenideElement(Object o) {
        if (o instanceof SelenideElement) {
            return (SelenideElement) o;
        }
        return null;
    }

    /**
     * Список всех элементов страницы
     */
    private Map<String, Object> namedElements;
    /**
     * Список элементов страницы, не помеченных аннотацией "Optional"
     */
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

    /**
     * Метод для поиска и инициализации элементов страницы
     */
    private Map<String, Object> readNamedElements() {
        checkNamedAnnotations();
        return Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getDeclaredAnnotation(Name.class) != null)
                .peek(f -> {
                    if(!SelenideElement.class.isAssignableFrom(f.getType()) && !List.class.isAssignableFrom(f.getType()))
                        throw new IllegalStateException(
                                format("Field with @Name annotation must be SelenideElement or List<SelenideElement>, but %s found", f.getType())
                        );
                })
                .collect(Collectors.toMap(f -> f.getDeclaredAnnotation(Name.class).value(), this::extractFieldValueViaReflection));
    }

    /**
     * Метод для поиска по аннотации "Name"
     */
    private void checkNamedAnnotations() {
        List<String> list = Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getDeclaredAnnotation(Name.class) != null)
                .map(f -> f.getDeclaredAnnotation(Name.class).value())
                .collect(Collectors.toList());
        if (list.size() != new HashSet<>(list).size()) {
            throw new IllegalStateException("Found two annotation with same value in class " + this.getClass());
        }
    }
    /**
     * Метод для поиска и инициализации элементов страницы без аннотации Optional
     */
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
