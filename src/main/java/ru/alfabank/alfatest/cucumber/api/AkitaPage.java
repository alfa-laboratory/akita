/**
 * Copyright 2017 Alfa Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.annotations.Name;
import ru.alfabank.alfatest.cucumber.annotations.Optional;
import ru.alfabank.alfatest.cucumber.utils.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Класс для реализации паттерна PageObject
 */
@Slf4j
public abstract class AkitaPage extends ElementsContainer {
    /**
     * Стандартный таймаут ожидания элементов в миллисекундах
     */
    private static final String WAITING_APPEAR_TIMEOUT_IN_MILLISECONDS = "8000";

    /**
     * Получение блока со страницы по имени (аннотированного "Name")
     */
    public AkitaPage getBlock(String blockName) {
        return (AkitaPage) java.util.Optional.ofNullable(namedElements.get(blockName))
                .orElseThrow(() -> new IllegalArgumentException("Блок " + blockName + " не описан на странице " + this.getClass().getName()));
    }

    /**
     * Получение элемента со страницы по имени (аннотированного "Name")
     */
    public SelenideElement getElement(String elementName) {
        return (SelenideElement) java.util.Optional.ofNullable(namedElements.get(elementName))
                .orElseThrow(() -> new IllegalArgumentException("Элемент " + elementName + " не описан на странице " + this.getClass().getName()));
    }

