package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Selenide;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Предназначен для хранения страниц, используемых при прогоне тестов
 */
public final class Pages {

    /**
     * Страницы, на которых будет производится тестирование < Имя, Страница >
     */
    private Map<String, TestPage> pages;

    /**
     * Страница, на которой в текущий момент производится тестирование
     */
    private TestPage currentPage;

    public Pages() {
        pages = Maps.newHashMap();
    }


    /**
     *  Возвращает текущую страницу, на которой в текущий момент производится тестирование
     */
    public TestPage getCurrentPage() {
        if (currentPage == null) throw new IllegalStateException("Текущая страница не задана");
        return currentPage;
    }

    /**
     *  Задает текущую страницу по ее имени
     */
    public void setCurrentPage(TestPage page) {
        this.currentPage = page;
    }

    /**
     * Реализация анонимных методов со страницей в качестве аргумента
     *
     * @param clazz класс страницы
     * @param checkIfElementsAppeared проверка всех не помеченных "@Optional" элементов
     */
    public static <T extends TestPage> void withPage(Class<T> clazz, boolean checkIfElementsAppeared, Consumer<T> consumer) {
        T page = getPage(clazz, checkIfElementsAppeared);
        consumer.accept(page);
    }

    /**
     * Получение страницы из "pages" по имени
     */
    public TestPage get(String pageName) {
        return getPageMapInstanceInternal().get(pageName);
    }

    /**
     * Получение страницы по классу
     */
    @SuppressWarnings("unchecked")
    public <T extends TestPage> T get(Class<T> clazz, String name) {
        TestPage page = getPageMapInstanceInternal().get(name);
        if(!clazz.isInstance(page)) {
            throw new IllegalStateException(name + " page is not a instance of " + clazz + ". Named page is a " + page);
        }
        return (T) page;
    }

    private Map<String, ? extends TestPage> getPageMapInstanceInternal() {
        return pages;
    }

    /**
     * Добавление инстанциированной страницы в "pages" с проверкой на NULL
     */
    public <T extends TestPage> void put(String pageName, T page) throws IllegalArgumentException {
        if (page == null)
            throw new IllegalArgumentException("Была передана пустая страница");
        pages.put(pageName, page);
    }

    /**
     * Получение страницы по классу с возможностью выполнить проверку элементов страницы
     */
    public static <T extends TestPage> T getPage(Class<T> clazz, boolean checkIfElementsAppeared) {
        T page = Selenide.page(clazz);
        if(checkIfElementsAppeared) {
            page.isAppeared();
        }
        return page;
    }

    /**
     * Добавление страницы в "pages" по классу
     */
    public void put(String pageName, Class<? extends TestPage> clazz) {
        pages.put(pageName, Selenide.page(clazz).initialize());
    }
}
