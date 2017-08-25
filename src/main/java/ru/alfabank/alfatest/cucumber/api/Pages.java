package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Selenide;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by ruslanmikhalev on 27/01/17.
 */
public final class Pages {
    private Map<String, AlfaPage> pages;
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

    public static <T extends AlfaPage> void withPage(Class<T> clazz, boolean checkIfElementsAppeared, Consumer<T> consumer) {
        T page = getPage(clazz, checkIfElementsAppeared);
        consumer.accept(page);
    }

    public AlfaPage get(String pageName) {
        return getPageMapInstanceInternal().get(pageName);
    }

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

    public static <T extends AlfaPage> T getPage(Class<T> clazz, boolean checkIfElementsAppeared) {
        T page = Selenide.page(clazz);
        if(checkIfElementsAppeared) {
            page.isAppeared();
        }
        return page;
    }

    public void put(String pageName, Class<? extends AlfaPage> clazz) {
        pages.put(pageName, Selenide.page(clazz).initialize());
    }
}
