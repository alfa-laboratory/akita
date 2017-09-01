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
     * Получение элемента со страницы по имени (аннотированного "Name")
     */
    public SelenideElement getElement(String elementName) {
        Object value = namedElements.get(elementName);
        if (value == null) throw new IllegalStateException("Элемент " + elementName + " на странице не найден.\n" +
                "Проверьте поля в описании страницы");
        return (SelenideElement) value;
    }

    /**
     * Получение элемента-списка со страницы по имени
     */
    @SuppressWarnings("unchecked")
    public List<SelenideElement> getElementsList(String listName) {
        Object value = namedElements.get(listName);
        if (!(value instanceof List))
            throw new IllegalStateException("Элемент-список " + listName + " на странице не найден.\n" +
                    "Проверьте поля в описании страницы.");
        Stream<Object> s = ((List) value).stream();
        return s.map(AlfaPage::castToSelenideElement).collect(Collectors.toList());
    }

    /**
     * Получение текста элемента, как редактируемого поля, так и статичного элемента по имени
     */
    public String getAnyElementText(String elementName) {
        SelenideElement element = getElement(elementName);
        if (element.getTagName().equals("input")) {
            return element.getValue();
        }
        else {
            return element.innerText();
        }
    }

    /**
     * Получение текстов всех элементов, содержащихся в элементе-списке,
     * состоящего как из редактируемых полей, так и статичных элементов по имени
     */
    public List<String> getAnyElementsListTexts(String listName) {
        List<SelenideElement> elementsList = getElementsList(listName);
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
     * служащая для отключения проверки появления элемента после загрузки страницы
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Optional {
    }

    /**
     * Получение всех элементов страницы, не помеченных аннотацией "Optional"
     */
    public List<SelenideElement> getPrimaryElements() {
        if (primaryElements == null) primaryElements = readWithWrappedElements();
        return new ArrayList<>(primaryElements);
    }

    /**
     * Обертка над AlfaPage.isAppeared
     * Ex: AlfaPage.appeared().doSomething();
     */
    public final AlfaPage appeared() {
        isAppeared();
        return this;
    }

    /**
     * Обертка над AlfaPage.isDisappeared
     * Ex: AlfaPage.disappeared().doSomething();
     */
    public final AlfaPage disappeared() {
        isDisappeared();
        return this;
    }

    /**
     * Проверка появления всех элементов страницы, не помеченных аннотацией "Optional"
     */
    protected void isAppeared() {
        String timeout = loadProperty("waitingAppearTimeout", WAITING_APPEAR_TIMEOUT);
        getPrimaryElements().parallelStream().forEach(elem ->
            elem.waitUntil(Condition.appear, Integer.valueOf(timeout)));
    }

    /**
     * Проверка, что все элементы страницы, не помеченные аннотацией "Optional", исчезли
     */
    protected void isDisappeared() {
        String timeout = loadProperty("waitingAppearTimeout", WAITING_APPEAR_TIMEOUT);
        getPrimaryElements().parallelStream().forEach(elem ->
            elem.waitWhile(Condition.exist, Integer.valueOf(timeout)));
    }

    /**
     * Обертка над Selenide.waitUntil для произвольного количества элементов
     *
     * @param condition Selenide.Condition
     * @param timeout максимальное время ожидания для перехода элементов в заданное состояние
     * @param elements произвольное количество selenide-элементов
     */
    public void waitElementsUntil(Condition condition, int timeout, SelenideElement... elements) {
        Spectators.waitElementsUntil(condition, timeout, elements);
    }

    /**
     * Обертка над Selenide.waitUntil для работы со списком элементов
     *
     * @param elements список selenide-элементов
     */
    public void waitElementsUntil(Condition condition, int timeout, List<SelenideElement> elements) {
        Spectators.waitElementsUntil(condition, timeout, elements);
    }

    /**
     * Проверка, что все переданные элементы в течении заданного периода времени
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
     * Поиск элемента по имени внутри списка элементов
     */
    public static SelenideElement getButtonFromListByName(List<SelenideElement> listButtons, String nameOfButton) {
        List<String> names = new ArrayList<>();
        for (SelenideElement button : listButtons) {
            names.add(button.getText());
        }
        return listButtons.get(names.indexOf(nameOfButton));
    }

    /**
     *  Приведение объекта к типу SelenideElement
     */
    private static SelenideElement castToSelenideElement(Object object) {
        if (object instanceof SelenideElement) {
            return (SelenideElement) object;
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
     * Поиск и инициализации элементов страницы
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
     * Поиск по аннотации "Name"
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
     * Поиск и инициализации элементов страницы без аннотации Optional
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

    private Object extractFieldValueViaReflection(Field field) {
        return Reflection.extractFieldValue(field, this);
    }
}