    /**
     * Получение элемента-списка со страницы по имени
     */
    @SuppressWarnings("unchecked")
    public List<SelenideElement> getElementsList(String listName) {
        Object value = namedElements.get(listName);
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("Список " + listName + " не описан на странице " + this.getClass().getName());
        }
        Stream<Object> s = ((List) value).stream();
        return s.map(AkitaPage::castToSelenideElement).collect(toList());
    }

    /**
     * Получение текстов всех элементов, содержащихся в элементе-списке,
     * состоящего как из редактируемых полей, так и статичных элементов по имени
     * Используется метод innerText(), который получает как видимый, так и скрытый текст из элемента,
     * обрезая перенос строк и пробелы в конце и начале строчки.
     */
    public List<String> getAnyElementsListInnerTexts(String listName) {
        List<SelenideElement> elementsList = getElementsList(listName);
        return elementsList.stream()
            .map(element -> element.getTagName().equals("input")
                ? element.getValue().trim()
                : element.innerText().trim()
            )
            .collect(toList());
    }

    /**
     * Получение текста элемента, как редактируемого поля, так и статичного элемента по имени
     */
    public String getAnyElementText(String elementName) {
        return getAnyElementText(getElement(elementName));
    }

    /**
     * Получение текста элемента, как редактируемого поля, так и статичного элемента по значению элемента
     */
    public String getAnyElementText(SelenideElement element) {
        if (element.getTagName().equals("input")) {
            return element.getValue();
        } else {
            return element.getText();
        }
    }

    /**
     * Получение текстов всех элементов, содержащихся в элементе-списке,
     * состоящего как из редактируемых полей, так и статичных элементов по имени
     */
    public List<String> getAnyElementsListTexts(String listName) {
        List<SelenideElement> elementsList = getElementsList(listName);
        return elementsList.stream()
                .map(element -> element.getTagName().equals("input")
                        ? element.getValue()
                        : element.getText()
                )
                .collect(toList());
    }

    /**
     * Получение всех элементов страницы, не помеченных аннотацией "Optional"
     */
    public List<SelenideElement> getPrimaryElements() {
        if (primaryElements == null) {
            primaryElements = readWithWrappedElements();
        }
        return new ArrayList<>(primaryElements);
    }

    /**
     * Обертка над AkitaPage.isAppeared
     * Ex: AkitaPage.appeared().doSomething();
     */
    public final AkitaPage appeared() {
        isAppeared();
        return this;
    }

    /**
     * Обертка над AkitaPage.isDisappeared
     * Ex: AkitaPage.disappeared().doSomething();
     */
    public final AkitaPage disappeared() {
        isDisappeared();
        return this;
    }

    /**
     * Проверка появления всех элементов страницы, не помеченных аннотацией "Optional"
     */
    protected void isAppeared() {
        String timeout = loadProperty("waitingAppearTimeout", WAITING_APPEAR_TIMEOUT_IN_MILLISECONDS);
        getPrimaryElements().parallelStream().forEach(elem ->
                elem.waitUntil(Condition.appear, Integer.valueOf(timeout)));
        eachForm(AkitaPage::isAppeared);
    }

    private void eachForm(Consumer<AkitaPage> func) {
        Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getDeclaredAnnotation(Optional.class) == null)
                .forEach(f -> {
                    if (AkitaPage.class.isAssignableFrom(f.getType())){
                        AkitaPage akitaPage = AkitaScenario.getInstance().getPage((Class<? extends AkitaPage>)f.getType());
                        func.accept(akitaPage);
                    }
                });
    }

    /**
     * Проверка, что все элементы страницы, не помеченные аннотацией "Optional", исчезли
     */
    protected void isDisappeared() {
        String timeout = loadProperty("waitingAppearTimeout", WAITING_APPEAR_TIMEOUT_IN_MILLISECONDS);
        getPrimaryElements().parallelStream().forEach(elem ->
                elem.waitWhile(Condition.exist, Integer.valueOf(timeout)));
    }

    /**
     * Обертка над AkitaPage.isAppearedInIe
     * Ex: AkitaPage.ieAppeared().doSomething();
     * Используется при работе с IE
     */
    public final AkitaPage ieAppeared() {
        isAppearedInIe();
        return this;
    }

    /**
     * Обертка над AkitaPage.isDisappearedInIe
     * Ex: AkitaPage.ieDisappeared().doSomething();
     * Используется при работе с IE
     */
    public final AkitaPage ieDisappeared() {
        isDisappearedInIe();
        return this;
    }

    /**
     * Проверка появления всех элементов страницы, не помеченных аннотацией "Optional".
     * Вместо parallelStream используется stream из-за медленной работы IE
     */
    protected void isAppearedInIe() {
        String timeout = loadProperty("waitingAppearTimeout", WAITING_APPEAR_TIMEOUT_IN_MILLISECONDS);
        getPrimaryElements().stream().forEach(elem ->
                elem.waitUntil(Condition.appear, Integer.valueOf(timeout)));
        eachForm(AkitaPage::isAppearedInIe);
    }

    /**
     * Проверка, что все элементы страницы, не помеченные аннотацией "Optional", исчезли
     * Вместо parallelStream используется stream из-за медленной работы IE
     */
    protected void isDisappearedInIe() {
        String timeout = loadProperty("waitingAppearTimeout", WAITING_APPEAR_TIMEOUT_IN_MILLISECONDS);
        getPrimaryElements().stream().forEach(elem ->
                elem.waitWhile(Condition.exist, Integer.valueOf(timeout)));
    }


    /**
     * Обертка над Selenide.waitUntil для произвольного количества элементов
     *
     * @param condition Selenide.Condition
     * @param timeout   максимальное время ожидания для перехода элементов в заданное состояние
     * @param elements  произвольное количество selenide-элементов
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
                .map(AkitaPage::castToSelenideElement)
                .filter(Objects::nonNull)
                .collect(toList());
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
     * Приведение объекта к типу SelenideElement
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

    public AkitaPage initialize() {
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
                .peek((Field f) -> {
                    if (!SelenideElement.class.isAssignableFrom(f.getType())
                            && !AkitaPage.class.isAssignableFrom(f.getType())) {
                        if (List.class.isAssignableFrom(f.getType())) {
                            ParameterizedType listType = (ParameterizedType) f.getGenericType();
                            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                            if (SelenideElement.class.isAssignableFrom(listClass) || AkitaPage.class.isAssignableFrom(listClass)) {
                                return;
                            }
                        }
                        throw new IllegalStateException(
                                format("Поле с аннотацией @Name должно иметь тип SelenideElement или List<SelenideElement>.\n" +
                                        "Если поле описывает блок, оно должно принадлежать классу, унаследованному от AkitaPage.\n" +
                                        "Найдено поле с типом %s", f.getType()));
                    }
                })
                .collect(toMap(f -> f.getDeclaredAnnotation(Name.class).value(), this::extractFieldValueViaReflection));
    }

    /**
     * Поиск по аннотации "Name"
     */
    private void checkNamedAnnotations() {
        List<String> list = Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getDeclaredAnnotation(Name.class) != null)
                .map(f -> f.getDeclaredAnnotation(Name.class).value())
                .collect(toList());
        if (list.size() != new HashSet<>(list).size()) {
            throw new IllegalStateException("Найдено несколько аннотаций @Name с одинаковым значением в классе " + this.getClass().getName());
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
                .map(AkitaPage::castToSelenideElement)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Object extractFieldValueViaReflection(Field field) {
        return Reflection.extractFieldValue(field, this);
    }
}
