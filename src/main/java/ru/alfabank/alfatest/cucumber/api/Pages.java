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
    private Map<String, AlfaPage> pages;
    /**
     * Страница, на которой в текущий момент производится тестирование
     */
    private AlfaPage currentPage;

    public Pages() {
        pages = Maps.newHashMap();
    }

    public AlfaPage getCurrentPage() {
        if (currentPage == null) throw new AssertionError("Current Page empty!");
        return currentPage;
    }

    public void setCurrentPage(AlfaPage page) {
        this.currentPage = page;
    }

    /**
     * ???
     */
    public static <T extends AlfaPage> void withPage(Class<T> clazz, boolean checkIsAppeared, Consumer<T> consumer) {
        T page = getPage(clazz, checkIsAppeared);
        consumer.accept(page);
    }

    public AlfaPage get(String name) {
        return getPageMapInstanceInternal().get(name);
    }

    /**
     * Получение страницы с определенным классом
     */
    @SuppressWarnings("unchecked")
    public <T extends AlfaPage> T get(Class<T> clazz, String name) {
        AlfaPage page = getPageMapInstanceInternal().get(name);
        if(!clazz.isInstance(page)) {
            throw new IllegalStateException(name + " page is not a instance of " + clazz + ". Named page is a " + page);
        }
        return (T) page;
    }

    private Map<String, ? extends AlfaPage> getPageMapInstanceInternal() {
        return pages;
    }

    public <T extends AlfaPage> void put(String pageName, T page) throws IllegalArgumentException {
        if (page == null)
            throw new IllegalArgumentException("Была передана пустая страница");
        pages.put(pageName, page);
    }

    public static <T extends AlfaPage> T getPage(Class<T> clazz, boolean checkIsAppeared) {
        T page = Selenide.page(clazz);
        if(checkIsAppeared) {
            page.isAppeared();
        }
        return page;
    }

    public void put(String key, Class<? extends AlfaPage> clazz) {
        pages.put(key, Selenide.page(clazz).initialize());
    }
}
